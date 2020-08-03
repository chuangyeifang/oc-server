package com.oc.core.bs.oauth;

import com.oc.auth.CustomerAuthCoder;
import com.oc.auth.CustomerInfo;
import com.oc.auth.WaiterAuthCoder;
import com.oc.auth.WaiterInfo;
import com.oc.core.bs.config.Configuration;
import com.oc.core.bs.message.PollOkMessage;
import com.oc.domain.waiter.Waiter;
import com.oc.exception.BsAuthorizeException;
import com.oc.message.Packet;
import com.oc.message.type.BodyType;
import com.oc.message.type.Identity;
import com.oc.message.type.Transport;
import com.oc.provider.cache.LocalTeamStore;
import com.oc.provider.db.WaiterProvider;
import com.oc.session.*;
import com.oc.util.JsonUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 分发及处理用户连接请求
 * @author chuangyeifang
 * @createDate 2020年6月3日
 * @version v 1.0
 */
@Slf4j
public class CertificationDispatch {

	@SuppressWarnings({"FieldCanBeLocal", "unused"})
	private Configuration config;

	public CertificationDispatch(Configuration config) {
		this.config = config;
	}
	
	public void handlerWebSocket(ChannelHandlerContext ctx, Packet packet, FullHttpRequest req) throws Exception {
		Channel channel = ctx.channel();
		Identity identity = packet.getFrom().getIdy();
		BodyType bodyType = packet.getBody().getType();
		if ( bodyType == BodyType.LOGIN) {
			if (identity == Identity.WAITER) {
				authorizeWaiter(channel, packet, req);
			} else if (identity == Identity.CUSTOMER) {
				authorizeCustomer(channel, packet, req);
			} else {
				throw new IllegalArgumentException("不支持的Identity: " + identity + " 类型");
			}
		} else {
			throw new IllegalArgumentException("BodyType: " + bodyType + ", 非LOGIN类型，不正规的登录参数");
		}
		//upgrade websocket
		ctx.fireChannelRead(req);
	}
	
	public void handlerHttp(ChannelHandlerContext ctx, Packet packet, FullHttpRequest req) throws Exception {
		Channel channel = ctx.channel();
		if(packet.getBody().getType() == BodyType.LOGIN) {
			authorizeCustomer(channel, packet, req);
			channel.writeAndFlush(new PollOkMessage());
		} else {
			throw new IllegalArgumentException("认证授权失败。");
		}
		req.release();
	}
	
	/**
	 * 验证用户，并创建连接
	 * @param channel
	 * @param packet
	 * @param req
	 * @throws BsAuthorizeException
	 */
	private void authorizeWaiter(Channel channel, Packet packet, FullHttpRequest req)
			throws Exception {
		WaiterSession session;
		String content = packet.getBody().getContent();
		WaiterCertificationContent certificationContent;
		try {
			certificationContent = JsonUtils.getJson().readClass(content, WaiterCertificationContent.class);
		} catch (Exception e) {
			throw new BsAuthorizeException("无法从客服登录信息中获取 token/status");
		}
		String ip = req.headers().get("X-Real-IP");
		WaiterAuthCoder.AuthResult authResult = WaiterAuthCoder.decode(certificationContent.getToken());
		if (authResult.getRc() == 0) {
			WaiterInfo waiterInfo = authResult.getInfo();
			Waiter waiter = WaiterProvider.getInst().authentication(waiterInfo.getTenantCode(), waiterInfo.getWaiterName());
			if (null == waiter) {
				throw new BsAuthorizeException("账号或密码错误.");
			}
			Transport transport = packet.getTs();
			waiter.setStatus(certificationContent.getStatus());
			session = new LocalWaiterSession(waiter.getWaiterCode(), channel, transport, Identity.WAITER, waiter);
			channel.attr(LocalWaiterSession.CLIENT_SESSION).set(session);
			// 记录客服登录信息
			WaiterProvider.getInst().insertWaiterLog(waiter, "1", ip);
		} else {
			throw new BsAuthorizeException("验证客服token信息不通过");
		}
	}

	/**
	 * 验证用户，并创建连接
	 * @param channel
	 * @param packet
	 * @param req
	 * @throws BsAuthorizeException
	 */
	private void authorizeCustomer(Channel channel, Packet packet, FullHttpRequest req)
			throws Exception {

		CustomerInfo customerInfo = CustomerAuthCoder.decode(req.headers());

		String skillName = LocalTeamStore.getInst().getSkillName(customerInfo.getTtc(), customerInfo.getSkc());
		String uid = customerInfo.getCc();
		String name = customerInfo.getCn();
		Transport transport = packet.getTs();
		boolean login = "1".equals(customerInfo.getReal());
		Customer customer = new Customer(uid, name, login, customerInfo.getTtc(), customerInfo.getTmc(),
				customerInfo.getSkc(), skillName, customerInfo.getGc(), customerInfo.getDevice());
		CustomerSession session = new LocalCustomerSession(channel, transport, Identity.CUSTOMER, customer);
		if (transport != Transport.POLLING) {
			channel.attr(Session.CLIENT_SESSION).set(session);
		}
		session.bindRoute();
	}
}
