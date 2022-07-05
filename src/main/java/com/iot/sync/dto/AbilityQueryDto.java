package com.iot.sync.dto;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

@Data
public class AbilityQueryDto {
    private String prdId;
    private int abilityHardwareType=1;
    private int abilityType=0;
    private String nameOrIdentifier="";
    private String abilityNameOrIdentifier="";
    private String lowerNameOrIdentifier="";
    private String rw="";
    private String dataType="";
    private String linkType="";
    private String status="";
    private String updateStatus="";
    private String codeDevelopStatus="";
    private List<Integer> codeDevelopStatusIns= Lists.newArrayList(0,1,2);

    private int cacheStatus=0;
    private int pageNo=1;
    private int pageSize=20;
}
