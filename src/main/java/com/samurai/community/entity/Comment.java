package com.samurai.community.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="Comment")
public class Comment {

    @Id
    private int id;

    @OneToOne
    @JoinColumn(name="user_id")
    private User user;

    /*
    There are 2 kinds of Comment,
    the replies to a DiscussPost are called as Comment, and the replies to Comment are also Comment

    entity_type indicates the type the user comment on
     entity_type=1 means the replies to a DiscussPost,
     entity_type=2 means the replies to a Comment,
     */
    @Column(name="entity_type")
    private int entityType;

    @Column(name="entity_id")
    private int entityId;

    // target_id only existed when we send a comment to someone's reply,
    // otherwise(like when we comment to someone's post) it should be null
    @ManyToOne
    @JoinColumn(name="target_id")
    private User targetUser;

    @Column(name="content")
    private String content;

    @Column(name="status")
    private int status;

    @Column(name="create_time")
    private Date createTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(User targetUser) {
        this.targetUser = targetUser;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getEntityType() {
        return entityType;
    }

    public void setEntityType(int entityType) {
        this.entityType = entityType;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }



    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", userId=" + user.getId() +
                ", entityType=" + entityType +
                ", entityId=" + entityId +
                ", targetId=" + targetUser.getId() +
                ", content='" + content + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                '}';
    }
}
