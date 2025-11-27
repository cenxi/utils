package common.utils.msgqueue.queue;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fengxi
 * @className MultiGroupQueue
 * @description
 * @date 2025年11月21日 15:01
 */
public class MultiTopicGroupQueue {

    private static Map<String, BroadcastQueue> multiTopicQueue = new ConcurrentHashMap<>();

    public static BroadcastQueue getQueue(String topic) {

        return multiTopicQueue.computeIfAbsent(topic, k -> new BroadcastQueue());
    }
}
