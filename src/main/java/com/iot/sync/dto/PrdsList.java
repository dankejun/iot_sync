package com.iot.sync.dto;

import lombok.Data;

import java.util.List;

@Data
public class PrdsList {
    private int totalCount;
    private List<PrdResult> result;
}
