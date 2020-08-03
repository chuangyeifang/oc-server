/**
 * 
 */
package com.oc.core.bs.message;

import com.oc.message.Packet;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年1月29日
 * @version v 1.0
 */
public class PongPacketMessage implements Message{

	private final Packet packet;
	
	public PongPacketMessage(Packet packet) {
		this.packet = packet;
	}

	public Packet getPacket() {
		return packet;
	}
}
