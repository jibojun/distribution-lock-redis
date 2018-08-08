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
            //add lock
            if (tryAddLock()) {
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
        String expireTimePoint = String.valueOf(System.currentTimeMillis() + lockExpireTime);
        //try add lock
        if (tryAddLock()) {
            return true;
        }
//        String value = jedis.get(lockKey);
//        if (value != null && isTimeExpired(value)) {//锁是过期的
//            //假设多个线程(非单jvm)同时走到这里
//            String oldValue = jedis.getSet(lockKey, stringOfLockExpireTime);//原子操作
//            // 但是走到这里时每个线程拿到的oldValue肯定不可能一样(因为getset是原子性的)
//            // 假如拿到的oldValue依然是expired的，那么就说明拿到锁了
//            if (oldValue != null && isTimeExpired(oldValue)) {//拿到锁
//                //设置相关标识
//                locked = true;
//                setExclusiveOwnerThread(Thread.currentThread());
//                return true;
//            }
//        }
        return false;
    }

    /**
     * add lock by redis setnx, setnx will return 1 when the KV is set successfully
     *
     * @return
     */
    private boolean tryAddLock() {
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
        return false;
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
