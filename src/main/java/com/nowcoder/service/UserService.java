package com.nowcoder.service;

import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.User;
import com.nowcoder.util.ToutiaoUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.security.krb5.internal.Ticket;
import sun.security.provider.MD5;

import java.util.*;

/**
 * @Description:
 * @Author: liyang
 * @Date: Create in 16:17 2019/6/23
 * @Modified By
 */
@Service
public class UserService {
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private LoginTicketDAO loginTicketDAO;

    public User getUser(int id) {
        return userDAO.selectById(id);
    }

    //注册
    public Map<String, Object> register(String username, String password) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isBlank(username)) {
            map.put("msgname", "用户名不能为空");
            return map;
        }

        if (StringUtils.isBlank(password)) {
            map.put("msgpwd", "密码不能为空");
            return map;
        }

        User user = userDAO.selectByName(username);
        if (user != null) {
            map.put("msgname", "该用户名已存在");
            return map;
        }


        //密码加密
        user = new User();
        user.setName(username);
        user.setSalt(UUID.randomUUID().toString().substring(0, 5));
        user.setPassword(ToutiaoUtil.MD5(password + user.getSalt()));
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png",
                new Random().nextInt(1000)));
        userDAO.addUser(user);

        //
        String ticket = addLoginTicket(user.getId());
        map.put("ticket",ticket);
        return map;
    }

    //登录
    public Map<String, Object> login(String username, String password) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isBlank(username)) {
            map.put("msgname", "用户名不能为空");
            return map;
        }

        if (StringUtils.isBlank(password)) {
            map.put("msgpwd", "密码不能为空");
            return map;
        }

        User user = userDAO.selectByName(username);
        if (user == null) {
            map.put("msgname", "该用户名不存在");
            return map;
        }

        if (!ToutiaoUtil.MD5(password + user.getSalt()).equals(user.getPassword())) {
            map.put("msgpwd", "密码错误");
            return map;
        }

        map.put("userId",user.getId());

        String ticket = addLoginTicket(user.getId());
        map.put("ticket",ticket);
        return map;
    }

    //生成一个ticket
    private String addLoginTicket(int userId){
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(userId);
        ticket.setStatus(0);
        Date date = new Date();
        date.setTime(date.getTime()+1000*3600*24);
        ticket.setExpired(date);
        ticket.setTicket(UUID.randomUUID().toString().replaceAll("-",""));

        loginTicketDAO.addTicket(ticket);

        return ticket.getTicket();
    }

    //登出
    public void logout(String ticket){
        loginTicketDAO.updateStatus(ticket,1);
    }
}
