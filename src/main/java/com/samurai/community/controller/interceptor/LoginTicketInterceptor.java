package com.samurai.community.controller.interceptor;

import com.samurai.community.service.UserService;
import com.samurai.community.util.CookieUtil;
import com.samurai.community.util.HostHolder;
import com.samurai.community.entity.LoginTicket;
import com.samurai.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ticket= CookieUtil.getValue(request,"ticket");

        if(ticket!=null) {
            LoginTicket loginTicket = userService.findLoginTicket(ticket);
            if (loginTicket != null
                    && loginTicket.getStatus() == 0
                    && loginTicket.getExpired().after(new Date())) {
                User currentUser = loginTicket.getUser();
                hostHolder.setUser(currentUser);
               /* Authentication authentication =
                        new UsernamePasswordAuthenticationToken(
                                currentUser, user.getPassword(), userService.getAuthorities(user.getId()));
                SecurityContextHolder.setContext(new SecurityContextImpl(authentication));*/

            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user != null && modelAndView != null)
            modelAndView.addObject("loginUser", user);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clear();
    }
}
