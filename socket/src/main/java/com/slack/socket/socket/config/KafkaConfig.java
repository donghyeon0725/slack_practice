package com.slack.socket.socket.config;

import com.slack.slack.common.messageBroker.MessageListener;
import com.slack.slack.common.messageBroker.kafka.MessageProcessor;
import com.slack.slack.common.messageBroker.kafka.common.code.Topic;
import com.slack.slack.common.messageBroker.kafka.listener.MessageListenerImpl;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;


//@RequiredArgsConstructor
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


    private final MessageProcessor cardAddEventMessageProcessor;

    private final MessageProcessor cardDeleteEventMessageProcessor;

    private final MessageProcessor cardRefreshEventMessageProcessor;

    private final MessageProcessor cardUpdateEventMessageProcessor;

    public KafkaConfig(
                        @Autowired
                        @Qualifier("CardAddEventMessageProcessor")
                        MessageProcessor cardAddEventMessageProcessor,
                        @Autowired
                        @Qualifier("CardDeleteEventMessageProcessor")
                        MessageProcessor cardDeleteEventMessageProcessor,
                        @Autowired
                        @Qualifier("CardRefreshEventMessageProcessor")
                        MessageProcessor cardRefreshEventMessageProcessor,
                        @Autowired
                        @Qualifier("CardUpdateEventMessageProcessor")
                        MessageProcessor cardUpdateEventMessageProcessor) {
        this.cardAddEventMessageProcessor = cardAddEventMessageProcessor;
        this.cardDeleteEventMessageProcessor = cardDeleteEventMessageProcessor;
        this.cardRefreshEventMessageProcessor = cardRefreshEventMessageProcessor;
        this.cardUpdateEventMessageProcessor = cardUpdateEventMessageProcessor;
    }

    // 리스너 등록하기
    @Bean
    public void cardAddEventMessageProcessorConfig() {
        Topic topic = Topic.REST_CARD_ADD_EVENT;

        MessageListener messageListener = new MessageListenerImpl(topic, cardAddEventMessageProcessor, getListenerProperties(topic));
        messageListener.startListening();
    }

    @Bean
    public void cardDeleteEventMessageProcessorConfig() {
        Topic topic = Topic.REST_CARD_DELETE_EVENT;

        MessageListener messageListener = new MessageListenerImpl(topic, cardDeleteEventMessageProcessor, getListenerProperties(topic));
        messageListener.startListening();
    }

    @Bean
    public void cardRefreshEventMessageProcessorConfig() {
        Topic topic = Topic.REST_CARD_REFRESH_EVENT;

        MessageListener messageListener = new MessageListenerImpl(topic, cardRefreshEventMessageProcessor, getListenerProperties(topic));
        messageListener.startListening();
    }

    @Bean
    public void cardUpdateEventMessageProcessorConfig() {
        Topic topic = Topic.REST_CARD_UPDATE_EVENT;

        MessageListener messageListener = new MessageListenerImpl(topic, cardUpdateEventMessageProcessor, getListenerProperties(topic));
        messageListener.startListening();
    }

    private Properties getListenerProperties(Topic topic) {
        // 프로퍼티 생성
        Properties configs = new Properties();

        // 필수 값
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, topic.getGroupID());
        configs.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        // api 사용을 위한 값
        configs.put("security.protocol", SECURITY_PROTOCOL);
        configs.put("sasl.mechanism", SASL_MECHANISM);
        configs.put("sasl.jaas.config", SASL_JAAS_CONFIG);

        return configs;
    }
}
