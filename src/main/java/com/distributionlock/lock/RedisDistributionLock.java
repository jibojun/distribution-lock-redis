package com.distributionlock.lock;

import java.util.concurrent.TimeUnit;

/**
 * @Author: Bojun Ji
 * @Description:
 * @Date: 2018/8/6_11:23 PM
 */
public class RedisDistributionLock extends AbstractDistributionLock {
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
