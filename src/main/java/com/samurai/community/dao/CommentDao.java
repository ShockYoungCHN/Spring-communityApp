package com.samurai.community.dao;

import com.samurai.community.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Repository
public class CommentDao {
    @Autowired
    private EntityManager entityManager;

    public List<Comment> selectCommentsByEntity(int entityType, int entityId, int offset, int limit){
        String hql="from Comment where status=0 " +
                "and entityType= :entity_type " +
                "and entityId= :entity_id " +
                "order by createTime ASC";
        Query query=entityManager.createQuery(hql);

        query.setParameter("entity_type",entityType);
        query.setParameter("entity_id",entityId);

        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    public int selectCountByEntity(int entityType, int entityId){
        String hql="from Comment " +
                "where entityType= :entity_type " +
                "and entityId= :entity_id";
        Query query=entityManager.createQuery(hql);

        query.setParameter("entity_type",entityType);
        query.setParameter("entity_id",entityId);
        return query.getResultList().size();
    }

    public int insertComment(Comment comment){
        entityManager.merge(comment);
        return 1;
    }

    public Comment selectCommentsById(int id){
        return entityManager.find(Comment.class,id);
    }
}
