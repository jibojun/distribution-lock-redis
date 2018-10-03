package test;

import com.distributionlock.lock.RedisDistributionLock;
import com.distributionlock.redis.RedisPool;
import org.junit.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: Bojun Ji
 * @Description:
 * @Date: 2018/10/4_1:17 AM
 */
public class testLock {
    private ExecutorService threadPool = Executors.newFixedThreadPool(10);
    private RedisDistributionLock redisDistributionLock = new RedisDistributionLock("testLock", 300);

    @Test
    public void testLock() {
        RedisPool.initRedisPool();
//        System.out.println(String.format("%s is trying to get lock", Thread.currentThread().getName()));
//        if (redisDistributionLock.tryLock()) {
//            System.out.println(String.format("%s got the lock", Thread.currentThread().getName()));
//            redisDistributionLock.unlock();
//            System.out.println(String.format("%s released the lock", Thread.currentThread().getName()));
//        }

        for (int i = 0; i < 10; i++) {
            threadPool.submit(() -> {
                System.out.println(String.format("%s is trying to get lock", Thread.currentThread().getName()));
                if (redisDistributionLock.tryLock()) {
                    System.out.println(String.format("%s got the lock", Thread.currentThread().getName()));
                    redisDistributionLock.unlock();
                    System.out.println(String.format("%s released the lock", Thread.currentThread().getName()));
                }
            });
        }
    }
}
