package common.utils.ftp;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;

/**
 * 连接池
 *
 * <p>连接池</p>
 *
 * @param <T> 连接对象
 */
@Slf4j
public abstract class ConnectionPool<T> {
    //配置对象
    @Autowired
    protected NetgateConfig netgateConfig;

    //默认连接池工具
    private GenericObjectPool<T> clientGenericObjectPool;

    /**
     * 初始化连接池
     */
    @PostConstruct
    public void init() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(netgateConfig.getMaxTotal());
        config.setMaxIdle(netgateConfig.getMaxIdle());
        config.setMinIdle(netgateConfig.getMinIdle());
        config.setMaxWaitMillis(netgateConfig.getMaxWaitMillis());
        config.setMinEvictableIdleTimeMillis(netgateConfig.getMinEvictableIdleTimeMillis());
        config.setTimeBetweenEvictionRunsMillis(netgateConfig.getTimeBetweenEvictionRunsMillis());
        config.setTestOnBorrow(true);
        config.setTestWhileIdle(true);
        config.setTestOnReturn(true);
        config.setBlockWhenExhausted(true);
        AbandonedConfig abandonedConfig = new AbandonedConfig();
        abandonedConfig.setRemoveAbandonedOnBorrow(true);
        abandonedConfig.setRemoveAbandonedOnMaintenance(true);
        clientGenericObjectPool = createGenericObjectPool(config);
        clientGenericObjectPool.setAbandonedConfig(abandonedConfig);
        clientGenericObjectPool.setLifo(false);
    }

    /**
     * 创建连接池
     *
     * @return
     */
    protected abstract GenericObjectPool<T> createGenericObjectPool(GenericObjectPoolConfig config);

    /**
     * 获取一个连接池中的链接
     *
     * @return 链接会话
     * @throws Exception
     */
    public T borrowObject() {
        int l = 1;
        Exception exception = null;
        while (l++ <= 3) {
            try {
                T borrowObject = clientGenericObjectPool.borrowObject();
                return borrowObject;
            } catch (Exception e) {
                exception = e;
            }
        }
        throw new RuntimeException("get connection fail", exception);
    }

    /**
     * 将连接还给连接池
     *
     * @param t 连接会话
     */
    public void returnObject(T t) {
        if (t == null) {
            return;
        }
        clientGenericObjectPool.returnObject(t);
    }

    public void invalidateObject(T t) throws Exception {
        clientGenericObjectPool.invalidateObject(t);
    }

    @Bean
    public GenericObjectPool<T> getGenericObjectPool() {
        return clientGenericObjectPool;
    }
}
