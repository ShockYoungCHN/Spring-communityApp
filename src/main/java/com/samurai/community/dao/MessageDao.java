package com.samurai.community.dao;

import com.samurai.community.entity.Message;
import com.samurai.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class MessageDao {
    @PersistenceContext
    EntityManager entityManager;

    User sysUser;

    MessageDao(){
        this.sysUser=new User();
        this.sysUser.setId(1);
    }

    public List<Message> selectConversations(User user, int offset, int limit){
        String hql="from Message where id in (" +
                "from Message where status != 2 AND fromUser != :sysUser " +
                "AND( toUser = :user or fromUser = :user)" +
                "group by conversationId" +
                ")" +
                "order by id DESC ";//hql can not using "limit OFFSET,LIMIT"
        Query query=entityManager.createQuery(hql);
        query.setParameter("sysUser",sysUser);
        query.setParameter("user",user);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    public int selectConversationCount(User user){
        String hql=" select max(id) from Message " +
                    "WHERE status != 2 AND fromUser != :sysUser " +
                    "AND (fromUser = :user or toUser = :user) " +
                    "GROUP BY conversationId";
        Query query=entityManager.createQuery(hql);
        query.setParameter("sysUser",sysUser);
        query.setParameter("user",user);
        return query.getResultList().size();
    }

    public List<Message> selectLetters(String conversationId, int offset, int limit){
        String hql="from Message " +
                "where status!=2 AND fromUser!= :sysUser AND conversationId = :conversationId " +
                "order by id DESC";
        Query query=entityManager.createQuery(hql);
        query.setParameter("sysUser",sysUser);
        query.setParameter("conversationId",conversationId);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    public int selectLetterCount(String conversationId){
        String hql="from Message where status!=2 " +
                "AND fromUser != :sysUser " +
                "AND conversationId = :conversationId ";
        Query query=entityManager.createQuery(hql);
        query.setParameter("sysUser",sysUser);
        query.setParameter("conversationId",conversationId);
        return query.getResultList().size();
    }

    public int selectLetterUnreadCount(User user, String conversationId){
        String hql="from Message where status!=2 " +
                "AND fromUser != :sysUser " +
                "AND toUser = :user ";
        Query query=entityManager.createQuery(hql);
        if(conversationId!=null) {
            hql += "AND conversationId = :conversationId";
            query=entityManager.createQuery(hql);
            query.setParameter("conversationId",conversationId);
        }

        query.setParameter("sysUser",sysUser);
        query.setParameter("user",user);

        return query.getResultList().size();
    }

    public int insertMessage(Message message){
        entityManager.merge(message);
        return 1;
    }
}
