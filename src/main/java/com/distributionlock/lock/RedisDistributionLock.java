package com.distributionlock.lock;

import com.distributionlock.redis.RedisUtil;

import java.util.concurrent.TimeUnit;

/**
 * @Author: Bojun Ji
 * @Description:
 * @Date: 2018/8/6_11:23 PM
 */
public class RedisDistributionLock extends AbstractDistributionLock {
    //lock name, also the ket for redis cache
    protected String lockName;
    //expire time for the lock
    protected long lockExpireTime;

    private RedisDistributionLock() {
    }

    public RedisDistributionLock(String lockName, long lockExpireTime) {
        this.lockName = lockName;
        this.lockExpireTime = lockExpireTime;
    }

    @Override
    public boolean isLocked() {
        if (locked) {
            return true;
        } else {
            //check whether it's expired
            String value = RedisUtil.get(lockName);
            return !checkTimeExpire(value);
        }
    }

    @Override
    protected boolean lock0(boolean useTimeout, long time, TimeUnit unit, boolean interrupt) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        long waitingTime = unit.toMillis(time);
        //try lock until it's time out while using time out
        while (useTimeout ? !checkTimeOut(startTime, waitingTime) : true) {
            if (interrupt) {
                checkThreadInterrupted();
            }
            //expire time point=current time+expire time
            String expireTimePoint = String.valueOf(System.currentTimeMillis() + lockExpireTime);
            //important, setnx, set if not existed, add lock, if the key is already here, means locking operation failed
            if (RedisUtil.setnx(lockName, expireTimePoint) == 1) {
                //expire time
                RedisUtil.expire(lockName, Integer.parseInt(String.valueOf(lockExpireTime)));
                this.locked = true;
                setLockOwnerThread(Thread.currentThread());
                return true;
            }
        }
        return false;
    }

    @Override
    protected void unlock0() {
        //check whether it's expired, unlock when it's not expired
        if (!checkTimeExpire(RedisUtil.get(lockName))) {
            RedisUtil.del(lockName);
            this.locked = false;
        }
    }

    @Override
    public boolean tryLock() {
        return super.tryLock();
    }

    /**
     * redis value hold the expire time，compare with current time
     *
     * @param expireTime
     * @return
     */
    private boolean checkTimeExpire(String expireTime) {
        return System.currentTimeMillis() > Long.parseLong(expireTime);
    }

    /**
     * check whether thread is interrupted
     */
    private void checkThreadInterrupted() throws InterruptedException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException();
        }
    }

    /**
     * check whether it's timeout, compare startTime+waitTime with current time
     *
     * @param startTime
     * @param waitingTime
     * @return
     */
    private boolean checkTimeOut(long startTime, long waitingTime) {
        return startTime + waitingTime > System.currentTimeMillis();
    }

}
