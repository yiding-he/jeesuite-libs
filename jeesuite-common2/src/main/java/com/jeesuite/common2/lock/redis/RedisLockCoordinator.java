package com.jeesuite.common2.lock.redis;

import com.jeesuite.cache.redis.JedisProvider;
import com.jeesuite.cache.redis.JedisProviderFactory;
import com.jeesuite.cache.redis.sentinel.JedisSentinelProvider;
import com.jeesuite.cache.redis.standalone.JedisStandaloneProvider;
import com.jeesuite.common.util.ResourceUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import redis.clients.jedis.BinaryJedis;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import javax.annotation.PreDestroy;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static com.jeesuite.cache.redis.JedisProviderFactory.getJedisCommands;

/**
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @since 2018年3月22日
 */
public class RedisLockCoordinator {

    private static final String DLOCK_GROUP_NAME = "_redisLock";

    private static final int HEALTH_CHECK_PERIOD = 30000;

    private static final String REDIS_LOCK_ACTIVE_NODES_KEY = "RedisLockActiveNodes";

    private static final long CLEAN_TIME = TimeUnit.SECONDS.toMillis(90);

    private static final String SPLIT_STR = "$$";

    private static final String EVENT_NODE_ID = RandomStringUtils.random(8, true, true);

    private static String channelName = "redisLockCoordinator";


    private AtomicLong eventIdSeq = new AtomicLong(0);

    private List<String> activeNodeIds = new ArrayList<>();

    private ScheduledExecutorService checkerSchedule;

    private Jedis subClient;

    private Map<String, List<String>> getLockEventIds = new ConcurrentHashMap<>();

    public RedisLockCoordinator() {

        String mode = ResourceUtils.getProperty("jeesuite.lock.redis.mode", ResourceUtils.getProperty("jeesuite.cache.mode", "standalone"));
        String server = ResourceUtils.getProperty("jeesuite.lock.redis.servers", ResourceUtils.getProperty("jeesuite.cache.servers"));
        String datebase = ResourceUtils.getProperty("jeesuite.lock.redis.datebase", ResourceUtils.getProperty("jeesuite.cache.datebase", "0"));
        String password = ResourceUtils.getProperty("jeesuite.lock.redis.password", ResourceUtils.getProperty("jeesuite.cache.password"));
        String maxPoolSize = ResourceUtils.getProperty("jeesuite.lock.redis.maxPoolSize", ResourceUtils.getProperty("jeesuite.cache.maxPoolSize", "100"));

        Validate.notBlank(server, "config[jeesuite.lock.redis.servers] not found");

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(1);
        poolConfig.setMinEvictableIdleTimeMillis(30 * 60 * 1000);
        poolConfig.setMaxTotal(Integer.parseInt(maxPoolSize));
        poolConfig.setMaxWaitMillis(5 * 1000);

        String[] servers = server.split(";|,");
        int timeout = 3000;
        if ("standalone".equals(mode)) {
            JedisProvider<Jedis, BinaryJedis> provider = new JedisStandaloneProvider(DLOCK_GROUP_NAME, poolConfig, servers, timeout, password, Integer.parseInt(datebase), null);
            JedisProviderFactory.addProvider(provider);
        } else if ("sentinel".equals(mode)) {
            String masterName = ResourceUtils.getProperty("jeesuite.lock.redis.masterName", ResourceUtils.getProperty("jeesuite.cache.masterName"));
            Validate.notBlank(masterName, "config[jeesuite.lock.redis.masterName] not found");
            JedisSentinelProvider provider = new JedisSentinelProvider(DLOCK_GROUP_NAME, poolConfig, servers, timeout, password, Integer.parseInt(datebase), null, masterName);
            JedisProviderFactory.addProvider(provider);
        }

        Jedis redisClient = getRedisClient();
        redisClient.hset(REDIS_LOCK_ACTIVE_NODES_KEY, EVENT_NODE_ID, String.valueOf(System.currentTimeMillis()));
        release(redisClient);
        activeNodeIds.add(EVENT_NODE_ID);

//		checkerSchedule = Executors.newScheduledThreadPool(1);
//		checkerSchedule.scheduleAtFixedRate(new Runnable() {
//			@Override
//			public void run() {
//				if(!subClient.isConnected()){
//					subClient.connect();
//				}
//				//
//				checkActiveNode();
//			}
//		}, 0, HEALTH_CHECK_PERIOD - 1000, TimeUnit.MILLISECONDS);


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                subClient = getRedisClient();
                subClient.subscribe(new LockStateListener(), new String[]{channelName});
            }
        }, channelName);
        thread.setDaemon(true);
        thread.start();
    }

    private void checkActiveNode() {
        Jedis redisClient = getRedisClient();
        try {
            long currentTime = System.currentTimeMillis();
            Map<String, String> map = redisClient.hgetAll(REDIS_LOCK_ACTIVE_NODES_KEY);
            Iterator<Entry<String, String>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, String> entry = iterator.next();
                if (currentTime - Long.parseLong(entry.getValue()) > HEALTH_CHECK_PERIOD) {
                    redisClient.hdel(REDIS_LOCK_ACTIVE_NODES_KEY, entry.getKey());
                } else {
                    redisClient.hset(REDIS_LOCK_ACTIVE_NODES_KEY, entry.getKey(), String.valueOf(currentTime));
                }
            }
            if (!map.containsKey(EVENT_NODE_ID)) {
                redisClient.hset(REDIS_LOCK_ACTIVE_NODES_KEY, EVENT_NODE_ID, String.valueOf(currentTime));
            }
        } finally {
            release(redisClient);
        }
    }

    public Jedis getRedisClient() {
        Jedis jedis = (Jedis) JedisProviderFactory.getJedisProvider(DLOCK_GROUP_NAME).get();
        if (!jedis.isConnected()) {
            jedis.connect();
        }
        return jedis;
    }

    public void release(Jedis jedis) {
        JedisProviderFactory.getJedisProvider(DLOCK_GROUP_NAME).release();
    }

    public String buildEvenId() {
        return new StringBuilder().append(EVENT_NODE_ID).append(System.currentTimeMillis()).append(eventIdSeq.incrementAndGet()).toString();
    }

    public void notifyNext(Jedis pubClient, String lockName) {
        long currentTimeMillis = System.currentTimeMillis();

        String nextEventId;
        while (true) {
            nextEventId = pubClient.rpop(lockName);
            if (StringUtils.isBlank(nextEventId)) {
                return;
            }
            if (activeNodeIds.contains(nextEventId.substring(0, 8))
                    && currentTimeMillis - Long.parseLong(nextEventId.substring(8, 21)) < CLEAN_TIME) {
                break;
            }
        }
        pubClient.publish(channelName, lockName + SPLIT_STR + nextEventId);
    }


    public boolean await(String lockName, String eventId, long timeoutMills) {

        List<String> eventIds = getGetLockEventIds(lockName);

        long start = System.currentTimeMillis();

        int deadLockCheckPoint = 0;
        while (true) {
            try {
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (InterruptedException e) {
            }
            if (eventIds.contains(eventId)) {
                eventIds.remove(eventId);
                return true;
            }

            long useTime = System.currentTimeMillis() - start;
            if (useTime > timeoutMills) {
                return false;
            }
            //防止redis的锁数据丢失或者redis挂掉造成死锁,1秒钟检查一次
            int useTimeSeconds = (int) (useTime / 1000);
            if (useTimeSeconds > 0 && useTimeSeconds > deadLockCheckPoint) {
                deadLockCheckPoint = useTimeSeconds;
                try {
                    if (getJedisCommands(DLOCK_GROUP_NAME).llen(lockName) == 0) {
                        return false;
                    }
                } catch (Exception e) {
                    return false;
                }
            }
        }
    }

    private List<String> getGetLockEventIds(String lockName) {
        List<String> eventIds = getLockEventIds.get(lockName);
        if (eventIds == null) {
            synchronized (getLockEventIds) {
                eventIds = getLockEventIds.get(lockName);
                if (eventIds == null) {
                    eventIds = new Vector<>();
                    getLockEventIds.put(lockName, eventIds);
                }
            }
        }

        return eventIds;
    }

    @PreDestroy
    public void close() {
        try {
            subClient.close();
        } catch (Exception e) {
        }
        if (checkerSchedule != null) checkerSchedule.shutdown();
    }

    private class LockStateListener extends JedisPubSub {

        public LockStateListener() {
        }


        @Override
        public void onMessage(String channel, String message) {
            super.onMessage(channel, message);
            if (StringUtils.isBlank(message)) {
                return;
            }
            String[] parts = StringUtils.splitByWholeSeparator(message, SPLIT_STR);
            String lockName = parts[0];
            String nextEventId = parts[1];
            if (!nextEventId.startsWith(EVENT_NODE_ID)) {
                return;
            }
            getGetLockEventIds(lockName).add(nextEventId);
        }
    }

    public static void main(String[] args) {
        System.out.println(1500 % 1000);
        System.out.println(1500 / 1000);
    }

}
