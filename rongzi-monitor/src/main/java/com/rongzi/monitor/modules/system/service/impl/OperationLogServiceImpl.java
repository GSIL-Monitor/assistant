package com.rongzi.monitor.modules.system.service.impl;

import com.rongzi.monitor.modules.system.dao.OperationLogMapper;
import com.rongzi.monitor.modules.system.model.OperationLog;
import com.rongzi.monitor.modules.system.service.IOperationLogService;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 操作日志 服务实现类
 * </p>
 *
 * @author stylefeng123
 * @since 2018-02-22
 */
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements IOperationLogService {

    @Override
    public List<Map<String, Object>> getOperationLogs(Page<OperationLog> page, String beginTime, String endTime, String logName, String s, String orderByField, boolean asc) {
        return this.baseMapper.getOperationLogs(page, beginTime, endTime, logName, s, orderByField, asc);
    }
}
