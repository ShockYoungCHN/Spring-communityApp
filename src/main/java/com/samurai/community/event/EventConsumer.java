package com.samurai.community.event;

import com.alibaba.fastjson.JSONObject;

import com.samurai.community.entity.User;
import com.samurai.community.service.MessageService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import com.samurai.community.entity.DiscussPost;
import com.samurai.community.entity.Event;
import com.samurai.community.entity.Message;
import com.samurai.community.service.DiscussPostService;
/*import com.samurai.community.service.ElasticsearchService;
import com.samurai.community.service.MessageService;*/
import com.samurai.community.util.CommunityConstant;
import com.samurai.community.util.CommunityUtil;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

public class EventConsumer implements CommunityConstant{
    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private MessageService messageService;

    @KafkaListener(topics={TOPIC_COMMENT,TOPIC_LIKE,TOPIC_FOLLOW})
    public void handleCommentMessage(ConsumerRecord record){
        if(record==null || record.value()==null){
            logger.error("msg is null");
            return;
        }

        Event event=JSONObject.parseObject(record.value().toString(),Event.class);

        if(event==null){
            logger.error("msg format is wrong");
            return;
        }

        Message message = new Message();

        User sysUser=new User();
        sysUser.setId(SYSTEM_USER_ID);
        message.setFromUser(sysUser);

        User u=new User();
        u.setId(event.getEntityUserId());
        message.setToUser(u);

        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());


        Map<String, Object> content = new HashMap<>(16);
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());

        if (!event.getData().isEmpty()) {
            content.putAll(event.getData());
/*            for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }*/
        }
        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }
}
