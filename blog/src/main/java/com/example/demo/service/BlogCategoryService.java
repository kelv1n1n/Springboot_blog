package com.example.demo.service;

import com.example.demo.model.BlogCategory;

import java.util.List;

public interface BlogCategoryService {

    //统计博客分类的总数
    Integer countBlogCategory();

    //获取分类列表
    List<BlogCategory> getAllCategories();

}
