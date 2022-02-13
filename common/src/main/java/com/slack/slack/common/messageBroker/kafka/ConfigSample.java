package com.slack.slack.common.messageBroker.kafka;

import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.slack.slack.common.messageBroker.kafka.common.code.Topic;
import com.slack.slack.common.messageBroker.kafka.sender.KafkaMessageSender;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * 설정 샘플입니다.
 * */
//@RequiredArgsConstructor
//@Configuration
public class ConfigSample {

//    @Autowired
//    @Qualifier("LogoutMessageMessageProcessor")
//    private final MessageProcessor processor;
//
//    // 리스너 등록하기
//    @Bean
//    public void messageListener() {
//        Topic topic = Topic.ADMIN_LOGOUT_EVENT;
//        MessageListener messageListener = new MessageListenerImpl(topic, processor, getListenerProperties(topic));
//        messageListener.startListening();
//    }
//
    // 샌더 등록하기
//    @Bean
//    public KafkaMessageSender messageSender() {
//        KafkaMessageSender stringMessageSender = new KafkaMessageSender(getSenderProperties());
//        return stringMessageSender;
//    }


    // 리스너 설정 (consumer)
//    @Value("${spring.kafka.bootstrap-servers}")
//    private String BOOTSTRAP_SERVERS;
//
//    @Value("${spring.kafka.confluent.sasl-jaas-config}")
//    private String SASL_JAAS_CONFIG;
//
//    @Value("${spring.kafka.confluent.security-protocol}")
//    private String SECURITY_PROTOCOL;
//
//    @Value("${spring.kafka.confluent.sasl-mechanism}")
//    private String SASL_MECHANISM;
//
//    @Bean
//    private Properties getListenerProperties(Topic topic) {
//        // 프로퍼티 생성
//        Properties configs = new Properties();
//
//        // 필수 값
//        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
//        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
//        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
//        configs.put(ConsumerConfig.GROUP_ID_CONFIG, topic.getGroupID());
//        configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
//
//        // api 사용을 위한 값
//        configs.put("security.protocol", SECURITY_PROTOCOL);
//        configs.put("sasl.mechanism", SASL_MECHANISM);
//        configs.put("sasl.jaas.config", SASL_JAAS_CONFIG);
//
//        return configs;
//    }

    // 샌더 설정 (producer)

//    @Value("${spring.kafka.bootstrap-servers}")
//    private String BOOTSTRAP_SERVERS;
//
//    @Value("${spring.kafka.confluent.sasl-jaas-config}")
//    private String SASL_JAAS_CONFIG;
//
//    @Value("${spring.kafka.confluent.security-protocol}")
//    private String SECURITY_PROTOCOL;
//
//    @Value("${spring.kafka.confluent.sasl-mechanism}")
//    private String SASL_MECHANISM;
//
//    @Bean
//    private Properties getSenderProperties() {
//        // 프로퍼티 생성
//        Properties configs = new Properties();
//
//        // 필수 값
//        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
//        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
//        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
//
//        configs.put("security.protocol", SECURITY_PROTOCOL);
//        configs.put("sasl.mechanism", SASL_MECHANISM);
//        configs.put("sasl.jaas.config", SASL_JAAS_CONFIG);
//
//        return configs;
//    }
}
