package com.example.demo.repository;

import com.example.demo.model.IndexTest;
import com.example.demo.model.IndexTestExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface IndexTestMapper {
    int countByExample(IndexTestExample example);

    int deleteByExample(IndexTestExample example);

    int deleteByPrimaryKey(Long id);

    int insert(IndexTest record);

    int insertSelective(IndexTest record);

    List<IndexTest> selectByExample(IndexTestExample example);

    IndexTest selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") IndexTest record, @Param("example") IndexTestExample example);

    int updateByExample(@Param("record") IndexTest record, @Param("example") IndexTestExample example);

    int updateByPrimaryKeySelective(IndexTest record);

    int updateByPrimaryKey(IndexTest record);
}