package com.example.demo;

import com.example.demo.model.TestGenerator;
import com.example.demo.repository.TestGeneratorMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class DemoApplicationTests {

    @Resource
    TestGeneratorMapper testGeneratorMapper;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testSave() throws Exception {
        TestGenerator user = new TestGenerator();
        user.setId(3);
        user.setUsername("admin");
        user.setPassword(123456);
        int i = testGeneratorMapper.insert(user);
        System.out.println(i);
    }

}
