package common.utils.designpattern.chain.impl;

import common.utils.designpattern.chain.BaseAlertHandler;
import common.utils.designpattern.chain.entity.AlertMsg;
import common.utils.designpattern.chain.entity.Config;
import common.utils.designpattern.chain.util.LockUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 报警去重处理器
 */
@Slf4j
@Component
public class UnrepeatableHandler extends BaseAlertHandler {

    private final ZSetOperations<String, String> operations;

    private final LockUtil lock;

    @Value("${alert.rect.cacheCleanSize:100}")
    private Long cacheCleanSize;

    public UnrepeatableHandler(Config configs, RedisTemplate<String, String> redisTemplate, LockUtil lock) {
        super(configs);
        this.operations = redisTemplate.opsForZSet();
        this.lock = lock;
    }

    @Override
    public boolean handle(AlertMsg msg) {
        ConfigUnrepeatableDo unrepeatableConfig = configs.unrepeatableConfig(msg.getModelVersionId());
        if (Objects.isNull(unrepeatableConfig) || ConfigStatus.OFF.equals(unrepeatableConfig.getConfigStatus())){
            return true;
        }
        boolean isRepeat = isRepeatCheck(msg, unrepeatableConfig);
        log.debug("isRepeat={}, modelId={}, deviceId={} rect={}", isRepeat, msg.getModelId(), msg.getDeviceId(), JsonUtil.toJsonString(msg.getRect()));
        return !isRepeat;
    }

    /**
     * 重复检测
     * @param alert 报警消息
     * @param config 去重配置
     * @return boolean
     */
    private boolean isRepeatCheck(AlertMsg alert, ConfigUnrepeatableDo config){
        String key = AlertIdUtil.imageRectRedisKey(alert.getModelId(), alert.getDeviceId());
        long currentAlertTime = TimeUtils.toTimestamp(alert.getAlertTime());
        long leftmostRepeatTime = currentAlertTime - TimeUnit.SECONDS.toMillis(config.getCheckIntervalSecond());
        long rightmostRepeatTime = currentAlertTime + TimeUnit.SECONDS.toMillis(config.getCheckIntervalSecond());
        String lockKey = AlertIdUtil.lockKey(key);
        return lock.syncExecute(lockKey, () -> {
            Set<String> set = operations.rangeByScore(key, leftmostRepeatTime, rightmostRepeatTime);
            boolean isRepeat = Optional.ofNullable(set)
                    .orElse(Collections.emptySet())
                    .stream().anyMatch(r -> {
                        ImageRect r1 = JsonUtil.toObject(r, ImageRect.class);
                        ImageRect r2 = alert.getRect();
                        return ImageRect.iou(r1, r2) >= config.getIou();
                    });
            if (!isRepeat){
                operations.add(key, JsonUtil.toJsonString(alert.getRect()), currentAlertTime);
            }
            if (Optional.ofNullable(operations.size(key)).orElse(0L) > cacheCleanSize) {
                operations.removeRangeByScore(key, 0, leftmostRepeatTime);
            }
            return isRepeat;
        });
    }


}
