package com.samurai.community.controller;

import com.samurai.community.entity.Page;
import com.samurai.community.entity.User;
import com.samurai.community.service.FollowService;
import com.samurai.community.service.UserService;
import com.samurai.community.util.CommunityUtil;
import com.samurai.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

import static com.samurai.community.util.CommunityConstant.*;

@RestController
public class FollowController {

    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    public UserService userService;

    @PostMapping("/follow")
    public String follow(int entityType, int entityId){
        User user=hostHolder.getUser();
        followService.follow(user.getId(), entityType, entityId);

        return CommunityUtil.getJSONString(0,"already followed");
    }

    @PostMapping("/unfollow")
    public String unfollow(int entityType, int entityId){
        User user=hostHolder.getUser();
        followService.unfollow(user.getId(), entityType, entityId);

        return CommunityUtil.getJSONString(0,"cancelled following");
    }

    @GetMapping("/followees/{userId}")
    public ModelAndView getFollowees(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("the user doesn't exist");
        }
        model.addAttribute("user", user);
        // pagination, page number was set in the frontend
        page.setLimit(5);
        page.setPath("/followees/" + userId);
        page.setRows((int) followService.findFolloweeCount(userId, ENTITY_TYPE_USER));

        List<Map<String, Object>> userList =
                followService.findFollowees(userId, page.getOffset(), page.getLimit());
        if (userList != null) {
            for (Map<String, Object> map : userList) {
                User u = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users", userList);
        return new ModelAndView("site/followee");
    }

    private boolean hasFollowed(int userId) {
        if (hostHolder.getUser() == null) {
            return false;
        }
        return followService.hasFollowed(hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
    }

    @GetMapping("/followers/{userId}")
    public ModelAndView getFollowers(@PathVariable("userId") int userId, Page page, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("user not existed");
        }
        model.addAttribute("user", user);
        page.setLimit(5);
        page.setPath("/followers/" + userId);
        page.setRows((int) followService.findFollowerCount(ENTITY_TYPE_USER, userId));

        List<Map<String, Object>> userList =
                followService.findFollowers(userId, page.getOffset(), page.getLimit());
        if (userList != null) {
            for (Map<String, Object> map : userList) {
                User u = (User) map.get("user");
                map.put("hasFollowed", hasFollowed(u.getId()));
            }
        }
        model.addAttribute("users", userList);
        return new ModelAndView("site/follower");
    }
}
