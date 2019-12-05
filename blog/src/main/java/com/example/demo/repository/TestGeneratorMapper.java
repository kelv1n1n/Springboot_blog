package com.example.demo.repository;

import com.example.demo.model.TestGenerator;
import com.example.demo.model.TestGeneratorExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TestGeneratorMapper {
    int countByExample(TestGeneratorExample example);

    int deleteByExample(TestGeneratorExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(TestGenerator record);

    int insertSelective(TestGenerator record);

    List<TestGenerator> selectByExample(TestGeneratorExample example);

    TestGenerator selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") TestGenerator record, @Param("example") TestGeneratorExample example);

    int updateByExample(@Param("record") TestGenerator record, @Param("example") TestGeneratorExample example);

    int updateByPrimaryKeySelective(TestGenerator record);

    int updateByPrimaryKey(TestGenerator record);
}