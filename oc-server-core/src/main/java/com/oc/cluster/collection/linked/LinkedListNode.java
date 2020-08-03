/**
 * 
 */
package com.oc.cluster.collection.linked;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年7月23日
 * @version v 1.0
 */
public class LinkedListNode<E> {
	
	public LinkedListNode<E> previous;
	public LinkedListNode<E> next;
	public E object;
	
	public long timestamp;
	
	public LinkedListNode() {
		previous = next = this;
	}
	
	public LinkedListNode(E object, LinkedListNode<E> next, LinkedListNode<E> previous) {
		if (next != null && previous != null) {
			this.insert(next, previous);
		}
		this.object = object;
	}
	
	public LinkedListNode<E> insert(LinkedListNode<E> next, LinkedListNode<E> previous) {
		this.next = next;
		this.previous = previous;
		this.previous.next = this.next.previous = this;
		return this;
	}
	
	public LinkedListNode<E> remove() {
		previous.next = next;
		next.previous = previous;
		previous = next = null;
		return this;
	}
	
	@Override
	public String toString() {
		return object == null ? "null" : object.toString();
	}

}
