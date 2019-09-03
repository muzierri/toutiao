package com.nowcoder;

import com.nowcoder.async.*;
import com.nowcoder.dao.*;
import com.nowcoder.model.*;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.NewsService;
import com.nowcoder.util.JedisAdapter;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ToutiaoApplication.class)
//@Sql("/init-schema.sql")
public class InitDatabaseTests {
    @Autowired
    UserDAO userDAO;
    @Autowired
    NewsDAO newsDAO;
    @Autowired
    NewsService newsService;
    @Autowired
    LoginTicketDAO loginTicketDAO;
    @Autowired
    CommentDAO commentDAO;
    @Autowired
    MessageDAO messageDAO;
    @Autowired
    EventProducer eventProducer;
    @Autowired
    JedisAdapter jedisAdapter;

    @Test
    public void initData() {
        Random random = new Random();
        for (int i = 0; i < 11; ++i) {
            User user = new User();
            user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
            user.setName(String.format("USER%d", i));
            user.setPassword("");
            user.setSalt("");
            userDAO.addUser(user);

            News news = new News();
            news.setCommentCount(i);
            Date date = new Date();
            date.setTime(date.getTime() + 1000 * 3600 * 5 * i);
            news.setCreatedDate(date);
            news.setImage(String.format("http://images.nowcoder.com/head/%dt.png", random.nextInt(1000)));
            news.setLikeCount(i + 1);
            news.setUserId(i + 1);
            news.setTitle(String.format("TITLE{%d}", i));
            news.setLink(String.format("http://www.nowcoder.com/%d.html", i));
            newsDAO.addNews(news);

            user.setPassword("newpassword");
            userDAO.updatePassword(user);

            LoginTicket ticket = new LoginTicket();
            ticket.setUserId(i + 1);
            ticket.setStatus(0);
            ticket.setExpired(date);
            ticket.setTicket(String.format("TICKET%d", i + 1));
            loginTicketDAO.addTicket(ticket);

            loginTicketDAO.updateStatus(ticket.getTicket(), 2);
        }

        Assert.assertEquals("newpassword", userDAO.selectById(1).getPassword());
        userDAO.deleteById(1);
        Assert.assertNull(userDAO.selectById(1));

        Assert.assertEquals(1, loginTicketDAO.selectByTicket("TICKET1").getUserId());
        Assert.assertEquals(2, loginTicketDAO.selectByTicket("TICKET1").getStatus());
    }

    @Test
    public void test1() {
        List<News> newsList = newsDAO.selectByUserIdAndOffset(0, 0, 3);
        for (News news : newsList) {
            System.out.println(news);
        }
    }

    //    @Test
//    public void test2(){
//        List<News> newsList = newsService.getLatestNews(0,0,2);
//        for (News news : newsList){
//            System.out.println(news);
//        }
//    }
    @Test
    public void test3() {
        System.out.println(UUID.randomUUID().toString().substring(0, 5));
    }


    @Test
    public void test4() {
        loginTicketDAO.updateStatus("TICKET1", 2);
    }


    @Test//添加评论
    public void test5() {
        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 3; j++) {
                Comment comment = new Comment();
                comment.setUserId(i + 1);
                comment.setCreatedDate(new Date());
                comment.setEntityId(i + 1);
                comment.setContent("Comment " + String.valueOf(j));
                comment.setStatus(0);
                comment.setEntityType(EntityType.ENTITY_NEWS);
                commentDAO.addComment(comment);
            }
        }
    }

    @Test
    public void test6() {
        Assert.assertNotNull(commentDAO.selectByEntity(1, EntityType.ENTITY_NEWS).get(0));
    }

    @Test
    public void test7() {
        newsDAO.updateCommentCount(1, 0);
    }

    @Test
    public void test8() {
        List<Message> messages = messageDAO.getConversationDetail("2_12", 0, 10);
        for (Message msg : messages) {
            System.out.println(msg);
        }
    }

    @Test
    public void testEventModel(){
        EventModel eventModel = new EventModel();
        EventModel model = eventModel.setType(EventType.LIKE).setActorId(1);
        System.out.println(model);
    }
    @Test
    public void test9(){

        System.out.println(eventProducer.fireEvent(new EventModel(EventType.LIKE).setActorId(1)
                .setEntityType(EntityType.ENTITY_NEWS).setEntityId(2)
                .setEntityOwnerId(3)));
    }
    @Test
    public void test10(){
        System.out.println(new EventModel(EventType.LIKE).setActorId(1)
                .setEntityType(EntityType.ENTITY_NEWS).setEntityId(2)
                .setEntityOwnerId(3));
    }
    @Test
    public void test11(){
        long l1 = jedisAdapter.lpush("zz","zzz");
        System.out.println(l1);
    }
    @Test
    public void test12(){
        List<String> events = jedisAdapter.brpop(0,"EVENT");
        for (String event: events){
            System.out.println(event);
        }
    }
    @Test
    public void test13() throws Exception{
        EventConsumer eventConsumer = new EventConsumer();
//        System.out.println(eventConsumer.config.containsKey("LIKE"));
//        System.out.println(eventConsumer.beans);


    }

}


