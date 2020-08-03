/**
 * 
 */
package com.oc.cluster.task;

import java.io.Externalizable;

/**
 * 集群任务调度
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年7月18日
 * @version v 1.0
 */
public interface ClusterTask<V> extends Runnable, Externalizable {

	/**
	 * 获取人物编码
	 * @return
	 */
	String getTaskId();

	/**
	 * 返回执行结果
	 * @return
	 */
	V getResult();
}
