package com.iot.sync.dto;

import lombok.Data;

import java.util.List;


@Data
public class AbilityProfile {

    private String id;
    private String prdId;
    private int abilityHardwareType=1;
    private int abilityType;
    private String name;
    private String identifier;
    private String displayName;
    private String dataType="enum";
    private boolean ifAdd=true;
    private List<Attribute> attributes;
    private String rw;
    private String remark;
    private String ctrlChannelId="COMMON_LUA";
    private String msgTypeId="/midea/appliance/status/*";
    private int triggerType=0;
    private int linkType=2;
    private List<String> abilityIfRelations;
    private List<AbilityThenRelation> abilityThenRelations;
    private List<String> specialAbilityFileIds;
    private String specialAbilityDesc;
    @Data
    public static class Attribute {

        private String enumDataType="string";
        private String identifier;
        private String name;
        private Integer value;

        private String displayName;
        private boolean isDefault=false;

    }
    @Data
    public static class AbilityThenRelation {

        private String identifier;
        private String dataType="enum";
        private String name;
        private boolean notSet=false;
        private List<DependsAttrSet> dependsAttrSet;
        private int order;
    }
    @Data
    public static class DependsAttrSet {
        private String identifier;
        private String name;
        private String displayName;
    }
}



