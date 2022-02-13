package com.slack.slack.common.messageBroker.kafka.listener;

import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import com.slack.slack.common.messageBroker.MessageListener;
import com.slack.slack.common.messageBroker.kafka.common.RebalanceListener;
import com.slack.slack.common.messageBroker.kafka.common.ConsumerShutdownThread;
import com.slack.slack.common.messageBroker.kafka.MessageProcessor;
import com.slack.slack.common.messageBroker.kafka.common.code.Topic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PreDestroy;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 이 빈을 주입받아서 startListening 호출
 *
 * 커밋과, 리밸런싱 대처, 예외상황 등을 처리합니다.
 * 비즈니스 로직을 포함하지 않기 때문에 재사용 가능합니다.
 *
 * 필요에 따라 Processor 를 생성해서 MessageListenerImpl 에 생성자로 주입해준 뒤, 빈으로 등록하면 사용이 가능합니다.
 * */
@Slf4j
public class MessageListenerImpl implements MessageListener {

    private final Topic topic;

    private final MessageProcessor listener;

    private final Properties properties;

    private KafkaConsumer<String, String> consumer;

    public MessageListenerImpl(Topic topic, MessageProcessor listener, Properties properties) {
        this.topic = topic;
        this.listener = listener;
        this.properties = properties;
        consumer = new KafkaConsumer<>(properties);
    }

    @PreDestroy
    public void destory() {
        if (consumer != null) {
            consumer.close();
        }
    }

    @Override
    public void startListening() {
        Map<TopicPartition, OffsetAndMetadata> currentOffset = new HashMap<>();

        consumer.subscribe(Arrays.asList(topic.getTopicName()), new RebalanceListener(consumer, currentOffset));

        // 쓰레드 안전 종료를 위한 코드
        Runtime.getRuntime().addShutdownHook(new ConsumerShutdownThread(consumer));

        try {

            while (true) {
                // 반복 가능한 객체로 되어 있음
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));

                for (ConsumerRecord<String, String> record : records) {
                    // ConsumerRecord(topic = hello.kafka, partition = 2, leaderEpoch = 0, offset = 2, CreateTime = 1643813098898, serialized key size = -1, serialized value size = 11, headers = RecordHeaders(headers = [], isReadOnly = false), key = null, value = testMessage)
                    log.info("record: {}", record);

                    // 예외시 커밋을 위한 정보 갱신 (리밸런싱 발생시 대비 코드)
                    currentOffset.put(
                            new TopicPartition(record.topic(), record.partition()),
                            new OffsetAndMetadata(record.offset() + 1, null)
                    );

                    listener.onMessage(record);
                }

                // 대량 커밋
                consumer.commitAsync((offsets, exception) -> {
                    if (exception != null) {
                        log.error("commit fail {}", offsets, exception);
                        return;
                    }

//                    log.info("commit succeeded");
                });
            }
        } catch (WakeupException e) {
            log.warn("wakeup consumer");
            // 리소스 종료 처리하기
        } finally {
            consumer.close();
        }

    }
}
