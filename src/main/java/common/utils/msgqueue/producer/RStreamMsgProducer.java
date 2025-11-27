package common.utils.msgqueue.producer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author fengxi
 * @className RStreamProducer
 * @description
 * @date 2025年11月21日 15:32
 */
@ConditionalOnProperty(name = "callLog.dealType", havingValue = "redis", matchIfMissing = true)
@Slf4j
@Component
public class RStreamMsgProducer implements MsgProducer {

    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public void produce(String topic, String data) {

        Map<String, Object> mapData = null;
        try {
            mapData = JSON.parseObject(data,new TypeReference<Map<String,Object>>(){});
        } catch (Exception e) {
            log.info("not json data->{}", data);
            mapData.put("data", data);
        }
        redisTemplate.opsForStream().add(topic, mapData);
    }
}
