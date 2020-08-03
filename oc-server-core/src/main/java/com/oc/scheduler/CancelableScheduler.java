/**
 * 
 */
package com.oc.scheduler;

import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelHandlerContext;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年1月31日
 * @version v 1.0
 */
public interface CancelableScheduler {

	/**
	 * 更新状态
	 * @param ctx
	 */
	void update(ChannelHandlerContext ctx);

	/**
	 * 取消任务
	 * @param key
	 */
	void cancel(SchedulerKey key);

	/**
	 * 执行任务
	 * @param key
	 * @param runnable
	 * @param delay
	 * @param unit
	 */
	void schedulerCallback(SchedulerKey key, Runnable runnable, long delay, TimeUnit unit);

	/**
	 * 执行任务
	 * @param runnable
	 * @param delay
	 * @param unit
	 */
	void scheduler(Runnable runnable, long delay, TimeUnit unit);

	/**
	 * 执行任务
	 * @param key
	 * @param runnable
	 * @param delay
	 * @param unit
	 */
	void scheduler(SchedulerKey key, Runnable runnable, long delay, TimeUnit unit);

	/**
	 * 关闭
	 */
	void shutdown();
}
