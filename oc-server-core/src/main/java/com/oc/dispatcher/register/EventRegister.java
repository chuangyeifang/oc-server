package com.oc.dispatcher.register;

import com.oc.cluster.collection.queue.CustomQueue;
import com.oc.dispatcher.NameFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年2月2日
 * @version v 1.0
 */
@Slf4j
public final class EventRegister {
	
	private final static String REG_EVENT_QUEUE = "RegEventQueue";
	
	private static CustomQueue<Event> registerQueue;

	public EventRegister(NameFactory nameFactory) {
		registerQueue = nameFactory.getQueue(REG_EVENT_QUEUE);
	}
	
	public void register(Event event) {
		registerQueue.add(event);
	}
	
	public boolean hasEvent(Event event) {
		return registerQueue.contains(event);
	}
	
	public boolean cancel(Event event) {
		return registerQueue.remove(event);
	}

	public Event acquireEvent() {
		return registerQueue.poll();
	}
}
