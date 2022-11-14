package common.utils.redislock.config;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RedisPoolProperties {

    private int maxIdle = 16;

    private int minIdle = 8;

    private int maxActive = 8;

    private int maxWait = 3000;

    private int connTimeout = 3000;

    private int soTimeout = 3000;


}
