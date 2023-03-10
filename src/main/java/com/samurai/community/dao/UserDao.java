package com.samurai.community.dao;

import com.samurai.community.entity.User;

public interface UserDao {
    User selectById(int id);

    User selectByName( String username);

    User selectByEmail(String email);

    int insertUser(User user);

    int updateStatus(int id, int status);

    int updateAvatar(int id, String avatarUrl);

    int updatePassword(int id, String password);
}
