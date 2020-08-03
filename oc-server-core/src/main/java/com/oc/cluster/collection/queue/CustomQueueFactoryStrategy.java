/**
 * 
 */
package com.oc.cluster.collection.queue;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年7月31日
 * @version v 1.0
 */
public interface CustomQueueFactoryStrategy {

	/**
	 * 创建队列
	 * @param name
	 * @param emptyQueueTtl
	 * @param <E>
	 * @return
	 */
	<E> CustomQueue<E> createOCQueue(String name, int emptyQueueTtl);
}
