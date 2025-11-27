package common.utils.msgqueue.test;

import common.utils.msgqueue.producer.MsgProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author fengxi
 * @className TestProduceLog
 * @description
 * @date 2025年11月26日 15:12
 */
@Slf4j
@Component
public class TestProduceLog {

    @Autowired
    MsgProducer msgProducer;

    String topic = "test-topic";

    public void produce(String msg) {
        msgProducer.produce(topic, msg);
    }

}
