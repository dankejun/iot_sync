
package com.iot.sync.dto;
import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;

@Data
public class Cycle {


    private List<CycleShop> paramData= Lists.newArrayList();
    private List<CycleShop> cycleShop= Lists.newArrayList();

    @Data
    public static class CycleShop {

        private Integer value;
        private String modeName;

    }


}

