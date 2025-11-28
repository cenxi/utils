package common.utils.msgqueue.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * @author fengxi
 * @className KafkaLogConsumer
 * @description
 * @date 2025年11月21日 16:51
 */
@ConditionalOnProperty(name = "callLog.dealType", havingValue = "kafka")
@Component
@Slf4j
public class KafkaMsgConsumer implements MsgConsumer {

    @Autowired
    ConsumerFactory consumerFactory;

    private List<KafkaConsumer<String, String>> consumers;
    ExecutorService executor = Executors.newFixedThreadPool(4);

    public KafkaMsgConsumer() {
    }

    @Override
    public void consume(String topic, String group, Consumer<String> logConsumer) {

        consumeWithCustomGroup(group, topic, logConsumer);
    }

    private void consumeWithCustomGroup(String groupId, String topic, Consumer<String> logConsumer) {
        // 只指定消费者组，不指定 clientId
        KafkaConsumer<String, String> consumer = (KafkaConsumer<String, String>) consumerFactory.createConsumer(groupId, null);
        consumer.subscribe(Collections.singletonList(topic));

        while (true) {
            try {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    logConsumer.accept(record.value());
                }
            } catch (Exception e) {
                log.error("Error consuming message: ", e);
            } finally {
                consumer.commitSync();
            }
        }
    }

}
