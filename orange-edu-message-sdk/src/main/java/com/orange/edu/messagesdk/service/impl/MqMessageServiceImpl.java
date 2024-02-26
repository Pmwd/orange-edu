package com.orange.edu.messagesdk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.orange.edu.messagesdk.mapper.MqMessageHistoryMapper;
import com.orange.edu.messagesdk.mapper.MqMessageMapper;
import com.orange.edu.messagesdk.model.po.MqMessage;
import com.orange.edu.messagesdk.service.MqMessageHistoryService;
import com.orange.edu.messagesdk.service.MqMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author pmwd
 */
@Slf4j
@Service
public class MqMessageServiceImpl extends ServiceImpl<MqMessageMapper, MqMessage> implements MqMessageService {

    @Autowired
    private MqMessageMapper mqMessageMapper;
    @Autowired
    private MqMessageHistoryMapper mqMessageHistoryMapper;

    @Autowired
    MqMessageHistoryService mqMessageHistoryService;

    @Override
    public List<MqMessage> getMessageList(int shardIndex, int shardTotal, String messageType, int count) {
        return null;
    }

    @Override
    public MqMessage addMessage(String messageType, String businessKey1, String businessKey2, String businessKey3) {
        MqMessage mqMessage = new MqMessage();
        mqMessage.setMessageType(messageType);
        mqMessage.setBusinessKey1(businessKey1);
        mqMessage.setBusinessKey2(businessKey2);
        mqMessage.setBusinessKey3(businessKey3);
        int insert = mqMessageMapper.insert(mqMessage);
        if (insert > 0) {
            return mqMessage;
        } else {
            return null;
        }
    }

    @Override
    public int completed(long id) {
        return 0;
    }

    @Override
    public int completedStageOne(long id) {
        return 0;
    }

    @Override
    public int completedStageTwo(long id) {
        return 0;
    }

    @Override
    public int completedStageThree(long id) {
        return 0;
    }

    @Override
    public int completedStageFour(long id) {
        return 0;
    }

    @Override
    public int getStageOne(long id) {
        return 0;
    }

    @Override
    public int getStageTwo(long id) {
        return 0;
    }

    @Override
    public int getStageThree(long id) {
        return 0;
    }

    @Override
    public int getStageFour(long id) {
        return 0;
    }
}
