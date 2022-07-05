package com.iot.sync.test;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.iot.sync.dto.AbilityProfile;
import com.iot.sync.model.Device;
import com.iot.sync.service.impl.ScenSendService;

import java.util.List;

public class JsonTest  {
    public static void main(String[] args) {
        Test test=new Test();
        List<Device> list = test.devices("7b9b5GSCsWyuo1z","pRCVExId865q95H");
        ScenSendService sendService=new ScenSendService();

        list.forEach(d->{
            List<AbilityProfile.Attribute> attributs = sendService.readPrograms("DB", d.getA0());

            ExcelWriter excelWriter = EasyExcel.write("C:\\Users\\guyl16\\Desktop\\ifttt\\program\\"+d.getProductModel()+"程序映射.xlsx")
                    .withTemplate("C:\\Users\\guyl16\\Desktop\\ifttt\\program.xlsx")
                    .build();
            WriteSheet sheet0 = EasyExcel.writerSheet(0).build();
// 填充第一个sheet中的枚举参数
            excelWriter.fill(attributs,sheet0);

            excelWriter.finish();

        });

    }
}
