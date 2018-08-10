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
        //check whether it's expired, unlock when it's not expired，also only allow owner thread to unlock
        if (Thread.currentThread() == getLockOwnerThread() && !checkTimeExpire(RedisUtil.get(lockName))) {
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
        //when try add lock failed, get value from redis and check whether it's expired
        String value = RedisUtil.get(lockName);
        if (value != null && checkTimeExpire(value)) {//expired lock, enter here
            //redis getset, set new value and return the old value, it's an atomic operation
            String oldValue = RedisUtil.getSet(lockName, expireTimePoint);
            //suppose a lot of threads from different JVM try to get lock, every threads want to set a new value which is not expired
            //if the returned old value is still equals to old value we got before, means getting lock successfully, otherwise, it means lock is got from other thread
            //TODO: potenial issue, the value：expire time overwrote by other threads
            if (oldValue != null && value.equalsIgnoreCase(oldValue)) {
                locked = true;
                setLockOwnerThread(Thread.currentThread());
                return true;
            }
        }
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
        //TODO: use Jedis's set functions with 5 parameters to put SETNX, set expire time operations in 1 method/command and let it be atomic
        if (RedisUtil.setNxWithExpireTime(lockName, expireTimePoint, Long.parseLong(String.valueOf(lockExpireTime))).equalsIgnoreCase("OK")) {
//        if (RedisUtil.setnx(lockName, expireTimePoint) == 1) {
//            RedisUtil.expire(lockName, Integer.parseInt(String.valueOf(lockExpireTime)));
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
