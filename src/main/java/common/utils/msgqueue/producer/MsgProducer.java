package common.utils.msgqueue.producer;

/**
 * @author fengxi
 * @className Producer
 * @description
 * @date 2025年11月21日 15:05
 */
public interface MsgProducer {

    void produce(String topic, String data);
}
