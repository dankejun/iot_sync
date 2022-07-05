package com.iot.sync.dto;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class PrdResult {
    private String prdId;
    private String catCode;
    private String a0;
    private String productModel;
    private Integer showStatus;
    private String sn8;
    private List<TagInfo> tagInfoList;
    private String createTime;
    private String name;
    private Integer publishAbilityCount;

    public  String getTags(){
        if (!CollectionUtils.isEmpty(tagInfoList)) {
            return tagInfoList.stream().map(tag -> tag.name).collect(Collectors.joining(","));
        }
        return null;
    }
    @Data
    private static class  TagInfo{
        private String name;
    }

}
