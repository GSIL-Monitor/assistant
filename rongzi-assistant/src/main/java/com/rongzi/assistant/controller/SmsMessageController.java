package com.rongzi.assistant.controller;

import com.alibaba.fastjson.JSON;
import com.rongzi.assistant.common.tips.AssistantTip;
import com.rongzi.assistant.common.util.ValidatorParamUtil;
import com.rongzi.assistant.model.SmsMessage;
import com.rongzi.assistant.model.SystemMessageParam;
import com.rongzi.assistant.service.sms.SmsMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sms")
public class SmsMessageController {


    @Autowired
    SmsMessageService smsMessageService;


    /**
     * 获取销售系统短信信息到手机中
     *
     * @param
     * @return
     */
    @PostMapping("/getMessages")
    public AssistantTip findAllMsgFromSaleSystem(@RequestBody @Valid SystemMessageParam systemMessageParam, BindingResult bindingResult) {

        AssistantTip assistantTip = new AssistantTip();
        Map<String, Object> bindingResultMap = new HashMap<String, Object>();

        if (bindingResult.hasErrors()) {
            assistantTip = ValidatorParamUtil.getAssistantTip(bindingResult, assistantTip, bindingResultMap);
        } else {
            List<SmsMessage> msgs = smsMessageService.findMsgsFromSaleSystem(systemMessageParam.getEmpCode(), systemMessageParam.getCustomerCode(), systemMessageParam.getCustomerMobile());
            assistantTip = AssistantTip.ok(JSON.toJSON(msgs));
        }
        return assistantTip;
    }


    /**
     * 同步手机短信到销售系统
     */
    @PostMapping("/addMessages")
    public AssistantTip addMsgsToSaleSystem(@RequestBody @Valid List<SmsMessage> msgs, BindingResult bindingResult) {

        AssistantTip assistantTip = new AssistantTip();
        Map<String, Object> bindingResultMap = new HashMap<String, Object>();
        if (bindingResult.hasErrors()) {
            assistantTip = ValidatorParamUtil.getAssistantTip(bindingResult, assistantTip, bindingResultMap);
        } else {
            Date lastSmsSyncTime= smsMessageService.addMsgsToSaleSystem(msgs);
            assistantTip = AssistantTip.ok(lastSmsSyncTime);
        }
        return assistantTip;
    }


}
