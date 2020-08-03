package com.oc.dispatcher.room;

import com.oc.cluster.collection.queue.CustomQueue;
import com.oc.core.OcImServer;
import com.oc.dispatcher.NameFactory;
import com.oc.domain.waiter.Waiter;
import com.oc.message.AddressFrom;
import com.oc.message.AddressTo;
import com.oc.message.Packet;
import com.oc.message.body.Body;
import com.oc.message.type.BodyType;
import com.oc.message.type.Identity;
import com.oc.message.type.PacketType;
import com.oc.session.Customer;
import com.oc.store.waiting.CustomerWaitStoreManager;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 客户的彼岸
 * @author chuangyeifang
 * @createDate 2020年2月2日
 * @version v 1.0
 */
@Slf4j
public final class CustomerRoomImpl implements CustomerRoom{
	
	private final static String CUSTOMER_WAIT_QUEUE = "CustomerWaitingQueue";
	private NameFactory nameFactory;

	private static Set<String> sets = new HashSet<>();
	
	public CustomerRoomImpl(NameFactory nameFactory) {
		this.nameFactory = nameFactory;
		init();
	}

	private void init() {
		ExecutorService executorService = new ThreadPoolExecutor(1,
				1,
				30L,
				TimeUnit.SECONDS,
				new ArrayBlockingQueue<>(1),
				r -> {
					Thread thread = new Thread(r);
					thread.setName("Thread-PushWaitingMessage-worker");
					return thread;
				});
		executorService.execute(new PushCustomerMessage());
	}
	
	@Override
	public synchronized int addWaiting(Customer customer) {
		Integer teamCode = customer.getTeamCode();
		CustomQueue<Customer> customers = nameFactory.getQueue(CUSTOMER_WAIT_QUEUE, teamCode.toString());
		int site = fixQueue(customers, customer);
		CustomerWaitStoreManager.getInst().enterQueue(customer, customers.size());
		return site;
	}
	
	@Override
	public synchronized void removeWaiting(Customer customer) {
		Integer teamCode = customer.getTeamCode();
		CustomQueue<Customer> customers = nameFactory.getQueue(CUSTOMER_WAIT_QUEUE, teamCode.toString());
		if (customers != null && customers.size() > 0) {
			customers.remove(customer);
			// 更新客户编号
			addEvent(customers.getName());
			// 更新客服排队数
			broadcastWaiter(customer.getTeamCode(), customers.size());
			CustomerWaitStoreManager.getInst().leaveQueue(customer, customers.size());
			log.info("排队过程中，客户:{} 离开队列，进行移除!", customer.getUid());
		}
	}
	
	@Override
	public Customer acquire(Integer teamCode) {
		CustomQueue<Customer> customers = nameFactory.getQueue(CUSTOMER_WAIT_QUEUE, teamCode.toString());
		Customer customer = customers.poll();
		if (customers.size() > 0) {
			addEvent(customers.getName());
		}
		while (null != customer) {
			long enterTime = customer.getTime();
			long waitTime = (System.currentTimeMillis() - enterTime) / 1000;
			// 如果排队时间超过2个小时，说明数据存在问题，直接过滤掉
			if (waitTime < 2 * 60 * 60) {
				broadcastWaiter(teamCode, customers.size());
				CustomerWaitStoreManager.getInst().leaveQueue(customer, customers.size());
				return customer;
			}
			customer = customers.poll();
		}
		return null;
	}
	
	@Override
	public List<Customer> getWaits(Integer teamCode) {
		CustomQueue<Customer> queue = nameFactory.getQueue(CUSTOMER_WAIT_QUEUE, teamCode.toString());
		if (null != queue && queue.size() > 0) {
			return new ArrayList<>(queue);
		}
		return null;
	}
	
	@Override
	public int size(Integer teamCode) {
		CustomQueue<Customer> queue = nameFactory.getQueue(CUSTOMER_WAIT_QUEUE, teamCode.toString());
		return queue.size();
	}
	
	private int fixQueue(Queue<Customer> queue, Customer customer) {
		queue.add(customer);
		broadcastWaiter(customer.getTeamCode(), queue.size());
		return queue.size();
	}
	
	private void broadcastWaiter(Integer teamCode, Integer queueCount) {
		Collection<Waiter> waiters = OcImServer.getInst().getDispatcher().getWaiters(teamCode);
		Body body = new Body(BodyType.WAITTING_NO, queueCount.toString());
		Packet packet = new Packet(PacketType.BROADCAST, body);
		AddressTo to;
		for (Waiter waiter : waiters) {
			to = new AddressTo(waiter.getWaiterCode(), waiter.getWaiterName(), Identity.WAITER);
			packet.setTo(to);
			if (!"4".equals(waiter.getStatus())) {
				OcImServer.getInst().getRoutingTable().routePacket(packet);
			}
		}
	}
	
	private void addEvent(String queueName) {
		sets.add(queueName);
	}
	
	/**
	 * 更新推送客户排队编号
	 */
	private class PushCustomerMessage implements Runnable {

		@SuppressWarnings("InfiniteLoopStatement")
		@Override
		public void run() {
			List<String> removeSets = new LinkedList<>();
			while(true) {
				try {
					for (String queueName : sets) {
						removeSets.add(queueName);
						CustomQueue<Customer> queue = nameFactory.getQueue(queueName);
						pushWaitMessage(queue);
					}
					if (!removeSets.isEmpty()) {
						sets.removeAll(removeSets);
						removeSets.clear();
					}
					TimeUnit.SECONDS.sleep(5);
				} catch (Exception e) {
					sets.removeAll(removeSets);
					removeSets.clear();
					log.error("更新排队编号发生异常：{}", e.getMessage());
				}
			}
		}

		private void pushWaitMessage(CustomQueue<Customer> queue) {
			AddressFrom from = new AddressFrom(Identity.WAITER);
			int i = 0;
			for (Customer customer : queue) {
				Packet packet = new Packet(PacketType.BROADCAST);
				packet.setBody(new Body(BodyType.WAITTING_NO, String.valueOf(++i)));
				packet.setFrom(from);
				packet.setTo(new AddressTo(customer.getUid(), customer.getName(), Identity.CUSTOMER));
				OcImServer.getInst().getRoutingTable().routePacket(packet);
			}
		}
	}
}
