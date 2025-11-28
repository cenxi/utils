package common.utils.msgqueue.consumer;

import java.util.function.Consumer;

/**
 * @author fengxi
 * @className Consumer
 * @description
 * @date 2025年11月21日 15:51
 */
public interface MsgConsumer {

    default void registerGroups(String topic,String[] groups){}

    void consume(String topic, String group, Consumer<String> logConsumer);
}
