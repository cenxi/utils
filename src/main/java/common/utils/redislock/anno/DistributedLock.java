package common.utils.redislock.anno;

import java.lang.annotation.*;

@Inherited
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {

    /**
     * 自定义加锁名称 ： "lock." + prefix + 请求参数“.”隔开
     * @return 前缀
     */
    String prefix() default "";

    /**
     * 锁最大等待时间
     * @return 最大等待时间
     */
    long maxSleepTime() default 30L;
}
