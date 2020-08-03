package com.oc.dispatcher.cache;

import com.oc.cluster.collection.cache.Cache;
import com.oc.dispatcher.NameFactory;
import com.oc.dispatcher.cache.po.ConciseWaiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 客服正常服务管理
 * @author chuangyeifang
 */
public class WaiterServiceCache {

    private static Logger log = LoggerFactory.getLogger(WaiterServiceCache.class);

    private final static Long LOCK_TIMEOUT = 1500L;
    private final static String SERVICE_CACHE_PREFIX = "WaiterServiceCache";

    private NameFactory nameFactory;

    public WaiterServiceCache(NameFactory nameFactory) {
        this.nameFactory = nameFactory;
    }

    public ConciseWaiter get(Integer teamCode, String waiterCode) {
        Cache<String, ConciseWaiter> caches = getCache(teamCode);
        return caches.get(waiterCode);
    }

    public List<ConciseWaiter> get(Integer teamCode) {
        Cache<String, ConciseWaiter> caches = getCache(teamCode);
        List<ConciseWaiter> values = new ArrayList<>(caches.values());
        values.sort((before, after) -> before.getScore() >= after.getScore() ? 0 : -1);
        return values;
    }

    public void put(Integer teamCode, ConciseWaiter conciseWaiter) {
        if (null == teamCode || null == conciseWaiter || null == conciseWaiter.getWaiterCode()) {
            log.warn("WaiterServiceCache put opt params has NULL");
            return;
        }
        Cache<String, ConciseWaiter> caches = getCache(teamCode);
        String waiterCode = conciseWaiter.getWaiterCode();
        try {
            caches.lock(waiterCode, LOCK_TIMEOUT);
            caches.put(waiterCode, conciseWaiter);
        } finally {
            caches.unlock(waiterCode);
        }
    }

    public void put(Integer teamCode, String waiterCode) {
        if (null == teamCode || null == waiterCode) {
            log.warn("WaiterServiceCache put opt params has NULL");
            return;
        }
        Cache<String, ConciseWaiter> caches = getCache(teamCode);
        ConciseWaiter conciseWaiter = caches.get(waiterCode);
        if (conciseWaiter == null) {
            conciseWaiter  = new ConciseWaiter(waiterCode, teamCode);
            try {
                caches.lock(waiterCode, LOCK_TIMEOUT);
                caches.put(waiterCode, conciseWaiter);
            } finally {
                caches.unlock(waiterCode);
            }
        }
    }

    public void update(Integer teamCode, ConciseWaiter conciseWaiter) {
        if (null == teamCode) {
            log.warn("团队编码不能为空");
            return;
        }
        Cache<String, ConciseWaiter> cache = getCache(teamCode);
        if (null != cache) {
            cache.put(conciseWaiter.getWaiterCode(), conciseWaiter);
        }
    }

    public ConciseWaiter remove(Integer teamCode, String waiterCode) {
        Cache<String, ConciseWaiter> caches = getCache(teamCode);
        ConciseWaiter conciseWaiter;
        try {
            caches.lock(waiterCode, LOCK_TIMEOUT);
            conciseWaiter = caches.remove(waiterCode);
        } finally {
            caches.unlock(waiterCode);
        }
        return conciseWaiter;
    }

    private Cache<String, ConciseWaiter> getCache(Integer teamCode) {
        return nameFactory.getCache(SERVICE_CACHE_PREFIX, teamCode.toString());
    }

}
