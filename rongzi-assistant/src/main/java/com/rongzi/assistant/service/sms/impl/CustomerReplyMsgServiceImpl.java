package com.rongzi.assistant.service.sms.impl;

import com.rongzi.assistant.common.datasource.AssistantDataSource;
import com.rongzi.assistant.common.datasource.AssistantDatasourceEnum;
import com.rongzi.assistant.dao.CustomerReplyMsgMapper;
import com.rongzi.assistant.model.SmsMessage;
import com.rongzi.assistant.service.sms.CustomerReplyMsgService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerReplyMsgServiceImpl implements CustomerReplyMsgService {


    private Logger logger= LoggerFactory.getLogger(UserSendMsgServiceImpl.class);

    @Autowired
    CustomerReplyMsgMapper customerReplyMsgMapper;

    /**
     * 获取销售系统里面的客户回复的短信
     *
     * @param customerMobile
     * @param customerCode
     * @param empCode
     * @return
     */
    @Override
    @AssistantDataSource(name = AssistantDatasourceEnum.DATA_SOURCE_MNG)
    public List<SmsMessage> findReplyMsgsByCustomerMobile(String customerMobile, String customerCode, String empCode) {
        List<SmsMessage> replyMsgs = customerReplyMsgMapper.queryCustomerReplyMsgs(customerMobile);
        List<SmsMessage> resultList = new ArrayList<SmsMessage>();
        for (SmsMessage replyMsg : replyMsgs) {
            replyMsg.setSenderRole(2);
            replyMsg.setSender(customerCode);
            replyMsg.setSenderMobile(customerMobile);
            replyMsg.setReceiver(empCode);
            replyMsg.setIsRead(1);
            resultList.add(replyMsg);
        }
        return resultList;
    }

    /**
     * 将客户回复的短信增加到销售系统
     *
     * @param replyList
     * @return
     */
    @Override
    @AssistantDataSource(name = AssistantDatasourceEnum.DATA_SOURCE_MNG)
    public boolean addMsgsToSaleSystem(List<SmsMessage> replyList) {
        logger.info("****************客户回复的短信*********************************");
        for (SmsMessage smsMessage : replyList) {
            logger.info(smsMessage.toString());
        }
        logger.info("*******************客户回复的短信**********************************");
        boolean flag = customerReplyMsgMapper.addMsgsToSaleSystem(replyList);
        return flag;
    }

}
