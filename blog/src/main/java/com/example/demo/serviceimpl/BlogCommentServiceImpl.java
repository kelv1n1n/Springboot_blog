package com.example.demo.serviceimpl;

import com.example.demo.model.BlogCommentExample;
import com.example.demo.repository.BlogCommentMapper;
import com.example.demo.service.BlogCommentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class BlogCommentServiceImpl implements BlogCommentService {

    @Resource
    BlogCommentMapper blogCommentMapper;

    /**
     * 统计评论数
     * @return
     */
    @Override
    public Integer countComment() {
        BlogCommentExample example = new BlogCommentExample();
        return blogCommentMapper.countByExample(example);
    }
}
