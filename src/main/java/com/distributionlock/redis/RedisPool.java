package com.distributionlock.redis;

import redis.clients.jedis.JedisPool;

/**
 * @Author: Bojun Ji
 * @Description:
 * @Date: 2018/8/6_12:45 AM
 */
public class RedisPool {

    private static JedisPool pool;

    public static void initRedisPool(){
        pool=new JedisPool();
    }

    public static JedisPool getPool() {
        return pool;
    }

    public static void setPool(JedisPool pool) {
        RedisPool.pool = pool;
    }
}
