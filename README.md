### Package Structure for cache (spring boot)
````text
com.porters
├── cache/
│   ├── CacheFacade.java           ← entry point for controllers
│   │
│   ├── builder/                   ← build cache logic
│   │   ├── CacheBuilder.java
│   │   ├── ChunkLoader.java
│   │   ├── RetryPolicy.java
│   │   └── CacheBuildTask.java
│   │
│   ├── key/                       ← key + version strategy
│   │   ├── CacheKeyGenerator.java
│   │   └── CacheVersionService.java
│   │
│   ├── lock/                      ← distributed lock
│   │   └── RedisLockService.java
│   │
│   ├── store/                     ← Redis operations
│   │   ├── RedisCacheStore.java
│   │   └── CacheSerializer.java
│   │
│   ├── invalidate/                ← invalidation logic
│   │   └── CacheInvalidator.java
│   │
│   ├── refresh/                   ← scheduled refresh
│   │   └── CacheRefreshJob.java
│   │
│   ├── metrics/                   ← hit/miss/build time
│   │   └── CacheMetrics.java
│   │
│   └── config/
│       ├── RedisConfig.java
│       ├── CacheConfig.java
│       └── CircuitBreakerConfig.java
````
#### how it connects to existing layers
**Before (no cache):**
````text
Service → Repository → DB/API
````

### Improved Architecture
````text
Client
  |
Controller
  |
CacheService
  |
  ├─ Check Redis (key with version)
  |     ├─ HIT → return
  |     └─ MISS →
  |
  ├─ Acquire Redis Lock
  |
  ├─ Build cache in chunks (50)
  |     ├─ Retry per chunk (3)
  |     └─ All success?
  |
  ├─ Save to Redis with:
  |     key = company:WF:v2
  |     TTL = 4h
  |
  └─ Release lock
````

### How to validate system works
1. Call API first time
   * Redis empty
   * build cache

2. Second call
   * Redis hit
   * no DB/API call

3. Change version
   * old keys ignored
   * rebuild

4. Kill CRM API
   * circuit breaker opens
   * fallback to stale cache

### 🧪 Test
#### Call API Cache Hit/Miss
```
GET http://localhost:9070/users/1
```

Console logs:
```
Loading from DB...
```

Second call:
```
GET http://localhost:9070/users/1
```
Console:
(no DB log → cache hit)

#### Check RedisInsight
You will see key like:
```
v1::users::1
```
Value:
```
{"id":1,"name":"Tom","age":20}
```

#### Invalidate single cache
```
DELETE http://localhost:9070/users/1/cache
```
RedisInsight:
Key `v1::users::1` disappears

#### 4️⃣ Clear all cache
```
DELETE http://localhost:9070/users/cache
```
All `v1::users::*` removed

#### Versioned cache (magic part ✨)
Change in `RedisConfig`:
```java
public static final String CACHE_VERSION = "v2";
```
Restart app and call:
```
GET http://localhost:9070/users/1
```
