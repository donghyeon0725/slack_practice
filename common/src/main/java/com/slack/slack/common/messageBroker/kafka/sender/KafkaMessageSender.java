package com.slack.slack.common.messageBroker.kafka.sender;

import com.slack.slack.common.messageBroker.Message;
import com.slack.slack.common.messageBroker.MessageSender;
import com.slack.slack.common.messageBroker.kafka.common.ProducerShutdownThread;
import com.slack.slack.common.messageBroker.kafka.common.code.Topic;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.util.Properties;

@Slf4j
public class KafkaMessageSender implements MessageSender<String, String> {

    @Value("${spring.kafka.bootstrap.servers}")
    private String BOOTSTRAP_SERVERS;

    @Value("${spring.kafka.confluent.sasl-jaas-config}")
    private String SASL_JAAS_CONFIG;

    @Value("${spring.kafka.confluent.security-protocol}")
    private String SECURITY_PROTOCOL;

    @Value("${spring.kafka.confluent.sasl-mechanism}")
    private String SASL_MECHANISM;


    // TODO 여기에 Properties 값이 안전하게 들어가는지 확인하기
    private KafkaProducer<String, String> producer = new KafkaProducer<>(getProperties());

    @PostConstruct
    public void initialize(){
        // 쓰레드 안전 종료를 위한 코드
        Runtime.getRuntime().addShutdownHook(new ProducerShutdownThread(producer));
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

    private Properties getProperties() {
        // 프로퍼티 생성
        Properties configs = new Properties();

        // 필수 값
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        configs.put("security.protocol", SECURITY_PROTOCOL);
        configs.put("sasl.mechanism", SASL_MECHANISM);
        configs.put("sasl.jaas.config", SASL_JAAS_CONFIG);

        return configs;
    }
}
