package com.distributionlock.lock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @Author: Bojun Ji
 * @Description:
 * @Date: 2018/8/6_12:45 AM
 */
public abstract class AbstractDistributionLock implements Lock {
    /**
     * whether it's locked, may be multiple threads in same JVM
     */
    protected volatile boolean locked;

    /**
     * lock owner thread in current JVM
     */
    private Thread lockOwnerThread;

    /**
     * check lock function
     * important
     * it needs to be implemented by son class
     *
     * @return true when it's locked by some other threads
     */
    protected abstract boolean isLocked();

    /**
     * lock function
     * important
     * it needs to be implemented by son class
     *
     * @param useTimeout whether to use timeout lock
     * @param time       time out time
     * @param unit       time unit for time out
     * @param interrupt  whether it can be interrupted
     * @return
     * @throws InterruptedException
     */
    protected abstract boolean lock0(boolean useTimeout, long time, TimeUnit unit, boolean interrupt)
            throws InterruptedException;

    /**
     * unlock function
     * important
     * it needs to be implemented by son class
     * <p>
     * unlock
     */
    protected abstract void unlock0();

    /**
     * no interrupt lock
     */
    @Override
    public void lock() {
        try {
            lock0(false, 0, null, false);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * interrupt lock
     *
     * @throws InterruptedException
     */
    @Override
    public void lockInterruptibly() throws InterruptedException {
        lock0(false, 0, null, true);
    }

    /**
     * try lock acquire only when it's free, it will return immediately, false means not successful
     *
     * @return
     */
    @Override
    public boolean tryLock() {
        try {
            return lock0(true, 0, TimeUnit.SECONDS, false);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * try lock, with time out
     *
     * @param time
     * @param unit
     * @return
     * @throws InterruptedException
     */
    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return lock0(true, time, unit, false);
    }

    /**
     * release the lock for current thread
     */
    @Override
    public void unlock() {
        if (lockOwnerThread != Thread.currentThread()) {
            throw new IllegalMonitorStateException("unlock failed for current thread, it's not the lock owner");
        }
        //if current thread is the owner, release it
        unlock0();
    }

    /**
     * new instance with the lock
     *
     * @return
     */
    @Override
    public Condition newCondition() {
        return null;
    }

    public Thread getLockOwnerThread() {
        return lockOwnerThread;
    }

    public void setLockOwnerThread(Thread lockOwnerThread) {
        this.lockOwnerThread = lockOwnerThread;
    }
}
