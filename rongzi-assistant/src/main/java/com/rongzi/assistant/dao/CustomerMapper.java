package com.rongzi.assistant.dao;


import com.baomidou.mybatisplus.plugins.Page;
import com.rongzi.assistant.model.CallRecord;
import com.rongzi.assistant.model.Customer;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface CustomerMapper {


    List<Customer> queryAllCutomers(@Param("page") Page page, @Param("empCode") String empCode, @Param("customerExeStatus") int customerExeStatus);

    void editCommentByCodeAndComment(@Param("customerCode") String customerCode, @Param("comment") String comment);

    boolean syncContactStatusByCallRecords(@Param("callRecords") List<CallRecord> callRecords);

    Customer queryCustomerNameAndCustomerCode(@Param("mobile")String mobile);

    void updateCustomerWechatStatus(@Param("wechatFriendStatus") int weChatStatus, @Param("date") Date date, @Param("customerMobile") String customerMobile);
}
