package com.orange.edu.orders.service;


import com.orange.edu.messagesdk.model.po.MqMessage;
import com.orange.edu.orders.model.dto.AddOrderDto;
import com.orange.edu.orders.model.dto.PayRecordDto;
import com.orange.edu.orders.model.dto.PayStatusDto;
import com.orange.edu.orders.model.po.PayRecord;

/**
 * 订单服务接口
 *
 */
public interface OrderService {

    /**
     * 创建商品订单，添加支付记录
     *
     * @param addOrderDto 订单信息
     * @return PayRecordDto 支付交易记录(包括二维码)
     *
     */
    PayRecordDto createOrder(String userId, AddOrderDto addOrderDto);

    /**
     * 请求支付宝查询支付结果
     * @param payNo 支付记录id
     * @return 支付记录信息
     */
    public PayRecordDto queryPayResult(String payNo);


    /**
     * 查询支付交易记录
     *
     * @param payNo 交易记录号
     *
     */
    PayRecord getPayRecordByPayNo(String payNo);

    /**
     * 保存支付宝支付结果（更新【支付记录】以及【订单】状态）
     *
     * @param payStatusDto 支付结果信息
     *
     */
    void saveAliPayStatus(PayStatusDto payStatusDto);


    /**
     * 发送通知结果
     * @param message
     */
    public void notifyPayResult(MqMessage message);



}
