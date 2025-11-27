package common.utils.msgqueue.test;

import common.utils.msgqueue.consumer.MsgConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author fengxi
 * @className TestConsumerLog
 * @description
 * @date 2025年11月26日 15:09
 */
@Component
public class TestConsumerLog {

    @Autowired
    MsgConsumer msgConsumer;

    String topic = "test-topic";

    Map<String, Consumer<String>> groupHandlerMap = new HashMap<>();
    @Autowired
    TestLogHandler testLogHandler;

    @PostConstruct
    public void init() {
        initGroupHandler();
    }

    public void consume() {
        for (Map.Entry<String, Consumer<String>> groupHandler : groupHandlerMap.entrySet()) {
            msgConsumer.startConsume(topic, groupHandler.getKey(), groupHandler.getValue());
        }
    }

    private void initGroupHandler() {

//        groupHandlerMap.put("group1", msg -> testLogHandler.printLog(msg));
        groupHandlerMap.put("group15", msg -> testLogHandler.printLog(msg));

        msgConsumer.registerGroups(topic,groupHandlerMap.keySet().toArray(new String[0]));
    }


}
