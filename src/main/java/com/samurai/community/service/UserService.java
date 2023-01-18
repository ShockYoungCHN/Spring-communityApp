package com.samurai.community.service;

import com.samurai.community.util.CommunityUtil;
import com.samurai.community.util.MailClient;
import com.samurai.community.dao.LoginTicketDao;
import com.samurai.community.dao.UserDao;
import com.samurai.community.entity.LoginTicket;
import com.samurai.community.entity.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.samurai.community.util.CommunityConstant.*;

@Repository
public class UserService {
    @Autowired
    private UserDao userDao;

    @Autowired
    private LoginTicketDao loginTicketDao;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private MailClient mailClient;

    public User findUserById(int id) {
        return userDao.selectById(id);
/*        User user = getCache(id);
        if (user == null) {
            user = initCache(id);
        }
        return user;*/
    }

    public int updatePassword(int id, String newPassword) {
        //clearCache(id);
        return userDao.updatePassword(id, newPassword);
    }


    /** register */
    @Transactional
    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>(16);
        // deal with null value
        if (user == null) {
            throw new IllegalArgumentException("The parameter cannot be empty!");
        }
        if (StringUtils.isBlank(user.getUsername())) {
            map.put("usernameMsg", "Account cannot be empty");
            return map;
        }
        if (StringUtils.isBlank(user.getPassword())) {
            map.put("passwordMsg", "Password cannot be empty");
            return map;
        }
        if (StringUtils.isBlank(user.getEmail())) {
            map.put("emailMsg", "Email   cannot be empty");
            return map;
        }

        // validate the account
        User u = userDao.selectByName(user.getUsername());
        if (u != null) {
            map.put("usernameMsg", "This account name already exists!");
            return map;
        }
        // validate the email
        u = userDao.selectByEmail(user.getEmail());
        if (u != null) {
            map.put("emailMsg", "This email address has been registered!");
            return map;
        }

        // register the account
        user.setSalt(CommunityUtil.generateUUID().substring(0, 5));
        user.setPassword(CommunityUtil.md5(user.getPassword() + user.getSalt()));
        user.setType(0);
        user.setStatus(0);
        user.setActivationCode(CommunityUtil.generateUUID());
        user.setAvatarUrl(
                String.format(
                        "http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        userDao.insertUser(user);

        // activate the email
        Context context = new Context();
        context.setVariable("email", user.getEmail());
        // http://localhost:8080/community/activation/101/code
        String url = domain
                        + contextPath
                        + "activation/"
                        + user.getId()
                        + "/"
                        + user.getActivationCode();
        context.setVariable("url", url);
        String content = templateEngine.process("/mail/activation", context);
        mailClient.sendMail(user.getEmail(), "Activation Email", content);

        return map;
    }


    @Transactional
    public Map<String, Object> login(String username, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>(16);

        // 空值处理
        if (StringUtils.isBlank(username)) {
            map.put("usernameMsg", "The account cannot be empty!");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "The password cannot be empty!");
            return map;
        }
        // 验证账号
        User user = userDao.selectByName(username);
        if (user == null) {
            map.put("usernameMsg", "The account does not exist!");
            return map;
        }

        // 验证状态
        if (user.getStatus() == 0) {
            map.put("usernameMsg", "The account is not activated!");
            return map;
        }
        // 验证密码
        password = CommunityUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "Incorrect password!");
            return map;
        }

        // generate LoginTicket
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUser(user);
        loginTicket.setTicket(CommunityUtil.generateUUID());
        loginTicket.setStatus(0);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000L));
        loginTicketDao.insertLoginTicket(loginTicket);

/*
        String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        // loginTicket会序列化成json
        redisTemplate.opsForValue().set(redisKey, loginTicket);
*/

        map.put("ticket", loginTicket.getTicket());
        return map;
    }


    public int activation(int userId, String code) {
        User user = userDao.selectById(userId);
        if (user.getStatus() == 1) {
            return ACTIVATION_REPEAT;
        } else if (user.getActivationCode().equals(code)) {
            userDao.updateStatus(userId, 1);
            //clearCache(userId);
            return ACTIVATION_SUCCESS;
        } else {
            return ACTIVATION_FAILED;
        }
    }

    public void logout(String ticket) {
    }

    public LoginTicket findLoginTicket(String ticket) {
        return loginTicketDao.selectByTicket(ticket);
    }

    public int updateAvatar(int userId, String avatarUrl) {
         return userDao.updateAvatar(userId, avatarUrl);
/*        int rows = userDao.updateAvatar(userId, avatarUrl);
        clearCache(userId);
        return rows;*/
    }
}
