package com.rongzi.assistant.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rongzi.assistant.common.context.UserContextHolder;
import com.rongzi.assistant.model.OpenApiSendAddFriendRequestForm;
import com.rongzi.assistant.model.UserInfo;
import com.rongzi.assistant.model.WechatParam;
import com.rongzi.assistant.service.WechatService;
import com.rongzi.config.aop.CityDataSource;
import com.rongzi.config.aop.CityDatasourceEnum;
import com.rongzi.config.exception.AssistantExceptionEnum;
import com.rongzi.core.exception.GunsException;
import com.rongzi.util.ByteMergeUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

@Service
public class WechatServiceImpl implements WechatService {


    @Override
    @CityDataSource(name = CityDatasourceEnum.DATA_SOURCE_CITY)
    public int addFriend(WechatParam wechatParam) {


        UserInfo currentUser = UserContextHolder.getCurrentUserInfo();

        OpenApiSendAddFriendRequestForm addFriendData=new OpenApiSendAddFriendRequestForm();

        addFriendData.setWechatId(wechatParam.getEmpWechatId());
        addFriendData.setAccountSecret(wechatParam.getAccountSecret());
        addFriendData.setMessage("东方融资网的融资顾问( "+currentUser.getEmpName()+" )");
        addFriendData.setPhone(wechatParam.getCustomerMobile());
        addFriendData.setRemark(wechatParam.getRemark());
        addFriendData.setTargetWechatId(wechatParam.getCustomerWechatId());

        CloseableHttpResponse response=null;

        try {

            String bodyParam = JSON.toJSONString(addFriendData);
            byte[] bodyBytes = bodyParam.getBytes();
            Long Timestamp=System.currentTimeMillis();
            byte[] timeBytes = Timestamp.toString().getBytes("UTF-8");
            String constants=wechatParam.getWeChatConstantStr();
            byte[] constantBytes = constants.getBytes("UTF-8");
            byte[] dataBytes =  ByteMergeUtil.byteMergerAll(bodyBytes, timeBytes, constantBytes);
            String targetStr = DigestUtils.sha1Hex(dataBytes);

            CloseableHttpClient httpclient = HttpClients.createDefault();
            String serverUrl="https://aochuang.qianzhitech.com:9991/api/ThirdParty/sendAddFriendRequest";
            HttpPost hpost = new HttpPost(serverUrl);
            hpost.setHeader("Timestamp",Timestamp.toString());
            hpost.setHeader("Signature",targetStr);
            hpost.setHeader("Content-type", "application/json; charset=utf-8");
            hpost.setHeader("Accept", "application/json");
            hpost.setEntity(new StringEntity(bodyParam, Charset.forName("UTF-8")));
            response = httpclient.execute(hpost);
            int statusCode = response.getStatusLine().getStatusCode();
            if(200==statusCode){
                JSONObject jsonObject = JSONObject.parseObject(EntityUtils.toString(response.getEntity(), "UTF-8"));
                int ret = (Integer) jsonObject.get("ret");
                if(0==ret){
                    return 3;
                }
            }else{
                throw  new GunsException(AssistantExceptionEnum.WECHAT_ADDFRIEND_ERROR);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 5;
    }
}