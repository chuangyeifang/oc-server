package com.oc.scheduler;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年1月31日
 * @version v 1.0
 */
public class SchedulerKey {

	public enum SchedulerType {
		// 轮询超时
		@SuppressWarnings("unused")
		POLL_TIMEOUT,
		// 心跳超时
		PING_TIMEOUT,
	}
	
	private final SchedulerType type;
	private Object uid;
	
	public SchedulerKey(SchedulerType type, Object uid) {
		this.type = type;
		this.uid = uid;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (uid == null ? 0 : uid.hashCode());
		result = prime * result + (type == null ? 0 : type.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SchedulerKey other = (SchedulerKey)obj;
		if (uid == null) {
			if (other.uid != null) {
				return false;
			}
		} else if (!uid.equals(other.uid)) {
			return false;
		}
		return type == other.type;
	}
}
