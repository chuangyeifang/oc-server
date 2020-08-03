package com.oc.dispatcher.scheduler;

import java.util.concurrent.*;

import com.hazelcast.core.HazelcastInstanceNotActiveException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oc.dispatcher.Dispatcher;
import com.oc.dispatcher.register.Event;
import com.oc.dispatcher.register.EventRegister;
import com.oc.dispatcher.register.EventType;
import com.oc.domain.waiter.Waiter;
import com.oc.core.OcImServer;
import com.oc.routing.CustomerRoute;
import com.oc.session.Customer;

/**
 * @Description: 调度中心
 * @author chuangyeifang
 * @createDate 2020年2月2日
 * @version v 1.0
 */
public final class EventScheduler {
	
	static Logger log = LoggerFactory.getLogger(EventScheduler.class);

	private volatile static int threadCount = 1;

	private final Dispatcher dispatcher;
	private final EventRegister register;
	
	private ExecutorService es;
	private static int waitTime = 100;

	public EventScheduler(final Dispatcher dispatcher, final EventRegister register) {
		this.dispatcher = dispatcher;
		this.register = register;
		createThreadPools();
	}

	/**
	 * 开启调度
	 */
	public void start() {
		ExecutorService es = new ThreadPoolExecutor(
				1,
				1,
				30L,
				TimeUnit.SECONDS,
				new ArrayBlockingQueue<>(1),
				r -> {
					Thread t = new Thread(r);
					t.setName("Scheduler-WC-worker");
					return t;
				}
		);
		es.execute(new EventRunnable());
	}

	private void createThreadPools() {
		int defaultPoolSize = 5;
		int maxPoolSize = 10;
		es = new ThreadPoolExecutor(defaultPoolSize,
				maxPoolSize,
				30L, TimeUnit.SECONDS,
				new LinkedBlockingQueue<>(),
				runnable -> {
					Thread thread = new Thread(runnable);
					thread.setName(createThreadName());
					return thread;
				});
	}

	private synchronized String createThreadName() {
		return "Scheduler-Waiter-" + threadCount++;
	}

	private class EventRunnable implements Runnable {

		@Override
		public void run() {
			while(true) {
				try {
					Event event = register.acquireEvent();
					if (null == event) {
						int waitTimeLimit = 500;
						if (waitTime > waitTimeLimit) {
							waitTime = waitTimeLimit;
						} else {
							int step = 100;
							waitTime += step;
						}
						TimeUnit.MILLISECONDS.sleep(waitTime);
					} else {
						waitTime = 100;
						es.execute(new EventExecutor(event));
					}
				} catch (HazelcastInstanceNotActiveException activeException){
					log.error("请关注当前集群已关闭，如非手动停止，请检测应用", activeException);
					return;
				} catch (Exception e) {
					log.error("处理事件发生错误", e);
				}
			}
		}
	}
	
	private class EventExecutor implements Runnable {
		
		private Event event;
		private String tenantCode;
		private Integer teamCode;

		public EventExecutor(Event event) {
			this.event = event;
			this.teamCode = event.getTeamCode();
			this.tenantCode = event.getTenantCode();
		}

		@Override
		public void run() {
			if (StringUtils.isEmpty(tenantCode) || null == teamCode) {
				log.warn("事件未绑定具体任务：{}", event);
				return;
			}
			switch (event.getType()) {
				case WAITER_IDLE:
					while(dispatcher.existWait(teamCode)) {
						Waiter waiter = dispatcher.acquireWaiter(teamCode);
						if (null != waiter) {
							Customer customer = dispatcher.acquireCustomer(teamCode);
							if (null != customer) {
								event = new Event(EventType.READY_DONE, customer.getUid(), tenantCode, teamCode,
										waiter.getWaiterName(), waiter.getWaiterCode());
								allot(event);
							} else {
								dispatcher.directReleaseRelation(waiter.getWaiterCode());
							}
						} else {
							break;
						}
					}
					break;
				case CUSTOMER_REQ:
				case TRANSFER_TEAM:
					allot(event);
					break;
				case WAITER_MANUAL_REQ:
					Customer customer = dispatcher.acquireCustomer(teamCode);
					while (customer != null) {
						event.setUid(customer.getUid());
						if(allot(event)) {
							break;
						} else {
							customer = dispatcher.acquireCustomer(teamCode);
						}
					}
					break;
				default:
					log.warn("分配事件类型不存在, Event: {}", event);
					break;
			}
		}
		
		private boolean allot(Event event) {
			CustomerRoute customerRoute = OcImServer.getInst().getRoutingTable().getCustomerRoute(event.getUid());
			if (null != customerRoute) {
				if(customerRoute.getNodeID().equals(OcImServer.getInst().getNodeId())) {
					dispatcher.buildRelation(event);
				} else {
					OcImServer.getInst().getClusterMessageRouter().routeEvent(customerRoute.getNodeID(), event);
				}
			} else {
				// 分配过程发现客户离开，READY_DONE 表示已经获取客服资源，需要释放客服资源
				if (event.getType() == EventType.READY_DONE) {
					dispatcher.directReleaseRelation(event.getWaiterCode());
				}
				log.info("客户已经离开，无需为当前客户分配客服： Event：{}, nodeID:{}", event, OcImServer.getInst().getNodeId());
				return false;
			}
			return true;
		}
	}
}
