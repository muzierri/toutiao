package com.nowcoder.controller;

import com.nowcoder.model.HostHolder;
import com.nowcoder.model.Message;
import com.nowcoder.model.User;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.ToutiaoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author: liyang
 * @Date: Create in 16:28 2019/7/14
 * @Modified By
 */
@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;

    @RequestMapping(path = "/msg/list", method = RequestMethod.GET)
    public String conversationList(Model model) {
        try {
            int localUserId = hostHolder.getUser().getId();
            List<Message> conversationList = messageService.getConversationList(localUserId, 0, 10);
            List<ViewObject> conversationVOs = new ArrayList<ViewObject>();
            for (Message msg : conversationList) {
                ViewObject vo = new ViewObject();
                vo.set("conversation", msg);
                int targetUserId = msg.getFromId() == localUserId ? msg.getToId() : msg.getFromId();
                User user = userService.getUser(targetUserId);//得到跟登录用户交互的用户
                vo.set("user", user);
                vo.set("unread", messageService.getConversationUnreadCount(localUserId, msg.getConversationId()));
                conversationVOs.add(vo);
            }
            model.addAttribute("conversations", conversationVOs);
        } catch (Exception e) {
            logger.error("获取站内信列表失败" + e.getMessage());
        }
        return "letter";
    }

    @RequestMapping(path = "/msg/detail", method = {RequestMethod.GET})
    public String conversationDetail(Model model, @RequestParam("conversationId") String conversationId) {
        try {
            List<Message> conversationList = messageService.getConversationDetail(conversationId, 0, 10);
            List<ViewObject> messageVOs = new ArrayList<>();
            for (Message msg : conversationList) {
                ViewObject vo = new ViewObject();
                vo.set("message", msg);
                User user = userService.getUser(msg.getFromId());
                if (user == null) {
                    continue;//不存在此用户，直接进入下次循环
                }
                vo.set("headUrl", user.getHeadUrl());
                vo.set("userId", user.getId());
                messageVOs.add(vo);
            }
            model.addAttribute("messages", messageVOs);
            //点进详情页后，清除未读数字
            //...
        } catch (Exception e) {
            logger.error("获取消息详情失败" + e.getMessage());
        }
        return "letterDetail";
    }

    @RequestMapping(path = "/msg/addMessage", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("fromId") int fromId,
                             @RequestParam("toId") int toId,
                             @RequestParam("content") String content) {
        try {
            Message message = new Message();
            message.setFromId(fromId);
            message.setToId(toId);
            message.setContent(content);
            message.setCreatedDate(new Date());
            message.setConversationId(fromId < toId ? String.format("%d_%d", fromId, toId) : String.format("%d_%d", toId, fromId));
            messageService.addMessage(message);

            return ToutiaoUtil.getJSONString(message.getId());
        } catch (Exception e) {
            logger.error("发送信息失败" + e.getMessage());
            return ToutiaoUtil.getJSONString(1, "发送信息失败");
        }

    }
}
