package com.example.demo.serviceimpl;

import com.aliyuncs.exceptions.ClientException;
import com.example.demo.config.AliyunConfig;
import com.example.demo.model.BlogUser;
import com.example.demo.model.BlogUserExample;
import com.example.demo.repository.BlogUserMapper;
import com.example.demo.service.BlogUserService;
import com.example.demo.util.MD5Util;
import com.example.demo.util.RedisUtil;
import com.example.demo.util.UUIDUtil;
import com.example.demo.vo.PersonalResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BlogUserServiceImpl implements BlogUserService {

    @Resource
    BlogUserMapper blogUserMapper;

    @Autowired
    RedisUtil redisUtil;

    /**
     * 登录
     * @param user 用户信息
     * @return
     */
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


    /**
     * 注册
     * @param user  用户信息
     * @return
     */
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


    /*短信验证码发送*/
    @Override
    public Map<String, Object> SmsVerification(String phone) {
        Map<String, Object> map = new HashMap<>();
        try {
            AliyunConfig.sendSms(phone);
            map.put("code", 200);
            map.put("msg", "短信验证发送成功");
            return map;
        } catch (ClientException e) {
            map.put("code", 300);
            map.put("msg", e.getMessage());
            return map;
        }
    }


    /*手机短信验证修改密码*/
    @Override
    public PersonalResult reSetPassword(BlogUser user, String code) {

        BlogUserExample example = new BlogUserExample();
        BlogUserExample.Criteria criteria = example.createCriteria();
        criteria.andPhoneEqualTo(user.getPhone());
        List<BlogUser> blogUserList = blogUserMapper.selectByExample(example);

        if (blogUserList.size() == 0){
            return PersonalResult.build(301,"该手机号尚未注册");
        }

        //校验验证码 从缓存中获取验证码并进行比对
        String phoneCode = String.valueOf(redisUtil.get("code_"));
        if (!code.equals(phoneCode)){
            return  PersonalResult.build(302,"短信验证码输入错误");
        }

        //修改密码
        criteria.andPhoneEqualTo(user.getPhone());
        user.setPassword(MD5Util.MD5(user.getPassword()));
        int i = blogUserMapper.updateByExampleSelective(user,example);

        //修改成功返回200状态码
        if (i > 0){
            return PersonalResult.build(200,"密码修改成功");
        }else {
            return PersonalResult.build(303,"密码修改失败");
        }
    }

    /*判单是否是管理员*/
    @Override
    public boolean isAdmin(String username, String password) {
        BlogUserExample example = new BlogUserExample();
        BlogUserExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(username).andStatusEqualTo("1");
        List<BlogUser> userList = blogUserMapper.selectByExample(example);
        if (userList.size() == 0){
            return false;
        }
        return true;
    }
}
