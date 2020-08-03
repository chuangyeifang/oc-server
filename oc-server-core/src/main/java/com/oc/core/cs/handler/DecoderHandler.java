package com.oc.core.cs.handler;

import java.util.List;

import com.oc.core.coder.PacketDecoder;
import com.oc.message.Packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class DecoderHandler extends ByteToMessageDecoder {
	
	private int HEAD_LENGTH = 4;
	
	private PacketDecoder decoder;

	public DecoderHandler(PacketDecoder decoder) {
		this.decoder = decoder;
	}
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		if (in.readableBytes() < HEAD_LENGTH) {
			return;
		}
		
		in.markReaderIndex();
		
		int bytesLen = in.readInt();
		
		if (bytesLen < 0) {
			ctx.close();
			return;
		}
		
		if (in.readableBytes() < bytesLen) {
			in.resetReaderIndex();
			return;
		}
		
		byte[] bytes = new byte[bytesLen];
		in.readBytes(bytes);
		try {
			Packet packet = decoder.decodePackets(bytes, Packet.class);
			out.add(packet);
		} catch (Exception e) {
			return;
		}
	}
}
