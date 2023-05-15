package common.utils.designpattern.chain.util;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 同步锁工具
 */
@Slf4j
@Component
@Lazy
public class LockUtil {

    private final RedissonClient lock;

    public LockUtil(RedissonClient lock){
        this.lock = lock;
    }

    /**
     * 指定分布式锁执行call方法
     * @param key 锁名称
     * @param call 执行方法
     * @param <V> 返回类型
     * @return 返回值
     */
    public <V> V syncExecute(String key, Callable<V> call){
        return syncExecute(key, 1, call);
    }

    /**
     * 指定分布式锁执行call方法
     * @param key 锁名称
     * @param waiteSeconds 锁过期秒数
     * @param call 执行方法
     * @param <V> 返回类型
     * @return 返回值
     */
    public <V> V syncExecute(String key, long waiteSeconds, Callable<V> call){
        RLock rLock = this.lock.getLock(key);
        try {
            rLock.tryLock(waiteSeconds, TimeUnit.SECONDS);
            return call.call();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("tryLock interrupt error:{}", e.getLocalizedMessage());
        } catch (Exception e) {
            throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
        } finally {
            rLock.unlock();
        }
        return null;
    }

    /**
     * 指定分布式锁执行run方法
     * @param key 锁名称
     * @param run 执行方法
     */
    public void syncExecute(String key, Runnable run){
        syncExecute(key, 1, run);
    }

    /**
     * 指定分布式锁执行run方法
     * @param key 锁名称
     * @param waiteSeconds 锁过期秒数
     * @param run 执行方法
     */
    public void syncExecute(String key, long waiteSeconds, Runnable run){
        RLock rLock = this.lock.getLock(key);
        try {
            rLock.tryLock(waiteSeconds, TimeUnit.SECONDS);
            run.run();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("tryLock interrupt error:{}", e.getLocalizedMessage());
        } finally {
            rLock.unlock();
        }
    }

}
