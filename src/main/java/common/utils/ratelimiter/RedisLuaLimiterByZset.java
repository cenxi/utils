package common.utils.ratelimiter;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.lang.generator.SnowflakeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 基于滑动窗口法限制，优点：解决了固定窗口算法在时间临界处，流量超过阙值的情况
 * 基于reids的zset实现
 * @author fengxi
 * @className RedisLuaLimiterByZset
 * @description
 * @date 2023年01月06日 13:46
 */
@Component
public class RedisLuaLimiterByZset {
    private String KEY_PREFIX = "limiter_";
    private String QPS = "4";
    private Snowflake snowflake = new Snowflake(1, 1);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public boolean acquire(String key) {
        long now = System.currentTimeMillis();
        key = KEY_PREFIX + key;
        String oldest = String.valueOf(now - 1_000);
        String score = String.valueOf(now);
        String scoreValue = snowflake.nextIdStr();
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setResultType(Long.class);
        //lua文件存放在resources目录下
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/ratelimiter.lua")));
        return stringRedisTemplate.execute(redisScript, Arrays.asList(key), oldest, score, QPS, scoreValue) == 1;
    }
}
