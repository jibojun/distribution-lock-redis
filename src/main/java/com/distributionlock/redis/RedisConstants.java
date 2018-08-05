package com.distributionlock.redis;

/**
 * @Author: Bojun Ji
 * @Description:
 * @Date: 2018/8/6_12:50 AM
 */
public class RedisConstants {
    public static final int MAX_TOTAL = 20;

    public static final int MAX_IDLE = 10;

    public static final int MIN_IDLE = 5;

    public static final String REDIS_HOST="127.0.0.1";

    public static final int REDIS_CONN_TIME_OUT=5000;

    public static final int REDIS_PORT=6379;

    public static final String REDIS_CONN_PWD="";

    public static final boolean REDIS_TEST_ON_BORROW=true;

    public static final boolean REDIS_TEST_ON_RETURN=false;

    public static final boolean REDIS_BLOCK_WHEN_EXHAUSTED=true;

}
