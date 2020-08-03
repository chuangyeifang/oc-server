package com.oc.session.moniter;

import com.oc.scheduler.CancelableScheduler;
import com.oc.scheduler.HashedWheelTimeoutScheduler;
import com.oc.scheduler.SchedulerKey;
import com.oc.scheduler.SchedulerKey.SchedulerType;
import com.oc.session.CustomerSession;

import java.util.concurrent.TimeUnit;

/**
 * @Description: 
 * 会话生命周期监听。<br>
 * @author chuangyeifang
 * @createDate 2020年2月7日
 * @version v 1.0
 */
public class HttpPollCycleMonitor {

	private final static long POLL_TIMEOUT_MILLISECOND = 72 * 1000;

	private CancelableScheduler scheduler = new HashedWheelTimeoutScheduler();
	private CustomerSession customerSession;
	private SchedulerKey pollTimeoutScheduler;

	public HttpPollCycleMonitor(CustomerSession customerSession) {
		this.customerSession = customerSession;
		pollTimeoutScheduler = new SchedulerKey(SchedulerType.POLL_TIMEOUT, customerSession.getUid());
	}

	/**
	 * 重置状态
	 */
	public void reset() {
		scheduler.cancel(pollTimeoutScheduler);
		excPollTimeoutScheduler();
	}

	/**
	 * 超时请求关闭
	 */
	private void excPollTimeoutScheduler() {
		scheduler.scheduler(pollTimeoutScheduler,
				() -> customerSession.disconnect(),
				POLL_TIMEOUT_MILLISECOND, TimeUnit.MILLISECONDS);
	}
}
