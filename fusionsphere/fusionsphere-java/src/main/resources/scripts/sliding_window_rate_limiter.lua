redis.replicate_commands()

local tokens_key = KEYS[1] -- key[1]: 存储令牌的键
local timestamp_key = KEYS[2] -- key[2]:
--redis.log(redis.LOG_WARNING, "tokens_key " .. tokens_key)

local rate = tonumber(ARGV[1]) -- ARGV[1]: 单位时间内执行请求的速率，用于计算时间窗口大小。
local capacity = tonumber(ARGV[2]) -- ARGV[2]: 容量，时间窗口内（单位时间内）最大的请求数量。
local now = tonumber(ARGV[3]) --  ARGV[3]: 当前时间(秒)

local window_size = tonumber(capacity / rate) -- 计算了滑动窗口的大小，即单位时间内令牌桶能够容纳的请求数。
local window_time = 1 -- 定义了窗口的时间单位，默认为1秒。

-- for testing, it should use redis system time in production
if now == nil then
    now = redis.call('TIME')[1]
end

--redis.log(redis.LOG_WARNING, "rate " .. ARGV[1])
--redis.log(redis.LOG_WARNING, "capacity " .. ARGV[2])
--redis.log(redis.LOG_WARNING, "now " .. ARGV[3])
--redis.log(redis.LOG_WARNING, "window_size " .. window_size)

local last_requested = 0 -- 初始化上一次请求的计数为0。
local exists_key = redis.call('exists', tokens_key) -- 检查令牌桶键是否存在。
if (exists_key == 1) then  -- 如果令牌桶键存在，则获取上一次请求的计数。
    last_requested = redis.call('zcard', tokens_key)
end
--redis.log(redis.LOG_WARNING, "last_requested " .. last_requested)

local remain_request = capacity - last_requested -- 计算剩余的请求数。
local allowed_num = 0
if (last_requested < capacity) then  -- 如果上一次请求的计数小于令牌桶的容量，则允许一个请求，并向令牌桶中添加当前时间的时间戳。
    allowed_num = 1
    redis.call('zadd', tokens_key, now, timestamp_key)
end

--redis.log(redis.LOG_WARNING, "remain_request " .. remain_request)
--redis.log(redis.LOG_WARNING, "allowed_num " .. allowed_num)

redis.call('zremrangebyscore', tokens_key, 0, now - window_size / window_time) -- 删除令牌桶中早于当前时间窗口的时间戳，以保持滑动窗口的大小。
redis.call('expire', tokens_key, math.ceil(window_size)) -- 设置令牌桶的过期时间为窗口大小的上限值。

return { allowed_num, remain_request }