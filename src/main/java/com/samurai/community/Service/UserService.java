package com.samurai.community.Service;

import com.samurai.community.dao.UserDao;
import com.samurai.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UserService {
    @Autowired
    private UserDao userDao;

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
}
