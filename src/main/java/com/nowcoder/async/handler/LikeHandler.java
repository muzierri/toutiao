package com.nowcoder.async.handler;

import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import com.nowcoder.model.Message;
import com.nowcoder.model.User;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author: liyang
 * @Date: Create in 23:12 2019/7/22
 * @Modified By
 */
@Component
public class LikeHandler implements EventHandler {

    @Autowired
    private MessageService messageService;
    @Autowired
    private UserService userService;

    @Override
    public void doHandle(EventModel model) {
        Message message = new Message();
        message.setFromId(5);//假设5为系统id
        message.setToId(model.getEntityOwnerId());
        //message.setToId(model.getActorId());
        User user = userService.getUser(model.getActorId());
        message.setContent("用户" + user.getName() + "赞了你的资讯" +
                ",http://127.0.0.1:8080/news/" + model.getEntityId());
        message.setCreatedDate(new Date());
        message.setConversationId(message.getFromId() < message.getToId() ?
                String.format("%d_%d", message.getFromId(), message.getToId()) : String.format("%d_%d", message.getToId(), message.getFromId()));
        messageService.addMessage(message);

    }

    @Override
    //只关注点赞的行为
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LIKE);//asList方法的参数必须是对象或者对象数组
    }
}
