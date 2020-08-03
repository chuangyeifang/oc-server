package com.oc.provider.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.oc.domain.waiter.Waiter;
import com.oc.provider.context.SpringContext;
import com.oc.service.user.WaiterService;
import com.oc.service.user.impl.WaiterServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 客服信息本地缓存
 * @author chuangyeifang
 * @createDate 2020年6月21日
 * @version v 1.0
 */
public class LocalWaiterStore {
	private static Logger log = LoggerFactory.getLogger(LocalWaiterStore.class);

	private WaiterService waiterService = SpringContext.getBean(WaiterServiceImpl.class);
	
	private LoadingCache<String, Waiter> waiterByCodeCache;

	private LocalWaiterStore() {
		initCache();
	}

	/**
	 * 初始化本地缓存
	 */
	private void initCache() {
		long expireOfSeconds = 60 * 5;
		waiterByCodeCache = CacheBuilder.newBuilder()
				.expireAfterWrite(expireOfSeconds, TimeUnit.SECONDS)
				.build(new CacheLoader<String, Waiter>() {

					@Override
					@ParametersAreNonnullByDefault
					public Waiter load(String waiterCode) {
						return waiterService.obtainWaiterByCode(waiterCode);
					}
				});
	}
	
	/**
	 * 根据客服编码获取客服所在团队
	 * @param waiterCode
	 * @return
	 */
	public Integer getTeamCode(String waiterCode) {
		try {
			Waiter waiter = waiterByCodeCache.get(waiterCode);
			if (null != waiter) {
				return waiter.getTeamCode();
			}
		} catch (ExecutionException e) {
			log.error("根据编码：{}, 获取客服团队编码发生异常：{}", waiterCode, e);
		}
		return null;
	}
	
	/**
	 * 根据客服编码获取客服自动回复语
	 * 如果客服设置自动回复，则返回消息内容。
	 * 否则返回NULL
	 * @param waiterCode
	 * @return
	 */
	public Result getWaiterReply(String waiterCode) {
		try {
			Waiter waiter = waiterByCodeCache.get(waiterCode);
			String autoReply = "1";
			if (null != waiter && autoReply.equals(waiter.getAutoReply())) {
				return new Result(0, waiter.getReplyMsg());
			}
		} catch (ExecutionException e) {
			log.error("根据编码：{}, 获取客服团队编码发生异常：{}", waiterCode, e);
		}
		return new Result(-1, "未开启自动回复");
	}

	public static class Result {
		private int rc;

		private String msg;

		public Result(int rc, String msg) {
			this.rc = rc;
			this.msg = msg;
		}

		public int getRc() {
			return rc;
		}

		public String getMsg() {
			return msg;
		}
	}
	
	public static LocalWaiterStore getInst() {
		return Single.instance;
	}
	
	private static class Single {
		private static LocalWaiterStore instance = new LocalWaiterStore();
	}
}
