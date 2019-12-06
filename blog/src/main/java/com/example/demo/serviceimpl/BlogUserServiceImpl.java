package com.example.demo.serviceimpl;

import com.example.demo.model.BlogUser;
import com.example.demo.model.BlogUserExample;
import com.example.demo.repository.BlogUserMapper;
import com.example.demo.service.BlogUserService;
import com.example.demo.util.MD5Util;
import com.example.demo.util.UUIDUtil;
import com.example.demo.vo.PersonalResult;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class BlogUserServiceImpl implements BlogUserService {

    @Resource
    BlogUserMapper blogUserMapper;

    @Override
    public PersonalResult login(BlogUser user) {
        BlogUserExample example = new BlogUserExample();
        BlogUserExample.Criteria criteria = example.createCriteria();
        //判断数据库中是否有该用户
        criteria.andUsernameEqualTo(user.getUsername());
        //密码加密比对
        criteria.andPasswordEqualTo(MD5Util.MD5(user.getPassword()));

        List<BlogUser> userList = blogUserMapper.selectByExample(example);
        if(userList.size() == 0){
            return PersonalResult.build(300,"用户名或密码错误!");
        }
        BlogUser loginUser = userList.get(0);
        loginUser.setPassword("");
        return PersonalResult.build(200,"登录成功!",loginUser);
    }

    @Override
    public PersonalResult register(BlogUser user) {
        String username = user.getUsername();
        String password = user.getPassword();
        String phone = user.getPhone();

        BlogUserExample blogUserExample = new BlogUserExample();
        BlogUserExample.Criteria criteria = blogUserExample.createCriteria();
        criteria.andUsernameEqualTo(username);

        List<BlogUser> blogUsers = blogUserMapper.selectByExample(blogUserExample);
        if (blogUsers.size() > 0){
            return PersonalResult.build(300,"用户名已存在~");
        }else {
            //注册用户
            //密码加密
            user.setPassword(MD5Util.MD5(password));
            //uuid生成
            user.setId(UUIDUtil.getUUID());
            int i = blogUserMapper.insert(user);
            if (i > 0){
                return PersonalResult.build(200,"注册成功");
            }else {
                return PersonalResult.build(300,"注册失败");
            }
        }
    }
}
