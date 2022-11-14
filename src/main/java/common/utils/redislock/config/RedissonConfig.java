package common.utils.redislock.config;

import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class RedissonConfig {

    @Autowired
    RedisProperties redisProperties;

    private static final String REDIS_PRE = "redis://";

    private static final String DEFAULT = "default";

    private static final String COMMA = ",";

    @Configuration
    @ConditionalOnClass({Redisson.class})
    @ConditionalOnExpression("'${spring.redis.mode}'=='single' or '${spring.redis.mode}'=='cluster' or '${spring.redis.mode}'=='sentinel'")
    protected class RedissonSingleClientConfiguration {

        /**
         * 单机模式 redisson 客户端
         *
         * @return RedissonClient
         */
        @Bean(name = "redissonClient")
        @ConditionalOnProperty(name = "spring.redis.mode", havingValue = "single")
        RedissonClient redissonSingle() {
            Config config = new Config();
            String node = redisProperties.getSingle().getAddress();
            node = node.startsWith(REDIS_PRE) ? node : REDIS_PRE + node;

            SingleServerConfig serverConfig = config.useSingleServer()
                    .setDatabase(redisProperties.getDatabase())
                    .setAddress(node)
                    .setTimeout(redisProperties.getPool().getConnTimeout())
                    .setConnectionPoolSize(redisProperties.getPool().getMaxActive())
                    .setConnectionMinimumIdleSize(redisProperties.getPool().getMinIdle());
            if (StringUtils.isNotBlank(redisProperties.getPassword())) {
                serverConfig.setPassword(redisProperties.getPassword());
            }
            config.setCodec(new JacksonCodec());
            RedissonClient redissonClient = Redisson.create(config);
            RedissonClientCache.set(DEFAULT, redissonClient);
            return redissonClient;
        }


        /**
         * 集群模式的 redisson 客户端
         *
         * @return RedissonClient
         */
        @Bean(name = "redissonClient")
        @ConditionalOnProperty(name = "spring.redis.mode", havingValue = "cluster")
        RedissonClient redissonCluster() {
            Config config = new Config();
            String[] nodes = redisProperties.getCluster().getNodes().split(COMMA);
            List<String> newNodes = new ArrayList<>(nodes.length);
            Arrays.stream(nodes).forEach(index -> newNodes.add(
                    index.startsWith(REDIS_PRE) ? index : REDIS_PRE + index));

            ClusterServersConfig serverConfig = config.useClusterServers()
                    .addNodeAddress(newNodes.toArray(new String[0]))
                    .setScanInterval(
                            redisProperties.getCluster().getScanInterval())
                    .setIdleConnectionTimeout(
                            redisProperties.getPool().getConnTimeout())
                    .setConnectTimeout(
                            redisProperties.getPool().getConnTimeout())
                    .setRetryAttempts(
                            redisProperties.getCluster().getFailedAttempts())
                    .setRetryAttempts(
                            redisProperties.getCluster().getRetryAttempts())
                    .setRetryInterval(
                            redisProperties.getCluster().getRetryInterval())
                    .setMasterConnectionPoolSize(redisProperties.getCluster()
                            .getMasterConnectionPoolSize())
                    .setSlaveConnectionPoolSize(redisProperties.getCluster()
                            .getSlaveConnectionPoolSize())
                    .setTimeout(redisProperties.getTimeout());
            if (StringUtils.isNotBlank(redisProperties.getPassword())) {
                serverConfig.setPassword(redisProperties.getPassword());
            }
            config.setCodec(new JacksonCodec());
            RedissonClient redissonClient = Redisson.create(config);
            RedissonClientCache.set(DEFAULT, redissonClient);
            return redissonClient;
        }

        /**
         * 哨兵模式 redisson 客户端
         *
         * @return RedissonClient
         */

        @Bean(name = "redissonClient")
        @ConditionalOnProperty(name = "spring.redis.mode", havingValue = "sentinel")
        RedissonClient redissonSentinel() {
            Config config = new Config();
            String[] nodes = redisProperties.getSentinel().getNodes().split(COMMA);
            List<String> newNodes = new ArrayList<>(nodes.length);
            Arrays.stream(nodes).forEach(index -> newNodes.add(
                    index.startsWith(REDIS_PRE) ? index : REDIS_PRE + index));

            SentinelServersConfig serverConfig = config.useSentinelServers()
                    .addSentinelAddress(newNodes.toArray(new String[0]))
                    .setMasterName(redisProperties.getSentinel().getMaster())
                    .setDatabase(redisProperties.getDatabase())
                    .setCheckSentinelsList(false)
                    .setReadMode(ReadMode.SLAVE)
                    .setRetryAttempts(redisProperties.getSentinel().getFailMax())
                    .setTimeout(redisProperties.getTimeout())
                    .setMasterConnectionMinimumIdleSize(redisProperties.getPool().getMinIdle())
                    .setSlaveConnectionMinimumIdleSize(redisProperties.getPool().getMinIdle())
                    .setSubscriptionConnectionMinimumIdleSize(redisProperties.getPool().getMinIdle())
                    .setMasterConnectionPoolSize(redisProperties.getPool().getMaxActive())
                    .setSlaveConnectionPoolSize(redisProperties.getPool().getMaxActive())
                    .setSubscriptionConnectionPoolSize(redisProperties.getPool().getMaxActive());

            if (StringUtils.isNotBlank(redisProperties.getPassword())) {
                serverConfig.setPassword(redisProperties.getPassword());
            }
            config.setCodec(new JacksonCodec());
            RedissonClient redissonClient = Redisson.create(config);
            RedissonClientCache.set(DEFAULT, redissonClient);
            return redissonClient;
        }
    }

}
