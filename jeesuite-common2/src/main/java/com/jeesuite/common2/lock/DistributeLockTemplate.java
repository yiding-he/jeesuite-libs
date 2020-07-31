/**
 *
 */
package com.jeesuite.common2.lock;

import com.jeesuite.common2.lock.redis.RedisDistributeLock;

/**
 * @author <a href="mailto:vakinge@gmail.com">vakin</a>
 * @since 2016年7月22日
 */
public class DistributeLockTemplate {

    private static final int _DEFAULT_LOCK_HOLD_MILLS = 30;


    public static <T> T execute(String lockId, LockCaller<T> caller) {
        return execute(lockId, caller, _DEFAULT_LOCK_HOLD_MILLS);
    }

    /**
     * @param lockId 要确保不和其他业务冲突（不能用随机生成）
     * @param caller 业务处理器
     * @param timeout 超时时间（秒）
     * @return
     */
    public static <T> T execute(String lockId, LockCaller<T> caller, int timeout) {
        RedisDistributeLock dLock = new RedisDistributeLock(lockId, (int) timeout / 1000);

        boolean getLock = false;
        try {
            if (dLock.tryLock()) {
                getLock = true;
                return caller.onHolder();
            } else {
                return caller.onWait();
            }
        } finally {
            if (getLock) dLock.unlock();
        }

    }
}
