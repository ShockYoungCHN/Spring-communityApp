package com.samurai.community.controller;

import com.samurai.community.entity.Comment;
import com.samurai.community.entity.Page;
import com.samurai.community.service.CommentService;
import com.samurai.community.service.DiscussPostService;
import com.samurai.community.service.LikeService;
import com.samurai.community.service.UserService;
import com.samurai.community.util.CommunityUtil;
import com.samurai.community.util.HostHolder;
import com.samurai.community.entity.DiscussPost;
import com.samurai.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.samurai.community.util.CommunityConstant.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;

    @PostMapping("/add")
    @ResponseBody
    public String addDiscussPost(String title, String content){
          User user=hostHolder.getUser();

          if(user==null){
              return CommunityUtil.getJSONString(403,"user logout");
          }
          DiscussPost post=new DiscussPost();
          post.setUser(user);
          post.setTitle(title);
          post.setContent(content);
          post.setCreateTime(new Date());
          discussPostService.addDiscussPost(post);

          return CommunityUtil.getJSONString(0,"post success");
    }

    @GetMapping("/detail/{discussPostId}")
    public String getDiscussPost(@PathVariable("discussPostId") int discussPostId, Model model, Page page){
        DiscussPost post=discussPostService.findDiscussPostById(discussPostId);
        model.addAttribute("post",post);

        User user=post.getUser();
        model.addAttribute("user",user);

        long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
        model.addAttribute("likeCount", likeCount);

        boolean likeStatus =
                hostHolder.getUser() != null && likeService.findEntityLikeStatus(
                        hostHolder.getUser().getId(), ENTITY_TYPE_POST, post.getId());
        model.addAttribute("likeStatus", likeStatus);
        //set pagination info
        page.setLimit(5);
        page.setPath("/discuss/detail/" + discussPostId);
        page.setRows(post.getCommentCount());

        List<Comment> commentList =
                commentService.findCommentsByEntity(
                        ENTITY_TYPE_POST, post.getId(), page.getOffset(), page.getLimit());

        // 'Vo' for view objects, commentVoList is a packaging for the objects to be displayed on webpage
        List<Map<String, Object>> commentVoList = new ArrayList<>();
        if (commentList != null) {
            for (Comment comment : commentList) {
                Map<String, Object> commentVo = new HashMap<>(16);
                commentVo.put("comment", comment);
                commentVo.put("user", comment.getUser());


                likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("likeCount", likeCount);

                likeStatus =
                        hostHolder.getUser() != null && likeService.findEntityLikeStatus(
                                hostHolder.getUser().getId(),
                                ENTITY_TYPE_COMMENT,
                                comment.getId());
                commentVo.put("likeStatus", likeStatus);


                // reply list
                List<Comment> replyList =
                        commentService.findCommentsByEntity(
                                ENTITY_TYPE_COMMENT, comment.getId(), 0, Integer.MAX_VALUE);

                List<Map<String, Object>> replyVoList = new ArrayList<>();
                if (replyList != null) {
                    for (Comment reply : replyList) {
                        Map<String, Object> replyVo = new HashMap<>(16);
                        replyVo.put("reply", reply);
                        replyVo.put("user", reply.getUser());
                        // reply target
                        User target =
                                reply.getTargetUser().getId() == 0
                                        ? null
                                        : reply.getTargetUser();
                        replyVo.put("target", target);


                        likeCount =
                                likeService.findEntityLikeCount(ENTITY_TYPE_COMMENT, reply.getId());
                        replyVo.put("likeCount", likeCount);

                        likeStatus =
                                hostHolder.getUser() != null && likeService.findEntityLikeStatus(
                                        hostHolder.getUser().getId(),
                                        ENTITY_TYPE_COMMENT,
                                        reply.getId());
                        replyVo.put("likeStatus", likeStatus);

                        replyVoList.add(replyVo);
                    }
                }
                commentVo.put("replys", replyVoList);

                int replyCount =
                        commentService.findCommentCount(ENTITY_TYPE_COMMENT, comment.getId());
                commentVo.put("replyCount", replyCount);
                commentVoList.add(commentVo);
            }
        }
        model.addAttribute("comments", commentVoList);
        return "/site/discuss-detail";
    }
}
