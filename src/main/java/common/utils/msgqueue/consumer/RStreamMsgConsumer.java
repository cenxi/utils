package common.utils.msgqueue.consumer;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author fengxi
 * @className RStreamLogConsumer
 * @description
 * @date 2025年11月21日 16:24
 */
@ConditionalOnProperty(name = "callLog.dealType", havingValue = "redis", matchIfMissing = true)
@Component
@Slf4j
public class RStreamMsgConsumer implements MsgConsumer {

    @Autowired
    RedisTemplate redisTemplate;


    @Override
    public void consume(String topic, String group, Consumer<String> logConsumer) {

        consumeMessages(topic, group, group, logConsumer);
    }

    @Override
    public void batchConsume(String topic, String group, int batchSize, Consumer<List<String>> logConsumer) {

    }

    /**
     * 消费 Redis Stream 数据
     *
     * @param streamKey    Redis Stream 的 key
     * @param groupName    消费者组名
     * @param consumerName 消费者名称
     */
    private void consumeMessages(String streamKey, String groupName, String consumerName, Consumer<String> logConsumer) {

        try {
            // 尝试创建消费者组（如果不存在）
            redisTemplate.opsForStream().createGroup(streamKey, ReadOffset.from("0"), groupName);
            log.info("Consumer group created: {},offset 0", groupName);
        } catch (Exception e) {
            // 忽略消费者组已存在的异常
            log.info("Consumer group already exists: {},offset lastConsumed", groupName);
        }
        while (true) {
            // 从 Redis Stream 中读取消息
            List<MapRecord<String, String, String>> messages = redisTemplate.opsForStream()
                    .read(
                            org.springframework.data.redis.connection.stream.Consumer.from(groupName, consumerName),
                            StreamReadOptions.empty().block(Duration.ofMillis(500)).count(10),
                            StreamOffset.create(streamKey, ReadOffset.lastConsumed())
                    );
            if (messages != null && !messages.isEmpty()) {
                for (MapRecord<String, String, String> message : messages) {
                    try {
                        // 处理消息
                        RecordId messageId = message.getId();
                        Map<String, String> value = message.getValue();
                        logConsumer.accept(JSON.toJSONString(value));
                        // 手动确认消息已被处理
                    } catch (Exception e) {
                        log.error("Error processing message: ", e);
                    } finally {
                        redisTemplate.opsForStream().acknowledge(groupName, message);
                    }
                }
            }
            /*try {
                TimeUnit.MILLISECONDS.sleep(500L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }*/
        }
    }

    private void batchConsumeMessages(String streamKey, String groupName, String consumerName, int batchSize, Consumer<List<String>> logConsumer) {

        try {
            // 尝试创建消费者组（如果不存在）
            redisTemplate.opsForStream().createGroup(streamKey, ReadOffset.from("0"), groupName);
            log.info("Consumer group created: {},offset 0", groupName);
        } catch (Exception e) {
            // 忽略消费者组已存在的异常
            log.info("Consumer group already exists: {},offset lastConsumed", groupName);
        }
        while (true) {
            // 从 Redis Stream 中读取消息
            List<MapRecord<String, String, String>> messages = redisTemplate.opsForStream()
                    .read(
                            org.springframework.data.redis.connection.stream.Consumer.from(groupName, consumerName),
                            StreamReadOptions.empty().block(Duration.ofMillis(500)).count(batchSize),
                            StreamOffset.create(streamKey, ReadOffset.lastConsumed())
                    );
            if (ObjectUtil.isNotEmpty(messages)) {
                List<RecordId> recordIds = new ArrayList<>();
                try {
                    List<String> msgs = messages.stream()
                            .map(t -> JSON.toJSONString(t.getValue()))
                            .collect(Collectors.toList());
                    logConsumer.accept(msgs);
                    recordIds = messages.stream().map(MapRecord::getId).collect(Collectors.toList());
                } catch (Exception e) {
                    log.error("Error processing message: ", e);
                } finally {
                    redisTemplate.opsForStream().acknowledge(streamKey, groupName, recordIds.toArray(new RecordId[0]));
                }
            }
        }
    }

    /*// 检查消费者组是否存在
    public boolean isConsumerGroupExists(String streamKey, String groupName) {
        return redisTemplate.execute((RedisCallback<Boolean>) connection -> {
            try {
                List<StreamGroup> groups = connection.streamCommands().xInfoGroups(streamKey.getBytes());
                for (StreamGroup group : groups) {
                    if (groupName.equals(new String(group.getName()))) {
                        return true;
                    }
                }
            } catch (Exception e) {
                System.err.println("Error checking consumer group: " + e.getMessage());
            }
            return false;
        });
    }*/

    // 检查消费者组是否存在
    /*private boolean isConsumerGroupExists(String streamKey, String groupName) {
        StreamInfo.XInfoConsumers consumers = redisTemplate.opsForStream().consumers(streamKey, groupName);
        return consumers != null && !consumers.isEmpty();
    }

    private ReadOffset earliestOffset(String streamKey, String groupName) {
        if (!isConsumerGroupExists(streamKey, groupName)) {
            return ReadOffset.from("0-0");
        }
        return ReadOffset.lastConsumed();
    }*/

}
