package com.example.demo.service;

import com.example.demo.model.Blog;
import com.example.demo.util.PageQueryUtil;
import com.example.demo.util.PageResult;
import com.example.demo.vo.PersonalResult;

import java.util.List;

public interface BlogsService {

    //查询数据库博客列表显示在首页
    List<Blog> getBlogList();

    //博客详情页
    Blog selectDetail(Long id);

    //统计文章总数
    Integer countBlog();

    //保存博客信息
    PersonalResult saveBlog(Blog blog);

    //博客文章管理查询博客列表
    PageResult getAllBlog(PageQueryUtil pageQueryUtil);

    //根据文章id查询信息
    Blog selectOneById(Long blogId);

}
