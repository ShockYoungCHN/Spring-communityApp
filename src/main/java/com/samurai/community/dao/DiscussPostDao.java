package com.samurai.community.dao;

import com.samurai.community.entity.DiscussPost;

import java.util.List;

public interface DiscussPostDao {
    List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit, int orderMode);

    int selectDiscussPostRows(int userId);

    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectDiscussPostById(int id);

    int updateCommentCount(int id, int commentCount);

    int updateType(int id, int type);

    int updateStatus(int id, int status);

    void updateScore(int id, double score);
}


