/**
 * 
 */
package com.oc.core.coder;

import java.io.IOException;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年1月28日
 * @version v 1.0
 */
public class PacketDecoder {

	private final JsonSupport jsonSupport;

	public PacketDecoder(JsonSupport jsonSupport) {
		this.jsonSupport = jsonSupport;
	}

	public <T> T decodePackets(Object packet, Class<T> clazz) throws IOException {
		return jsonSupport.readClass(packet, clazz);
	}
}
