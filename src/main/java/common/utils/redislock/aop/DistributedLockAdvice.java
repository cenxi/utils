package common.utils.redislock.aop;

import common.utils.redislock.anno.DistributedLock;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Slf4j
@Aspect
@Component
public class DistributedLockAdvice {

    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;
    private static final String LOCK = "lock.";

    @Autowired
    private RedissonClient redissonClient;

    public DistributedLockAdvice() {
    }

    @Pointcut("@annotation(common.utils.redislock.anno.DistributedLock)")
    private void aspectjMethod() {
    }

    @Before("aspectjMethod()")
    public void beforeAdvice(JoinPoint joinPoint) {
    }

    @After("aspectjMethod()")
    public void afterAdvice(JoinPoint joinPoint) {
    }

    /**
     * aroundAdvice
     *
     * @param pjp pjp
     * @return result
     * @throws Throwable 异常
     */
    @SneakyThrows
    @Around("aspectjMethod()")
    public Object aroundAdvice(ProceedingJoinPoint pjp) {
        Signature sig = pjp.getSignature();
        MethodSignature msig = null;
        if (!(sig instanceof MethodSignature)) {
            throw new IllegalArgumentException();
        }
        msig = (MethodSignature) sig;
        Object target = pjp.getTarget();
        Method method = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());
        Object[] arguments = pjp.getArgs();
        String methodName = getClassAndMethodName(method);
        log.info("分布式锁拦截,方法:{},参数:{}", methodName, arguments);
        DistributedLock annotation = method.getAnnotation(DistributedLock.class);
        String prefix = annotation.prefix();
        long maxSleepTime = annotation.maxSleepTime();
        Object result;
        String lockKey = this.getMsiLockKey(prefix, methodName);
        RLock rLock = redissonClient.getLock(lockKey);
        if (rLock.tryLock(maxSleepTime, TIME_UNIT)) {
            log.info("加锁成功:{}", lockKey);
            try {
                result = pjp.proceed();
            } finally {
                rLock.unlock();
                log.info("释放分布式锁[{}]", lockKey);
            }
        } else {
            log.info("获取锁失败方法:{},参数:{}", methodName, arguments);
            return "获取锁失败";
        }
        return result;
    }

    /**
     * 获取方法类全名+方法名
     * @param method 方法
     * @return 方法名
     */
    private String getClassAndMethodName(Method method) {
        //获取类全名
        String className = method.getDeclaringClass().getName();
        //获取方法名
        String methodName = method.getName();
        return className + "." + methodName;
    }

    /**
     * 获得方法的key
     * @param prefix prefix
     * @param invokedMethod invokedMethod
     * @return String
     */
    private String getMsiLockKey(String prefix, String invokedMethod) {
        return StringUtils.hasText(prefix) ? LOCK + prefix : invokedMethod;
    }
}
