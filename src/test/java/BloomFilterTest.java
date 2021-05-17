import common.utils.bloomfilter.BloomFilter;
import common.utils.bloomfilter.BloomFilterConfig;
import common.utils.bloomfilter.BloomFilterFactory;
import common.utils.bloomfilter.RedissonBitSet;
import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

/**
 * @author ：fengxi
 * @date ：Created in 2021/1/5 2:04 下午
 * @description：布隆过滤器测试用例(使用姿势)
 * @modified By：
 */
public class BloomFilterTest {

    @Test
    public void testBloomFilter(){
        System.out.println("布隆过滤器测试");
        String redisHost = "127.0.0.1";
        Integer redisPort=6379;
        String redisPassword = "123456";
        Integer db=5;
        BloomFilter<String> bloomFilter = constructFilter(redisHost, redisPort, redisPassword, db);
    }

    @Test
    public void testBloomFilter1(){
        BloomFilterConfig bloomFilterConfig = new BloomFilterConfig("127.0.0.1", 6379, 10, "123456");
        BloomFilter<String> filter = BloomFilterFactory.createBloomFilter(bloomFilterConfig, 0.0001, 1000 * 1000);

        String testStr = "aaa";
        filter.add(testStr);
        filter.contains(testStr);
    }

    private BloomFilter<String> constructFilter(String redisHost,Integer redisPort,String redisPassword,Integer db) {
        BloomFilter<String> filter = new BloomFilter<>(0.0001, 100000000);
        Config config = new Config();
        String redisIp = "redis://" + redisHost + ":" + redisPort;
        config.useSingleServer()
                .setAddress(redisIp)
                .setPassword(redisPassword)
                .setDatabase(db)
                .setConnectionPoolSize(100);
        RedissonClient redisson = Redisson.create(config);
        filter.bind(new RedissonBitSet(redisson, "bloomfilter:profile"));
        return filter;
    }
}
