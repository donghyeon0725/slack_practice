package com.slack.slack.common.messageBroker.kafka.sender;

import com.slack.slack.common.messageBroker.Message;
import com.slack.slack.common.messageBroker.MessageSender;
import com.slack.slack.common.messageBroker.kafka.common.ProducerShutdownThread;
import com.slack.slack.common.messageBroker.kafka.common.code.Topic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Properties;

@Slf4j
public class KafkaMessageSender implements MessageSender<String, String> {

    private final Properties properties;
    // TODO 여기에 Properties 값이 안전하게 들어가는지 확인하기
    private KafkaProducer<String, String> producer;

    public KafkaMessageSender(Properties properties) {
        this.properties = properties;
        this.producer = new KafkaProducer<>(properties);
    }

    @PostConstruct
    public void initialize(){
        // 쓰레드 안전 종료를 위한 코드
        Runtime.getRuntime().addShutdownHook(new ProducerShutdownThread(producer));
    }

    @PreDestroy
    public void destory() {
        if (producer != null) {
            producer.close();
        }
    }

    @Override
    public void send(Message<String, String> message) {
        Topic topic = message.getTopic();
        String messageKey = message.getMessageKey();
        String messageValue = message.getMessageValue();

        // 키 있는 레코드
        ProducerRecord<String, String> keyRecord = new ProducerRecord<>(topic.getTopicName(), messageKey, messageValue);
        try {
            producer.send(keyRecord);
            log.info("{}", keyRecord);

            producer.flush();
        } catch (Exception e) {
            throw e;
        }
//        producer.close();
    }

    @Override
    public void send(Message<String, String>... messages) {
        try {
            for (Message<String, String> message : messages) {
                Topic topic = message.getTopic();
                String messageKey = message.getMessageKey();
                String messageValue = message.getMessageValue();

                // 키 있는 레코드
                ProducerRecord<String, String> keyRecord = new ProducerRecord<>(topic.getTopicName(), messageKey, messageValue);

                producer.send(keyRecord);
                log.info("{}", keyRecord);
            }
            producer.flush();
        } catch (Exception e) {
            throw e;
        }
    }


}
