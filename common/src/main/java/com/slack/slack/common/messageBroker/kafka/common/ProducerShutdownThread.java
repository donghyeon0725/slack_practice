package com.slack.slack.common.messageBroker.kafka.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;

@Slf4j
@RequiredArgsConstructor
public class ProducerShutdownThread extends Thread {
    private final KafkaProducer kafkaProducer;

    @Override
    public void run() {
        log.info("shutdown hook");
        kafkaProducer.close();
    }
}
