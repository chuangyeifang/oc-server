/**
 * 
 */
package com.oc.core.bs.message;

import com.oc.message.type.Transport;
import com.oc.session.Session;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年1月29日
 * @version v 1.0
 */
public class OutPacketMessage implements Message{

	private final Session session;
	
	public OutPacketMessage(Session session) {
		this.session = session;
	}

	public Transport getTransport() {
		return session.getTransport();
	}

	public Session getSession() {
		return session;
	}
}
