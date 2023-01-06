package com.samurai.community.controller;

import com.samurai.community.Service.DiscussPostService;
import com.samurai.community.Service.UserService;
import com.samurai.community.entity.DiscussPost;
import com.samurai.community.entity.Page;
import com.samurai.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @GetMapping("/index")
    public String getIndexPage(
            Model model,
            Page page,
            @RequestParam(name = "orderMode", defaultValue = "0") int orderMode) {

        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/?orderMode=" + orderMode);
        List<DiscussPost> list =
                discussPostService.findDiscussPosts(
                        0, page.getOffset(), page.getLimit(), orderMode);
        List<Map<String, Object>> discussPosts = new ArrayList<>();

        if (list != null) {
            for (DiscussPost post : list) {
                Map<String, Object> map = new HashMap<>(16);
                map.put("post", post);
                map.put("user", post.getUser());

                //long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId());
                map.put("likeCount", 1);

                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("orderMode", orderMode);
        return "/index";
    }

}
