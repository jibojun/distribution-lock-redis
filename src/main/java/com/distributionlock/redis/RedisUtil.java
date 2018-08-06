package com.distributionlock.redis;

import redis.clients.jedis.Jedis;

/**
 * @Author: Bojun Ji
 * @Description:
 * @Date: 2018/8/6_12:45 AM
 */
public class RedisUtil {

    /**
     * set if not existed, return 1 if ok, return 0 when not updated
     *
     * @param key
     * @param value
     * @return
     */
    public static Long setnx(String key, String value) {
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = RedisPool.getConnResource();
            result = jedis.setnx(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
            return result;
        }
    }


    /**
     * get value by key
     *
     * @param key
     * @return
     */
    public static String get(String key) {
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisPool.getConnResource();
            result = jedis.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
            return result;
        }
    }

    /**
     * set value and return the old value
     *
     * @param key
     * @param value
     * @return
     */
    public static String getSet(String key, String value) {
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisPool.getConnResource();
            result = jedis.getSet(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
            return result;
        }
    }

    /**
     * delete value on the target key，return number of keys removed
     *
     * @param key
     * @return
     */
    public static Long del(String key) {
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = RedisPool.getConnResource();
            result = jedis.del(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
            return result;
        }
    }

    /**
     * set expire time for the key, 1 is ok, 0 is not set
     *
     * @param key
     * @param seconds
     * @return
     */
    public static Long expire(String key, int seconds) {
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = RedisPool.getConnResource();
            result = jedis.expire(key, seconds);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
            return result;
        }
    }


}
