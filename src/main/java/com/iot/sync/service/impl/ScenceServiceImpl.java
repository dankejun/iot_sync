//package com.iot.sync.service.impl;
//
//import cn.hutool.core.date.DatePattern;
//import cn.hutool.core.date.LocalDateTimeUtil;
//import com.google.common.collect.Lists;
//import com.iot.sync.dto.*;
//import com.iot.sync.model.Device;
//import com.iot.sync.model.R;
//import com.iot.sync.service.DeviceService;
//import com.iot.sync.service.ScenceService;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.ParameterizedTypeReference;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.util.CollectionUtils;
//import org.springframework.web.client.RestTemplate;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Service
//@Slf4j
//public class ScenceServiceImpl implements ScenceService {
//
//    @Autowired
//    private DeviceService deviceService;
//   private static final String PRDS_LIST_URL = "https://mis.midea.com/v1/prds/list?showStatus=1&nameOrModel=&ownerGroupId=6&catCode=%S&accessMethod=&pageNo=%d&pageSize=" + 500 + "&tagId=&brandCode=&controlTerminal=0";
//    private static final  String CONNECTED_PRODUCT_LIST_URL = "https://openadmin.smartmidea.net/v1/scene/connectedProductList";
//
//    @Override
//   public int updateconnectedProduct(String catCode, String cookie,int pageNo){
//        PrdsList prds = connectedProductList(catCode,cookie,pageNo);
//
//        if(prds!=null && !CollectionUtils.isEmpty(prds.getResult())){
//            List<Device> devices= Lists.newArrayList();
//            for (PrdResult result : prds.getResult()) {
//                Device device = new Device();
//                device.setId(result.getPrdId());
//                //状态设置为已添加
//                device.setScence("1");
//                devices.add(device);
//            }
//            deviceService.updateBatchById(devices);
//            return prds.getTotalCount();
//        }
//        return 0;
//
//
//   }
//
//   public void addAbilityProfile(AbilityProfile profile){
//
//   }
//    @Override
//    public Abilitys abilitys(String prdId, String token){
//        return null;
////       HttpHeaders headers = new HttpHeaders();
////        headers.add("token", token);
////
////       String data="{\"prdId\":\"%s\",\"abilityHardwareType\":1,\"abilityType\":0,\"nameOrIdentifier\":\"\",\"abilityNameOrIdentifier\":\"\",\"lowerNameOrIdentifier\":\"\",\"rw\":\"\",\"dataType\":\"\",\"linkType\":\"\",\"status\":\"\",\"updateStatus\":\"\",\"cacheStatus\":0,\"pageNo\":1,\"pageSize\":10}";
////
////       HttpEntity<String> requestEntity = new HttpEntity<>(String.format(data,prdId), headers);
////       ParameterizedTypeReference<R<Abilitys>> responseBodyType = new ParameterizedTypeReference<R<Abilitys>>() {};
////
////       ResponseEntity<R<Abilitys>> resEntity = restTemplate.exchange("https://mis.midea.com/v1/scene/ability/list", HttpMethod.POST, requestEntity, responseBodyType);
////       return resEntity.getBody().getData();
//
//   }
//    private PrdsList connectedProductList(String catCode, String cookie,int pageNo){
//        return null;
////        HttpHeaders headers = new HttpHeaders();
////        headers.add("Cookie", cookie);
////
////        Map<String, Object> map = new HashMap<>();
////        map.put("catCode", catCode);
////        map.put("pageNo", pageNo);
////
////        map.put("pageSize", 500);
////        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(map, headers);
////        ParameterizedTypeReference<R<PrdsList>> responseBodyType = new ParameterizedTypeReference<R<PrdsList>>() {};
////
////        ResponseEntity<R<PrdsList>> resEntity = restTemplate.exchange(CONNECTED_PRODUCT_LIST_URL, HttpMethod.POST, requestEntity, responseBodyType);
////        return resEntity.getBody().getData();
//
//    }
//
//    @Override
//    public int savePrdList(String token,String catCode,int pageNo) {
////        PrdsList prds = prdsList(token,catCode,pageNo);
////
////        if(prds!=null && !CollectionUtils.isEmpty(prds.getResult())){
////            List<Device> devices= Lists.newArrayList();
////            for (PrdResult result : prds.getResult()) {
////                Device device = new Device();
////                device.setId(result.getPrdId());
////                device.setCatCode(result.getCatCode());
////                device.setA0(result.getA0());
////                device.setProductModel(result.getProductModel());
////                device.setShowStatus(result.getShowStatus());
////                device.setSn8(result.getSn8());
////                device.setTags(result.getTags());
////                if(StringUtils.isNotBlank(result.getCreateTime())){
////                    LocalDateTime createTime = LocalDateTimeUtil.parse(result.getCreateTime(), DatePattern.NORM_DATETIME_PATTERN);
////                    device.setCreateTime(createTime);
////                }
////                devices.add(device);
////            }
////            deviceService.saveBatch(devices);
// //           return prds.getTotalCount();
// //       }
//        return 0;
//    }
//
//    private PrdsList prdsList(String token, String catCode, int pageNo){
//        return null;
////        String url=String.format(PRDS_LIST_URL,catCode,pageNo);
////        HttpHeaders headers = new HttpHeaders();
////        headers.add("token", token);
////        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
////        ParameterizedTypeReference<R<PrdsList>> responseBodyType = new ParameterizedTypeReference<R<PrdsList>>() {};
////
////        ResponseEntity<R<PrdsList>> resEntity = restTemplate.exchange(url.toString(), HttpMethod.GET, requestEntity, responseBodyType);
////        return resEntity.getBody().getData();
//    }
//    @Override
//    public int abilityCheck(String prdId, String token){
//        return 0;
////        String url=String.format("https://mis.midea.com/v1/scene/ability/audit/check?prdId=%s&abilityHardwareType=1",prdId);
////        HttpHeaders headers = new HttpHeaders();
////        headers.add("token", token);
////        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
////        ParameterizedTypeReference<R<AbilityCheck>> responseBodyType = new ParameterizedTypeReference<R<AbilityCheck>>() {};
////
////        ResponseEntity<R<AbilityCheck>> resEntity = restTemplate.exchange(url.toString(), HttpMethod.GET, requestEntity, responseBodyType);
////        return resEntity.getBody().getData().getUpSceneSize();
//
//    }
//    @Override
//    public void abilityUpdate(List<String> ids, String token){
////        String url="https://mis.midea.com/v1/scene/ability/audit/batch/update";
////        HttpHeaders headers = new HttpHeaders();
////        headers.add("token", token);
////        List<Map<String,Object>> list=new ArrayList<>();
////        for (String id : ids) {
////            Map<String,Object> data=new HashMap<>();
////            data.put("id",id);
////            data.put("publishStatus","1");
////            list.add(data);
////        }
////
////        HttpEntity< List<Map<String,Object>>> requestEntity = new HttpEntity<>(list, headers);
////
////        ResponseEntity<String> resEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
////        log.info("{}",resEntity.getBody());
//    }
//    @Override
//    public void updatePrograme(String json, String token){
////        String url="https://mis.midea.com/v1/scene/ability/profile/save";
////        HttpHeaders headers = new HttpHeaders();
////        headers.add("token", token);
////        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
////        headers.setContentType(type);
////
////        HttpEntity<String> requestEntity = new HttpEntity<>(json, headers);
////
////        ResponseEntity<String> resEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
////        log.info("{}",resEntity.getBody());
//    }
//    @Override
//    public void sandbox(String prdId, String token){
////        String url=String.format("https://mis.midea.com/v1/scene/ability/sandbox/sync?prdId=%s",prdId);
////        HttpHeaders headers = new HttpHeaders();
////        headers.add("token", token);
////        HttpEntity<String> requestEntity = new HttpEntity<>(null, headers);
////
////        ResponseEntity<String> resEntity = restTemplate.exchange(url.trim(), HttpMethod.GET, requestEntity, String.class);
////        log.info("{}",resEntity.getBody());
//
//    }
//
//
//}
