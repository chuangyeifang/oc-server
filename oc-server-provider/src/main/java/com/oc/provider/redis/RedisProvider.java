package com.oc.provider.redis;

import com.oc.provider.context.SpringContext;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author chuangyeifang
 */
public class RedisProvider {

    private static StringRedisTemplate stringRedisTemplate = SpringContext.getBean(StringRedisTemplate.class);

    private RedisProvider() {}

    public StringRedisTemplate getRedisTmp() {
        return stringRedisTemplate;
    }

    public static  RedisProvider getInst() {
        return Single.inst;
    }

    private static class Single {
        private static RedisProvider inst = new RedisProvider();
    }
}
