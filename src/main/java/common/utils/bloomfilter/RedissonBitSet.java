package common.utils.bloomfilter;

import org.redisson.Redisson;
import org.redisson.api.RBitSet;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

/**
 * Implement bloom filter on redisson bitset.
 */
public class RedissonBitSet implements BaseBitSet {
  private RedissonClient redissonClient;
  private RBitSet rBitSet;

  private RedissonBitSet() {
  }

  public RedissonBitSet(RedissonClient redissonClient, String name) {
    this.redissonClient = redissonClient;
    this.rBitSet = redissonClient.getBitSet(name);
  }

  /**
   * Create a single model redissonClient.
   *
   * @param ip
   * @param port
   * @param password
   * @param name
   */
  public RedissonBitSet(String ip, int port, String password, String name) {
    Config config = new Config();
    config.useSingleServer().setAddress("redis://" + ip + ":" + port).setPassword(password);
    RedissonClient redisson = Redisson.create(config);
    this.redissonClient = redisson;
    this.rBitSet = redisson.getBitSet(name);
  }

  public RedissonBitSet(String cluster, String password, String name) {
    String[] nodes = cluster.split(",");
    for (int i = 0; i < nodes.length; i++) {
      nodes[i] = "redis://" + nodes[i];
    }
    Config config = new Config();
    config.useClusterServers() //这是用的集群server
        .setScanInterval(2000) //设置集群状态扫描时间
        .addNodeAddress(nodes)
        .setPassword(password);
    RedissonClient redisson = Redisson.create(config);
    this.redissonClient = redisson;
    this.rBitSet = redisson.getBitSet(name);
  }

  @Override
  public void set(int bitIndex) {
    rBitSet.set(bitIndex);
  }

  @Override
  public void set(int bitIndex, boolean value) {
    rBitSet.set(bitIndex, value);
  }

  @Override
  public boolean get(int bitIndex) {
    return rBitSet.get(bitIndex);
  }

  @Override
  public void clear(int bitIndex) {
    redissonClient.shutdown();
  }

  @Override
  public void clear() {
    rBitSet.clear();
  }

  @Override
  public long size() {
    return rBitSet.size();
  }

  @Override
  public boolean isEmpty() {
    return size() <= 0;
  }
}