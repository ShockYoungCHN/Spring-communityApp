package com.samurai.community;

import com.samurai.community.dao.DiscussPostDao;
import com.samurai.community.entity.DiscussPost;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;


@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class CommunityApplicationTests {

    @Autowired
    DiscussPostDao dp;

    @Test
    public void contextLoads() {
        Pageable pg = PageRequest.of(1,10);
        List<DiscussPost> dpi=  dp.selectDiscussPosts(101,0,1,0);
        System.out.println(dpi.get(0));
    }
}
