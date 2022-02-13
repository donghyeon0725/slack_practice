package com.slack.socket.socket.messageProcessor;

import com.slack.slack.common.messageBroker.kafka.MessageProcessor;
import com.slack.slack.common.socket.channel.Channel;
import com.slack.slack.common.util.JsonUtils;
import com.slack.socket.socket.SubscriptionHub;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;

@Component(value = "CardAddEventMessageProcessor")
public class CardAddEventMessageProcessor implements MessageProcessor<String, String> {


    // /team/%s
    @Override
    public void onMessage(ConsumerRecord<String, String> record) {

        String teamChannelKey = record.key();
        String value = record.value();

        SubscriptionHub.send(
            teamChannelKey,
            value
        );
    }
}

