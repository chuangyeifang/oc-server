/**
 * 
 */
package com.oc.cluster.collection.set;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年1月4日
 * @version v 1.0
 */
public interface CustomSetFactoryStrategy {

	<E> CustomSet<E> createOCSet(String name);
}
