package com.samurai.community.service;

import com.samurai.community.dao.CommentDao;
import com.samurai.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;
import com.samurai.community.dao.CommentDao;
import com.samurai.community.entity.Comment;
import com.samurai.community.util.CommunityConstant;
import com.samurai.community.util.SensitiveFilter;

import java.util.List;


@Service
public class CommentService implements CommunityConstant {
    @Autowired private CommentDao commentDao;

    @Autowired private SensitiveFilter sensitiveFilter;

    @Autowired private DiscussPostService discussPostService;

    public List<Comment> findCommentsByEntity(int entityType, int entityId, int offset, int limit) {
        return commentDao.selectCommentsByEntity(entityType, entityId, offset, limit);
    }

    public int findCommentCount(int entityType, int entityId) {
        return commentDao.selectCountByEntity(entityType, entityId);
    }

    @Transactional(
            isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class)
    public int addComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("comment cannot be null!");
        }
        // 添加评论
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));
        int rows = commentDao.insertComment(comment);

        // 更新帖子数量
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            int count =
                    commentDao.selectCountByEntity(
                            comment.getEntityType(), comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(), count);
        }

        return rows;
    }

    public Comment findCommentsById(int entityId) {
        return commentDao.selectCommentsById(entityId);
    }
}
