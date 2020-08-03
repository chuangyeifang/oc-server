/**
 * 
 */
package com.oc.sasl.server;

import java.util.Map;

import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;

/**
 * @Description: 匿名认证服务
 * @author chuangyeifang
 * @createDate 2020年1月8日
 * @version v 1.0
 */
public class AnonymousSaslServer implements SaslServer{

	private final static String NAME = "ANONYMOUS";
	
	private Map<String, ?> props;
	private boolean completed;
	
	public AnonymousSaslServer(Map<String, ?> props) {
		this.props = props;
		this.completed = false;
	}
	
	/**
	 * @return
	 */
	@Override
	public String getMechanismName() {
		return NAME;
	}

	/**
	 * @param response
	 * @return
	 * @throws SaslException
	 */
	@Override
	public byte[] evaluateResponse(byte[] response) throws SaslException {
		if (isComplete()) {
			throw new IllegalStateException("认证交换已经完成。");
		}
		props.clear();
		throw new SaslException("ANONYMOUS: 不支持此类型用户授权");
	}

	/**
	 * @return
	 */
	@Override
	public boolean isComplete() {
		return completed;
	}

	/**
	 * @return
	 */
	@Override
	public String getAuthorizationID() {
		if (!isComplete()) {
			throw new IllegalStateException("PLAIN 认证尚未完成。");
		}
		return null;
	}

	/**
	 * @param incoming
	 * @param offset
	 * @param len
	 * @return
	 * @throws SaslException
	 */
	@Override
	public byte[] unwrap(byte[] incoming, int offset, int len) throws SaslException {
		if(completed) {
            throw new IllegalStateException("PLAIN 不支持");
        } else {
            throw new IllegalStateException("PLAIN 认证未完成");
        }
	}

	/**
	 * @param outgoing
	 * @param offset
	 * @param len
	 * @return
	 * @throws SaslException
	 */
	@Override
	public byte[] wrap(byte[] outgoing, int offset, int len) throws SaslException {
		if(completed) {
            throw new IllegalStateException("PLAIN 不支持");
        } else {
            throw new IllegalStateException("PLAIN 认证未完成");
        }
	}

	/**
	 * @param propName
	 * @return
	 */
	@Override
	public Object getNegotiatedProperty(String propName) {
		if(completed) {
            throw new IllegalStateException("PLAIN 不支持");
        } else {
            throw new IllegalStateException("PLAIN 认证未完成");
        }
	}

	/**
	 * @throws SaslException
	 */
	@Override
	public void dispose() throws SaslException {
		props = null;
		completed = false;
	}

}
