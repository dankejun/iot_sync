package com.iot.sync.test;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.util.List;

public class DcTest {
    public static void main(String[] args) {
        String str="AA23DA00000000000004040101001BFF3F02FF0070100000000002070040DA306D4C0013";
        List<String>list= Lists.newArrayList();
        while (str.length()>2){
            list.add(str.substring(0,2));
            str=str.substring(2);
        }
        System.out.println(Joiner.on(",").join(list));
    }
}
