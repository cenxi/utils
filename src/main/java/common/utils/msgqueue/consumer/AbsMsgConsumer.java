package common.utils.msgqueue.consumer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * @author fengxi
 * @className AbsLogConsumer
 * @description
 * @date 2025年11月26日 15:00
 */
public abstract class AbsMsgConsumer implements MsgConsumer {

    @Override
    public void startConsume(String topic, String group, Consumer<String> logConsumer) {
        ExecutorService es = Executors.newFixedThreadPool(1);
        es.execute(() -> this.consume(topic, group, logConsumer));
    }

    public abstract void consume(String topic, String group, Consumer<String> logConsumer);
}
