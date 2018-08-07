package com.distributionlock.lock;

import redis.clients.jedis.Jedis;

import java.util.concurrent.TimeUnit;

/**
 * @Author: Bojun Ji
 * @Description:
 * @Date: 2018/8/6_11:23 PM
 */
public class RedisDistributionLock extends AbstractDistributionLock {
    //connection resource
    private Jedis jedis;
    //lock name, also the ket for redis cache
    protected String lockName;
    //expire time for the lock
    protected long lockExpireTime;

    private RedisDistributionLock(){}

    public RedisDistributionLock(Jedis jedis, String lockName, long lockExpireTime){
        this.jedis=jedis;
        this.lockName=lockName;
        this.lockExpireTime=lockExpireTime;
    }

    @Override
    public boolean isLocked() {
        return false;
    }

    @Override
    protected boolean lock0(boolean useTimeout, long time, TimeUnit unit, boolean interrupt) throws InterruptedException {
        return false;
    }

    @Override
    protected void unlock0() {

    }

    @Override
    public boolean tryLock() {
        return super.tryLock();
    }
}
