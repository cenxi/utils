package common.utils.bloomfilter;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

/**
 * @author :fengxi
 * @date :Created in 2021/5/17 2:26 下午
 * @description：
 * @modified By:
 */
public class BloomFilterFactory {

    /**
     *
     * @param config                    redis配置
     * @param falsePositiveProbability  误判概率
     * @param expectedNumberOfElements  预期最大存储的元素
     * @param <E>
     * @return
     */
    public <E> BloomFilter<E> createBloomFilter(BloomFilterConfig config, double falsePositiveProbability, int expectedNumberOfElements) {
        BloomFilter<E> filter = new BloomFilter<>(0.0001, 1000 * 1000);
        Config redissonConfig = new Config();
        redissonConfig.useSingleServer()
                .setAddress(config.getRedisAddr())
                .setPassword(config.getRedisPassword())
                .setDatabase(config.getDbNum())
                .setConnectionPoolSize(100);
        RedissonClient redissonClient = Redisson.create(redissonConfig);
        filter.bind(new RedissonBitSet(redissonClient, "bloomfilter:test"));

        return filter;
    }
}
