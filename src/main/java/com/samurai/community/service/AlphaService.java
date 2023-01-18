package com.samurai.community.service;

import com.samurai.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import com.samurai.community.dao.DiscussPostDao;
import com.samurai.community.dao.UserDao;
import com.samurai.community.entity.DiscussPost;
import com.samurai.community.entity.User;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Date;


@Service
public class AlphaService {


    @Autowired private UserDao userDao;

    @Autowired DiscussPostDao discussPostDao;

    private static final Logger LOGGER = LoggerFactory.getLogger(AlphaService.class);

    public AlphaService() {
        System.out.println("实例化AlphaService");
    }

    @PostConstruct
    public void init() {
        System.out.println("AlphaService init().");
    }

    @PreDestroy
    public void destory() {
        System.out.println("销毁AlphaService");
    }


    /**
     * REQUIRED:支持当前事务(外部事务),如果不存在则创建新事务 REQUIRES_NEW:创建一个新事务，并且暂停当前事务(外部事务) NESTED:
     * 如果当前存在事务(外部事务)则嵌套在该事务中执行(独立的提交和回滚)，否则就和REQUIRED一样
     *
     * @return
     */
    @Transactional(
            isolation = Isolation.READ_COMMITTED,
            propagation = Propagation.REQUIRED,
            rollbackFor = Exception.class)
    public Object save1() {
        // 新增用户
        User user = new User();
        user.setUsername("alpha");
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
        user.setEmail("alpha@qq.com");
        user.setAvatarUrl("http://image.nowcoder.com/head/99t.png");
        user.setCreateTime(new Date());
        userDao.insertUser(user);

        // 新增帖子
        DiscussPost post = new DiscussPost();
        post.setUser(user);
        post.setTitle("hello");
        post.setContent("新人报道！");
        post.setCreateTime(new Date());
        discussPostDao.insertDiscussPost(post);

        Integer.valueOf("abc");
        return "ok";
    }

    @Autowired private TransactionTemplate transactionTemplate;

    public Object save2() {
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return transactionTemplate.execute(
                (TransactionCallback<Object>)
                        status -> {
                            // 新增用户
                            User user = new User();
                            user.setUsername("alpha2");
                            user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
                            user.setPassword(CommunityUtil.md5("123" + user.getSalt()));
                            user.setEmail("alpha2@qq.com");
                            user.setAvatarUrl("http://image.nowcoder.com/head/999t.png");
                            user.setCreateTime(new Date());
                            userDao.insertUser(user);

                            // 新增帖子
                            DiscussPost post = new DiscussPost();
                            post.setUser(user);
                            post.setTitle("hello2");
                            post.setContent("新人报道2！");
                            post.setCreateTime(new Date());
                            discussPostDao.insertDiscussPost(post);

                            Integer.valueOf("abc");
                            return "ok";
                        });
    }

    /** 让该方法在多线性环境下，被异步的调用 */
    @Async
    public void execute1() {
        LOGGER.debug("execute1");
    }

    //    @Scheduled(initialDelay = 10_000, fixedRate = 1_000)
    public void execute2() {
        LOGGER.debug("execute2");
    }
}
