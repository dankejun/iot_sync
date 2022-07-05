package com.iot.sync.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.iot.sync.enums.CatCodeEnum;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("device")
public class Device {
    @TableField(exist = false)
    private String sbu="洗衣机事业部";

    @TableId
    private String id;

    @TableField(exist = false)
    private String catName;

    private String catCode;

    private String productModel;

    @TableField(exist = false)
    private String identifier="A0";

    @TableField(exist = false)
    private String scenceName="无";

    private String scence;

    private String a0;

    private String sn8;

    private String name;


    private String scenceType;

    private Integer publishAbilityCount;
    @TableField(exist = false)
    private String status="已上架";


    private String tags;
    private Integer showStatus;

    private LocalDateTime createTime;

    public String getCatName(){
        CatCodeEnum codeEnum=   CatCodeEnum.formCode(catCode);
        if(codeEnum!=null){
            return codeEnum.getName();
        }else{
            return null;
        }
    }

}
