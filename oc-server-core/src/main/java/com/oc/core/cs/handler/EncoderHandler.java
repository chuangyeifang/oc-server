/**
 * 
 */
package com.oc.core.cs.handler;

import com.oc.core.coder.PacketEncoder;
import com.oc.message.Packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年1月28日
 * @version v 1.0
 */
@Sharable
public class EncoderHandler extends MessageToByteEncoder<Packet>{
	
	private PacketEncoder encoder;
	
	public EncoderHandler(PacketEncoder encoder) {
		this.encoder = encoder;
	}

	@Override
	protected void encode(ChannelHandlerContext ctx, Packet packet, ByteBuf out) throws Exception {
		byte[] bytes = encoder.encodePacket(packet);
		
		int len = bytes.length;
		out.writeInt(len);
		out.writeBytes(bytes);
	}
	
}
