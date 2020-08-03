package com.oc.core.coder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import com.oc.message.Packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufOutputStream;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年1月28日
 * @version v 1.0
 */
public class PacketEncoder {
	
	private final boolean isPreferDirectBuffer;
	private final JsonSupport jsonSupport;

	public PacketEncoder(boolean isPreferDirectBuffer, JsonSupport jsonSupport) {
		this.isPreferDirectBuffer = isPreferDirectBuffer;
		this.jsonSupport = jsonSupport;
	}
	
	public ByteBuf allocateBuffer(ByteBufAllocator allocator) {
		if (isPreferDirectBuffer) {
			return allocator.ioBuffer();
		}
		return allocator.heapBuffer();
	}
	
	public void encodePackets(Queue<Packet> packets, ByteBuf buffer, ByteBufAllocator allocator, int limit) throws IOException {
		List<Packet> result = new ArrayList<>();
		while (true) {
			Packet packet = packets.poll();
			if (packet == null) {
				break;
			}
			result.add(packet);
		}
		if (!result.isEmpty()) {
			encodePacketHandler(result, buffer, allocator);
		}
	}

    public void encodePacket(Packet packet, ByteBuf buf, ByteBufAllocator allocator) throws IOException {
		encodePacketHandler(packet, buf, allocator);
	}

	private void encodePacketHandler(Object packet, ByteBuf buf, ByteBufAllocator allocator) throws IOException {
		ByteBuf encBuf = allocateBuffer(allocator);
		try (ByteBufOutputStream out = new ByteBufOutputStream(encBuf)){
			jsonSupport.write(out, packet);
			if (encBuf != null) {
				buf.writeBytes(encBuf);
			}
		} finally {
			if (encBuf != null) {
				encBuf.release();
			}
		}
	}

	public byte[] encodePacket(Packet packet) throws IOException {
		return jsonSupport.writeBytes(packet);
    }
}
