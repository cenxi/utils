-- 获取zset的key
local key = KEYS[1]

-- 脚本传入的限流大小
local limit = tonumber(ARGV[1])

-- 脚本传入的限流起始时间戳
local start = tonumber(ARGV[2])

-- 脚本传入的限流当前时间戳
local now = tonumber(ARGV[3])

-- 脚本传入当前访问实例的id
local uuid = ARGV[4]


-- 获取当前流量总数
local count = tonumber(redis.call('zcount',key, start, now))

--是否超出限流值
if count + 1 >limit then
    return false
-- 不需要限流
else
    -- 添加当前访问时间戳到zset
    redis.call('zadd', key, now, uuid)
    -- 移除时间区间以外不用的数据，不然会导致zset过大
    redis.call('zremrangebyscore',key, 0, start)
    redis.call("expire", key, now-start)
    return true
end




