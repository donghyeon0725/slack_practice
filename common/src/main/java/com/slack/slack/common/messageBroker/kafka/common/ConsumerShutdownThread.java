package com.slack.slack.common.messageBroker.kafka.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.KafkaConsumer;

// 안전 종료를 위한 쓰레드
@Slf4j
@RequiredArgsConstructor
public class ConsumerShutdownThread extends Thread {
    private final KafkaConsumer kafkaConsumer;

    @Override
    public void run() {
        log.info("shutdown hook");
        kafkaConsumer.wakeup();
    }
}
