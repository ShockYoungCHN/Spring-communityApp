package com.samurai.community;

import com.mysql.cj.log.Log;
import com.samurai.community.Util.CommunityUtil;
import com.samurai.community.dao.LoginTicketDao;
import com.samurai.community.dao.UserDao;
import com.samurai.community.entity.LoginTicket;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LoginTicketDaoTests {
    @Autowired
    LoginTicketDao loginTicketDao;

    @Autowired
    UserDao userDao;

    @Test

     public void test(){
/*        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUser(userDao.selectById(111));
        loginTicket.setTicket("21412414");
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + 0 * 1000L));
        loginTicketDao.insertLoginTicket(loginTicket);*/

        System.out.println(loginTicketDao.selectByTicket("21412414"));
    }
}
