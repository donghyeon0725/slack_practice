package com.slack.slack.common.messageBroker;

import com.slack.slack.common.messageBroker.kafka.common.code.Topic;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 메세지
 * */
@Data
@AllArgsConstructor(staticName = "of")
public class Message<K, V> {
    private Topic topic;
    private K messageKey;
    private V messageValue;
}
