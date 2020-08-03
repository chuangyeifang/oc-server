package com.oc.restcontroller.dashboard;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.core.IQueue;
import com.oc.restcontroller.AbstractBasicRestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 统计HZ信息
 * @author chuangyeifang
 */
@RestController
@RequestMapping("dashboard/hz")
public class HazelcastRestController extends AbstractBasicRestController {

    @Autowired
    private HazelcastInstance hazelcastInstance;

    @RequestMapping("map")
    public Object getMap(String name) {
        IMap<Object, Object> map = hazelcastInstance.getMap(name);
        if (null != map && map.size() > 0) {
            return success(map.values());
        }
        return success();
    }

    @RequestMapping("queue")
    public Object getList(String name) {
        IQueue<Object> queue = hazelcastInstance.getQueue(name);
        if (null != queue && queue.size() > 0) {
            return success(queue.toArray());
        }
        return success();
    }
}
