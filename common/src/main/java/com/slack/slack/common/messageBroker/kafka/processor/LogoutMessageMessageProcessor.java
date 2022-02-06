package com.slack.slack.common.messageBroker.kafka.processor;

import com.slack.slack.common.messageBroker.kafka.MessageProcessor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;

/**
 * 실제 비스니스 로직에 따라서 다르게 처리될 부분입니다.
 * MessageProcessor 는 여러개 등록될 수 있기 때문에 @Component 어노테이션에 value 지정이 필요합니다.
 * */
@Component(value = "LogoutMessageMessageProcessor")
public class LogoutMessageMessageProcessor implements MessageProcessor<String, String> {
    @Override
    public void onMessage(ConsumerRecord<String, String> record) {
        // 메세지 처리
    }
}
