package com.iot.sync.test;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.iot.sync.mapper.DeviceMapper;
import com.iot.sync.mapper.MapperFactory;
import com.iot.sync.model.Device;
import com.iot.sync.service.impl.ScenSendService;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.List;

public class DcService {
    private static final ScenSendService scenSendService = new ScenSendService();

    private List<Device> devices(String ... id) {
        SqlSessionFactory sessionFactory = MapperFactory.initSqlSessionFactory();
        try (SqlSession session = sessionFactory.openSession(true)) {
            DeviceMapper mapper = session.getMapper(DeviceMapper.class);
            return mapper.selectList(Wrappers.<Device>lambdaQuery().in(Device::getId, Lists.newArrayList(id)));
        }
    }
}
