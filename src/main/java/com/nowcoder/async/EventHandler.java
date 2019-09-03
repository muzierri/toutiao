package com.nowcoder.async;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description:
 * @Author: liyang
 * @Date: Create in 23:09 2019/7/22
 * @Modified By
 */
@Component
public interface EventHandler {
    void doHandle(EventModel model);
    List<EventType> getSupportEventTypes();
}
