package common.utils.msgqueue.consumer;

import cn.hutool.core.util.ObjectUtil;
import common.utils.msgqueue.queue.BroadcastQueue;
import common.utils.msgqueue.queue.MultiTopicGroupQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * @author fengxi
 * @className MemLogConsumer
 * @description
 * @date 2025年11月21日 15:51
 */
@ConditionalOnProperty(name = "callLog.dealType", havingValue = "memory")
@Component
@Slf4j
public class MemMsgConsumer implements MsgConsumer {

    @Override
    public void registerGroups(String topic, String[] groups) {
        if (ObjectUtil.isNotEmpty(groups)) {
            for (String group : groups) {
                MultiTopicGroupQueue.getQueue(topic).registerGroup(group);
            }
        }
    }

    @Override
    public void consume(String topic, String group, Consumer<String> logConsumer) {
        BroadcastQueue queue = MultiTopicGroupQueue.getQueue(topic);
        if (ObjectUtil.isNull(queue)) {
            throw new RuntimeException("消费者组未初始化");
        }

        while (true) {
            try {
                String msg = queue.consume(group);
                logConsumer.accept(msg);
            } catch (Exception e) {
                log.error("Error consuming message: ", e);
            }
        }
    }
}
