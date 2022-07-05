package com.iot.sync.test;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.LocalDateTimeUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Lists;
import com.iot.sync.dto.AbilityProfile;
import com.iot.sync.dto.AbilityResult;
import com.iot.sync.dto.PrdResult;
import com.iot.sync.model.Device;
import com.iot.sync.service.DeviceService;
import com.iot.sync.service.impl.ScenSendService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest
public class ScenceProgramTest {
    @Autowired
    private DeviceService deviceService;
    @Autowired
    private ScenSendService sendService;

    /**
     * 上架流程处理完成更新数据库 show_status=3
     */
    @Test
    public void updateconnectedProduct(){
        List<PrdResult> list = sendService.connectedProductList();

        if(list!=null && !CollectionUtils.isEmpty(list)){
            list=list.stream().filter(p-> p.getPublishAbilityCount()>0).collect(Collectors.toList());
            List<Device> devices= Lists.newArrayList();
            for (PrdResult result : list) {
                Device device = new Device();
                device.setId(result.getPrdId());
                //状态设置为已添加
                device.setScence("1");
                devices.add(device);
                device.setPublishAbilityCount(result.getPublishAbilityCount());
                if(result.getPublishAbilityCount()>1){
                    device.setShowStatus(3);
                }

            }
            deviceService.updateBatchById(devices);
        }
    }
    /**
     * 同步开发者平台中的设备到数据库
     */
    @Test
    public void savePrdList() {
        List<PrdResult> list = sendService.deviceList();
        for (PrdResult result : list) {
            Device device = new Device();
            device.setId(result.getPrdId());
            device.setCatCode(result.getCatCode());
            device.setName(result.getName());

            device.setA0(result.getA0());
            device.setProductModel(result.getProductModel());
            device.setShowStatus(result.getShowStatus());
            device.setSn8(result.getSn8());
            device.setTags(result.getTags());
            if (StringUtils.isNotBlank(device.getA0())) {
                if (StringUtils.isNotBlank(result.getCreateTime())) {
                    LocalDateTime createTime = LocalDateTimeUtil.parse(result.getCreateTime(), DatePattern.NORM_DATETIME_PATTERN);
                    device.setCreateTime(createTime);
                }
                if(deviceService.count(Wrappers.<Device>lambdaQuery().eq(Device::getId,device.getId()))==0){
                    deviceService.save(device);
                }
                //else{
                //    deviceService.updateById(device);
                //}
            }
        }

    }

    @Test
    public void testSubmitCodeDev() {
        List<Device> list = deviceService.list(Wrappers.<Device>lambdaQuery().in(Device::getSn8, Lists.newArrayList("38100HQ9",
                "38100HR0",
                "38121345",
                "38121346",
                "38122265",
                "38122264",
                "38100JA1",
                "38100JA0",
                "38123445",
                "38123448",
                "38100JK9",
                "38100JF4",
                "38115425")));
        for (Device device : list) {
            List<AbilityResult.Result> abilitys = sendService.queryAbilitys(device.getId()).getResult();
            log.info("{}---{}", device.getId(), sendService.submitCodeDev(device.getId(), abilitys.stream().map(AbilityResult.Result::getId).collect(Collectors.toList())));
        }
    }

    @Test
    public void deleteAbility() {
        List<Device> list = deviceService.list(Wrappers.<Device>lambdaQuery().in(Device::getSn8, Lists.newArrayList("38100HQ9",
                "38100HR0",
                "38121345",
                "38121346",
                "38122265",
                "38122264",
                "38100JA1",
                "38100JA0",
                "38123445",
                "38123448",
                "38100JK9",
                "38100JF4",
                "38115425")));
        for (Device device : list) {
            List<AbilityResult.Result> abilitys = sendService.queryAbilitys(device.getId()).getResult();
            log.info("{}---{}", device.getId(), sendService.batchDelete(abilitys.stream().map(AbilityResult.Result::getId).collect(Collectors.toList())));
        }
    }

    @Test
    public void syncSandbox() {
        List<Device> list = deviceService.list(Wrappers.<Device>lambdaQuery().in(Device::getSn8, Lists.newArrayList("38100HQ9",
                "38100HR0",
                "38121345",
                "38121346",
                "38122265",
                "38122264",
                "38100JA1",
                "38100JA0",
                "38123445",
                "38123448",
                "38100JK9",
                "38100JF4",
                "38115425")));
        for (Device device : list) {
            log.info("{}------------{}", device.getId(), sendService.syncSandbox(device.getId()));
        }
    }

    @Test
    public void testUpAbility() {
        List<Device> list = deviceService.list(Wrappers.<Device>lambdaQuery().in(Device::getSn8, Lists.newArrayList(
                "38100HR0",
                "38121345",
                "38121346",
                "38122265",
                "38122264",
                "38100JA1",
                "38100JA0",
                "38123445",
                "38123448",
                "38100JK9",
                "38100JF4",
                "38115425")));
        for (Device device : list) {
            List<AbilityResult.Result> abilitys = sendService.queryAbilitys(device.getId()).getResult();
            List<String> ids = abilitys.stream().filter(a -> a.getIdentifier().equals("control_status"))
                    .map(AbilityResult.Result::getId).collect(Collectors.toList());
            log.info("{}------------{}", device.getId(), sendService.upAbility(ids));

        }
    }

    @Test
    public void testsaveAbilityProfile() {
        List<Device> list = deviceService.list(Wrappers.<Device>lambdaQuery().in(Device::getSn8, Lists.newArrayList("38100HQ9",
                "38100HR0",
                "38121345",
                "38121346",
                "38122265",
                "38122264",
                "38100JA1",
                "38100JA0",
                "38123445",
                "38123448",
                "38100JK9",
                "38100JF4",
                "38115425")));
        AbilityProfile.AbilityThenRelation relation = new AbilityProfile.AbilityThenRelation();
        relation.setOrder(1);
        relation.setIdentifier("power");
        relation.setName("电源");
        List<AbilityProfile.AbilityThenRelation> relations = Lists.newArrayList(relation);
        AbilityProfile.DependsAttrSet d = new AbilityProfile.DependsAttrSet();
        d.setIdentifier("on");
        d.setDisplayName("开机");
        d.setName("开机");

        relation.setDependsAttrSet(Lists.newArrayList(d));
        for (Device device : list) {

            String result = sendService.saveAbilityProfile(device.getId(), null, "控制状态", "control_status", "start-启动,pause-暂停", relations);
            log.info("{}------------{}", device.getId(), result);
        }
    }

    @Test
    public void addRelation() {
        List<Device> list = deviceService.list(Wrappers.<Device>lambdaQuery().in(Device::getSn8, Lists.newArrayList("38100HQ9",
                "38100HR0",
                "38121345",
                "38121346",
                "38122265",
                "38122264",
                "38100JA1",
                "38100JA0",
                "38123445",
                "38123448",
                "38100JK9",
                "38100JF4",
                "38115425")));
        //"eco-节能,fast_wash-快洗/快洗15',mixed_wash-混合洗,wool-羊毛,ssp-筒自洁,single_dehytration-单脱水,rinsing_dehydration-漂洗+脱水,big-大件,down_jacket-羽绒服,intelligent-智能洗"
        for (Device device : list) {
            AbilityProfile.AbilityThenRelation relation = new AbilityProfile.AbilityThenRelation();
            relation.setOrder(1);
            relation.setIdentifier("power");
            relation.setName("电源");
            List<AbilityProfile.AbilityThenRelation> relations = Lists.newArrayList(relation);
            AbilityProfile.DependsAttrSet d = new AbilityProfile.DependsAttrSet();
            d.setIdentifier("on");
            d.setDisplayName("开机");
            d.setName("开机");

            relation.setDependsAttrSet(Lists.newArrayList(d));
            List<AbilityResult.Result> abilitys = sendService.queryAbilitys(device.getId()).getResult();

            String result = sendService.saveAbilityProfile(device.getId(),
                    abilitys.stream().filter(r -> r.getIdentifier().equals("control_status")).map(AbilityResult.Result::getId).collect(Collectors.toList()).get(0),
                    "控制状态",
                    "control_status", "start-启动",
                    relations);
            log.info("{}------------{}", device.getId(), result);




        }
    }


}
