package com.samurai.community.controller;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.samurai.community.entity.Comment;
import com.samurai.community.entity.DiscussPost;
import com.samurai.community.entity.Event;
import com.samurai.community.entity.User;
import com.samurai.community.event.EventProducer;
import com.samurai.community.service.CommentService;
import com.samurai.community.service.DiscussPostService;
import com.samurai.community.util.CommunityConstant;
import com.samurai.community.util.HostHolder;
//import com.samurai.community.util.RedisKeyUtil;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {
    @Autowired private CommentService commentService;

    @Autowired private HostHolder hostHolder;

    @Autowired private EventProducer eventProducer;

    @Autowired private DiscussPostService discussPostService;

    //@Autowired private RedisTemplate redisTemplate;

    @PostMapping("/add/{discussPostId}")
    public String addComment(@PathVariable("discussPostId") int discussPostId, Comment comment) {
        User user = hostHolder.getUser();
        comment.setUser(user);
        comment.setStatus(0);
        comment.setCreateTime(new Date());
        commentService.addComment(comment);

        // Trigger comment events
        Event event =
                new Event()
                        .setTopic(TOPIC_COMMENT)
                        .setUserId(comment.getUser().getId())
                        .setEntityType(comment.getEntityType())
                        .setEntityId(comment.getEntityId())
                        .setData("postId", discussPostId);
        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUser().getId());
        } else if (comment.getEntityType() == ENTITY_TYPE_COMMENT) {
            Comment target = commentService.findCommentsById(comment.getEntityId());
            event.setEntityUserId(target.getUser().getId());
        }
        eventProducer.fireEvent(event);

        if (comment.getEntityType() == ENTITY_TYPE_POST) {
            // 触发发帖事件
            event =
                    new Event()
                            .setTopic(TOPIC_PUBLISH)
                            .setUserId(comment.getUser().getId())
                            .setEntityType(ENTITY_TYPE_POST)
                            .setEntityId(discussPostId);
            eventProducer.fireEvent(event);

/*            String redisKey = RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey, discussPostId);*/
        }

        return "redirect:/discuss/detail/" + discussPostId;
    }
}
