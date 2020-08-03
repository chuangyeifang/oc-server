/**
 * 
 */
package com.oc.cluster.collection.queue;

import java.util.concurrent.BlockingQueue;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年7月31日
 * @version v 1.0
 */
public interface CustomQueue<E> extends BlockingQueue<E>{

	/**
	 * 获取队列名称
	 * @return
	 */
	String getName();

	/**
	 * 设置队列名称
	 * @param name
	 */
	void setName(String name);

	/**
	 * 获取队列大小
	 * @return
	 */
	long getQueueSize();

	/**
	 * 获取锁
	 */
	void lock();

	/**
	 * 尝试获取锁
	 * @param timeout
	 * @throws InterruptedException
	 */
	void tryLock(long timeout) throws InterruptedException;

	/**
	 * 释放锁
	 */
	void unlock();
	
}
