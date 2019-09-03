package com.nowcoder.async;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nowcoder.model.EntityType;
import com.nowcoder.util.JedisAdapter;
import com.nowcoder.util.RedisKeyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: liyang
 * @Date: Create in 17:04 2019/7/23
 * @Modified By
 */
@Service
public class EventConsumer implements InitializingBean, ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);
    private ApplicationContext applicationContext;
    //可以通过 事件类型找出对应的一批事件处理器
    private Map<EventType, List<EventHandler>> config = new HashMap<EventType, List<EventHandler>>();


    @Autowired
    private JedisAdapter jedisAdapter;

    @Override
    public void afterPropertiesSet() throws Exception {
        //找出所有实现了EventHandler接口的类
        Map<String, EventHandler> beans = applicationContext.getBeansOfType(EventHandler.class);
        //System.out.println(beans);
        if (!beans.isEmpty()) {
            for (Map.Entry<String, EventHandler> entry : beans.entrySet()) {
                List<EventType> eventTypes = entry.getValue().getSupportEventTypes();
                for (EventType type : eventTypes) {
                    if (!config.containsKey(type)) {
                        config.put(type, new ArrayList<EventHandler>());
                    }
                    //注册每个事件的处理函数
                    config.get(type).add(entry.getValue());
                }
            }
        } else {
            logger.error("beans没有内容");
        }
        //启动线程去消费事件
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    String key = RedisKeyUtil.getEventQueueKey();
                    List<String> events = jedisAdapter.brpop(0, key);//一直等待,阻塞式
                    for (String event : events) {
                        if (event.equals(key)) {
                            continue;
                        }
                        EventModel model = JSON.parseObject(event, EventModel.class);//反序列化
                        if (!config.containsKey(model.getType())) {
//                            System.out.println(config);
//                            System.out.println(model.getType());
                            logger.error("不能识别的事件");
                            continue;
                        }
                        // 找到这个事件的handler列表
                        List<EventHandler> eventHandlers = config.get(model.getType());
                        for (EventHandler handler : eventHandlers) {
                            handler.doHandle(model);
                        }
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        //在Spring中，如果Bean对象实现了ApplicationContextAware接口,
        //则Spring在完成Bean的初始化后,会将ApplicationContext上下文对象注入至该Bean对象中,
        //注入方法为调用Bean的setApplicationContext方法
    }
}
