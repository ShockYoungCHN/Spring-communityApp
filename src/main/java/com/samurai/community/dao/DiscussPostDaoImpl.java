package com.samurai.community.dao;

import com.samurai.community.entity.DiscussPost;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class DiscussPostDaoImpl implements DiscussPostDao{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<DiscussPost> selectDiscussPosts(int userId, int offset, int limit, int orderMode) {
        String hql="from DiscussPost DP";
        Query query = entityManager.createQuery(hql);
        if(userId != 0) {
            hql += " where DP.user.id= :user_id";
            query.setParameter("user_id",userId);
        }

        query.setFirstResult(offset);//0 is the first row                                                //输入起始的记录数
        query.setMaxResults(limit);
        return query.getResultList();
    }

    @Override
    public int selectDiscussPostRows(int userId) {
        String hql="SELECT COUNT(*) FROM DiscussPost DP WHERE DP.status != 2";
        Query query = entityManager.createQuery(hql);
        if(userId != 0) {
            hql += "AND user_id = :user_id";
            query = entityManager.createQuery(hql);
            query.setParameter("user_id",userId);
        }
        return ((Number)query.getSingleResult()).intValue();
    }

    @Override
    public int insertDiscussPost(DiscussPost discussPost) {
        try{
            entityManager.merge(discussPost);
            return 1;
        }
        catch(Exception e){
            return 0;
        }
    }

    @Override
    public DiscussPost selectDiscussPostById(int id) {
        return entityManager.find(DiscussPost.class,id);
    }

    @Override
    public int updateCommentCount(int discussPostId, int commentCount) {
        DiscussPost dp=entityManager.find(DiscussPost.class,discussPostId);
        dp.setCommentCount(commentCount);
        entityManager.detach(dp);
        return 1;
    }

    @Override
    public int updateType(int id, int type) {

        return 0;
    }

    @Override
    public int updateStatus(int id, int status) {
        DiscussPost DP=entityManager.find(DiscussPost.class,id);
        DP.setScore(status);
        entityManager.merge(DP);
        return 0;
    }

    @Override
    public void updateScore(int id, double score) {
        DiscussPost DP=entityManager.find(DiscussPost.class,id);
        DP.setScore(score);
        entityManager.merge(DP);
    }
}
