package com.samurai.community.service;

import com.samurai.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import com.samurai.community.dao.MessageDao;
import com.samurai.community.entity.Message;
import com.samurai.community.util.SensitiveFilter;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    private MessageDao messageDao;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<Message> findConversations(User user, int offset, int limit) {
        return messageDao.selectConversations(user, offset, limit);
    }

    public int findConversationCount(User user) {
        return messageDao.selectConversationCount(user);
    }

    public List<Message> findLetters(String conversationId, int offset, int limit) {
        return messageDao.selectLetters(conversationId, offset, limit);
    }

    public int findLetterCount(String conversationId) {
        return messageDao.selectLetterCount(conversationId);
    }

    public int findLetterUnreadCount(User user, String conversationId) {
        return messageDao.selectLetterUnreadCount(user, conversationId);
    }


    public int addMessage(Message message) {
        message.setContent(HtmlUtils.htmlEscape(message.getContent()));
        message.setContent(sensitiveFilter.filter(message.getContent()));
        return messageDao.insertMessage(message);
    }
}