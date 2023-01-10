package com.samurai.community.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "login_ticket")
public class LoginTicket {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "ticket")
    private String ticket;

    public LoginTicket(Long id, String ticket, User user, int status, Date expired) {
        this.id = id;
        this.ticket = ticket;
        this.user = user;
        this.status = status;
        this.expired = expired;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setUserId(int id) {
        if(user != null)
            this.user.setId(id);
    }

    @OneToOne
    @JoinColumn(name="user_id")
    private User user;

    @Column(name = "status")
    private int status;

    @Column(name = "expired")
    private Date expired;

    public LoginTicket() {
    }


    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getExpired() {
        return expired;
    }

    public void setExpired(Date expired) {
        this.expired = expired;
    }

    @Override
    public String toString() {
        return "LoginTicket{" +
                "id=" + id +
                ", ticket='" + ticket + '\'' +
                ", userId=" + user.getId() +
                ", status=" + status +
                ", expired=" + expired +
                '}';
    }
}

