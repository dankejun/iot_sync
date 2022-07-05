package com.iot.sync.dto;

import lombok.Data;

import java.util.List;
@Data
public class SubmitCodeDevDto {
    private List<String> sceneIds;
    private String prdId;
}
