package com.nowcoder.async.handler;

import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import com.nowcoder.model.Message;
import com.nowcoder.service.MessageService;
import com.nowcoder.util.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @Description:
 * @Author: liyang
 * @Date: Create in 16:02 2019/7/24
 * @Modified By
 */
@Component
public class LoginExceptionHandler implements EventHandler {
    @Autowired
    private MessageService messageService;
    @Autowired
    private MailSender mailSender;

    @Override
    public void doHandle(EventModel model) {
        //尚未判断是否有异常登录
        Message message = new Message();
        message.setFromId(5);
        message.setToId(model.getActorId());
        message.setContent("你上次的登录IP异常");
        message.setCreatedDate(new Date());
        message.setConversationId(message.getFromId() < message.getToId() ?
                String.format("%d_%d", message.getFromId(), message.getToId()) : String.format("%d_%d", message.getToId(), message.getFromId()));
        messageService.addMessage(message);

        //登录异常时发送邮件
        Map<String, Object> map = new HashMap<>();
        map.put("username", model.getExt("username"));
        mailSender.sendWithHTMLTemplate(model.getExt("email"), "登录异常",
                "mails/welcome.html", map);

    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LOGIN);
    }
}
