package com.oc.core.heart;

import lombok.extern.slf4j.Slf4j;

import com.oc.message.Packet;
import com.oc.message.body.Body;
import com.oc.message.type.BodyType;
import com.oc.message.type.PacketType;
import com.oc.session.Session;

import io.netty.channel.Channel;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年5月27日
 * @version v 1.0
 */
@Slf4j
public class HeartDetector {
	
	public static void pongCs(Channel channel) {
		if (log.isDebugEnabled()) {
			Session session = channel.attr(Session.CLIENT_SESSION).get();
			log.debug("cs 响应客户端:{}, PING请求.", session.getUid());
		}
		Packet packet = new Packet(PacketType.PONG);
		Body body = new Body(BodyType.TEXT, String.valueOf(System.currentTimeMillis()));
		packet.setBody(body);
		channel.writeAndFlush(packet);
	}
	
	public static void pongBs(Session session) {
		if (log.isDebugEnabled()) {
			log.debug("bs 响应客户端:{}, PING请求.", session.getUid());
		}
		Packet pingPacket = new Packet(PacketType.PONG);
		Body body = new Body(BodyType.TEXT, String.valueOf(System.currentTimeMillis()));
		pingPacket.setBody(body);
		session.sendPacket(pingPacket);
	}
}
