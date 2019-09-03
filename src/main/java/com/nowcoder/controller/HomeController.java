package com.nowcoder.controller;

import com.nowcoder.model.EntityType;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.News;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.LikeService;
import com.nowcoder.service.NewsService;
import com.nowcoder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: liyang
 * @Date: Create in 22:52 2019/6/23
 * @Modified By
 */
@Controller
public class HomeController {
    @Autowired
    private UserService userService;
    @Autowired
    private NewsService newsService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private LikeService likeService;

    private List<ViewObject> getNews(int userId, int offset, int limit) {
        int localUserId = hostHolder.getUser() != null ? hostHolder.getUser().getId() : 0;
        List<ViewObject> vos = new ArrayList<>();
        List<News> newsList = newsService.getLatestNews(userId, offset, limit);
        for (News news : newsList) {
            ViewObject vo = new ViewObject();
            vo.set("news", news);
            vo.set("user", userService.getUser(news.getUserId()));
            if (localUserId != 0) {
                int likeOrNot = likeService.getLikeStatus(localUserId, EntityType.ENTITY_NEWS, news.getId());
                vo.set("like", likeOrNot);
            } else {
                vo.set("like", 0);
            }
            vos.add(vo);
        }
        return vos;
    }

    @RequestMapping(path = {"/", "/index"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String index(Model model) {
        model.addAttribute("vos", getNews(0, 0, 10));
//        System.out.println("After:"+model.asMap());
        return "home";
    }


    @RequestMapping(path = {"/user/{userId}"}, method = {RequestMethod.GET, RequestMethod.POST})
    public String userIndex(Model model, @PathVariable("userId") int userId,
                            @RequestParam(value = "pop", defaultValue = "0") int pop) {
        model.addAttribute("vos", getNews(userId, 0, 10));
        model.addAttribute("pop", pop);
        return "home";
    }

//    @RequestMapping("/hello")
//    @ResponseBody
//    public String hello(){
//        return "你好";
//    }

//    @RequestMapping(path = {"/test"}, method = {RequestMethod.GET, RequestMethod.POST})
//    public String test(Model model) {
//        News news = newsService.selectById(10);
//        model.addAttribute("vos",news);
//        return "home";
//    }

//    @RequestMapping("/test")
//    @ResponseBody
//    public List test() {
//        return newsService.getLatestNews(0, 0, 10);
//    }
}

