package com.example.demo.service;

import com.example.demo.model.Blog;

import java.util.List;

public interface BlogsService {

    //查询数据库博客列表显示在首页
    List<Blog> getBlogList();

    //博客详情页
    Blog selectDetail(Integer id);

}
