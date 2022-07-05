package com.iot.sync.model;

import lombok.Data;

@Data
public class R<T> {

    private String code;

    private String msg;

    private T data;

    public R() {

    }


}
