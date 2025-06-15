redis.replicate_commands()

local leaky_bucket_key = KEYS[1] -- KEY[1]: 漏桶的键
local last_bucket_key = KEYS[2] -- KEY[2]: 上次漏桶更新时间的键

local rate = tonumber(ARGV[1]) -- ARGV[1]: 漏水速率
local capacity = tonumber(ARGV[2]) -- ARGV[2]: 漏桶的容量
local now = tonumber(ARGV[3]) -- ARGV[3]: 当前时间戳(秒)
local requested = tonumber(ARGV[4]) -- ARGV[4]: 请求的令牌数

local key_lifetime = math.ceil((capacity / rate) + 1) -- 两个键的生命周期

-- for testing, it should use redis system time in production
if now == nil then
  now = redis.call('TIME')[1]
end

local key_bucket_count = tonumber(redis.call("GET", leaky_bucket_key)) or 0 -- 获取当前漏桶的水量，默认为 0
local last_time = tonumber(redis.call("GET", last_bucket_key)) or now -- 获取上次漏桶更新的时间戳，默认为当前时间。
local millis_since_last_leak = now - last_time -- 计算两次请求的时间差
local leaks = millis_since_last_leak * rate -- 计算在两次请求之间从漏桶中流出的水量

if leaks > 0 then -- 如果有水流出漏桶，则根据流出的水量更新漏桶状态。
    if leaks >= key_bucket_count then -- 如果流出的水量(leaks) >= 当前漏桶的水量，则把桶清空
        key_bucket_count = 0
    else                              -- 否则，计算剩余漏桶中的水量
        key_bucket_count = key_bucket_count - leaks
    end
    last_time = now
end

local is_allow = 0 -- 是否允许通过，默认不允许

local new_bucket_count = key_bucket_count + requested -- 更新后的水量 = 剩余水量 + 本次请求的令牌数
-- allow
if new_bucket_count <= capacity then -- 更新后的水量 <= 漏桶的容量，则允许通过
    is_allow = 1
else                                 -- 否则，不通过
    return {is_allow, new_bucket_count}
end

redis.call("SETEX", leaky_bucket_key, key_lifetime, new_bucket_count) -- 更新漏桶中的水量
redis.call("SETEX", last_bucket_key, key_lifetime, now) -- 更新最新更新时间

return {is_allow, new_bucket_count} -- 返回是否通过，以及更新后的水量