package com.samurai.community.service;

import com.github.benmanes.caffeine.cache.LoadingCache;
import com.samurai.community.util.SensitiveFilter;
import com.samurai.community.dao.DiscussPostDao;
import com.samurai.community.entity.DiscussPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;
//import com.samurai.community.Util.SensitiveFilter;

import java.util.List;

@Service
public class DiscussPostService {
    @Autowired
    DiscussPostDao DPDao;

    @Autowired
    private SensitiveFilter sensitiveFilter;

/*    @Value("${caffeine.posts.max-size}")
    private int maxSize;

    @Value("${caffeine.posts.expire-seconds}")
    private int expireSeconds;*/

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscussPostService.class);

    // Caffeine核心接口：Cache, LoadingCache, AsyncLoadingCache
    // 帖子列表缓存
    private LoadingCache<String, List<DiscussPost>> postListCache;

    // 帖子总数缓存
    private LoadingCache<Integer, Integer> postRowsCache;

/*
    @PostConstruct
    public void init() {
        // 初始化帖子列表缓存
        postListCache =
                Caffeine.newBuilder()
                        .maximumSize(maxSize)
                        .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                        .build(
                                key -> {
                                    if (key == null || key.length() == 0) {
                                        throw new IllegalArgumentException("参数错误!");
                                    }
                                    String[] params = key.split(":");
                                    if (params == null || params.length != 2) {
                                        throw new IllegalArgumentException("参数错误!");
                                    }
                                    int offset = Integer.valueOf(params[0]);
                                    int limit = Integer.valueOf(params[1]);
                                    // 二级缓存：Redis -> mysql
                                    LOGGER.debug("load post list from DB");
                                    return DPDao.selectDiscussPosts(
                                            0, offset, limit, 1);
                                });
        // 初始化帖子总数缓存
        postRowsCache =
                Caffeine.newBuilder()
                        .maximumSize(maxSize)
                        .expireAfterWrite(expireSeconds, TimeUnit.SECONDS)
                        .build(
                                key -> {
                                    LOGGER.debug("load post list from DB");
                                    return DPDao.selectDiscussPostRows(key);
                                });
    }
*/

    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit, int orderMode) {
        if (userId == 0 && orderMode == 1) {
            return postListCache.get(offset + ":" + limit);
        }
        LOGGER.debug("load post list from DB");
        return DPDao.selectDiscussPosts(userId, offset, limit, orderMode);
    }

    public int findDiscussPostRows(int userId) {
/*        if (userId == 0)
            return postRowsCache.get(userId);
        */
        LOGGER.debug("load post list from DB");
        return DPDao.selectDiscussPostRows(userId);
    }

    @Transactional
    public int addDiscussPost(DiscussPost post) {
        if (post == null) {
            throw new IllegalArgumentException("Post can not be null");
        }
        // Escape HTML tags
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));

        // Filtering sensitive words
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        return DPDao.insertDiscussPost(post);
    }

    public DiscussPost findDiscussPostById(int id) {
        return DPDao.selectDiscussPostById(id);
    }

    public int updateCommentCount(int discussPostId, int commentCount) {
        return DPDao.updateCommentCount(discussPostId, commentCount);
    }

    public int updateType(int id, int type) {
        return DPDao.updateType(id, type);
    }

    public int updateStatus(int id, int status) {
        return DPDao.updateStatus(id, status);
    }

    public void updateScore(int id, double score) {
        DPDao.updateScore(id, score);
    }
}
