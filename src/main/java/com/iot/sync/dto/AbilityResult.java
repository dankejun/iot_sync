package com.iot.sync.dto;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

@Data
public class AbilityResult {
    private int totalCount;
    private List<Result> result;

    @Data
    public static class  Result{
        private String id;
        private String identifier;
        private String name;
        private int publishStatus;
        private int linkType;
        private int status;
        private List<AbilityProfile.Attribute> attributes= Lists.newArrayList();


    }

    @Data
    public static class Attribute {

        private String enumDataType="string";
        private String identifier;
        private String name;
        private Integer value;

        private String displayName;
        private boolean isDefault=false;

    }
}
