import common.utils.ratelimiter.RedisLuaLimiterByZset;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalTime;

/**
 * @author fengxi
 * @className RateLimiterTest
 * @description
 * @date 2023年01月06日 13:49
 */
public class RateLimiterTest extends BaseTest {

    @Autowired
    RedisLuaLimiterByZset redisLimiter;

    @Test
    public void test() throws InterruptedException {
        for (int i = 0; i < 20; i++) {
            Thread.sleep(200);
            System.out.println(LocalTime.now() + " " + redisLimiter.acquire("user1"));
        }
    }
}
