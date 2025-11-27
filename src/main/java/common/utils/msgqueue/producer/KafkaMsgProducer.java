package common.utils.msgqueue.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/**
 * @author fengxi
 * @className KafkaLogProducer
 * @description
 * @date 2025年11月21日 15:46
 */
@ConditionalOnProperty(name = "callLog.dealType", havingValue = "kafka")
@Component
public class KafkaMsgProducer implements MsgProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void produce(String topic, String data) {
        kafkaTemplate.send(topic, data);
    }
}
