package com.slack.slack.common.messageBroker;

/**
 * 메세지 key와 value의 타입을 생성 타이밍에 정할 수 있도록 제네릭으로
 * */
public interface MessageSender<K, V> {
    // send message
    void send(Message<K, V> message);

    void send(Message<K, V>... messages);
}
