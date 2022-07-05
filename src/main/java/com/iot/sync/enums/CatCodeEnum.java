package com.iot.sync.enums;

public enum CatCodeEnum {
    DA("DA","波轮洗衣机"),
    DB("DB","滚筒洗衣机"),
    D9("D9","复式洗衣机"),

    DC("DC","干衣机"),
    D46("46","衣物护理柜"),

    D47("47","鞋盒"),
            ;
    private String code;
    private String name;


    CatCodeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public  static CatCodeEnum formCode(String code){
        for (CatCodeEnum codeEnum : CatCodeEnum.values()) {
            if(codeEnum.code.equals(code)){
                return codeEnum;
            }
        }
        return null;
    }
}
