package com.samurai.community;

import com.samurai.community.dao.MessageDao;
import com.samurai.community.entity.Message;
import com.samurai.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MessagDaoTest {
    @Autowired
    private MessageDao messageDao;

    @Test
    public void test() {
        User u=new User();
        u.setId(111);
        List<Message> list = messageDao.selectConversations(u, 0, 20);
        for (Message message : list) {
            System.out.println(message);
        }
        int count = messageDao.selectConversationCount(u);
        System.out.println(count);

        list = messageDao.selectLetters("111_112", 0, 10);
        for (Message message : list) {
            System.out.println(message);
        }
        count = messageDao.selectLetterCount("111_112");
        System.out.println(count);

        u.setId(131);
        count = messageDao.selectLetterUnreadCount(u, "111_131");
        System.out.println(count);

    }

}
