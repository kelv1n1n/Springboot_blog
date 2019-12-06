package com.example.demo.service;

import com.example.demo.model.BlogUser;
import com.example.demo.vo.PersonalResult;

public interface BlogUserService {
    /*登录*/
    PersonalResult login(BlogUser user);

    /*注册*/
    PersonalResult register(BlogUser user);
}
