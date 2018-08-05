package com.distributionlock.redis;

/**
 * @Author: Bojun Ji
 * @Description:
 * @Date: 2018/8/6_12:50 AM
 */
public class RedisConstants {
    private static final int MAX_TOTAL = 20;

    private static final int MAX_IDLE = 10;

    private static final int MIN_IDLE = 5;

    private static final String REDIS_HOST="127.0.0.1";

    private static final int REDIS_CONN_TIME_OUT=5000;

    private static final int REDIS_PORT=6379;

    private static final String REDIS_CONN_PWD="";

    private static final boolean REDIS_TEST_ON_BORROW=true;

    private static final boolean REDIS_TEST_ON_RETURN=false;

    private static final boolean REDIS_BLOCK_WHEN_EXHAUSTED=true;

}
