package common.utils.designpattern.chain.impl;

import cn.hutool.core.thread.lock.LockUtil;
import common.utils.designpattern.chain.BaseAlertHandler;
import common.utils.designpattern.chain.entity.AlertMsg;
import common.utils.designpattern.chain.entity.Config;
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

    @Value("${alert.rect.cacheCleanSize:100}")
    private Long cacheCleanSize;

    public UnrepeatableHandler(Config configs, RedisTemplate<String, String> redisTemplate) {
        super(configs);
        this.operations = redisTemplate.opsForZSet();
    }

    @Override
    public boolean handle(AlertMsg msg) {
        return true;
    }


}
