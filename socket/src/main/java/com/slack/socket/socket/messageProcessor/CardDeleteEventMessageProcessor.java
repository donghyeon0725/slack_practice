package com.slack.socket.socket.messageProcessor;

import com.slack.slack.common.messageBroker.kafka.MessageProcessor;
import com.slack.socket.socket.SubscriptionHub;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;

@Component(value = "CardDeleteEventMessageProcessor")
public class CardDeleteEventMessageProcessor implements MessageProcessor<String, String> {
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
