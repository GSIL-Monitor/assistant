package com.rongzi.monitor.modules.system.factory;

import com.rongzi.monitor.modules.system.model.User;
import com.rongzi.monitor.modules.system.transfer.UserDto;
import org.springframework.beans.BeanUtils;

/**
 * 用户创建工厂
 *
 * @author fengshuonan
 * @date 2017-05-05 22:43
 */
public class UserFactory {

    public static User createUser(UserDto userDto){
        if(userDto == null){
            return null;
        }else{
            User user = new User();
            BeanUtils.copyProperties(userDto,user);
            return user;
        }
    }
}
