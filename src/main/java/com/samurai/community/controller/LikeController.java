package com.samurai.community.controller;

import com.samurai.community.entity.User;
import com.samurai.community.service.LikeService;
import com.samurai.community.util.CommunityUtil;
import com.samurai.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class LikeController {
    @Autowired
    HostHolder hostHolder;

    @Autowired
    private LikeService likeService;

    @PostMapping("/like")
    public String like(int entityType, int entityId, int entityUserId){
        User user = hostHolder.getUser();

        likeService.like(user.getId(), entityType, entityId,entityUserId);
        long likeCount= likeService.findEntityLikeCount(entityType, entityId);

        //true for already like, false for not like yet
        boolean likeStatus=likeService.findEntityLikeStatus(user.getId(),entityType,entityId);

        Map<String, Object> map=new HashMap<>();
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);

        return CommunityUtil.getJSONString(0,null,map);
    }
}
