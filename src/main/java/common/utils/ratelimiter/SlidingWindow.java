package common.utils.ratelimiter;

import cn.hutool.core.lang.Snowflake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * @author fengxi
 * @className SlidingWindow
 * @description
 * @date 2024年03月05日 14:44
 */
@Component
public class SlidingWindow {
    
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private final RedisScript<Boolean> rateLimitScript;
    private final Snowflake snowflake = new Snowflake(1, 1);

    public SlidingWindow(){
        DefaultRedisScript<Boolean> redisScript = new DefaultRedisScript<>();
        //lua脚本路径
        redisScript.setLocation(new ClassPathResource("lua/sliding_window_ratelimit.lua"));
        //lua脚本返回值
        redisScript.setResultType(Boolean.class);
        rateLimitScript = redisScript;
    }


    /**
     * 判断行为是否被允许
     *
     * @param key           行为key
     * @param period        限流周期
     * @param maxCount      最大请求次数（滑动窗口大小）
     * @return
     */
    public boolean isActionAllowed(String key, long period, long maxCount) {

        //当前时间戳
        long now = System.currentTimeMillis();
        Boolean isAccess = stringRedisTemplate.execute(
                //lua限流脚本
                rateLimitScript,
//                redisScript,
                //限流资源名称
                Collections.singletonList(key),
                //限流大小
                String.valueOf(maxCount),
                //限流窗口的左区间
                String.valueOf(now - period * 1000),
                //限流窗口的右区间
                String.valueOf(now),
                //id值，保证zset集合里面不重复，不然会覆盖
                snowflake.nextIdStr()
        );
        return isAccess;
    }

}
