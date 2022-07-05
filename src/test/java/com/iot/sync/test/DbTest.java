package com.iot.sync.test;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.iot.sync.dto.AbilityResult;
import com.iot.sync.enums.CatCodeEnum;
import com.iot.sync.model.Device;
import com.iot.sync.model.R;
import com.iot.sync.service.impl.ResService;
import com.iot.sync.util.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@SuppressWarnings("all")
public class DbTest extends com.iot.sync.test.Test {
    private static final ResService resService = new ResService();

    /**
     * 获取设备
     *
     * @return
     */
    private List<Device> getDevices() {
        return devices(
                "o4haDZ9ggaLPfo4",
                "0ojIZwy4xQ9PdxI"
        );
    }

    @Test
    public void testImportFile() {
        //List<Device> list = devices(Wrappers.<Device>lambdaQuery().eq(Device::getCatCode,"DC").eq(Device::getShowStatus,1));
        List<Device> list = getDevices();
        list.parallelStream().forEach(d -> {
            //更新已上架的running_status
            //updateRunnStatusDA(d.getId());
            //删除能力
            deleteAbility(d.getId());
            //导入能力
            importProfile(d.getId());
            //保存能力到列表
            testUP(d.getId());
            ////同步到沙箱环境
            syncSandbox(d.getId());
        });
    }

    /**
     * 导入能力
     *
     * @param prdId
     */
    private void importProfile(String prdId) {
        try {
            String json = FileUtils.readFileToString(new File("E:\\IFTTT\\ifttt\\模板\\滚筒5能力.json"), StandardCharsets.UTF_8);

            JavaType type = mapper.getTypeFactory().constructCollectionType(List.class, mapper.getTypeFactory().constructMapType(LinkedHashMap.class, String.class, Object.class));

            List<Map<String, Object>> list = mapper.readValue(json, type);
            for (Map<String, Object> data : list) {
                data.put("prdId", prdId);
                FileUtils.writeLines(new File("E:\\IFTTT\\ifttt\\log\\ifttt.log"), Lists.newArrayList(resService.postString("https://mis.midea.com/v1/scene/ability/profile/save", mapper.writeValueAsString(data))
                ), true);
            }
        } catch (Exception e) {
            log.error("error", e);
        }

    }

    @Test
    void updateRunnStatusDA(String prdId) {
        try {
            String json = resService.postString("https://mis.midea.com/v1/scene/ability/list", String.format("{\"prdId\":\"%s\",\"abilityHardwareType\":1,\"abilityType\":0,\"nameOrIdentifier\":\"\",\"abilityNameOrIdentifier\":\"\",\"lowerNameOrIdentifier\":\"\",\"rw\":\"\",\"dataType\":\"\",\"linkType\":\"\",\"status\":\"\",\"updateStatus\":\"\",\"cacheStatus\":0,\"pageNo\":1,\"pageSize\":10}",prdId));
            Map<String, Object> map = parseJson(json);
            Map<String, Object> data = (Map<String, Object>) map.get("data");
            List<Map<String, Object>> result = (List<Map<String, Object>>) map.get("result");
            result = result.stream().filter(a -> {
                String identifier = (String) a.get("identifier");
                return "running_status".equals(identifier);
            }).collect(Collectors.toList());
            final Map<String, Object> map1 = result.get(0);
            final String id = (String) map1.get("id");
            resService.postString("https://mis.midea.com/v1/scene/ability/profile/save", String.format("{\"id\":\"%s\",\"prdId\":\"%s\",\"abilityHardwareType\":1,\"abilityType\":0,\"name\":\"洗涤状态\",\"identifier\":\"running_status\",\"displayName\":\"洗涤状态\",\"dataType\":\"enum\",\"ifAdd\":true,\"attributes\":[{\"enumDataType\":\"string\",\"identifier\":\"end\",\"name\":\"完成\",\"displayName\":\"完成\",\"isDefault\":false},{\"enumDataType\":\"string\",\"identifier\":\"work\",\"name\":\"运行中\",\"displayName\":\"运行中\",\"isDefault\":false}],\"rw\":\"r\",\"remark\":\"\",\"ctrlChannelId\":\"COMMON_LUA\",\"msgTypeId\":\"/midea/appliance/status/*\",\"triggerType\":0,\"linkType\":1,\"selectType\":\"single\",\"abilityIfRelations\":[],\"abilityThenRelations\":[]}", id, prdId));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加能力到列表
     */
    @Test
    public void testUP() {
        List<Device> list = devices(Wrappers.<Device>lambdaQuery().eq(Device::getCatCode, "DB").eq(Device::getShowStatus, 2));
        ;
        list.parallelStream().forEach(d -> {
            ParameterizedTypeReference<R<AbilityResult>> responseBodyType = new ParameterizedTypeReference<R<AbilityResult>>() {
            };
            List<AbilityResult.Result> abilits = resService.post("https://mis.midea.com/v1/scene/ability/upAudit/list\n", String.format("{\"pageNo\":1,\"pageSize\":10,\"prdId\":\"%s\",\"nameOrIdentifier\":\"\",\"dataType\":\"\",\"status\":\"\",\"updateStatus\":\"\",\"linkTypeIns\":[1,2,3],\"abilityHardwareType\":1,\"cacheStatus\":0}",
                    d.getId()), responseBodyType).getResult();
            if (CollUtil.isNotEmpty(abilits)) {
                List<Map<String, String>> datas = abilits.stream().map(a -> {
                    Map<String, String> data = Maps.newHashMap();
                    data.put("id", a.getId());
                    data.put("publishStatus", "1");
                    return data;
                }).collect(Collectors.toList());
                resService.postString(" https://mis.midea.com/v1/scene/ability/audit/batch/update", JsonMapper.defaultMapper().toJson(datas));
                //Request URL: https://mis.midea.com/v1/scene/ability/audit/batch/update
            }

        });

    }

    /**
     * 添加能力到列表
     */
    @Test
    public void testUP(String prdId) {
        //List<Device> list = devices(Wrappers.<Device>lambdaQuery().eq(Device::getCatCode,"DB").eq(Device::getShowStatus,2));;
        //list.parallelStream().forEach(d->{
        ParameterizedTypeReference<R<AbilityResult>> responseBodyType = new ParameterizedTypeReference<R<AbilityResult>>() {
        };
        List<AbilityResult.Result> abilits = resService.post("https://mis.midea.com/v1/scene/ability/upAudit/list\n", String.format("{\"pageNo\":1,\"pageSize\":10,\"prdId\":\"%s\",\"nameOrIdentifier\":\"\",\"dataType\":\"\",\"status\":\"\",\"updateStatus\":\"\",\"linkTypeIns\":[1,2,3],\"abilityHardwareType\":1,\"cacheStatus\":0}",
                prdId), responseBodyType).getResult();
        if (CollUtil.isNotEmpty(abilits)) {
            List<Map<String, String>> datas = abilits.stream().map(a -> {
                Map<String, String> data = Maps.newHashMap();
                data.put("id", a.getId());
                data.put("publishStatus", "1");
                return data;
            }).collect(Collectors.toList());
            resService.postString(" https://mis.midea.com/v1/scene/ability/audit/batch/update", JsonMapper.defaultMapper().toJson(datas));
            //Request URL: https://mis.midea.com/v1/scene/ability/audit/batch/update
        }

        //});

    }

    /**
     * 生成测试报告(单个）
     */
    @Test
    public void testExcel() {
        //String prdId="pAFS8OE2gpUU4WZ";
        //String productModel="CLDZ13HD";
        //List<Device> list = devices("mZ2cqQ6VV5So4HY");
        List<Device> list = getDevices();
        list.forEach(d -> {
            try {
                Map<String, Object> map = new HashMap<>();
                map.put("startDate", "2022-07-04");
                map.put("endDate", "2022-07-04");
                map.put("catCode", CatCodeEnum.formCode(d.getCatCode()).getName());

                map.put("sn8", d.getProductModel());
                String json = resService.postString("https://mis.midea.com/v1/scene/ability/audit/list",
                        String.format("{\"pageNo\":1,\"pageSize\":10,\"prdId\":\"%s\",\"abilityHardwareType\":1,\"publishStatus\":1,\"cacheStatus\":0,\"orderField\":3}", d.getId())
                );
                ExcelWriter excelWriter = EasyExcel.write("E:\\IFTTT\\ifttt\\report\\场景测试报告-" + d.getProductModel() + ".xlsx", Map.class)
                        .withTemplate("E:\\IFTTT\\ifttt\\template.xlsx")
                        .build();
                WriteSheet sheet0 = EasyExcel.writerSheet(0).build();
// 填充第一个sheet中的枚举参数
                excelWriter.fill(map, sheet0);
// 填充第二个sheet中的列表
                JsonMapper mapper = JsonMapper.defaultMapper();
                Map<String, Object> result = mapper.fromJson(json, mapper.buildMapType(Map.class, String.class, Object.class));
                Map<String, Object> data = (Map<String, Object>) result.get("data");
                List<Map<String, Object>> abilitys = (List<Map<String, Object>>) data.get("result");

                List<Map<String, Object>> attributes = abilitys.stream().map(a -> {
                    Map<String, Object> attribute = new HashMap<>();
                    attribute.put("lua", "通用Lua");
                    attribute.put("msg", "Lua设备消息");
                    attribute.put("result", "通过");
                    attribute.put("tester", "于志成");


                    String identifier = (String) a.get("identifier");
                    attribute.put("identifier", identifier);
                    Map<String, Object> curSnapshot = (Map<String, Object>) a.get("curSnapshot");
                    Map<String, Object> fullSource = (Map<String, Object>) curSnapshot.get("fullSource");
                    Integer linkType = (Integer) fullSource.get("linkType");
                    if (linkType == 1) {
                        attribute.put("result1", "通过");
                        attribute.put("result2", "不涉及");
                        attribute.put("type", "条件");
                        attribute.put("readType", "只读");
                    }
                    if (linkType == 2) {
                        attribute.put("result2", "通过");
                        attribute.put("result1", "不涉及");
                        attribute.put("type", "动作");
                        attribute.put("readType", "只写");

                    }
                    attribute.put("dataType", "枚举型");


                    attribute.put("oper1", "");
                    attribute.put("oper2", "");
                    attribute.put("oper3", "");

                    List<Map<String, Object>> attributes_sub = (List<Map<String, Object>>) fullSource.get("attributes");
                    attribute.put("name", fullSource.get("name"));

                    if ("remain_time".equals(identifier)) {
                        attribute.put("dataType", "数值型");

                        attribute.put("attribute", "默认值：无；步长：1；数值范围：1 - 100；单位：分钟\n" +
                                "\n");
                    } else {
                        attribute.put("attribute", attributes_sub.stream().map(a1 -> {
                            if (StringUtils.isNotBlank((String) a1.get("identifier"))) {
                                return a1.get("identifier") + "-" + a1.get("name") + "-" + a1.get("name");
                            } else {
                                return "";
                            }
                        }).collect(Collectors.joining("\r")));
                    }
                    return attribute;
                }).collect(Collectors.toList());
                WriteSheet writeSheet1 = EasyExcel.writerSheet(1).build();
                excelWriter.fill(attributes, writeSheet1);
                excelWriter.finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }


    /**
     * 上传测试报告
     */
    @Test
    void uploadTestReport() {
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
        map.add("mf", new FileSystemResource(new File("E:\\IFTTT\\ifttt\\report\\dataType特殊能力\\滚筒\\滚筒洗衣机批量场景能力测试报告0616.xlsx")));

        try {
            String json = resService.postFile("https://mis.midea.com/v1/file/upload", map);

            JsonNode node = mapper.readTree(json);
            String sceneAbilityReportFileId = node.findValue("data").asText();
            System.out.println(sceneAbilityReportFileId);
        } catch (Exception e) {
        }
    }

    /**
     * 上传测试报告并批量提交审核
     */
    @Test
    public void saveTestReport() {
        List<Device> list=getDevices();
        //List<Device> list = devices(Wrappers.<Device>lambdaQuery().eq(Device::getCatCode, "DB").eq(Device::getShowStatus, 2));
        //List<Device> list = devices(Wrappers.<Device>lambdaQuery().eq(Device::getCatCode,"DA").ne(Device::getShowStatus,3).ne(Device::getScenceType,"1").ne(Device::getId,"7lSm2u9XTd6p6hf"));

        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        list.forEach(d -> {
//            MultiValueMap<String, Object> map=new LinkedMultiValueMap<String, Object>();
//            map.add("mf",new FileSystemResource(new File("C:\\Users\\guyl16\\Desktop\\ifttt\\report\\场景测试报告-"+d.getProductModel()+".xlsx")));

            try {
//                String json= resService.postFile("https://mis.midea.com/v1/file/upload",map);
//
//                JsonNode  node = mapper.readTree(json);
//                String sceneAbilityReportFileId = node.findValue("data").asText();
                String result = resService.postString("https://mis.midea.com/v1/prds/release/ext/save", String.format("{\"prdId\":\"%s\",\"testReportFileId\":\"\"," +
                                "\"consumableTestFileId\":\"\",\"metricsReportFileId\":\"\"," +
                                "\"uiWalkAroundReportFileId\":\"\",\"testCaseFileId\":\"\",\"safeTestFileId\":\"\",\"testUsername\":\"sit_8.30.0172,15547702597,123456,2597的家，%s\"" +
                                ",\"expressPrd\":0,\"wotTestReportFileId\":\"\",\"sceneAbilityReportFileId\":\"%s\",\"safeSecretReportFileId\":\"\",\"dataProtectionFileId\":\"\",\"otherFileId\":\"\",\"plugRecordeScreenFileId\":\"\",\"plugRecordeScreenDiskUrl\":\"\"}"
                        , d.getId(), "13129", "85e813a47c614fbd9e1d8a8372fbf40d"));

                JsonNode node1 = mapper.readTree(resService.getString(String.format("https://mis.midea.com/v1/prds/release/latest?prdId=%s", d.getId())));
                String id = node1.findValue("data").findValue("id").asText();
                String s = resService.postString("https://mis.midea.com/v1/prds/release/submit\n",
                        String.format("{\"id\":%s,\"recVer\":2,\"prdId\":\"%s\",\"releaseTime\":\"\",\"n3\":\"liuxq17\",\"n10\":\"luls17\",\"n8\":\"yuzc7\",\"securityOfficer\":\"\",\"legalAffairsOfficer\":\"\",\"colmoEnable\":0,\"pointChange\":\"\",\"uiChange\":\"\",\"meijuApp\":\"\",\"meijuApplet\":\"\",\"ovApp\":\"\",\"mSmartLifeApp\":\"\",\"toshibaHAApp\":\"\",\"uiPersonEnable\":\"\",\"needModifyItems\":\"sceneAbility\",\"testReportFileId\":\"\",\"metricsReportFileId\":\"\",\"uiWalkAroundReportFileId\":\"\",\"testCaseFileId\":\"\",\"safeTestFileId\":\"\"," +
                                        "\"testUsername\":\"测试环境，15547702597，123456，2597的家，%s\"," +
                                        "\"testPassword\":\"测试环境，15547702597，123456，2597的家，%s\",\"expressPrd\":0,\"wotTestReportFileId\":\"\",\"sceneAbilityReportFileId\":\"%s\",\"safeSecretReportFileId\":\"\",\"dataProtectionFileId\":\"\",\"consumableTestFileId\":\"\",\"otherFileId\":\"\",\"plugRecordeScreenFileId\":\"\",\"plugRecordeScreenDiskUrl\":\"\"}"
                                , id, d.getId(), "13129", "13129", "85e813a47c614fbd9e1d8a8372fbf40d"));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

        });
    }

    /**
     * 比对失败
     *
     * @throws Exception
     */
    @Test
    public void compareFlow() throws Exception {
        String json = resService.postString("https://mis.midea.com/v1/iflow/prd/list", "{\"pageNo\":1,\"pageSize\":100,\"fdId\":\"\",\"fdSubject\":\"\",\"ownerGroupId\":\"\",\"creatorName\":\"邬瀚锋\",\"iflowStatus\":0,\"startCreateTime\":\"\",\"endCreateTime\":\"\"}");
        Map<String, Object> data = parseJson(json);
        List<Map<String, Object>> result = (List<Map<String, Object>>) data.get("result");
        List<String> datas = FileUtils.readLines(new File("E:\\IFTTT\\ifttt\\log\\compare.log"), Charset.defaultCharset());
        List<String> datas1 = Lists.newArrayList();
        result.forEach(r -> {
            String prdId = (String) r.get("prdId");
            datas1.add(prdId);
        });
        datas.forEach(d -> {
            if (!datas1.contains(d)) {
                try {
                    FileUtils.writeLines(new File("E:\\IFTTT\\ifttt\\log\\whf\\compare.log"), Lists.newArrayList(d), true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    /**
     * 补全设备信息
     */
    @Test
    public void updateEquipInfo() {
        List<Device> list = devices(Wrappers.<Device>lambdaQuery().eq(Device::getCatCode, "DA").eq(Device::getScenceType, '1'));
        //List<Device> list = devices(Wrappers.<Device>lambdaQuery().eq(Device::getCatCode,"DA").ne(Device::getShowStatus,3).ne(Device::getScenceType,"1").ne(Device::getId,"7lSm2u9XTd6p6hf"));
        list.parallelStream().forEach(d -> {

            String json = resService.getString("https://mis.midea.com/v1/prds/get?prdId=" + d.getId());
            try {
                Map<String, Object> data = parseJson(json);
                String marketModel = (String) data.get("marketModel");
                Integer id = (Integer) data.get("id");
                String productModel = (String) data.get("productModel");
                String name = (String) data.get("name");
                //if (StringUtils.isBlank(productModel) || "null".equals(productModel)) {
                //    productModel = name;
                //}


                if (StringUtils.isBlank(marketModel) || "null".equals(marketModel) || null == marketModel) {
                    //    String param=String.format(
                    //            "{\"catCode\":\"DB\",\"description\":\"\",\"id\":%s,\"accessMethod\":0,\"ownerGroupId\":6,\"dispCat1\":\"D3\",\"dispCat2\":\"D3X1\"," +
                    //                    "\"voiceEquipment\":\"0\",\"brandCode\":\"midea\",\"productModel\":\"%s\",\"marketModel\":\"%s\",\"customerModel\":\"%s\",\"alias\":\"滚筒洗衣机\"}",
                    //            id,productModel,productModel,productModel);
                    FileUtils.writeLines(new File("E:\\IFTTT\\ifttt\\log\\whf\\iftttDA.log"), Lists.newArrayList(d.getId()), true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * 查询特殊代码工单是否处理完毕
     */

    @Test
    public void testShowStatus() {
        List<Device> list = devices(Wrappers.<Device>lambdaQuery().eq(Device::getCatCode, "DB").eq(Device::getScenceType, "1").eq(Device::getShowStatus, 0));
        list.parallelStream().forEach(d -> {
            ParameterizedTypeReference<R<AbilityResult>> responseBodyType = new ParameterizedTypeReference<R<AbilityResult>>() {
            };
            List<AbilityResult.Result> abilits = resService.post("https://mis.midea.com/v1/scene/ability/list", String.format("{\"prdId\":\"%s\",\"abilityHardwareType\":1,\"abilityType\":1,\"nameOrIdentifier\":\"\",\"status\":\"\",\"updateStatus\":\"\",\"codeDevelopStatus\":\"\",\"cacheStatus\":0,\"codeDevelopStatusIns\":[3],\"pageNo\":1,\"pageSize\":10}",
                    d.getId()), responseBodyType).getResult();
            if (CollUtil.isNotEmpty(abilits)) {
                abilits = abilits.stream().filter(a -> {
                    return "running_status".equals(a.getIdentifier());
                }).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(abilits)) {
                    updateShowStatus(d.getId());
                }

                //特殊能力
                // resService.postString("https://mis.midea.com/v1/scene/ability/submitCodeDev",String.format("{sceneIds: [\"%s\"], prdId: \"%s\"}",abilits.get(0).getId(),d.getId()));

            }

        });

    }

    @Test
    public void testSpec() {
        List<Device> list = devices();
        list.parallelStream().forEach(d -> {
            ParameterizedTypeReference<R<AbilityResult>> responseBodyType = new ParameterizedTypeReference<R<AbilityResult>>() {
            };
            List<AbilityResult.Result> abilits = resService.post("https://mis.midea.com/v1/scene/ability/list", String.format("{\"prdId\":\"%s\",\"abilityHardwareType\":1,\"abilityType\":1,\"nameOrIdentifier\":\"\",\"status\":\"\",\"updateStatus\":\"\",\"codeDevelopStatus\":\"\",\"cacheStatus\":0,\"codeDevelopStatusIns\":[3],\"pageNo\":1,\"pageSize\":10}",
                    d.getId()), responseBodyType).getResult();
            if (CollUtil.isNotEmpty(abilits)) {
                abilits = abilits.stream().filter(a -> {
                    return "running_status".equals(a.getIdentifier());
                }).collect(Collectors.toList());
                //if (CollUtil.isNotEmpty(abilits)) {
                    //updateShowStatus(d.getId());
                //}

                 //resService.postString("https://mis.midea.com/v1/scene/ability/submitCodeDev",String.format("{sceneIds: [\"%s\"], prdId: \"%s\"}",abilits.get(0).getId(),d.getId()));

            }

        });

    }

    @Test
    public void testAddProgram() throws Exception {
        String deviceId = "N90myvvX1Ywh36V";
        addProgramProfile(deviceId, "DA", "27953");
        upAbility(deviceId);
        syncSandbox(deviceId);

    }

    @Test
    public void generateExcel() throws Exception {
        List<Device> list = devices("wkQ5qOtYA4LzxlI",
                "K9HG2EZ9sXg87QB",
                "4k04XNZ7KlcWiCP",
                "WqcT7eVyYCj3k32",
                "6blaZRT8KL1iiBT",
                "u15Hjk4Zlr9VYYQ",
                "MZSVQ6JbFvoV7hT",
                "boYFRHNlEOOOQZ4",
                "6VggAcFYcqqyRkE",
                "Sv5VCSRP9AyXGVW",
                "ehezlyAzkTZlRIn",
                "uz06d8OCunQXirw",
                "cxawlBDwMKSLq6B");

        list.forEach(d -> {
            try {
                Map<String, Object> map = new HashMap<>();
                map.put("startDate", "2022-05-21");
                map.put("endDate", "2022-05-21");
                map.put("catCode", CatCodeEnum.formCode(d.getCatCode()).getName());

                map.put("sn8", d.getProductModel());
                syncSandbox(d.getId());
                String json = resService.postString("https://mis.midea.com/v1/scene/ability/audit/list",
                        String.format("{\"pageNo\":1,\"pageSize\":10,\"prdId\":\"%s\",\"abilityHardwareType\":1,\"publishStatus\":1,\"cacheStatus\":0,\"orderField\":3}", d.getId())
                );
                ExcelWriter excelWriter = EasyExcel.write("C:\\Users\\guyl16\\Desktop\\ifttt\\report\\场景测试报告-" + d.getProductModel() + ".xlsx", Map.class)
                        .withTemplate("C:\\Users\\guyl16\\Desktop\\ifttt\\template.xlsx")
                        .build();
                WriteSheet sheet0 = EasyExcel.writerSheet(0).build();
// 填充第一个sheet中的枚举参数
                excelWriter.fill(map, sheet0);
// 填充第二个sheet中的列表
                JsonMapper mapper = JsonMapper.defaultMapper();
                Map<String, Object> result = mapper.fromJson(json, mapper.buildMapType(Map.class, String.class, Object.class));
                Map<String, Object> data = (Map<String, Object>) result.get("data");
                List<Map<String, Object>> abilitys = (List<Map<String, Object>>) data.get("result");

                List<Map<String, Object>> attributes = abilitys.stream().map(a -> {
                    Map<String, Object> attribute = new HashMap<>();
                    attribute.put("identifier", a.get("identifier"));
                    Map<String, Object> curSnapshot = (Map<String, Object>) a.get("curSnapshot");
                    Map<String, Object> fullSource = (Map<String, Object>) curSnapshot.get("fullSource");
                    Integer linkType = (Integer) fullSource.get("linkType");
                    if (linkType == 1) {
                        attribute.put("result1", "通过");
                        attribute.put("result2", "不涉及");
                    }
                    if (linkType == 2) {
                        attribute.put("result2", "通过");
                        attribute.put("result1", "不涉及");
                    }
                    List<Map<String, Object>> attributes_sub = (List<Map<String, Object>>) fullSource.get("attributes");
                    attribute.put("name", fullSource.get("name"));


                    attribute.put("attribute", attributes_sub.stream().map(a1 -> {
                        if (StringUtils.isNotBlank((String) a1.get("identifier"))) {
                            return a1.get("identifier") + "-" + a1.get("name") + "-" + a1.get("name");
                        } else {
                            return "";
                        }
                    }).collect(Collectors.joining("\r")));


                    return attribute;
                }).collect(Collectors.toList());
                WriteSheet writeSheet1 = EasyExcel.writerSheet(1).build();
                excelWriter.fill(attributes, writeSheet1);
                excelWriter.finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


    }


    @Test
    public void temp() {
        List<Device> list = getDevices();
        list.parallelStream().forEach(d -> {
            String json = resService.getString("https://mis.midea.com/v1/prds/release/current/0?prdId=" + d.getId());
            JsonMapper jsonMapper = JsonMapper.defaultMapper();
            Map<String, Object> map = jsonMapper.fromJson(json, jsonMapper.buildMapType(Map.class, String.class, Object.class));
            Map<String, Object> data = (Map<String, Object>) map.get("data");
            Map<String, Object> releaseInfo = (Map<String, Object>) data.get("releaseInfo");
            String fdId = (String) releaseInfo.get("fdId");
            try {
                FileUtils.writeLines(new File("D:\\guyl16\\ifttt.log"), Lists.newArrayList(d.getId() + "                  " + fdId), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void testProcess() {
        String json = resService.postString("https://mis.midea.com/v1/iflow/prd/list", "{\"pageNo\":1,\"pageSize\":100,\"fdId\":\"\",\"fdSubject\":\"\",\"ownerGroupId\":\"\",\"creatorName\":\"顾云龙\",\"iflowStatus\":0,\"startCreateTime\":\"\",\"endCreateTime\":\"\"}");
        JsonMapper jsonMapper = JsonMapper.defaultMapper();
        Map<String, Object> map = jsonMapper.fromJson(json, jsonMapper.buildMapType(Map.class, String.class, Object.class));
        Map<String, Object> data = (Map<String, Object>) map.get("data");

        List<Map<String, Object>> result = (List<Map<String, Object>>) data.get("result");
        result.parallelStream().forEach(r -> {
            try {
                FileUtils.writeLines(new File("D:\\guyl16\\ifttt.log"), Lists.newArrayList(r.get("fdId")), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    @Test
    public void readFile() {
        try {
            MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
            map.add("mf", new FileSystemResource(new File("C:\\Users\\guyl16\\Desktop\\ifttt\\report\\滚筒洗衣机批量场景能力测试报告.xlsx")));
            String json = resService.postFile("https://mis.midea.com/v1/file/upload", map);

            JsonNode node = mapper.readTree(json);
            String sceneAbilityReportFileId = node.findValue("data").asText();
            System.out.println(sceneAbilityReportFileId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 删除能力
     *
     * @param prdId
     */
    public void deleteAbility(String prdId) {

        boolean run = true;
        while (run) {
            List<AbilityResult.Result> abilitys = scenSendService.queryAbilitys(prdId).getResult();
            abilitys = abilitys.stream().filter(a -> a.getStatus() == 0).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(abilitys)) {
                abilitys.forEach(a -> {
                    scenSendService.batchDelete(Lists.newArrayList(a.getId()));
                });

            } else {
                run = false;
            }

        }

    }

    static ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @Test
    public void testFile() throws IOException {
        //String json= "{\"code\":\"00000000000\",\"message\":\"\",\"data\":{\"totalCount\":2,\"result\":[{\"id\":\"6295c0f963aab100018616bb\",\"entpCode\":\"0000\",\"brand\":\"xiaotiane\",\"catCode\":\"DB\",\"pid\":\"28725\",\"prdId\":\"HeSFMHSsutZpFqL\",\"identifier\":\"remain_time\",\"abilityHardwareType\":1,\"abilityHardwareTypeName\":\"PROFILE\",\"abilityType\":0,\"abilityTypeName\":\"标准能力\",\"cacheStatus\":0,\"cacheStatusName\":\"正常保存\",\"status\":0,\"statusName\":\"未发布\",\"updateStatus\":null,\"updateStatusName\":\"\",\"triggerType\":0,\"name\":\"剩余时间\",\"displayName\":\"剩余时间\",\"dataType\":\"number\",\"dataTypeName\":\"数值型\",\"ifAdd\":true,\"attributes\":[{\"min\":1.0,\"max\":100.0,\"unit\":\"分钟\",\"step\":1.0,\"initialValue\":null}],\"rw\":\"r\",\"rwName\":\"只读\",\"remark\":\"\",\"ctrlChannelId\":\"COMMON_LUA\",\"ctrlChannelName\":\"\",\"msgTypeId\":\"/midea/appliance/status/*\",\"msgTypeName\":\"\",\"linkOps\":[\"$lt\",\"$eq\"],\"selectType\":\"multi\",\"selectTypeName\":\"多选\",\"linkType\":1,\"linkTypeName\":\"条件\",\"abilityIfRelations\":[],\"abilityThenRelations\":[],\"lowerAbilityIfRelations\":[],\"lowerAbilityThenRelations\":[],\"publishStatus\":0,\"publishStatusName\":\"\",\"sourceType\":null,\"sourceIdentify\":\"\",\"sourceDesc\":\"\",\"ifHasComment\":false,\"safe\":false,\"idempotent\":false,\"specialAbilityFileIds\":[],\"specialAbilityDesc\":\"\",\"ifOrder\":3,\"thenOrder\":0,\"codeDevelopStatus\":0,\"ifIotInteriorFound\":false},{\"id\":\"6295c0fa63aab100018616bc\",\"entpCode\":\"0000\",\"brand\":\"xiaotiane\",\"catCode\":\"DB\",\"pid\":\"28725\",\"prdId\":\"HeSFMHSsutZpFqL\",\"identifier\":\"error_code\",\"abilityHardwareType\":1,\"abilityHardwareTypeName\":\"PROFILE\",\"abilityType\":0,\"abilityTypeName\":\"标准能力\",\"cacheStatus\":0,\"cacheStatusName\":\"正常保存\",\"status\":0,\"statusName\":\"未发布\",\"updateStatus\":null,\"updateStatusName\":\"\",\"triggerType\":0,\"name\":\"故障\",\"displayName\":\"故障\",\"dataType\":\"enum\",\"dataTypeName\":\"枚举型\",\"ifAdd\":true,\"attributes\":[{\"identifier\":\"16\",\"name\":\"滚筒进水超时\",\"enumDataType\":\"string\",\"displayName\":\"滚筒进水超时\",\"isDefault\":false},{\"identifier\":\"33\",\"name\":\"滚筒排水超时\",\"enumDataType\":\"string\",\"displayName\":\"滚筒排水超时\",\"isDefault\":false},{\"identifier\":\"48\",\"name\":\"门锁没有闭合\",\"enumDataType\":\"string\",\"displayName\":\"门锁没有闭合\",\"isDefault\":false}],\"rw\":\"r\",\"rwName\":\"只读\",\"remark\":\"\",\"ctrlChannelId\":\"COMMON_LUA\",\"ctrlChannelName\":\"\",\"msgTypeId\":\"/midea/appliance/status/*\",\"msgTypeName\":\"\",\"linkOps\":[],\"selectType\":\"single\",\"selectTypeName\":\"单选\",\"linkType\":1,\"linkTypeName\":\"条件\",\"abilityIfRelations\":[],\"abilityThenRelations\":[],\"lowerAbilityIfRelations\":[],\"lowerAbilityThenRelations\":[],\"publishStatus\":0,\"publishStatusName\":\"\",\"sourceType\":null,\"sourceIdentify\":\"\",\"sourceDesc\":\"\",\"ifHasComment\":false,\"safe\":false,\"idempotent\":false,\"specialAbilityFileIds\":[],\"specialAbilityDesc\":\"\",\"ifOrder\":6,\"thenOrder\":0,\"codeDevelopStatus\":0,\"ifIotInteriorFound\":false}]}}";
        String json = FileUtils.readFileToString(new File("E:\\IFTTT\\ifttt\\模板\\test.json"), StandardCharsets.UTF_8);
        JavaType type = mapper.getTypeFactory().constructMapType(LinkedHashMap.class, String.class, Object.class);
        Map<String, Object> map = mapper.readValue(json, type);

        Map<String, Object> data = (Map<String, Object>) map.get("data");

        List<Map<String, Object>> list = (List<Map<String, Object>>) data.get("result");
        List<Map<String, Object>> list1 = Lists.newArrayList();
        for (Map<String, Object> data1 : list) {
            data1.remove("prdId");
            data1.remove("id");

            if (!data1.get("identifier").equals("program")) {
                list1.add(data1);
            }

        }
        FileUtils.writeLines(new File("E:\\IFTTT\\ifttt\\模板\\干衣机4能力.json"), Lists.newArrayList(mapper.writeValueAsString(list1)));

    }

    private Map<String, Object> parseJson(String json) throws Exception {
        JavaType type = mapper.getTypeFactory().constructMapType(LinkedHashMap.class, String.class, Object.class);
        Map<String, Object> map = mapper.readValue(json, type);

        Map<String, Object> data = (Map<String, Object>) map.get("data");
        return data;
    }

    @Test
    public void requestsList() throws Exception {
        String json = resService.getString("https://mis.midea.com/v1/requests/list?pageSize=10&pageNo=1&requestId=&content=&categoryId=&priority=&stage=1&status=&startTime=2022-06-09+00:00:00&endTime=2022-06-09+23:59:59");

        Map<String, Object> data = parseJson(json);
        List<Map<String, Object>> result = (List<Map<String, Object>>) data.get("result");
        for (Map<String, Object> r : result) {
            String requestId = (String) r.get("requestId");

            data = parseJson(resService.getString("https://mis.midea.com/v1/requests/get?requestId=" + requestId));
            List<String> list = (List<String>) data.get("prdIdList");

//Request URL:
            FileUtils.writeLines(new File("D:\\guyl16\\ifttt.log"), Lists.newArrayList(list.get(0))
                    , true);
        }


    }


}
