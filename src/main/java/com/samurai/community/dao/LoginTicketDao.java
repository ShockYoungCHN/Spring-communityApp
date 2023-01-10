package com.samurai.community.dao;

import com.samurai.community.entity.LoginTicket;


public interface LoginTicketDao {
    int insertLoginTicket(LoginTicket loginTicket);

    LoginTicket selectByTicket(String ticket);

    int updateStatus(String ticket,int status);
}
