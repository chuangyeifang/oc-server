/**
 * 
 */
package com.oc.scheduler;

import java.util.Map;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import io.netty.util.internal.PlatformDependent;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年1月31日
 * @version v 1.0
 */
public class HashedWheelScheduler implements CancelableScheduler{

	private final Map<SchedulerKey, Timeout> scheduledFutures = PlatformDependent.newConcurrentHashMap();
	private final HashedWheelTimer executorService;
	
	private volatile ChannelHandlerContext ctx;
	
	public HashedWheelScheduler() {
		executorService = new HashedWheelTimer();
	}
	
	public HashedWheelScheduler(ThreadFactory threadFactory) {
		executorService = new HashedWheelTimer(threadFactory);
	}
	
	@Override
	public void update(ChannelHandlerContext ctx) {
		this.ctx = ctx;
	}

	@Override
	public void cancel(SchedulerKey key) {
		if (null == key) {
			return;
		}
		Timeout timeout = scheduledFutures.remove(key);
		if (timeout != null) {
			timeout.cancel();
		}
	}

	@Override
	public void schedulerCallback(SchedulerKey key, Runnable runnable, long delay, TimeUnit unit) {
		Timeout timeout = executorService.newTimeout(new TimerTask() {
			
			@Override
			public void run(Timeout timeout) throws Exception {
				ctx.executor().execute(new Runnable() {
					
					@Override
					public void run() {
						try {
		                    runnable.run();
		                } finally {
		                    scheduledFutures.remove(key);
		                }
					}
				});
			}
		}, delay, unit);
		
		if (!timeout.isExpired()) {
			scheduledFutures.put(key, timeout);
		}
	}

	@Override
	public void scheduler(Runnable runnable, long delay, TimeUnit unit) {
		executorService.newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                    runnable.run();
            }
        }, delay, unit);

	}

	@Override
	public void scheduler(SchedulerKey key, Runnable runnable, long delay, TimeUnit unit) {
		Timeout timeout = executorService.newTimeout(new TimerTask() {
            @Override
            public void run(Timeout timeout) throws Exception {
                try {
                    runnable.run();
                } finally {
                    scheduledFutures.remove(key);
                }
            }
        }, delay, unit);

        if (!timeout.isExpired()) {
            scheduledFutures.put(key, timeout);
        }
		
	}

	@Override
	public void shutdown() {
		executorService.stop();
	}

}
