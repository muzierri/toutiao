package com.nowcoder.model;

import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Author: liyang
 * @Date: Create in 16:40 2019/7/3
 * @Modified By
 */
@Component
public class HostHolder {
    private static ThreadLocal<User> users = new ThreadLocal<User>();

    public User getUser(){
        return users.get();
    }
    public void setUser(User user){
        users.set(user);
    }
    public void clear(){
        users.remove();
    }
}
