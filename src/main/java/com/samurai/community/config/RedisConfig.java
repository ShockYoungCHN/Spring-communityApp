package com.samurai.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory factory){
        RedisTemplate<String,Object> template=new RedisTemplate<>();
        template.setConnectionFactory(factory);

        //set the method of serializing keys
        template.setKeySerializer(RedisSerializer.string());
        //set the method of serializing values
        template.setValueSerializer(RedisSerializer.json());
        //set the method of serializing keys in hash
        template.setHashKeySerializer(RedisSerializer.string());
        //set the method of serializing values in hash
        template.setHashValueSerializer(RedisSerializer.json());

        template.afterPropertiesSet();
        return template;
    }
}
