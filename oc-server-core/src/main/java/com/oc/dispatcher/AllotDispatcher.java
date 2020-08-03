package com.oc.dispatcher;

import com.oc.common.utils.UUIDUtils;
import com.oc.core.OcImServer;
import com.oc.core.factory.PacketTransferFactory;
import com.oc.dispatcher.register.Event;
import com.oc.dispatcher.register.EventRegister;
import com.oc.dispatcher.register.EventType;
import com.oc.dispatcher.room.CustomerRoom;
import com.oc.dispatcher.room.CustomerRoomImpl;
import com.oc.dispatcher.room.WaiterRoom;
import com.oc.dispatcher.room.WaiterRoomImpl;
import com.oc.dispatcher.scheduler.EventScheduler;
import com.oc.domain.BuildChat;
import com.oc.domain.team.Team;
import com.oc.domain.waiter.Waiter;
import com.oc.message.AddressFrom;
import com.oc.message.AddressTo;
import com.oc.message.Packet;
import com.oc.message.body.Body;
import com.oc.message.type.BodyType;
import com.oc.message.type.Identity;
import com.oc.message.type.PacketType;
import com.oc.provider.cache.LocalTeamStore;
import com.oc.provider.cache.LocalWaiterStore;
import com.oc.provider.redis.IDProvider;
import com.oc.provider.redis.NearServiceProvider;
import com.oc.session.Customer;
import com.oc.session.CustomerAssignStatus;
import com.oc.session.CustomerSession;
import com.oc.session.WaiterSession;
import com.oc.transfer.TransferTeam;
import com.oc.transfer.TransferWaiter;
import com.oc.util.JsonUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Queue;

/**
 * @Description: 调度中心
 * @author chuangyeifang
 * @createDate 2020年2月2日
 * @version v 1.0
 */
@Slf4j
public final class AllotDispatcher implements Dispatcher{

	@SuppressWarnings("FieldCanBeLocal")
	private NameFactory nameFactory;
	private EventRegister eventRegister;
	private WaiterRoom waiterRoom;
	private CustomerRoom customerTable;
	@SuppressWarnings("FieldCanBeLocal")
	private EventScheduler eventScheduler;

	public AllotDispatcher() {
		nameFactory = new NameFactory();
		eventRegister = new EventRegister(nameFactory);
		waiterRoom = new WaiterRoomImpl(nameFactory, eventRegister);
		customerTable = new CustomerRoomImpl(nameFactory);
		eventScheduler = new EventScheduler(this, eventRegister);
		eventScheduler.start();
	}

	@Override
	public void login(Waiter waiter) {
		waiterRoom.login(waiter);
	}

	@Override
	public void logout(WaiterSession session) {
		if (null != session) {
			Integer teamCode = session.getWaiter().getTeamCode();
			waiterRoom.logout(teamCode, session.getUid());
		}
	}

	@Override
	public void changeWaiterStatus(Integer teamCode, String waiterCode, String status) {
		waiterRoom.changeStatus(teamCode, waiterCode, status);
	}

	@Override
	public Collection<Waiter> getWaiters(Integer teamCode) {
		return waiterRoom.getWaiters(teamCode);
	}

	@Override
	public Waiter acquireWaiter(Integer teamCode) {
		return waiterRoom.acquire(teamCode);
	}

	@Override
	public Waiter acquireWaiter(Integer teamCode, String waiterCode) {
		return waiterRoom.acquire(teamCode, waiterCode);
	}

	@Override
	public Customer acquireCustomer(Integer teamCode) {
		return customerTable.acquire(teamCode);
	}

	@Override
	public void addWaiting(Customer customer) {
		customer.setTime(System.currentTimeMillis());
		int waitNo = customerTable.addWaiting(customer);
		Body body = new Body(BodyType.WAITTING, String.valueOf(waitNo));
		AddressTo to = new AddressTo(customer.getUid(), Identity.CUSTOMER);
		AddressFrom from = new AddressFrom("sys", Identity.SYS);
		Packet packet = new Packet(PacketType.BUILD_CHAT, from, to, body);
		OcImServer.getInst().getRoutingTable().routePacket(packet);
	}

	@Override
	public List<Customer> getWaits(Integer teamCode) {
		return customerTable.getWaits(teamCode);
	}

	@Override
	public void removeWait(Customer customer) {
		customerTable.removeWaiting(customer);
	}

	@Override
	public boolean existWait(Integer teamCode) {
		return 0 != customerTable.size(teamCode);
	}

	@Override
	public void registerAllotEvent(Event event) {
    	if (!hasAllotEvent(event)) {
    		eventRegister.register(event);
    	} else {
    	    log.info("分配事件已经存在：{}", event);
        }
	}

	@Override
	public boolean hasAllotEvent(Event event) {
		return eventRegister.hasEvent(event);
	}

	@Override
	public boolean cancelAllotRegister(Event event) {
		return eventRegister.cancel(event);
	}

	@Override
	public void buildRelation(Event event) {
		if (null != event) {
			switch (event.getType()) {
				case CUSTOMER_REQ:
					dealCustomerRequestEvent(event);
					break;
				case READY_DONE:
					dealReadyDoneEvent(event);
					break;
				case WAITER_MANUAL_REQ:
					dealWaiterManualReq(event);
					break;
				case TRANSFER_TEAM:
					dealTransferTeam(event);
					break;
				default:
					throw new IllegalArgumentException("不支持的分配事件类型");
			}
		} else {
			throw new IllegalArgumentException("解析的分配事件为空");
		}
	}

	@Override
	public void directReleaseRelation(String waiterCode) {
		Integer teamCode = LocalWaiterStore.getInst().getTeamCode(waiterCode);
		waiterRoom.release(teamCode, waiterCode);
	}

	@Override
	public synchronized void closeChat(CustomerSession session) {
		if (session.getStatus() == CustomerAssignStatus.ASSIGNED) {
			waiterRoom.release(session.getTeamCode(), session.getWaiterCode());
			session.setStatus(CustomerAssignStatus.UNASSIGNED);
			session.setWaiter(null, null);
		}
	}
	
	@Override
	public boolean transferByWaiter(TransferWaiter transferWaiter) {
		return waiterRoom.tryTransferByWaiter(transferWaiter);
	}

	@Override
	public boolean transferByTeam(TransferTeam transferTeam) {
		Event event = new Event(EventType.TRANSFER_TEAM, transferTeam.getUid(),
				transferTeam.getTtc(), transferTeam.getTmc());
		try {
			event.setContent(JsonUtils.getJson().writeString(transferTeam));
		} catch (IOException e) {
			log.error("序列化团队转接信息失败 TransferTeam：{}", transferTeam);
		}
		eventRegister.register(event);
		return false;
	}

	/**
	 * 执行最后分配任务
	 * @param event
	 */
	private void dealReadyDoneEvent(Event event) {
		String waiterName = event.getWaiterName();
		String waiterCode = event.getWaiterCode();
		CustomerSession customerSession = OcImServer.getInst().getRoutingTable().getLocalCustomerSession(event.getUid());
		if (null == customerSession) {
			directReleaseRelation(event.getWaiterCode());
			event.setType(EventType.WAITER_IDLE);
			// 分配失败 重新注册客服空闲事件
			registerAllotEvent(event);
			log.warn("客户uid: {}, 分配客服过程离开，无需处理 READY_DONE", event.getUid());
		} else {
			synchronized (customerSession) {
				if (customerSession.getStatus() != CustomerAssignStatus.ASSIGNED) {
					this.calcCustomerWaitTime(customerSession.getCustomer());
					Waiter waiter = waiterRoom.getWaiter(event.getTeamCode(), waiterCode);
					customerSession.setWaiter(waiterCode, waiterName);
					buildChat(customerSession, waiter);
				}
			}
		}
	}
	
	/**
	 * 客户发起请求
	 * @param event
	 */
	private void dealCustomerRequestEvent(Event event) {
		CustomerSession customerSession = OcImServer.getInst().getRoutingTable().getLocalCustomerSession(event.getUid());
		if (null != customerSession) {
			allotWaiterForCustomer(customerSession);
		} else {
            log.warn("处理客户请求分配事件，无法找到会话Session, Event: {}", event);
        }
	}

	/**
	 * 处理客服手动接入
	 * @param event
	 */
	private void dealWaiterManualReq(Event event) {
		CustomerSession customerSession = OcImServer.getInst().getRoutingTable().getLocalCustomerSession(event.getUid());
		if (null != customerSession) {
			synchronized (customerSession) {
				Waiter waiter = waiterRoom.directAcquire(event.getTeamCode(), event.getWaiterCode());
				if (null != waiter) {
					this.calcCustomerWaitTime(customerSession.getCustomer());
					customerSession.setWaiter(waiter.getWaiterCode(), waiter.getWaiterName());
					buildChat(customerSession, waiter);
				}
			}
		} else {
			log.info("客服自动接入过程中，客户自动离开，停止为当前客户分配客服");
		}
	}

	/**
	 * 处理按团队转接客户
	 * @param event
	 */
	private void dealTransferTeam(Event event) {
		TransferTeam transferTeam;
		try {
			transferTeam = JsonUtils.getJson().readClass(event.getContent(), TransferTeam.class);
		} catch (IOException e) {
			log.error("转接团队反序列化失败， TransferTeam: {}", event.getContent());
			return;
		}
		CustomerSession customerSession = OcImServer.getInst().getRoutingTable().getLocalCustomerSession(event.getUid());
		if (null != customerSession) {
			if (null != transferTeam) {
				customerSession.setStatus(CustomerAssignStatus.ASSIGNING);
				customerSession.setWaiter(null, null);

				PacketTransferFactory.getInst().transferByTeamSuccess(customerSession.getCid(),
						transferTeam, "提示，转入客户成功。");

				// 转接成功释放连接
				directReleaseRelation(transferTeam.getFromWc());

				Customer customer = customerSession.getCustomer();
				customer.setTenantCode(transferTeam.getTtc());
				customer.setTeamCode(transferTeam.getTmc());
				customer.setSkillCode(transferTeam.getSkc());
				customer.setSkillName(transferTeam.getSkn());
				// 获取客服
				Waiter waiter = allotTransferTeam(customerSession);
				Packet transferTeamPacket;
				String content;
				AddressTo to;
				// 处于排队情况，需要通知客户正在排队
				if (null == waiter) {
					content = "非常抱歉，为您转接的服务队列客服人员繁忙，已加入服务队列等待列表，还请您耐心等待...";
					transferTeamPacket = createCacheTransferPacket(customerSession, transferTeam, content);
					to = new AddressTo(customer.getUid(), customerSession.getName(), Identity.CUSTOMER);

					content = "提示，客户来自工号【" + transferTeam.getFromWc() + "】转入， 备注信息：" + transferTeam.getReason();
					Packet cacheTransferTeamPacket = createCacheTransferPacket(customerSession, transferTeam, content);
					customerSession.cachePacket(cacheTransferTeamPacket);
				} else {
					content = "客户来自工号【" + transferTeam.getFromWc() + "】的转接， 备注信息：" + transferTeam.getReason();
					transferTeamPacket = createCacheTransferPacket(customerSession, transferTeam, content);
					to = new AddressTo(waiter.getWaiterCode(), waiter.getWaiterName(), Identity.WAITER);
				}
				transferTeamPacket.setTo(to);
				OcImServer.getInst().getRoutingTable().routePacket(transferTeamPacket);
			}
		} else {
			PacketTransferFactory.getInst().transferByTeamSuccess(customerSession.getCid(),
					transferTeam, "提示，客户状态有误， 拒绝转入。");
		}
	}

	/**
	 * 当按团队转接时，未直接获取客服，则缓存转接备注信息
	 * @param customerSession
	 * @param transferTeam
	 * @param content
	 * @return
	 */
	private Packet createCacheTransferPacket(CustomerSession customerSession, TransferTeam transferTeam, String content) {
		Packet packet = new Packet(PacketType.MESSAGE);
		Body body = new Body(BodyType.TIP, content);
		AddressFrom from = new AddressFrom(transferTeam.getUid(), transferTeam.getName(), Identity.CUSTOMER);
		packet.setPid(UUIDUtils.createPid(911));
		packet.setTtc(customerSession.getTenantCode());
		packet.setTmc(customerSession.getTeamCode());
		packet.setFrom(from);
		packet.setBody(body);
		return packet;
	}

	/**
	 * 分配转接团队
	 * @param customerSession
	 * @return
	 */
	private Waiter allotTransferTeam(CustomerSession customerSession) {
		Customer customer = customerSession.getCustomer();
		// 判断排队队列是否有排队客户，如果存在直接进行排队
		if (isWaitingAndAdd(customer)) {
			return null;
		}
		synchronized (customerSession) {
			Waiter waiter = cyclicAllot(customer);
			// 分配客服
			if (null != waiter) {
				customerSession.setWaiter(waiter.getWaiterCode(), waiter.getWaiterName());
				buildChat(customerSession, waiter);
				return waiter;
			}
		}
		return null;
	}

	/**
	 * 分配客服
	 * @param customerSession
	 */
	private void allotWaiterForCustomer(CustomerSession customerSession) {
		Customer customer = customerSession.getCustomer();
		// 判断排队队列是否有排队客户，如果存在直接进行排队
		if (isWaitingAndAdd(customer)) {
			return;
		}
		synchronized (customerSession) {
			if (customerSession.getStatus() != CustomerAssignStatus.ASSIGNED) {
				// 记忆分配
				Waiter waiter = memoryAllot(customer);
				// 默认按照轮训分配客服
				if (null == waiter) {
					waiter = cyclicAllot(customer);
				}
				// 分配客服
				if (null != waiter) {
					customerSession.setWaiter(waiter.getWaiterCode(), waiter.getWaiterName());
					buildChat(customerSession, waiter);
				}
			}
		}
	}

	/**
	 * 判断是否有排队
	 * @param customer
	 * @return
	 */
	private boolean isWaitingAndAdd(Customer customer) {
		// 判断排队队列是否有排队客户，如果存在直接进行排队
		if (this.existWait(customer.getTeamCode())) {
			this.addWaiting(customer);
			log.info("团队编号：{}, 当前团队客服繁忙进入排队队列. 当前客户：{}", customer.getTeamCode(), customer.getUid());
			return true;
		}
		return false;
	}

	/**
	 * 记忆分配
	 * @param customer
	 * @return
	 */
	private Waiter memoryAllot(Customer customer) {
		Waiter waiter = null;
		String assignRule = LocalTeamStore.getInst().getTeamAssignRule(customer.getTeamCode());
		if ("0".equals(assignRule)) {
			String nearWaiterCode = NearServiceProvider.getInst().getNearWaiter(customer.getTeamCode(), customer.getUid());
			if (null != nearWaiterCode) {
				waiter = this.acquireWaiter(customer.getTeamCode(), nearWaiterCode);
			}
			if (null != waiter) {
				log.info("团队编号：{}, 采用记忆分配规则. 当前客户：{}", customer.getTeamCode(), customer.getUid());
			}
		}
		return waiter;
	}

	/**
	 * 轮训分配
	 * @param customer
	 * @return
	 */
	private Waiter cyclicAllot(Customer customer) {
		Waiter waiter = this.acquireWaiter(customer.getTeamCode());
		// 当前客服繁忙，没有空闲客服，客户加入等待队列
		if (null == waiter) {
			this.addWaiting(customer);
			log.info("团队编号：{}, 未获取到客服资源，加入等待队列. 当前客户：{}", customer.getTeamCode(), customer.getUid());
		} else {
			log.info("团队编号：{}, 采用轮训分配规则. 当前客户：{}", customer.getTeamCode(), customer.getUid());
		}
		return waiter;
	}
	
	/**
	 * 构建会话
	 * @param customerSession
	 */
	private void buildChat(CustomerSession customerSession, Waiter waiter) {
		// 设置客户分配状态为已分配
		customerSession.setStatus(CustomerAssignStatus.ASSIGNED);
		// 生成ChatID
		String chatId = IDProvider.getInstance().getChatId();
		customerSession.setCid(chatId);
		// 分配客服成功， 给用户发送消息
		noticeBuildChatToCustomer(customerSession);
		// 发送团队自动回复语
		teamAutoReply(customerSession);
		// 发送客服自动回复语
		waiterAutoReply(customerSession);
		// 通知客服 与 客户创建连接成功
		noticeBuildChatToWaiter(customerSession);
		// 缓存的消息
		sendCustomerPacketCache(customerSession);
		// 缓存最近接待客服
		cacheNearCustomer(customerSession.getCustomer(), waiter.getWaiterCode());
	}

	/**
	 * 通知客户分配客服成功 创建会话成功
	 * @param customerSession
	 */
	private void noticeBuildChatToCustomer(CustomerSession customerSession) {
		String uid = customerSession.getUid();
		String waiterCode = customerSession.getWaiterCode();
		Packet buildChatPacket = new Packet(PacketType.BUILD_CHAT);

		String content = createBuildChat(customerSession);
		Body body = new Body(BodyType.SUCCESS, content);
		AddressTo to = new AddressTo(uid, customerSession.getIdy());
		AddressFrom from = new AddressFrom(waiterCode, Identity.SYS);
		buildChatPacket.setPid(UUIDUtils.createPid(901));
		buildChatPacket.setCid(customerSession.getCid());
		buildChatPacket.setTo(to);
		buildChatPacket.setFrom(from);
		buildChatPacket.setBody(body);
		OcImServer.getInst().getRoutingTable().routePacket(buildChatPacket);
	}

	/**
	 * 通知客服 客户分配进线
	 * @param customerSession
	 */
	private void noticeBuildChatToWaiter(CustomerSession customerSession) {
		String uid = customerSession.getUid();
		String waiterCode = customerSession.getWaiterCode();
		Customer customer = customerSession.getCustomer();
		String chatId = customerSession.getCid();
		String content = createBuildChat(customerSession);

		Packet buildChatPacket = new Packet(PacketType.BUILD_CHAT);
		Body body = new Body(BodyType.SUCCESS, content);
		buildChatPacket.setPid(UUIDUtils.createPid(904));
		buildChatPacket.setTtc(customerSession.getTenantCode());
		buildChatPacket.setTmc(customerSession.getTeamCode());
		AddressTo to = new AddressTo(waiterCode, Identity.WAITER);
		AddressFrom from = new AddressFrom(uid, customer.getName(), customerSession.getIdy());
		buildChatPacket.setCid(chatId);
		buildChatPacket.setTo(to);
		buildChatPacket.setFrom(from);
		buildChatPacket.setBody(body);
		OcImServer.getInst().getRoutingTable().routePacket(buildChatPacket);
	}

	/**
	 * 构建会话信息
	 * @param customerSession
	 * @return
	 */
	private String createBuildChat(CustomerSession customerSession) {
		Customer customer = customerSession.getCustomer();
		Team team = LocalTeamStore.getInst().getTeam(customer.getTeamCode());
		BuildChat buildChat = new BuildChat(customer.getTenantCode(), customer.getTeamCode(),
				team.getBriefName(),customer.getSkillCode(), customer.getSkillName(),
				customer.getGoodsCode(), customer.isLogin(), customer.getDevice());
		String content = null;
		try {
			content = JsonUtils.getJson().writeString(buildChat);
		} catch (IOException e) {
			log.error("序列化失败 BuildChat: {}", buildChat);
			e.printStackTrace();
		}
		return content;
	}

	/**
	 * 团队自动回复语
	 * @param customerSession
	 */
	private void teamAutoReply(CustomerSession customerSession) {
		String replyMsg = LocalTeamStore.getInst().getTeamReply(customerSession.getTeamCode());
		if (null != replyMsg) {
			sendAutoReplyMsg(customerSession, replyMsg, 909, BodyType.TEAM_GREET);
		}
	}

	/**
	 * 判断客服是否设置自动回复，如果设置则发送。
	 * @param customerSession
	 */
	private void waiterAutoReply(CustomerSession customerSession) {
		String waiterCode = customerSession.getWaiterCode();

		LocalWaiterStore.Result result = LocalWaiterStore.getInst().getWaiterReply(waiterCode);
		if (result.getRc() == 0) {
			sendAutoReplyMsg(customerSession, result.getMsg(), 902, BodyType.WAITER_GREET);
		}
	}

	/**
	 * 发送自动回复语
	 * @param customerSession
	 * @param replyMsg
	 * @param prePid
	 * @param bodyType
	 */
	private void sendAutoReplyMsg(CustomerSession customerSession, String replyMsg, int prePid, BodyType bodyType) {
		String uid = customerSession.getUid();
		String waiterCode = customerSession.getWaiterCode();
		Body body = new Body(bodyType, replyMsg);

		Packet greetPacketCustomer = new Packet(PacketType.BUILD_CHAT);
		greetPacketCustomer.setPid(UUIDUtils.createPid(prePid));
		greetPacketCustomer.setCid(customerSession.getCid());
		greetPacketCustomer.setTtc(customerSession.getTenantCode());
		greetPacketCustomer.setTmc(customerSession.getTeamCode());
		greetPacketCustomer.setTo(new AddressTo(uid, customerSession.getName(), customerSession.getIdy()));
		greetPacketCustomer.setFrom(new AddressFrom(waiterCode, Identity.SYS));
		greetPacketCustomer.setBody(body);
		OcImServer.getInst().getRoutingTable().routePacket(greetPacketCustomer);

		// TODO SYS
		Packet greetPacketWaiter = new Packet(PacketType.BUILD_CHAT);
		greetPacketWaiter.setPid(UUIDUtils.createPid(prePid));
		greetPacketWaiter.setCid(customerSession.getCid());
		greetPacketWaiter.setTtc(customerSession.getTenantCode());
		greetPacketWaiter.setTmc(customerSession.getTeamCode());
		greetPacketWaiter.setTo(new AddressTo(waiterCode, Identity.SYS));
		greetPacketWaiter.setFrom(new AddressFrom(uid, customerSession.getName(), customerSession.getIdy()));
		greetPacketWaiter.setBody(body);
		OcImServer.getInst().getRoutingTable().routePacket(greetPacketWaiter);
	}

	/**
	 * 发送客户未分配客服前缓存的消息
	 * @param customerSession
	 */
	private void sendCustomerPacketCache(CustomerSession customerSession) {
		String waiterCode = customerSession.getWaiterCode();
		Queue<Packet> packets = customerSession.transportStore().getFuturePackets();
		Packet pt = packets.poll();
		while (pt != null) {
			pt.setCid(customerSession.getCid());
			pt.setTo(new AddressTo(waiterCode, Identity.WAITER));
			OcImServer.getInst().getRoutingTable().routePacket(pt);
			pt = packets.poll();
		}
	}

	/**
	 * 缓存客户最近咨询客服ID
	 * @param customer
	 * @param waiterCode
	 */
	private void cacheNearCustomer(Customer customer, String waiterCode) {
		String assignRule = LocalTeamStore.getInst().getTeamAssignRule(customer.getTeamCode());
		if ("0".equals(assignRule)) {
			// 缓存最近咨询客服工号
			NearServiceProvider.getInst().cacheNearWaiter(customer.getTeamCode(), customer.getUid(), waiterCode);
		}
	}

	/**
	 * 计算排队等待时间
	 * @param customer
	 */
	private void calcCustomerWaitTime(Customer customer) {
		long waitTime = (System.currentTimeMillis() - customer.getTime()) / 1000;
		customer.setWait(Math.toIntExact(waitTime));
	}
}
