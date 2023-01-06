package com.samurai.community.dao;

import com.samurai.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;

@Repository
public class UserDaoImpl implements UserDao {
    @Autowired
    EntityManager entityManager;
    @Override
    public User selectById(int id) {
        return entityManager.find(User.class, id);
    }

    @Override
    public User selectByName(String username) {
        String hql="from User u where u.username= :user_name";
        Query query=entityManager.createQuery(hql);
        query.setParameter("user_name",username);
        return (User)query.getSingleResult();
    }

    @Override
    public User selectByEmail(String email) {
        String hql="from User u where u.email= :email";
        Query query=entityManager.createQuery(hql);
        query.setParameter("email",email);
        return (User)query.getSingleResult();
    }

    @Override
    public int insertUser(User user) {
        try {
            entityManager.persist(user);
            return 1;
        }
        catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int updateStatus(int id, int status) {
        try {
            User u=entityManager.find(User.class,id);
            u.setStatus(status);
            entityManager.persist(u);
            return 1;
        }
        catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int updateHeader(int id, String headerUrl) {
        try {
            User u=entityManager.find(User.class,id);
            u.setHeaderUrl(headerUrl);
            entityManager.persist(u);
            return 1;
        }
        catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int updatePassword(int id, String password) {
        try {
            User u=entityManager.find(User.class,id);
            u.setHeaderUrl(password);
            entityManager.persist(u);
            return 1;
        }
        catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
