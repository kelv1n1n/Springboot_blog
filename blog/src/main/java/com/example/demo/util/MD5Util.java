package com.example.demo.util;

import org.springframework.util.DigestUtils;

public  class MD5Util {

    //将字符串加密
    public static String MD5(String str){
        return DigestUtils.md5DigestAsHex(str.getBytes());

    }
}
