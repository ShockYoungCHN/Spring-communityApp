package com.samurai.community.dao;

import com.samurai.community.entity.LoginTicket;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Repository
public class LoginTicketDaoImpl implements LoginTicketDao{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public int insertLoginTicket(LoginTicket loginTicket) {
        try{
            entityManager.persist(loginTicket);
            return 1;
        }
        catch(Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public LoginTicket selectByTicket(String ticket) {
        String hql = "from LoginTicket where ticket= :ticket";
        Query query=entityManager.createQuery(hql);
        query.setParameter("ticket",ticket);
        return (LoginTicket) query.getSingleResult();
    }

    @Override
    public int updateStatus(String ticket, int status) {
        String hql="update LoginTicket set status = :status " +
                "where ticket= :ticket";

        Query query=entityManager.createQuery(hql);
        query.setParameter("status",status);
        query.setParameter("ticket",ticket);
        return query.executeUpdate();
    }
}
