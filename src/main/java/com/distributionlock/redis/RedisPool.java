package com.distributionlock.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @Author: Bojun Ji
 * @Description:
 * @Date: 2018/8/6_12:45 AM
 */
public class RedisPool {

    private static JedisPool pool;

    /**
     * init a redis connection pool with configuration assigned in constants
     */
    public static void initRedisPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(RedisConstants.MAX_TOTAL);
        config.setMaxIdle(RedisConstants.MAX_IDLE);
        config.setMinIdle(RedisConstants.MIN_IDLE);
        config.setTestOnBorrow(RedisConstants.REDIS_TEST_ON_BORROW);
        config.setTestOnReturn(RedisConstants.REDIS_TEST_ON_RETURN);
        config.setBlockWhenExhausted(RedisConstants.REDIS_BLOCK_WHEN_EXHAUSTED);
        pool = new JedisPool(config, RedisConstants.REDIS_HOST);
    }

    private static JedisPool getPool() {
        return pool;
    }

    private static void setPool(JedisPool pool) {
        RedisPool.pool = pool;
    }

    public static Jedis getConnResource() {
        return pool.getResource();
    }

    public static void closeConnResource(Jedis jedis) {
        jedis.close();
    }
}
