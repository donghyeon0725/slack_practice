package com.slack.slack.common.messageBroker.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * 실제 토픽으로 들어온 메세지를 받아 처리하는 로직
 * */
public interface MessageProcessor<K, V> {
    void onMessage(ConsumerRecord<K, V> record);
}
