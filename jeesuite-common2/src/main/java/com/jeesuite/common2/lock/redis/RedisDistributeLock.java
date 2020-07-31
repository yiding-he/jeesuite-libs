package com.jeesuite.common2.lock.redis;

import com.jeesuite.common2.lock.LockException;
import redis.clients.jedis.Jedis;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;


/**
 * 基于redis的锁
 *
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @since 2018年3月22日
 */
public class RedisDistributeLock implements Lock {

    private static final String GET_LOCK_FLAG = "1";

    private static RedisLockCoordinator coordinator = new RedisLockCoordinator();

    private static String getLockLua = "local res = redis.call('setnx', KEYS[1],'1')\n" +
            "if tonumber(res) > 0 then\n" +
            "	redis.call('expire', KEYS[1], ARGV[1])\n" +
            "	return 1\n" +
            "else \n" +
            "	return 0\n" +
            "end";

    private static final String LOCK_KEY_PREFIX = "dlock:";

    private static final int _DEFAULT_MAX_WAIT = 60;

    private String lockName;

    private int maxLiveSeconds;


    /**
     * 默认最大存活时间60秒
     */
    public RedisDistributeLock(String lockName) {
        this(lockName, _DEFAULT_MAX_WAIT);
    }

    /**
     * @param maxLiveSeconds 锁最大存活时间（秒）
     */
    public RedisDistributeLock(String lockName, int maxLiveSeconds) {
        this.lockName = LOCK_KEY_PREFIX + lockName;
        this.maxLiveSeconds = maxLiveSeconds;
    }

    @Override
    public void lock() {

        boolean locked = false;
        try {
            locked = tryLock(maxLiveSeconds, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new LockException(e);
        }
        if (!locked) {
            unlock();
            throw new LockException("Lock[" + lockName + "] timeout");
        }

    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        Jedis client = coordinator.getRedisClient();
        try {
            return checkLock(client);
        } finally {
            coordinator.release(client);
        }

    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        Jedis client = coordinator.getRedisClient();
        try {
            long start = System.currentTimeMillis();
            boolean res = checkLock(client);
            if (res) return res;

            long sleep = 300;
            while (!res) {
                try {
                    TimeUnit.MILLISECONDS.sleep(sleep);
                } catch (InterruptedException e) {
                    throw e;
                }

                res = checkLock(client);

                if (res) {
                    return res;
                } else if (sleep > 10) {
                    sleep = sleep - 10;
                }

                if (System.currentTimeMillis() - start > unit.toMillis(time)) {
                    return false;
                }
            }
        } finally {
            coordinator.release(client);
        }
        return false;
    }

    @Override
    public void unlock() {
        Jedis client = coordinator.getRedisClient();
        try {
            client.del(lockName);
        } finally {
            coordinator.release(client);
        }
    }

    @Override
    public Condition newCondition() {
        return null;
    }

    private boolean checkLock(Jedis client) {
        Object result = client.evalsha(client.scriptLoad(getLockLua), Arrays.asList(lockName), Arrays.asList(String.valueOf(maxLiveSeconds)));
        return GET_LOCK_FLAG.equals(String.valueOf(result));
    }

}
