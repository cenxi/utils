package common.cache;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 缓存类
 */
@Data
@NoArgsConstructor
public class Cache implements Comparable<Cache> {

    // 键
    private Object key;
    // 缓存值
    private Object value;
    // 最后一次访问时间
    private long accessTime;
    // 创建时间
    private long writeTime;
    // 存活时间
    private long expireTime;
    // 命中次数
    private Integer hitCount;

    public Cache(Object key, Object value, Long expireTime) {
        this.key = key;
        this.value = value;
        this.expireTime = expireTime;
        this.hitCount = 1;
        this.accessTime = System.currentTimeMillis();
        this.writeTime = System.currentTimeMillis();
    }

    @Override
    public int compareTo(Cache o) {
        return hitCount.compareTo(o.hitCount);
    }
}