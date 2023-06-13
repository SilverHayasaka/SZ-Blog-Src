package com.Silver.service.impl;

import com.Silver.constants.SystemConstants;
import com.Silver.domain.ResponseResult;
import com.Silver.domain.entity.Comment;
import com.Silver.domain.vo.CommentVo;
import com.Silver.domain.vo.PageVo;
import com.Silver.enums.AppHttpCodeEnum;
import com.Silver.exception.SystemException;
import com.Silver.mapper.CommentMapper;
import com.Silver.service.CommentService;
import com.Silver.service.UserService;
import com.Silver.utils.BeanCopyUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 评论表(Comment)表服务实现类
 *
 * @author makejava
 * @since 2023-03-11 09:50:23
 */
@Service("commentService")
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private UserService userService;

    @Override
    public ResponseResult commentList(String commentType, Long articleId, Integer pageNum, Integer pageSize) {
        //查询对应文章的根评论
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        //对articleId进行判断
        queryWrapper.eq(SystemConstants.ARTICLE_COMMENT.equals(commentType), Comment::getArticleId, articleId);
        //根评论 rootId为-1
        queryWrapper.eq(Comment::getToCommentId, SystemConstants.ROOT_COMMENT);

        //评论类型
        queryWrapper.eq(Comment::getType, commentType);

        //分页查询
        Page<Comment> page = new Page<>(pageNum, pageSize);
        page(page, queryWrapper);

        List<CommentVo> commentVoList = toCommentVoList(page.getRecords());

        //查询所有根评论对应的子评论
        commentVoList.stream()
                .map(commentVo -> commentVo.setAvatarUrl(userService.getAvatarUrl(commentVo.getCreateBy())))
                .map(commentVo -> commentVo.setChildren(getChildren(commentVo.getId())))
                .collect(Collectors.toList());

        return ResponseResult.okResult(new PageVo(commentVoList, page.getTotal()));
    }

    @Override
    public ResponseResult addComment(Comment comment) {
        //评论内容不能为空
        if (!StringUtils.hasText(comment.getContent())) {
            throw new SystemException(AppHttpCodeEnum.CONTENT_NOT_NULL);
        }
        save(comment);
        return ResponseResult.okResult();
    }

    /**
     * 根据根评论的id查询所对应的子评论的集合
     *
     * @param id 根评论的id
     * @return
     */
    private List<CommentVo> getChildren(Long id) {

        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getRootId, id);
        queryWrapper.orderByAsc(Comment::getCreateTime);
        List<Comment> comments = list(queryWrapper);
        List<CommentVo> commentVos = toCommentVoList(comments);
        commentVos.stream()
                .map(commentVo -> commentVo.setAvatarUrl(userService.getAvatarUrl(commentVo.getCreateBy())))
                .collect(Collectors.toList());
        return commentVos;
    }

    private List<CommentVo> toCommentVoList(List<Comment> list) {
        List<CommentVo> commentVos = BeanCopyUtils.copyBeanList(list, CommentVo.class);
        //遍历vo集合
        //通过createBy查询用户的昵称并赋值
        //toCommentUserId不为-1才查询
        //通过toCommentUserId查询用户的昵称并赋值
        /*if (commentVo.getToCommentUserId() != -1) {
                        commentVo.setToCommentUserName(userService.getById(commentVo.getToCommentUserId()).getNickName());
                    }*/
        commentVos.stream()
                .peek(commentVo -> commentVo.setUsername(userService.getById(commentVo.getCreateBy()).getUserName()))
                .filter(commentVo -> !commentVo.getToCommentUserId().equals(-1L))
                .peek(commentVo -> commentVo.setToCommentUserName(userService.getById(commentVo.getToCommentUserId()).getNickName()))
                .collect(Collectors.toList());
        return commentVos;
    }


}

