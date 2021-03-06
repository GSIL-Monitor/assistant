package com.rongzi.assistant.common.datasource;

import com.rongzi.assistant.common.web.context.UserContextHolder;
import com.rongzi.assistant.model.UserInfo;
import com.rongzi.core.mutidatasource.DataSourceContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class MultiDataSourceAop implements Ordered {

    private Logger logger = LoggerFactory.getLogger(MultiDataSourceAop.class);

    @Pointcut(value = "@annotation(com.rongzi.assistant.common.datasource.AssistantDataSource)")
    public void cut() {
    }

    @Around("cut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Signature signature = proceedingJoinPoint.getSignature();
        if (!(signature instanceof MethodSignature)) {
            throw new IllegalArgumentException("该注解只能用于方法");
        }

        MethodSignature methodSignature = (MethodSignature) signature;
        Object target = proceedingJoinPoint.getTarget();
        Method currentMethod = target.getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
        AssistantDataSource assistantDataSource = currentMethod.getAnnotation(AssistantDataSource.class);
        String name = assistantDataSource.name();

        if (name != null) {
            if (name.equals(AssistantDatasourceEnum.DATA_SOURCE_PRODUCT)) {
                // 产品数据库
                DataSourceContextHolder.setDataSourceType(AssistantDatasourceEnum.DATA_SOURCE_PRODUCT);
                logger.debug("系统当前所在数据源为：" + AssistantDatasourceEnum.DATA_SOURCE_PRODUCT);
            } else if (name.equals(AssistantDatasourceEnum.DATA_SOURCE_MNG)) {
                // MNG数据库
                DataSourceContextHolder.setDataSourceType(AssistantDatasourceEnum.DATA_SOURCE_MNG);
                logger.debug("系统当前所在数据源为：" + AssistantDatasourceEnum.DATA_SOURCE_MNG);
            } else {
                // 分公司城市数据库
                UserInfo currentUserInfo = UserContextHolder.getCurrentUserInfo();
                DataSourceContextHolder.setDataSourceType(currentUserInfo.getCityCode());
                logger.debug("设置当前城市数据源为：" + currentUserInfo.getCityCode());
            }
        }

        try {
            return proceedingJoinPoint.proceed();
        } finally {
            DataSourceContextHolder.clearDataSourceType();
        }
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
