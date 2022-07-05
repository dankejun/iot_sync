package com.iot.sync.test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.sync.service.impl.ResService;
import com.iot.sync.service.impl.ScenSendService;

import java.util.List;
import java.util.stream.Collectors;

public class SpcialTest extends Test {
    private ResService resService = new ResService();
    private ScenSendService sendService = new ScenSendService();

    @org.junit.jupiter.api.Test
    public  void test()throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
      String prdId= "uz06d8OCunQXirw";
      String json="{\"prdId\":\"%s\",\"abilityHardwareType\":1,\"abilityType\":1,\"name\":\"数据类型\",\"identifier\":\"data_type\",\"displayName\":\"数据类型\",\"dataType\":\"enum\",\"ifAdd\":false,\"attributes\":[{\"enumDataType\":\"string\",\"identifier\":\"0404\",\"name\":\"洗衣数据上报\",\"displayName\":\"洗衣数据上报\",\"isDefault\":true}],\"rw\":\"r\",\"remark\":\"\",\"ctrlChannelId\":\"COMMON_LUA\",\"msgTypeId\":\"/midea/appliance/status/*\",\"triggerType\":0,\"linkType\":1,\"selectType\":\"single\",\"abilityIfRelations\":[],\"abilityThenRelations\":[],\"specialAbilityFileIds\":[],\"specialAbilityDesc\":\"将data_type作为隐藏条件，对用户不可见\"}";
      resService.postString("https://mis.midea.com/v1/scene/ability/profile/save",String.format(json,prdId));

        JsonNode node1 = mapper.readTree(resService.postString("https://mis.midea.com/v1/scene/ability/list",String.format("{\"prdId\":\"%s\",\"abilityHardwareType\":1,\"abilityType\":1,\"nameOrIdentifier\":\"\",\"status\":\"\",\"updateStatus\":\"\",\"codeDevelopStatus\":\"\",\"cacheStatus\":0,\"codeDevelopStatusIns\":[0,1,2],\"pageNo\":1,\"pageSize\":10}",prdId)));
       String sceneId=node1.findValue("data").findValues("result").get(0).findValue("id").asText();

        resService.postString("https://mis.midea.com/v1/scene/ability/submitCodeDev",String.format("{sceneIds: [\"%s\"], prdId: \"%s\"}",sceneId,prdId));

    }
    @org.junit.jupiter.api.Test
    public void test1()throws Exception{
       String list=resService.postString("https://mis.midea.com/v1/iflow/prd/list","{\"pageNo\":1,\"pageSize\":100,\"fdId\":\"\",\"fdSubject\":\"\",\"ownerGroupId\":\"\",\"creatorName\":\"顾云龙\",\"iflowStatus\":0,\"startCreateTime\":\"\",\"endCreateTime\":\"\"}");
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        JsonNode root =mapper.readTree(list);
        List<JsonNode> vaules =  root.findValue("data").findValue("result").findValues("prdId");
        List<String> prdIds = vaules.stream().map(JsonNode::asText).collect(Collectors.toList());
        prdIds.forEach(prdId->{
            try {
                JsonNode node = mapper.readTree(resService.getString(String.format("https://mis.midea.com/v1/prds/release/latest?prdId=%s", prdId)));
                String id=node.findValue("id").asText();
                System.out.println(resService.postString("https://mis.midea.com/v1/prds/release/cancel",String.format("{\"id\":%s,\"recVer\":10,\"prdId\":\"%s\"}",id,prdId)));
            } catch (Exception e) {
                e.printStackTrace();
            }
            ;
        });
    }
}
