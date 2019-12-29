package com.example.demo.service;

import com.example.demo.model.BlogTag;

import java.util.List;

public interface BlogTagService {

    //统计标签的数目
    Integer countTag();

    //获取标签的名字
    List<BlogTag> selectTag();
}
