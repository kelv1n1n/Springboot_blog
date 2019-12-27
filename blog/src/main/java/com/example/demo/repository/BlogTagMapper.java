package com.example.demo.repository;

import com.example.demo.model.BlogTag;
import com.example.demo.model.BlogTagExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface BlogTagMapper {
    int countByExample(BlogTagExample example);

    int deleteByExample(BlogTagExample example);

    int deleteByPrimaryKey(Integer tagId);

    int insert(BlogTag record);

    int insertSelective(BlogTag record);

    List<BlogTag> selectByExample(BlogTagExample example);

    BlogTag selectByPrimaryKey(Integer tagId);

    int updateByExampleSelective(@Param("record") BlogTag record, @Param("example") BlogTagExample example);

    int updateByExample(@Param("record") BlogTag record, @Param("example") BlogTagExample example);

    int updateByPrimaryKeySelective(BlogTag record);

    int updateByPrimaryKey(BlogTag record);

    //批量插入新的标签
    int batchInsertBlogTag(List<BlogTag> tagList);
}