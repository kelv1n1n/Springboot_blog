package com.example.demo.service;

import com.example.demo.model.BlogUser;
import com.example.demo.vo.PersonalResult;

import java.util.Map;

public interface BlogUserService {
    /*登录*/
    PersonalResult login(BlogUser user);

    /*注册*/
    PersonalResult register(BlogUser user);

    /*短信测试*/
    Map<String, Object> SmsVerification(String phone);

    /*修改密码*/
    PersonalResult reSetPassword(BlogUser user, String code);

    /*是否是管理员*/
    boolean isAdmin(String username, String password);
}
