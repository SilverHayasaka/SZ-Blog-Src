package com.Silver.service;

import com.Silver.domain.ResponseResult;
import com.Silver.domain.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;


/**
 * 评论表(Comment)表服务接口
 *
 * @author makejava
 * @since 2023-03-11 09:50:23
 */
public interface CommentService extends IService<Comment> {

    ResponseResult commentList(String commentType, Long articleId, Integer pageNum, Integer pageSize);

    ResponseResult addComment(Comment comment);
}

