package com.sparta.config;

import com.sparta.domain.bill.service.BillStatusSubscriber;
import com.sparta.domain.chat.service.RedisSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@Configuration
@EnableRedisRepositories
public class RedisConfig {





    /**
     * Redis 메시지 구성 설정
     * @param redisConnectionFactory Redis 연결
     * @param redisSubscriber 수신된 메시지 처리 서비스
     */
    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
        RedisConnectionFactory redisConnectionFactory,
        RedisSubscriber redisSubscriber
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(redisSubscriber, new PatternTopic("chatroom:*"));
        return container;
    }

    // Redis 리스너 설정 추가 (주문 생성 자동삭제)
    @Bean
    public RedisMessageListenerContainer expireEventListener(
        RedisConnectionFactory redisConnectionFactory,
        RedisExpireListener redisExpireListener
    ) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(redisExpireListener, new PatternTopic("__keyevent@*__:expired"));
        return container;
    }

    // Redis 리스너 설정 추가 (결제 관련 알림)
    @Bean
    public RedisMessageListenerContainer redisMessageListener(
        RedisConnectionFactory connectionFactory,
        MessageListenerAdapter messageListenerAdapter,
        ChannelTopic BillStatusChannel) {

        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(messageListenerAdapter, BillStatusChannel);
        return container;
    }

    @Bean
    public MessageListenerAdapter messageListenerAdapter(BillStatusSubscriber subscriber) {
        return new MessageListenerAdapter(subscriber, "onMessage");
    }

    @Bean
    public ChannelTopic BillStatusChannel() {
        return new ChannelTopic("billStatusChannel");
    }
}
