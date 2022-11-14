package common.utils.redislock.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Slf4j
public class RedissonClientCache {

    private static final Map<String, RedissonClient> CLIENT_MAP = new ConcurrentHashMap<>();

    /**
     * 获得redisson客户端
     *
     * @param clientName clientName
     * @return RedissonClient
     * @author dangzerong
     */
    public static RedissonClient get(String clientName) {
        RedissonClient client = (RedissonClient) CLIENT_MAP.get(clientName);
        if (client == null) {
            log.error("获取到redis客户端为空，请检查对应配置文件", new NullPointerException());
        }
        return client;
    }

    /**
     * 保存reisson客户端
     *
     * @param clientName clientName
     * @param client     client
     * @author dangzerong
     */
    public static void set(String clientName, RedissonClient client) {
        CLIENT_MAP.put(clientName, client);
    }

    /**
     * 是否存在redisson客户端
     *
     * @param client client
     * @return boolean
     * @author dangzerong
     */
    public static boolean exist(String client) {
        return CLIENT_MAP.containsKey(client);
    }
}
