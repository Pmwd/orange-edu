package com.orange.edu.orders.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orange.base.exception.OrangeEduException;
import com.orange.base.utils.IdWorkerUtils;
import com.orange.base.utils.QRCodeUtil;
import com.orange.edu.messagesdk.model.po.MqMessage;
import com.orange.edu.messagesdk.service.MqMessageService;
import com.orange.edu.orders.config.AlipayConfig;
import com.orange.edu.orders.config.PayNotifyConfig;
import com.orange.edu.orders.mapper.GoodsMapper;
import com.orange.edu.orders.mapper.OrdersMapper;
import com.orange.edu.orders.mapper.PayRecordMapper;
import com.orange.edu.orders.model.dto.AddOrderDto;
import com.orange.edu.orders.model.dto.PayRecordDto;
import com.orange.edu.orders.model.dto.PayStatusDto;
import com.orange.edu.orders.model.po.Orders;
import com.orange.edu.orders.model.po.OrdersGoods;
import com.orange.edu.orders.model.po.PayRecord;
import com.orange.edu.orders.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 订单接口实现类
 *
 * @author Wuxy
 * @version 1.0
 * @since 2022/10/25 11:42
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrdersMapper ordersMapper;

    @Autowired
    GoodsMapper ordersGoodsMapper;

    @Autowired
    PayRecordMapper payRecordMapper;

    @Autowired
    MqMessageService mqMessageService;

    @Value("${pay.alipay.APP_ID}")
    String APP_ID;

    @Value("${pay.alipay.APP_PRIVATE_KEY}")
    String APP_PRIVATE_KEY;

    @Value("${pay.alipay.ALIPAY_PUBLIC_KEY}")
    String ALIPAY_PUBLIC_KEY;

    @Autowired
    OrderService currentProxy;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Transactional
    @Override
    public PayRecordDto createOrder(String userId, AddOrderDto addOrderDto) {
        // 创建商品订单
        Orders orders = saveOrders(userId, addOrderDto);
        // 添加支付记录
        PayRecord payRecord = createPayRecord(orders);

        // 生成支付二维码
        String qrCode = null;
        try {
            // url要可以被模拟器访问到，url为下单接口(稍后定义)
            qrCode = new QRCodeUtil().createQRCode("http://192.168.247.1:63030/orders/requestpay?payNo=" + payRecord.getPayNo(), 200, 200);
        } catch (IOException e) {
            OrangeEduException.cast("生成二维码出错");
        }
        // 封装要返回的数据
        PayRecordDto payRecordDto = new PayRecordDto();
        BeanUtils.copyProperties(payRecord, payRecordDto);
        // 支付二维码
        payRecordDto.setQrcode(qrCode);

        return payRecordDto;
    }

    @Override
    public PayRecord getPayRecordByPayNo(String payNo) {
        LambdaQueryWrapper<PayRecord> queryWrapper = new LambdaQueryWrapper<PayRecord>().eq(PayRecord::getPayNo, payNo);
        return payRecordMapper.selectOne(queryWrapper);
    }

    @Transactional
    @Override
    public void saveAliPayStatus(PayStatusDto payStatusDto) {
        // 当支付成功才更新订单状态
        // 首先判断是否支付成功
        String trade_status = payStatusDto.getTrade_status();
        if (trade_status.equals("TRADE_SUCCESS")) {
            // 支付成功了才去更新订单状态
            // 拿到支付记录交易号
            String payNo = payStatusDto.getOut_trade_no();
            PayRecord payRecord = getPayRecordByPayNo(payNo);

            if (payRecord == null) {
                log.info("收到支付结果通知查询不到支付记录，收到的信息：{}", payStatusDto);
                return;
            }

            // 支付结果
            String status = payRecord.getStatus();
            if ("601002".equals(status)) {
                log.info("收到支付结果通知，支付记录状态已经为支付成功，不进行任务操作");
                return;
            }
            // 支付宝传给我们appid
            String appid_alipay = payStatusDto.getApp_id();
            // 支付记录表中记录的总金额
            int totalPriceDb = (int) (payRecord.getTotalPrice() * 100);//转成分
            int total_amount = (int) (Float.parseFloat(payStatusDto.getTotal_amount()) * 100);//转成分
            if (totalPriceDb != total_amount || !appid_alipay.equals(APP_ID)) {
                log.info("收到支付结果通知，校验失败，支付宝参数 appid:{}，total_amount:{}，我们自己的数据：appid：{}, TotalPrice:{}", appid_alipay, payStatusDto.getTotal_amount(), APP_ID, totalPriceDb);
                return;
            }
            // 首先要更新支付记录
            PayRecord payRecord_u = new PayRecord();
            payRecord_u.setStatus("601002"); // 支付成功
            payRecord_u.setOutPayNo(payStatusDto.getTrade_no()); // 支付宝自己的订单号
            payRecord_u.setOutPayChannel("603002"); // 通过支付宝支付
            payRecord_u.setPaySuccessTime(LocalDateTime.now());
            LambdaQueryWrapper<PayRecord> queryWrapper = new LambdaQueryWrapper<PayRecord>().eq(PayRecord::getPayNo, payNo);
            int update = payRecordMapper.update(payRecord_u, queryWrapper);
            if (update > 0) {
                log.info("收到支付宝支付结果通知，更新支付记录表成功：{}", payStatusDto);
            } else {
                log.info("收到支付宝支付结果通知，更新支付记录表失败：{}", payStatusDto);
            }
            // 获取订单
            Long orderId = payRecord.getOrderId(); // 订单id
            Orders orders = ordersMapper.selectById(orderId);
            if (orders == null) {
                log.info("收到支付宝支付结果通知，查询不到订单,支付宝传过来的参数：{}，订单号：{}", payStatusDto, orderId);
                return;
            }

            // 再更新订单状态
            Orders orders_u = new Orders();
            orders_u.setStatus("600002"); // 更新订单状态为支付成功
            int update1 = ordersMapper.update(orders_u, new LambdaQueryWrapper<Orders>().eq(Orders::getId, orderId));
            if (update1 > 0) {
                log.info("收到支付宝支付结果通知，更新订单表成功,支付宝传过来的参数：{}，订单号：{}", payStatusDto, orderId);
                // 找到订单表所关联的外部业务系统的主键
                String outBusinessId = orders.getOutBusinessId();
                //保存消息记录,参数1：支付结果通知类型，2: 业务id，3:业务类型
                MqMessage mqMessage = mqMessageService.addMessage("payresult_notify", orders.getOutBusinessId(), orders.getOrderType(), null);

                //通知消息
                notifyPayResult(mqMessage);

            } else {
                log.info("收到支付宝支付结果通知，更新订单表失败,支付宝传过来的参数：{}，订单号：{}", payStatusDto, orderId);
            }
        }
    }
//    public void saveWxPayStatus(PayStatusDto payStatusDto) {
//        //支付渠道编号603001
//        //先根据支付记录交易号查询支付记录
//        //从支付记录中拿到订单号，查询订单
//        //更新订单的状态
//    }

    // 添加支付记录
    public PayRecord createPayRecord(Orders orders) {
        PayRecord payRecord = new PayRecord();
        long payNo = IdWorkerUtils.getInstance().nextId();
        payRecord.setPayNo(payNo); // 支付记录交易号
        // 记录关键订单id
        payRecord.setOrderId(orders.getId());
        payRecord.setOrderName(orders.getOrderName());
        payRecord.setTotalPrice(orders.getTotalPrice());
        payRecord.setCurrency("CNY");
        payRecord.setCreateDate(LocalDateTime.now());
        payRecord.setStatus("601001"); // 未支付
        payRecord.setUserId(orders.getUserId());

        payRecordMapper.insert(payRecord);

        return payRecord;
    }

    // 创建商品订单
    @Transactional
    public Orders saveOrders(String userId, AddOrderDto addOrderDto) {
        // 幂等性校验
        // 选课记录id
        String outBusinessId = addOrderDto.getOutBusinessId();
        // 对订单插入进行幂等性处理
        // 根据选课记录id从数据库查询订单信息
        Orders orders = getOrderByBusinessId(outBusinessId);
        if (orders != null) {
            return orders;
        }

        // 添加订单
        orders = new Orders();
        long orderId = IdWorkerUtils.getInstance().nextId(); // 订单号
        orders.setId(orderId);
        orders.setTotalPrice(addOrderDto.getTotalPrice());
        orders.setCreateDate(LocalDateTime.now());
        orders.setStatus("600001"); // 未支付
        orders.setUserId(userId);
        orders.setOrderType(addOrderDto.getOrderType());
        orders.setOrderName(addOrderDto.getOrderName());
        orders.setOrderDetail(addOrderDto.getOrderDetail());
        orders.setOrderDescrip(addOrderDto.getOrderDescrip());
        orders.setOutBusinessId(addOrderDto.getOutBusinessId());//选课记录id

        ordersMapper.insert(orders);

        // 插入订单明细表
        String orderDetailJson = addOrderDto.getOrderDetail();
        // 将json转成List
        List<OrdersGoods> OrdersGoods = JSON.parseArray(orderDetailJson, OrdersGoods.class);
        // 将明细List插入数据库
        OrdersGoods.forEach(ordersGood -> {
            // 在明细中记录订单号
            ordersGood.setOrderId(orderId);
            ordersGoodsMapper.insert(ordersGood);
        });

        return orders;
    }

    /**
     * 根据业务id查询订单
     *
     * @param businessId 业务 id
     * @return 对应的订单信息
     */
    public Orders getOrderByBusinessId(String businessId) {
        return ordersMapper.selectOne(new LambdaQueryWrapper<Orders>().eq(Orders::getOutBusinessId, businessId));
    }


    @Override
    public PayRecordDto queryPayResult(String payNo){

        PayRecord payRecord = getPayRecordByPayNo(payNo);
        if (payRecord == null) {
            OrangeEduException.cast("请重新点击支付获取二维码");
        }
        //支付状态
        String status = payRecord.getStatus();
        //如果支付成功直接返回
        if ("601002".equals(status)) {
            PayRecordDto payRecordDto = new PayRecordDto();
            BeanUtils.copyProperties(payRecord, payRecordDto);
            return payRecordDto;
        }
        //从支付宝查询支付结果
        PayStatusDto payStatusDto = queryPayResultFromAlipay(payNo);
        //保存支付结果
        currentProxy.saveAliPayStatus( payStatusDto);
        //重新查询支付记录
        payRecord = getPayRecordByPayNo(payNo);
        PayRecordDto payRecordDto = new PayRecordDto();
        BeanUtils.copyProperties(payRecord, payRecordDto);
        return payRecordDto;

    }

    /**
     * 请求支付宝查询支付结果
     * @param payNo 支付交易号
     * @return 支付结果
     */
    public PayStatusDto queryPayResultFromAlipay(String payNo) {

        //========请求支付宝查询支付结果=============
        AlipayClient alipayClient = new DefaultAlipayClient(AlipayConfig.URL, APP_ID, APP_PRIVATE_KEY, "json", AlipayConfig.CHARSET, ALIPAY_PUBLIC_KEY, AlipayConfig.SIGNTYPE); //获得初始化的AlipayClient
//        AlipayClient alipayClient = DefaultAlipayClient(AlipayConfig.URL, APP_ID, APP_PRIVATE_KEY, AlipayConfig.FORMAT, AlipayConfig.CHARSET, ALIPAY_PUBLIC_KEY, AlipayConfig.SIGNTYPE);//获得初始化的AlipayClient
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", payNo);
        request.setBizContent(bizContent.toString());
        AlipayTradeQueryResponse response = null;
        try {
            response = alipayClient.execute(request);
            if (!response.isSuccess()) {
                OrangeEduException.cast("请求支付查询查询失败");
            }
        } catch (AlipayApiException e) {
            log.error("请求支付宝查询支付结果异常:{}", e.toString(), e);
            OrangeEduException.cast("请求支付查询查询失败");
        }

        //获取支付结果
        String resultJson = response.getBody();
        //转map
        Map resultMap = JSON.parseObject(resultJson, Map.class);
        Map alipay_trade_query_response = (Map) resultMap.get("alipay_trade_query_response");
        //支付结果
        String trade_status = (String) alipay_trade_query_response.get("trade_status");
        String total_amount = (String) alipay_trade_query_response.get("total_amount");
        String trade_no = (String) alipay_trade_query_response.get("trade_no");
        //保存支付结果
        PayStatusDto payStatusDto = new PayStatusDto();
        payStatusDto.setOut_trade_no(payNo);
        payStatusDto.setTrade_status(trade_status);
        payStatusDto.setApp_id(APP_ID);
        payStatusDto.setTrade_no(trade_no);
        payStatusDto.setTotal_amount(total_amount);
        return payStatusDto;

    }


    @Override
    public void notifyPayResult(MqMessage message) {

        //1、消息体，转json
        String msg = JSON.toJSONString(message);
        //设置消息持久化
        Message msgObj = MessageBuilder.withBody(msg.getBytes(StandardCharsets.UTF_8))
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                .build();
        // 2.全局唯一的消息ID，需要封装到CorrelationData中
        CorrelationData correlationData = new CorrelationData(message.getId().toString());
        // 3.添加callback
        correlationData.getFuture().addCallback(
                result -> {
                    if(result.isAck()){
                        // 3.1.ack，消息成功
                        log.debug("通知支付结果消息发送成功, ID:{}", correlationData.getId());
                        //删除消息表中的记录
                        mqMessageService.completed(message.getId());
                    }else{
                        // 3.2.nack，消息失败
                        log.error("通知支付结果消息发送失败, ID:{}, 原因{}",correlationData.getId(), result.getReason());
                    }
                },
                ex -> log.error("消息发送异常, ID:{}, 原因{}",correlationData.getId(),ex.getMessage())
        );
        // 发送消息
        rabbitTemplate.convertAndSend(PayNotifyConfig.PAYNOTIFY_EXCHANGE_FANOUT, "", msgObj,correlationData);

    }



}
