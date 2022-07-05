package com.iot.sync.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.iot.sync.dto.*;
import com.iot.sync.model.R;
import com.iot.sync.util.JsonMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("all")
public class ScenSendService {
    @Autowired
    private ResService resService = new ResService();

    public void updateDevice(String prdId){
        String url="https://mis.midea.com/v1/prds/get?prdId=%s";
        JSONObject result = JSONUtil.parseObj(resService.getString(String.format(url,prdId))).getJSONObject("data");;
        JSONArray array = result.getJSONArray("tagInfoList");
        List<Integer> tagIds = array.stream().map(json -> {
            JSONObject jsonObject = (JSONObject) json;
            return (int) jsonObject.get("id");
        }).collect(Collectors.toList());
        if(!tagIds.contains(354)){
            tagIds.add(354);
        }
        //String url="https://mis.midea.com/v1/prds/update";
        Map<String,Object> data = new HashMap<>();
        data.put("id",result.get("id"));
        data.put("tagIds",tagIds);

        resService.postString("https://mis.midea.com/v1/prds/update", JSONUtil.toJsonStr(data));

    }
    public List<PrdResult> connectedProductList() {
        Map<String, Object> map = new HashMap<>();
        map.put("pageNo", 1);
        map.put("pageSize", 1000);
        map.put("groupName", "洗衣机事业部");
        ParameterizedTypeReference<R<PrdsList>> responseBodyType = new ParameterizedTypeReference<R<PrdsList>>() {
        };


        return resService.postCookie("https://openadmin.smartmidea.net/v1/scene/connectedProductList", JSONUtil.toJsonStr(map), responseBodyType).getResult();
    }

    public List<PrdResult> deviceList() {

        String url = "https://mis.midea.com/v1/prds/list?showStatus=1&nameOrModel=&catCode=&accessMethod=&pageNo=1&pageSize=1000&tagId=&brandCode=&controlTerminal=0";
        ParameterizedTypeReference<R<PrdsList>> responseBodyType = new ParameterizedTypeReference<R<PrdsList>>() {
        };

        return resService.get(url, responseBodyType).getResult();

    }

    public AbilityResult queryAbilitys(String prdId) {
        AbilityQueryDto queryDto = new AbilityQueryDto();
        queryDto.setPrdId(prdId);
        ParameterizedTypeReference<R<AbilityResult>> responseBodyType = new ParameterizedTypeReference<R<AbilityResult>>() {
        };
        return resService.post("https://mis.midea.com/v1/scene/ability/list", JSONUtil.toJsonStr(queryDto), responseBodyType);

    }

    public void updateIfAdd(String prdId)throws Exception {
        AbilityQueryDto queryDto = new AbilityQueryDto();
        queryDto.setPrdId(prdId);





    }

    public String saveAbilityProfile(String json) {
        return resService.postString("https://mis.midea.com/v1/scene/ability/profile/save", json);

    }
    public String saveAbilityProfile(String prdId, String abilityId, String name, String identifier, String attributesStr, List<AbilityProfile.AbilityThenRelation> relations) {
        AbilityProfile profile = new AbilityProfile();
        profile.setPrdId(prdId);
        profile.setName(name);
        profile.setDisplayName(name);
        profile.setIdentifier(identifier);
        profile.setAbilityType(0);
        profile.setLinkType(1);
        profile.setRw("r");
        if (StringUtils.isNotBlank(abilityId)) {
            profile.setId(abilityId);
        }

        List<AbilityProfile.Attribute> attributes = Lists.newArrayList();
        for (String s : attributesStr.split(",")) {
            AbilityProfile.Attribute attribute = new AbilityProfile.Attribute();
            String[] array = s.split("-");
            attribute.setIdentifier(array[0]);
            attribute.setDisplayName(array[1]);
            attribute.setName(array[1]);
            attributes.add(attribute);
        }
        profile.setAttributes(attributes);
        if (!CollectionUtils.isEmpty(relations)) {
            profile.setAbilityThenRelations(relations);
        }
        return  saveAbilityProfile(JSONUtil.toJsonStr(profile));
    }

   static Map<String,String>da=new HashMap<>();
    static Map<String,Map<String,String>>cycleMap=new HashMap<>();
    static Map<String,String>dc=new HashMap<>();
    static Map<String,String>db=new HashMap<>();

    static {
        da.put("00","standard");
        da.put("01","fast");
        da.put("02","blanket");
        da.put("03","wool");
        da.put("04","embathe");
        da.put("05","memory");
        da.put("06","child");
        da.put("07","strong_wash");
        da.put("08","down_jacket");
        da.put("09","stir");
        da.put("0A","mute");
        da.put("0B","bucket_self_clean");
        da.put("0C","air_dry");
        da.put("0D","cycle");
        da.put("10","remain_water");
        da.put("11","summer");
        da.put("12","big");
        da.put("13","home");
        da.put("14","cowboy");
        da.put("15","soft");
        da.put("16","hand_wash");
        da.put("17","water_flow");
        da.put("18","fog");
        da.put("19","bucket_dry");
        da.put("1A","fast_clean_wash");
        da.put("1B","dehydration");
        da.put("1C","under_wear");
        da.put("1D","rinse_dehydration");
        da.put("1E","five_clean");
        da.put("1F","degerm");
        da.put("20","in_15");
        da.put("21","in_25");
        da.put("22","love_baby");
        da.put("23","outdoor");
        da.put("24","silk");
        da.put("25","shirt");
        da.put("26","cook_wash");
        da.put("27","towel");
        da.put("28","memory_2");
        da.put("29","memory_3");
        da.put("2A","half_energy");
        da.put("2B","all_energy");
        da.put("2C","soft_wash");
        da.put("2D","prevent_allergy");
        da.put("2E","wash_cube");
        da.put("2F","winter_jacket");
        da.put("30","leisure_wash");
        da.put("31","no_iron");
        da.put("32","remove_mite_wash");
        da.put("33","stubborn_stain");
        da.put("34","silk_wash");
        da.put("35","sterilize_wash");
        da.put("36","cloud_wash");
        da.put("37","smart");

        dc.put("00","cotton");
        dc.put("01","fiber");
        dc.put("02","mixed_wash");
        dc.put("03","jean");
        dc.put("04","bedsheet");
        dc.put("05","outdoor");
        dc.put("06","down_jacket");
        dc.put("07","plush");
        dc.put("08","wool");
        dc.put("09","dehumidify");
        dc.put("0A","cold_air_fresh_air");
        dc.put("0B","hot_air_dry");
        dc.put("0C","sport_clothes");
        dc.put("0D","underwear");
        dc.put("0E","baby_clothes");
        dc.put("0F","shirt");
        dc.put("10","standard");
        dc.put("11","quick_dry");
        dc.put("12","fresh_air");
        dc.put("13","low_temp_dry");
        dc.put("14","eco_dry");
        dc.put("15","quick_dry_30");
        dc.put("16","towel");
        dc.put("17","intelligent_dry");
        dc.put("18","steam_care");
        dc.put("19","big");
        dc.put("1A","fixed_time_dry");
        dc.put("1B","night_dry");
        dc.put("1C","bracket_dry");
        dc.put("1D","western_trouser");
        dc.put("1E","dehumidification");
        dc.put("1F","smart_dry");
        dc.put("20","four_piece_suit");
        dc.put("21","warm_clothes");
        dc.put("22","quick_dry_20");
        dc.put("23","steam_sterilize");
        dc.put("24","enzyme");
        dc.put("25","big_60");
        dc.put("26","steam_no_iron");
        dc.put("27","air_wash");
        dc.put("28","bed_clothes");
        dc.put("29","little_fast_dry");
        dc.put("2A","small_piece_dry");
        dc.put("2B","big_dry");
        dc.put("2C","wool_nurse");
        dc.put("2D","sun_quilt");
        dc.put("2E","fresh_remove_smell");
        dc.put("2F","bucket_self_clean");
        dc.put("30","silk");
        dc.put("31","sterilize");
        dc.put("32","heavy_duty");
        dc.put("33","towel_warmer");
        dc.put("34","air_fluff");
        dc.put("35","delicates");
        dc.put("36","time_drying_30");
        dc.put("37","time_drying_60");
        dc.put("38","time_drying_90");
        dc.put("39","dry_softnurse");
        dc.put("40","uniforms");
        dc.put("41","remove_electricity");


        db.put("00", "cotton");
        db.put("01", "eco");
        db.put("02", "fast_wash");
        db.put("03", "mixed_wash");
        db.put("05", "wool");
        db.put("07", "ssp");
        db.put("08", "sport_clothes");
        db.put("09", "single_dehytration");
        db.put("0A", "rinsing_dehydration");
        db.put("0B", "big");
        db.put("0C", "baby_clothes");
        db.put("0F", "down_jacket");
        db.put("10", "color");
        db.put("11", "intelligent");
        db.put("12", "quick_wash");
        db.put("1C", "shirt");
        db.put("04", "fiber");
        db.put("06", "enzyme");
        db.put("0D", "underwear");
        db.put("0E", "outdoor");
        db.put("15", "air_wash");
        db.put("16", "single_drying");
        db.put("1D", "steep");
        db.put("14 ", "water_cotton");
        db.put("17", "fast_wash_30");
        db.put("18", "fast_wash_60");
        db.put("1F", "water_mixed_wash");
        db.put("20", "water_fiber");
        db.put("21", "water_kids");
        db.put("22", "water_underwear");
        db.put("23", "specialist");
        db.put("FE", "love");
        db.put("19", "water_intelligent");
        db.put("1A", "water_steep");
        db.put("1B", "water_fast_wash_30");
        db.put("1E", "new_water_cotton");
        db.put("24", "water_eco");
        db.put("25", "wash_drying_60");
        db.put("26", "self_wash_5");
        db.put("27", "fast_wash_min");
        db.put("28", "mixed_wash_min");
        db.put("29", "dehydration_min");
        db.put("2A", "self_wash_min");
        db.put("2B", "baby_clothes_min");
        db.put("50", "diy0");
        db.put("51", "diy1");
        db.put("52", "diy2");
        db.put("65", "silk_wash");
        db.put("2C", "prevent_allergy");
        db.put("2D", "cold_wash");
        db.put("2E", "soft_wash");
        db.put("2F", "remove_mite_wash");
        db.put("30", "water_intense_wash");
        db.put("31", "fast_dry");
        db.put("32", "water_outdoor");
        db.put("33", "spring_autumn_wash");
        db.put("34", "summer_wash");
        db.put("35", "winter_wash");
        db.put("36", "jean");
        db.put("37", "new_clothes_wash");
        db.put("38", "silk");
        db.put("39", "insight_wash");
        db.put("3A", "fitness_clothes");
        db.put("3B", "mink");
        db.put("3C", "fresh_air");
        db.put("3D", "bucket_dry");
        db.put("3E", "jacket");
        db.put("3F", "bath_towel");
        db.put("40", "night_fresh_wash");
        db.put("60", "heart_wash");
        db.put("61", "water_cold_wash");
        db.put("62", "water_prevent_allergy");
        db.put("63", "water_remove_mite_wash");
        db.put("64", "water_ssp");
        db.put("66", "standard");
        db.put("67", "green_wool");
        db.put("68", "cook_wash");
        db.put("69", "fresh_remove_wrinkle");
        db.put("6A", "steam_sterilize_wash");
        db.put("6B", "aromatherapy");
        db.put("70", "sterilize_wash");
        db.put("83", "white_clothes_clean");
        db.put("84", "clean_stains");
        db.put("85", "tube_clean_all");
        db.put("86", "no_channeling_color");
        db.put("87", "scald_wash");
        db.put("88", "hanfu_spring_summer");
        db.put("89", "hanfu_autumn_winter");
        db.put("8B", "skin_care_wash");
        db.put("8D", "hanfu_wash");


        cycleMap.put("dc",dc);

        cycleMap.put("da",da);
        cycleMap.put("db",db);

    }

    private String translateCyclyeValue(String catCode,int code){
        String key=StringUtils.leftPad(Integer.toHexString(code),2,"0");
        String value= cycleMap.get(catCode.toLowerCase()).get(key.toUpperCase());
        return StringUtils.defaultIfBlank(value,String.valueOf(code));
    }

    public String saveProgramProfile(String prdId, String catCode, String a0) {
        AbilityProfile profile = new AbilityProfile();
        profile.setPrdId(prdId);
        profile.setName("选择程序");
        profile.setDisplayName("选择程序");
        profile.setIdentifier("program");
        profile.setAbilityType(0);

        List<AbilityProfile.Attribute> attributes = Lists.newArrayList();
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
                                         @Override
                                         // Ignore 400
                                         public void handleError(ClientHttpResponse response) throws IOException {
                                         }
                                     }
        );
        String url = "https://wmspecs.oss-cn-hangzhou.aliyuncs.com/midea_json_new/%s/0000.%s.%s.json";
        ResponseEntity<String> resp = restTemplate.getForEntity(String.format(url, catCode, catCode.toLowerCase(), a0), String.class);
        if(!resp.getStatusCode().isError()){
            Cycle cycle = JsonMapper.defaultMapper().fromJson(resp.getBody(), Cycle.class);
            if (cycle != null) {
                cycle.getParamData().forEach(data -> {
                    AbilityProfile.Attribute attribute = new AbilityProfile.Attribute();
                    attribute.setIdentifier(translateCyclyeValue(catCode,data.getValue()));
                    attribute.setDisplayName(data.getModeName());
                    attribute.setName(data.getModeName());
                    attribute.setEnumDataType("string");
                    attributes.add(attribute);
                });

                profile.setAttributes(attributes);
                profile.setRw("rw");
                AbilityProfile.AbilityThenRelation relation = new AbilityProfile.AbilityThenRelation();
                relation.setOrder(1);
                relation.setIdentifier("power");
                relation.setName("电源");
                AbilityProfile.DependsAttrSet d = new AbilityProfile.DependsAttrSet();
                d.setIdentifier("on");
                d.setDisplayName("开机");
                d.setName("开机");

                relation.setDependsAttrSet(Lists.newArrayList(d));
                List<AbilityProfile.AbilityThenRelation> relations = Lists.newArrayList(relation);
                profile.setAbilityThenRelations(relations);

                return resService.postString("https://mis.midea.com/v1/scene/ability/profile/save", JSONUtil.toJsonStr(profile));
            }
        }
        
        return catCode+","+a0+"not config";


    }
    public   List<AbilityProfile.Attribute> readPrograms(String catCode,String a0){
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
                                         @Override
                                         // Ignore 400
                                         public void handleError(ClientHttpResponse response) throws IOException {
                                         }
                                     }
        );
        List<AbilityProfile.Attribute> attributes = Lists.newArrayList();

        String url = "https://wmspecs.oss-cn-hangzhou.aliyuncs.com/midea_json_new/%s/0000.%s.%s.json";
        ResponseEntity<String> resp = restTemplate.getForEntity(String.format(url, catCode, catCode.toLowerCase(), a0), String.class);
        if(!resp.getStatusCode().isError()){
            Cycle cycle = JsonMapper.defaultMapper().fromJson(resp.getBody(), Cycle.class);
            if (cycle != null) {
                cycle.getParamData().forEach(data -> {
                    AbilityProfile.Attribute attribute = new AbilityProfile.Attribute();
                    attribute.setIdentifier(translateCyclyeValue(catCode,data.getValue()));
                    attribute.setDisplayName(data.getModeName());
                    attribute.setName(data.getModeName());
                    attribute.setValue(data.getValue());

                    attribute.setEnumDataType("string");
                    attributes.add(attribute);
                });

            }
        }
        return attributes;
    }

    public String upAbility(List<String> ids) {
        List<Map<String, Object>> list = Lists.newArrayList();
        for (String id : ids) {
            Map<String, Object> map = Maps.newHashMap();
            map.put("id", id);
            map.put("publishStatus", 1);
            list.add(map);

        }
        return resService.postString(" https://mis.midea.com/v1/scene/ability/audit/batch/update", JSONUtil.toJsonStr(list));

    }

    public String syncSandbox(String prdId) {
        String url = String.format("https://mis.midea.com/v1/scene/ability/sandbox/sync?prdId=%s", prdId);
        return resService.getString(url);
    }

    public String submitCodeDev(String prdId, List<String> sceneIds) {
        String url = "https://mis.midea.com/v1/scene/ability/submitCodeDev";
        SubmitCodeDevDto submitCodeDevDto = new SubmitCodeDevDto();
        submitCodeDevDto.setPrdId(prdId);
        submitCodeDevDto.setSceneIds(sceneIds);
        return resService.postString(url, JSONUtil.toJsonStr(submitCodeDevDto));
    }

    public String batchDelete(List<String> ids) {
        String url = "https://mis.midea.com/v1/scene/ability/batchDelete";
        return resService.postString(url, JSONUtil.toJsonStr(ids));

    }


}
