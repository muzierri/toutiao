package com.nowcoder.controller;

import com.nowcoder.model.*;
import com.nowcoder.service.*;
import com.nowcoder.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author: liyang
 * @Date: Create in 20:13 2019/7/8
 * @Modified By
 */
@Controller
public class NewsController {
    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);
    @Autowired
    private NewsService newsService;
    @Autowired
    private UserService userService;
    @Autowired
    private QiniuService qiniuService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private CommentService commentService;
    @Autowired
    private LikeService likeService;

    @RequestMapping(path = "/news/{newsId}", method = RequestMethod.GET)
    public String newsDetail(@PathVariable("newsId") int newsId, Model model) {
        try {
            News news = newsService.getById(newsId);
            if (news != null) {
                int localUserId = hostHolder.getUser() != null ? hostHolder.getUser().getId() : 0;
                if (localUserId != 0) {
                    int likeOrNot = likeService.getLikeStatus(localUserId, EntityType.ENTITY_NEWS, newsId);
                    model.addAttribute("like", likeOrNot);
                } else {
                    model.addAttribute("like", 0);
                }
                //评论
                List<Comment> comments = commentService.getCommentsByEntity(news.getId(), EntityType.ENTITY_NEWS);
                List<ViewObject> commentVOs = new ArrayList<ViewObject>();
                for (Comment comment : comments) {
                    ViewObject commentVO = new ViewObject();
                    commentVO.set("comment", comment);
                    commentVO.set("user", userService.getUser(comment.getUserId()));
                    commentVOs.add(commentVO);
                }
                model.addAttribute("comments", commentVOs);
            }
            model.addAttribute("news", news);
            model.addAttribute("owner", userService.getUser(news.getUserId()));
        } catch (Exception e) {
            logger.error("读取资讯明细错误" + e.getMessage());
        }

        return "detail";
    }

    @RequestMapping(path = {"/addComment"}, method = RequestMethod.POST)
    public String addComment(@RequestParam("newsId") int newsId,
                             @RequestParam("content") String content) {
        try {
            //content = HtmlUtils.htmlEscape(content);
            //怎么过滤content,敏感词过滤
            Comment comment = new Comment();
            comment.setEntityId(newsId);
            comment.setEntityType(EntityType.ENTITY_NEWS);
            comment.setUserId(hostHolder.getUser().getId());
            comment.setContent(content);
            comment.setCreatedDate(new Date());
            comment.setStatus(0);
            commentService.addComment(comment);
            //更新news中的评论数量
            int count = commentService.getCommentCount(comment.getEntityId(), comment.getEntityType());
            newsService.updateCommentCount(comment.getEntityId(), count);
        } catch (Exception e) {
            logger.error("插入评论失败" + e.getMessage());
        }
        //怎么异步化
        return "redirect:/news/" + String.valueOf(newsId);//重定向回该资讯详情页
    }

    @RequestMapping(path = "/image", method = RequestMethod.GET)
    @ResponseBody
    public void getImage(@RequestParam("name") String imageName,
                         HttpServletResponse response) {
        try {
            response.setContentType("image/jpeg");
            StreamUtils.copy(new FileInputStream(new File(ToutiaoUtil.IMAGE_DIR + imageName)),
                    response.getOutputStream());
        } catch (Exception e) {
            logger.error("读取图片错误：" + e.getMessage());
        }
    }

    //上传图片
    @RequestMapping(path = "/uploadImage/", method = RequestMethod.POST)
    @ResponseBody
    public String uploadImage(@RequestParam("file") MultipartFile file) {
        try {
//            String fileUrl = newsService.saveImage(file);
            String fileUrl = qiniuService.saveImage(file);
            if (fileUrl == null) {
                return ToutiaoUtil.getJSONString(1, "上传图片失败");
            }
            return ToutiaoUtil.getJSONString(0, fileUrl);
        } catch (Exception e) {
            logger.error("上传图片失败" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "上传失败");
        }
    }

    //添加资讯
    @RequestMapping(path = "/user/addNews/", method = RequestMethod.POST)
    @ResponseBody
    public String addNews(@RequestParam("image") String image,
                          @RequestParam("title") String title,
                          @RequestParam("link") String link) {
        try {
            News news = new News();
            if (hostHolder.getUser() != null) {
                news.setUserId(hostHolder.getUser().getId());
            } else {
                //匿名id
                news.setUserId(3);
            }
            news.setImage(image);
            news.setCreatedDate(new Date());
            news.setTitle(title);
            news.setLink(link);
            newsService.addNews(news);
            return ToutiaoUtil.getJSONString(0);
        } catch (Exception e) {
            logger.error("添加资讯错误：" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "发布失败");
        }
    }

}
