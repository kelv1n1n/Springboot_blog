package com.example.demo.repository;

import com.example.demo.model.BlogTagRelation;
import com.example.demo.model.BlogTagRelationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface BlogTagRelationMapper {
    int countByExample(BlogTagRelationExample example);

    int deleteByExample(BlogTagRelationExample example);

    int deleteByPrimaryKey(Long relationId);

    int insert(BlogTagRelation record);

    int insertSelective(BlogTagRelation record);

    List<BlogTagRelation> selectByExample(BlogTagRelationExample example);

    BlogTagRelation selectByPrimaryKey(Long relationId);

    int updateByExampleSelective(@Param("record") BlogTagRelation record, @Param("example") BlogTagRelationExample example);

    int updateByExample(@Param("record") BlogTagRelation record, @Param("example") BlogTagRelationExample example);

    int updateByPrimaryKeySelective(BlogTagRelation record);

    int updateByPrimaryKey(BlogTagRelation record);

    //批量插入 文章&标签 表
    int batchInsert(@Param("relationList") List<BlogTagRelation> blogTagRelationList);
}