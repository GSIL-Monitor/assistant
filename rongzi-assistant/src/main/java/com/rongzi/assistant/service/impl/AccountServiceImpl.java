package com.rongzi.assistant.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.rongzi.assistant.common.exception.AssistantExceptionEnum;
import com.rongzi.assistant.dao.AccountMapper;
import com.rongzi.assistant.model.Account;
import com.rongzi.assistant.service.AccountService;
import com.rongzi.core.exception.GunsException;
import com.rongzi.core.util.DesUtil;
import com.rongzi.core.util.MD5Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author rongzi123
 * @since 2018-08-27
 */
@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {

    private Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    @Override
    public Account login(String username, String password) throws GunsException {
        EntityWrapper<Account> wrapper = new EntityWrapper<>();
        wrapper.eq("SUBSTRING(EMP_CODE, 3, 10)", username);
        wrapper.eq("USE_FLAG", 1);
        Account account = selectOne(wrapper);
        if (account == null) {
            throw new GunsException(AssistantExceptionEnum.WRONG_USERNAME_PASSWORD);
        }

        String clearText = null;
        try {
            clearText = DesUtil.decrypt(account.getLgnPswd(), "ddddffff");
            logger.debug("ClearText:" + clearText);
        } catch (Exception e) {
            throw new GunsException(AssistantExceptionEnum.WRONG_USERNAME_PASSWORD);
        }

        String truePassword = MD5Util.encrypt(clearText);
        logger.debug("TruePassword:" + truePassword);
        logger.debug("InputPassword:" + password + "; UserName:" + username);
        if (!password.equals(truePassword)) {
            throw new GunsException(AssistantExceptionEnum.WRONG_USERNAME_PASSWORD);
        }

        return account;
    }

}
