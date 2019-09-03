package com.nowcoder;

import com.nowcoder.dao.*;
import com.nowcoder.model.*;
import com.nowcoder.service.NewsService;
import com.nowcoder.util.JedisAdapter;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ToutiaoApplication.class)
public class JedisTests {
    @Autowired
    private JedisAdapter jedisAdapter;

    @Test
    public void testObject() {
        User user = new User();
        user.setHeadUrl("http://www.zz.com");
        user.setSalt("salt");
        user.setPassword("pwd");
        user.setName("zz");
        jedisAdapter.setObject("user", user);
        //127.0.0.1:6379> get user
        //"{\"headUrl\":\"http://www.zz.com\",\"id\":0,\"name\":\"zz\",\"password\":\"pwd\",\"salt\":\"salt\"}"

        User u = jedisAdapter.getObject("user",User.class);
        System.out.println(ToStringBuilder.reflectionToString(u));
    }
}


