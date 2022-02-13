package com.slack.slack.config;

import com.slack.slack.common.messageBroker.kafka.sender.KafkaMessageSender;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@RequiredArgsConstructor
@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String BOOTSTRAP_SERVERS;

    @Value("${spring.kafka.confluent.sasl-jaas-config}")
    private String SASL_JAAS_CONFIG;

    @Value("${spring.kafka.confluent.security-protocol}")
    private String SECURITY_PROTOCOL;

    @Value("${spring.kafka.confluent.sasl-mechanism}")
    private String SASL_MECHANISM;

    private Properties getSenderProperties() {
        Properties configs = new Properties();

        // 필수 값
        configs.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        configs.put("security.protocol", SECURITY_PROTOCOL);
        configs.put("sasl.mechanism", SASL_MECHANISM);
        configs.put("sasl.jaas.config", SASL_JAAS_CONFIG);

        return configs;
    }

    @Bean
    public KafkaMessageSender messageSender() {
        KafkaMessageSender stringMessageSender = new KafkaMessageSender(getSenderProperties());
        return stringMessageSender;
    }

}
