redis.replicate_commands()

local tokens_key = KEYS[1] -- key[1]: 存储令牌的数量的键
local timestamp_key = KEYS[2] -- key[2]: 存储上次刷新的时间戳的键
--redis.log(redis.LOG_WARNING, "tokens_key " .. tokens_key)

local rate = tonumber(ARGV[1]) -- ARGV[1]: 令牌桶的填充速率，即允许用户每秒执行多少请求
local capacity = tonumber(ARGV[2]) -- ARGV[2]: 令牌桶的容量
local now = tonumber(ARGV[3]) -- ARGV[3]: 当前时间戳(秒)
local requested = tonumber(ARGV[4]) -- ARGV[4]: 请求的令牌数

local fill_time = capacity / rate -- 令牌桶填满所需的时间
local ttl = math.floor(fill_time * 2) -- 根据填充时间计算了key的生存时间（ttl）。ttl 的值是填充时间的两倍。

-- for testing, it should use redis system time in production
if now == nil then
  now = redis.call('TIME')[1]
end

--redis.log(redis.LOG_WARNING, "rate " .. ARGV[1])
--redis.log(redis.LOG_WARNING, "capacity " .. ARGV[2])
--redis.log(redis.LOG_WARNING, "now " .. now)
--redis.log(redis.LOG_WARNING, "requested " .. ARGV[4])
--redis.log(redis.LOG_WARNING, "filltime " .. fill_time)
--redis.log(redis.LOG_WARNING, "ttl " .. ttl)

local last_tokens = tonumber(redis.call("get", tokens_key)) -- 获取上一次存储的令牌数量。如果没有获取到，就假设桶是满的。
if last_tokens == nil then
  last_tokens = capacity
end
--redis.log(redis.LOG_WARNING, "last_tokens " .. last_tokens)

local last_refreshed = tonumber(redis.call("get", timestamp_key)) -- 获取上一次刷新的时间戳。如果没有获取到，就将其设置为 0。
if last_refreshed == nil then
  last_refreshed = 0
end
--redis.log(redis.LOG_WARNING, "last_refreshed " .. last_refreshed)

local delta = math.max(0, now-last_refreshed) -- 计算了两次请求之间的时间差（delta）
local filled_tokens = math.min(capacity, last_tokens+(delta*rate)) -- 据此计算出了当前桶中的令牌数。它确保了令牌桶按照速率进行填充。
local allowed = filled_tokens >= requested -- 判断请求是否小于等于当前桶中的令牌数，如果是，则允许请求。
local new_tokens = filled_tokens -- 这段 根据允许的情况更新令牌数和允许的请求数。如果请求允许，则减去请求的令牌数，并将允许的请求数设置为 1。
local allowed_num = 0
if allowed then
  new_tokens = filled_tokens - requested
  allowed_num = 1
end

--redis.log(redis.LOG_WARNING, "delta " .. delta)
--redis.log(redis.LOG_WARNING, "filled_tokens " .. filled_tokens)
--redis.log(redis.LOG_WARNING, "allowed_num " .. allowed_num)
--redis.log(redis.LOG_WARNING, "new_tokens " .. new_tokens)

if ttl > 0 then -- 用于更新令牌桶中的令牌数量和时间戳，并设置它们的生存时间为 ttl。只有当 ttl 大于 0 时，才会执行更新操作。
  redis.call("setex", tokens_key, ttl, new_tokens)
  redis.call("setex", timestamp_key, ttl, now)
end

-- return { allowed_num, new_tokens, capacity, filled_tokens, requested, new_tokens }
return { allowed_num, new_tokens } -- 返回一个包含允许的请求数和更新后的令牌数量的数组，allowed_num为1表示通过