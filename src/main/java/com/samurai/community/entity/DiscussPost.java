package com.samurai.community.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.*;
import java.util.Date;


@Entity
@Table(name="discuss_post")
@Document(indexName = "discusspost", shards = 6, replicas = 3)
public class DiscussPost {

    @javax.persistence.Id
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private int id;


    @ManyToOne
    @JoinColumn(name="user_id")
    @Field(type = FieldType.Integer)
    private User user;

    @Column(name="title")
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String title;

    @Column(name="content")
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String content;

    @Column(name="type")
    @Field(type = FieldType.Integer)
    private int type;

    @Column(name="status")
    @Field(type = FieldType.Integer)
    private int status;

    @Column(name="create_time")
    @Field(type = FieldType.Date)
    private Date createTime;

    @Column(name="comment_count")
    @Field(type = FieldType.Integer)
    private int commentCount;

    @Column(name="score")
    @Field(type = FieldType.Double)
    private double score;


    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "DiscussPost{" +
                "id=" + id +
                ", userId=" + user.getId() +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", createTime=" + createTime +
                ", score=" + score +
                '}';
    }
}
