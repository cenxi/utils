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
    long waitTime() default 30L;

    /**
     * 锁过期时间，避免程序崩溃，锁一直没释放
     * @return
     */
    long leaseTime() default 30L;
}
