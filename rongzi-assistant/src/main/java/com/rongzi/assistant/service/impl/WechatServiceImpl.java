package com.rongzi.assistant.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rongzi.assistant.common.datasource.AssistantDataSource;
import com.rongzi.assistant.common.datasource.AssistantDatasourceEnum;
import com.rongzi.assistant.common.exception.AssistantExceptionEnum;
import com.rongzi.assistant.common.util.ByteMergeUtil;
import com.rongzi.assistant.dao.CustomerMapper;
import com.rongzi.assistant.model.OpenApiSendAddFriendRequestForm;
import com.rongzi.assistant.model.WechatParam;
import com.rongzi.assistant.service.WechatService;
import com.rongzi.core.exception.GunsException;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Date;

@Service
public class WechatServiceImpl implements WechatService {

    private Logger logger = LoggerFactory.getLogger(WechatServiceImpl.class);

    @Autowired
    CustomerMapper customerMapper;

    @Override
    @AssistantDataSource(name = AssistantDatasourceEnum.DATA_SOURCE_CITY)
    public int addFriend(WechatParam wechatParam) {
        OpenApiSendAddFriendRequestForm addFriendData = new OpenApiSendAddFriendRequestForm();
        addFriendData.setWechatId(wechatParam.getEmpWechatId());
        addFriendData.setAccountSecret(wechatParam.getAccountSecret());
        addFriendData.setMessage("东方融资网的融资顾问( " + wechatParam.getEmpName() + " )");
        addFriendData.setPhone(wechatParam.getCustomerMobile());
        addFriendData.setRemark(wechatParam.getRemark());
        addFriendData.setTargetWechatId(wechatParam.getCustomerWechatId());
        CloseableHttpResponse response = null;
        try {
            String bodyParam = JSON.toJSONString(addFriendData);
            byte[] bodyBytes = bodyParam.getBytes();
            Long Timestamp = System.currentTimeMillis();
            byte[] timeBytes = Timestamp.toString().getBytes("UTF-8");
            String constants = wechatParam.getWeChatConstantStr();
            byte[] constantBytes = constants.getBytes("UTF-8");
            byte[] dataBytes = ByteMergeUtil.byteMergerAll(bodyBytes, timeBytes, constantBytes);
            String targetStr = DigestUtils.sha1Hex(dataBytes);

            CloseableHttpClient httpclient = HttpClients.createDefault();
            String serverUrl = "https://aochuang.qianzhitech.com:9991/api/ThirdParty/sendAddFriendRequest";
            HttpPost hpost = new HttpPost(serverUrl);
            hpost.setHeader("Timestamp", Timestamp.toString());
            hpost.setHeader("Signature", targetStr);
            hpost.setHeader("Content-type", "application/json; charset=utf-8");
            hpost.setHeader("Accept", "application/json");
            hpost.setEntity(new StringEntity(bodyParam, Charset.forName("UTF-8")));
            response = httpclient.execute(hpost);
            int statusCode = response.getStatusLine().getStatusCode();
            logger.info("请求奥创返回的状态码是：" + statusCode);
            if (200 == statusCode) {
                JSONObject jsonObject = JSONObject.parseObject(EntityUtils.toString(response.getEntity(), "UTF-8"));
                int ret = (Integer) jsonObject.get("ret");
                logger.info("请求奥创添加微信返回的状态码是：" + ret);
                if (0 == ret) {
                    customerMapper.updateCustomerWechatStatus(3, new Date(), wechatParam.getCustomerMobile());
                    return 3;
                } else {
                    throw new GunsException(AssistantExceptionEnum.WECHAT_ADDFRIEND_ERROR);
                }
            } else {
//                return  7;
                throw new GunsException(AssistantExceptionEnum.WECHAT_ADDFRIEND_ERROR);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 5;
    }

    @Override
    @AssistantDataSource(name = AssistantDatasourceEnum.DATA_SOURCE_CITY)
    public void updateFriendStatus(WechatParam wechatParam) {
        customerMapper.updateCustomerWechatStatus(wechatParam.getFriendStatus(), new Date(), wechatParam.getCustomerMobile());
    }
}
