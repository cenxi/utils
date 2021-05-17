package common.utils.bloomfilter;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author :fengxi
 * @date :Created in 2021/5/17 2:22 下午
 * @description：
 * @modified By:
 */
@Data
public class BloomFilterConfig {

    private String redisHost;

    private Integer redisPort;

    private Integer dbNum;

    private String redisPassword;

    private String redisAddr;

    public BloomFilterConfig(String redisHost, Integer redisPort, Integer dbNum, String redisPassword) {

        this.redisHost=redisHost;
        this.redisPort=redisPort;
        this.dbNum=dbNum;
        this.redisPassword = redisPassword;
        this.redisAddr = "redis://" + redisHost + ":" + redisPort;
    }

}
