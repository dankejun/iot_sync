package com.iot.sync.test;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.iot.sync.dto.AbilityResult;
import com.iot.sync.mapper.DeviceMapper;
import com.iot.sync.mapper.MapperFactory;
import com.iot.sync.model.Device;
import com.iot.sync.service.impl.ScenSendService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
@Slf4j
public class Test {
    protected static final ScenSendService scenSendService = new ScenSendService();
    protected SqlSessionFactory getSqlSessionFactory(){
        return MapperFactory.initSqlSessionFactory();
    }
    protected List<Device> devices(String ... ids) {
        SqlSessionFactory sessionFactory = MapperFactory.initSqlSessionFactory();
        try (SqlSession session = sessionFactory.openSession(true)) {
            DeviceMapper mapper = session.getMapper(DeviceMapper.class);
            return mapper.selectList(Wrappers.<Device>lambdaQuery().in(Device::getId, Lists.newArrayList(ids)));
        }
    }
    public List<Device> devices() {
        SqlSessionFactory sessionFactory = MapperFactory.initSqlSessionFactory();
        try (SqlSession session = sessionFactory.openSession(true)) {
            DeviceMapper mapper = session.getMapper(DeviceMapper.class);
            return mapper.selectList(Wrappers.<Device>lambdaQuery().eq(Device::getShowStatus,"2").eq(Device::getCatCode,"DB"));
        }
    }
    public List<Device> devices(LambdaQueryWrapper<Device> wrapper) {
        SqlSessionFactory sessionFactory = MapperFactory.initSqlSessionFactory();
        try (SqlSession session = sessionFactory.openSession(true)) {
            DeviceMapper mapper = session.getMapper(DeviceMapper.class);
            return mapper.selectList(wrapper);
        }
    }


    public void updateScenseType(String id) {
        SqlSessionFactory sessionFactory = MapperFactory.initSqlSessionFactory();
        try (SqlSession session = sessionFactory.openSession(true)) {
            DeviceMapper mapper = session.getMapper(DeviceMapper.class);
             mapper.update(null,Wrappers.<Device>lambdaUpdate().set(Device::getScenceType,"1").eq(Device::getId,id));
        }
    }
    public void updateShowStatus(String id) {
        SqlSessionFactory sessionFactory = MapperFactory.initSqlSessionFactory();
        try (SqlSession session = sessionFactory.openSession(true)) {
            DeviceMapper mapper = session.getMapper(DeviceMapper.class);
            mapper.update(null,Wrappers.<Device>lambdaUpdate().set(Device::getShowStatus,"1").eq(Device::getId,id).eq(Device::getShowStatus,"0"));
        }
    }


    ExecutorService executorService = Executors.newFixedThreadPool(10);

    public void addProgramProfile(String deviceId,String catCode,String a0) throws  IOException {

        String result = scenSendService.saveProgramProfile(deviceId, catCode,a0);
        FileUtils.writeLines(new File("D:\\guyl16\\ifttt.log"), Lists.newArrayList(deviceId + "-----------------" + result), true);
    }




    public void syncSandbox( List<Device> list ) {
        for (Device device : list) {
            scenSendService.syncSandbox(device.getId());
        }
    }
    /**
     * 删除复式机程序能力
     */
    @org.junit.jupiter.api.Test
    public void testD9ProgramProfile() throws InterruptedException {
        List<Device> devices = devices();
        String[]array=new String[]{"down_light"};

        for (String s : array) {
            delete(devices,s);
        }

    }


    protected void upAbility(String prdId) {
        List<AbilityResult.Result> abilitys = scenSendService.queryAbilitys(prdId).getResult();
        abilitys=abilitys.stream().filter(a-> "data_type".equals(a.getIdentifier())).collect(Collectors.toList());

        List<String> ids = abilitys.stream().map(AbilityResult.Result::getId).collect(Collectors.toList());
        log.info("testUpAbility {}------------{}", prdId, scenSendService.upAbility(ids));
    }
    protected void syncSandbox(String prdId) {

        log.info("{}------------{}", prdId, scenSendService.syncSandbox(prdId));
    }

    protected void delete( List<Device> devices,String name)throws InterruptedException{
        if (CollectionUtils.isNotEmpty(devices)) {
            CountDownLatch latch=new CountDownLatch(devices.size());
            devices.forEach(device -> {
                executorService.submit(() -> {

                    try {
                        AbilityResult abilitys = scenSendService.queryAbilitys(device.getId());
                        String id = abilitys.getResult().stream().filter(r -> r.getIdentifier().equals(name)).collect(Collectors.toList()).get(0).getId();
                        String result =scenSendService.batchDelete(Lists.newArrayList(id));

                        FileUtils.writeLines(new File("D:\\guyl16\\ifttt.log"), Lists.newArrayList(device.getId() + "-----------------" + result), true);
                    } catch (IOException ignored) {
                        try {
                            FileUtils.writeLines(new File("D:\\guyl16\\ifttt.log"), Lists.newArrayList(device.getId() + "-----------------" + ignored.getMessage()), true);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    finally {
                        latch.countDown();
                    }
                });

            });
            latch.await();
        }
    }

}
