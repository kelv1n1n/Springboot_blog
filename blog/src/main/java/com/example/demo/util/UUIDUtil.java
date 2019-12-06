package com.example.demo.util;

import java.util.UUID;

//生成32位uuid
public class UUIDUtil {

    //生成32位uuid
    public static String getUUID(){
        return UUID.randomUUID().toString().replace("-","");
    }


}
