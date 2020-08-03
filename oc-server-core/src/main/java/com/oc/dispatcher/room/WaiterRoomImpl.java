package com.oc.dispatcher.room;

import com.oc.dispatcher.NameFactory;
import com.oc.dispatcher.cache.WaiterCache;
import com.oc.dispatcher.cache.WaiterServiceCache;
import com.oc.dispatcher.cache.po.ConciseWaiter;
import com.oc.dispatcher.register.Event;
import com.oc.dispatcher.register.EventRegister;
import com.oc.dispatcher.register.EventType;
import com.oc.domain.waiter.Waiter;
import com.oc.provider.db.WaiterMonitorProvider;
import com.oc.provider.db.WaiterProvider;
import com.oc.transfer.TransferWaiter;

import java.util.Collection;
import java.util.List;

/**
 * @Description: Waiter 为沙滩管理员
 * @author chuangyeifang
 * @createDate 2020年2月2日
 * @version v 1.0
 */
public final class WaiterRoomImpl implements WaiterRoom{
	
	private final static String SHUNT_YES = "1";
	private final static  String STATUS_ONLINE = "1";


	private WaiterServiceCache waiterServiceCache;
	private WaiterCache waiterCache;
	private EventRegister register;

	
	public WaiterRoomImpl(NameFactory nameFactory, EventRegister register) {
		this.register = register;
		this.waiterCache = new WaiterCache(nameFactory);
		this.waiterServiceCache = new WaiterServiceCache(nameFactory);
	}
	
	/**
	 * 客服登入
	 * @param waiter
	 */
	@Override
	public synchronized void login(Waiter waiter) {
		String waiterCode = waiter.getWaiterCode();
		Integer teamCode = waiter.getTeamCode();
		Long score = System.currentTimeMillis();

		//如果客服登录过系统，则保存客服状态，防止客服端异常关闭
		Waiter existWaiter = waiterCache.get(teamCode, waiterCode);
		if (null != existWaiter) {
			waiter.setCurReception(existWaiter.getCurReception());
		}
		waiterCache.put(teamCode, waiter);

		//判定是否分流 0 不分流 1分流
		if(SHUNT_YES.equals(waiter.getShunt())) {
			ConciseWaiter conciseWaiter = waiterServiceCache.get(teamCode, waiterCode);
			if (null == conciseWaiter) {
				conciseWaiter = new ConciseWaiter(waiterCode, teamCode, score);
			}
			waiterServiceCache.put(teamCode, conciseWaiter);
			registerReleaseEvent(waiterCode, waiter.getTenantCode(), waiter.getTeamCode());
		}
		// 更新客服监控
		WaiterMonitorProvider.getInst().waiterEnterService(waiter, waiter.getStatus());
		// 更新客服状态
		WaiterProvider.getInst().updateStatus(waiter.getId(), waiter.getStatus());
	}

	/**
	 * 客服登出
	 * @param waiterCode
	 * @param teamCode
	 */
	@Override
	public synchronized void logout(Integer teamCode, String waiterCode) {
		waiterServiceCache.remove(teamCode, waiterCode);
		Waiter waiter = waiterCache.remove(teamCode, waiterCode);
		if (null != waiter) {
			// 更新客服监控状态
			WaiterMonitorProvider.getInst().updateStatus(waiter, waiter.getStatus(), "4");
			// 更新客服状态
			WaiterProvider.getInst().waiterLogout(waiter.getId());
		}
	}
	
	/**
	 * 按客服编码获取客服，适用于记忆分配规则
	 * 如果当前客服编码客服处于接待上限，则按照分配规则进行分配
	 * @return
	 */
	@Override
	public Waiter acquire(Integer teamCode, String waiterCode) {
		WaiterCache.AcquireResult acquire = waiterCache.acquire(teamCode, waiterCode);
		if (acquire.isHas()) {
			// 当前接待数 等于 最大接待数 移除服务队列不在参与分流;
			if(acquire.isThreshold()) {
				waiterServiceCache.remove(teamCode, acquire.getWaiter().getWaiterCode());
			}
		}
		return acquire.getWaiter();
	}
	
	/**
	 * 根据团队编码，按照分配规则进行获取客服。
	 * 客服需要处于在线状态并且未达到接待上限，方可获取
	 * 未获取到则返回null
	 * @return
	 */
	@Override
	public Waiter acquire(Integer teamCode) {
		Waiter waiter;
		List<ConciseWaiter> conciseWaiters = waiterServiceCache.get(teamCode);
		for (ConciseWaiter conciseWaiter : conciseWaiters) {
			WaiterCache.AcquireResult acquire = waiterCache.acquire(teamCode, conciseWaiter.getWaiterCode());
			if (acquire.isHas()) {
				waiter = acquire.getWaiter();
				// 当前接待数 等于 最大接待数 移除服务队列不在参与分流; 否则更新分数
				if(acquire.isThreshold()) {
					waiterServiceCache.remove(teamCode, conciseWaiter.getWaiterCode());
				} else {
					conciseWaiter.setScore(System.currentTimeMillis());
					waiterServiceCache.update(teamCode, conciseWaiter);
				}
				return waiter;
			}
		}
		return null;
	}
	
	/**
	 * 直接获取客服
	 * 无论处于何状态或接待是否已到上限，都可获取
	 * @param waiterCode
	 * @return
	 */
	@Override
	public Waiter directAcquire(Integer teamCode, String waiterCode) {
		WaiterCache.AcquireResult acquire = waiterCache.directAcquire(teamCode, waiterCode);
		// 当前接待数 等于 最大接待数 移除服务队列不在参与分流;
		if (acquire.isHas() && acquire.isThreshold()) {
			waiterServiceCache.remove(teamCode, waiterCode);
		}
		return acquire.getWaiter();
	}

	@Override
	public Waiter getWaiter(Integer teamCode, String waiterCode) {
		return waiterCache.get(teamCode, waiterCode);
	}

	@Override
	public void release(Integer teamCode, String waiterCode) {
		WaiterCache.ReleaseResult release = waiterCache.release(teamCode, waiterCode);
		if (release.isHas()) {
			// 当接待数 等于 （最大接待数 - 1） 空闲临界
			if (release.isThreshold()) {
				// 客服是否在线状态
				if (STATUS_ONLINE.equals(release.getWaiter().getStatus())) {
					// 客服是否分流
					if (SHUNT_YES.equals(release.getWaiter().getShunt())) {
						// 将客服放入服务队列
						waiterServiceCache.put(teamCode, waiterCode);
						// 注册客服空闲事件
						registerReleaseEvent(waiterCode, release.getWaiter().getTenantCode(),
								release.getWaiter().getTeamCode());
					}
				}
			}
		}
	}

	@Override
	public boolean tryTransferByWaiter(TransferWaiter transferWaiter){
		Integer teamCode = transferWaiter.getTmc();
		String waiterCode = transferWaiter.getToWc();
		WaiterCache.AcquireResult acquire = waiterCache.acquire(teamCode, waiterCode);
		if (acquire.isHas()) {
			// 当前接待数 等于 最大接待数 移除服务队列不在参与分流;
			if (acquire.isThreshold()) {
				waiterServiceCache.remove(teamCode, waiterCode);
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean changeStatus(Integer teamCode, String waiterCode, String status) {
		WaiterCache.UpdateStatusResult updateStatusResult = waiterCache.updateStatus(teamCode, waiterCode, status);

		Waiter waiter = updateStatusResult.getWaiter();
		String beforeStatus = updateStatusResult.getOldStatus();

		if (status.equals(beforeStatus)) {
			return true;
		}
		// 更新客服监控状态
		WaiterMonitorProvider.getInst().updateStatus(waiter, beforeStatus, status);
		// 更新客服表 状态
		WaiterProvider.getInst().updateStatus(waiter.getId(), status);
		// 记录客服状态操作日志
		WaiterProvider.getInst().insertWaiterLog(waiter, "2", null);

		if (STATUS_ONLINE.equals(status)) {
			if(SHUNT_YES.equals(waiter.getShunt())) {
				ConciseWaiter conciseWaiter = new ConciseWaiter(waiterCode, teamCode);
				waiterServiceCache.put(teamCode, conciseWaiter);
				registerReleaseEvent(waiterCode, waiter.getTenantCode(), waiter.getTeamCode());
			}
		} else {
			waiterServiceCache.remove(teamCode, waiterCode);
		}
		return true;
	}

	@Override
	public Collection<Waiter> getWaiters(Integer teamCode) {
		return waiterCache.get(teamCode);
	}

	/**
	 * 客服有空闲资源时，注册可服务事件
	 * @param waiterCode
	 * @param tenantCode
	 * @param teamCode
	 */
	private void registerReleaseEvent(String waiterCode, String tenantCode, Integer teamCode) {
		Event event = new Event(EventType.WAITER_IDLE, waiterCode, tenantCode, teamCode);
		register.register(event);
	}
}
