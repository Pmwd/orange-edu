package com.orange.edu.learning.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.orange.edu.learning.config.PayNotifyConfig;
import com.orange.edu.learning.mapper.ChooseCourseMapper;
import com.orange.edu.learning.model.po.ChooseCourse;
import com.orange.edu.learning.service.MyCourseTablesService;
import com.orange.edu.learning.service.ReceivePayNotifyService;
import com.orange.edu.messagesdk.model.po.MqMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 *
 */
@Slf4j
@Service
public class ReceivePayNotifyServiceImpl implements ReceivePayNotifyService {

    @Resource
    private ChooseCourseMapper chooseCourseMapper;

    @Autowired
    private MyCourseTablesService myCourseTablesService;

    @Autowired
    private RabbitTemplate rabbitTemplate;


    /**
     * 监听支付结果通知队列，更新选课状态，添加课程表 功能，同时发送回复信息
     *
     * @param message 消息
     */
    @Transactional
    @RabbitListener(queues = {PayNotifyConfig.PAYNOTIFY_QUEUE})
    public void receive(String message) {
        // 解析消息
        MqMessage mqMessage = JSON.parseObject(message, MqMessage.class);
        // 判断是否是自己的消息
        String messageType = mqMessage.getMessageType();
        // 记录了订单信息
        String orderType = mqMessage.getBusinessKey2();
        // 只处理支付结果通知的消息并且是学生购买课程的订单消息
        if (PayNotifyConfig.MESSAGE_TYPE.equals(messageType) && "60201".equals(orderType)) {
            // 根据选课id查询选课表的记录
            String chooseCourseId = mqMessage.getBusinessKey1();
            ChooseCourse chooseCourse = chooseCourseMapper.selectById(chooseCourseId);
            if (chooseCourse == null) {
                log.info("收到支付结果通知，查询不到选课记录，chooseCourseId：{}", chooseCourseId);
                return;
            }

            // 更新选课状态
            ChooseCourse chooseCourse_u = new ChooseCourse();
            chooseCourse_u.setStatus("701001"); // 选课成功
            chooseCourseMapper.update(chooseCourse_u, new LambdaQueryWrapper<ChooseCourse>().eq(ChooseCourse::getId, chooseCourseId));

            // 查询最新的选课记录
            chooseCourse = chooseCourseMapper.selectById(chooseCourseId);
            // 添加一条课程表记录
            myCourseTablesService.addCourseTables(chooseCourse);

            // 发送回复消息
            send(mqMessage);
        }
    }

    @Override
    public void send(MqMessage mqMessage) {
        // 转 json
        String msg = JSON.toJSONString(mqMessage);
        // 发送消息
        rabbitTemplate.convertAndSend(PayNotifyConfig.PAYNOTIFY_REPLY_QUEUE, msg);
        log.info("学习中心服务向订单服务回复消息：{}", mqMessage);
    }
}
