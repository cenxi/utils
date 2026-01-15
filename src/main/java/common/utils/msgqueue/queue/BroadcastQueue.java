package common.utils.msgqueue.queue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author fengxi
 * @className BroadcastQueue
 * @description
 * @date 2025年11月21日 14:08
 */
public class BroadcastQueue {

    // 每个消费者组的队列
    private final Map<String, BlockingQueue<String>> groupQueues = new ConcurrentHashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

    // 注册消费者组
    public void registerGroup(String groupName) {

        lock.writeLock().lock();
        try {
            if (!groupQueues.containsKey(groupName)) {
                groupQueues.put(groupName, new LinkedBlockingQueue<>());
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    // 生产者广播数据到所有消费者组
    public void produce(String data) {
        lock.readLock().lock();
        try {
            for (BlockingQueue<String> queue : groupQueues.values()) {
                queue.add(data);
            }
            System.out.println("Broadcasted: " + data);
        } finally {
            lock.readLock().unlock();
        }
    }

    // 消费者组消费数据
    public String consume(String groupName) {
        BlockingQueue<String> queue = groupQueues.get(groupName);
        if (queue == null) {
            throw new IllegalArgumentException("Consumer group not registered: " + groupName);
        }

        try {
            return queue.take();
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
        return null;
    }

    public List<String> batchConsume(String groupName, int batchSize) {
        BlockingQueue<String> queue = groupQueues.get(groupName);
        if (queue == null) {
            throw new IllegalArgumentException("Consumer group not registered: " + groupName);
        }

        List<String> msgs = new ArrayList<>(batchSize);
        try {
            int drainedCount = queue.drainTo(msgs, batchSize);
            if (drainedCount == 0){
                return Collections.emptyList();
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt();
        }
        return msgs;
    }
}
