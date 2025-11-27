package common.utils.msgqueue.producer;

import common.utils.msgqueue.queue.MultiTopicGroupQueue;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * @author fengxi
 * @className MemProducer
 * @description
 * @date 2025年11月21日 15:05
 */
@ConditionalOnProperty(name = "callLog.dealType", havingValue = "memory")
@Component
public class MemMsgProducer implements MsgProducer {

    @Override
    public void produce(String topic, String data) {

        MultiTopicGroupQueue.getQueue(topic).produce(data);
    }
}
