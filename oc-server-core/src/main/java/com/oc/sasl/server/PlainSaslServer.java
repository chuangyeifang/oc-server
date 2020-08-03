package com.oc.sasl.server;

import com.oc.domain.waiter.Waiter;
import com.oc.message.Packet;
import com.oc.message.type.Identity;
import com.oc.provider.db.WaiterProvider;
import com.oc.sasl.SASLAuthentication;
import io.netty.channel.ChannelHandlerContext;

import javax.security.sasl.Sasl;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @Description: PLAIN认证服务
 * @author chuangyeifang
 * @createDate 2020年1月8日
 * @version v 1.0
 */
public class PlainSaslServer implements SaslServer {
	
	private final String NAME = "PLAIN";
	
	private String userName;
	private String password;
	private String status;
	private boolean completed;
	private boolean aborted;
	private Map<String, ?> props;
	
	public PlainSaslServer(Map<String, ?> props) {
		this.completed = false;
		this.props = props;
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
		
		if (completed) {
			throw new IllegalStateException("PLAIN 认证已经完成");
		}
		if (aborted) {
            throw new IllegalStateException("PLAIN 由于错误而导致认证被打断");
        }
		
		Packet packet = (Packet)props.get(Packet.class.getCanonicalName());
		ChannelHandlerContext ctx = (ChannelHandlerContext)props.get(ChannelHandlerContext.class.getCanonicalName());
		
		if (null == ctx || null == packet) {
			throw new SaslException("PLAIN: 不支持此类型用户授权");
		}
		try {
			if (response.length > 0) {
				String data = new String(response, StandardCharsets.UTF_8);
				StringTokenizer tokens = new StringTokenizer(data, " ");
				if (tokens.countTokens() == 3) {
					userName = tokens.nextToken();
					password = tokens.nextToken();
					status = tokens.nextToken();
				} else {
					throw new SaslException("缺失认证参数");
				}
				if (packet.getFrom().getIdy() == Identity.WAITER) {
					Waiter waiter = WaiterProvider.getInst().authentication(userName, password);
					if (null == waiter) {
						throw new SaslException("PLAIN: 账号或者密码错误");
					}
					waiter.setStatus(status);
					ctx.channel().attr(SASLAuthentication.WAITER).set(waiter);
					this.completed = true;
				} else {
					throw new SaslException("PLAIN: 不支持此类型用户授权");
				}
			} else {
				throw new SaslException("PLAIN 当前为空, 期望一个Response.");
			}
		} catch (IOException e) {
			aborted = true;
            throw new SaslException("PLAIN 用户认证失败， 失败用户为: " + userName, e);
		}
		return null;
	}

	@Override
	public boolean isComplete() {
		return completed;
	}

	@Override
	public String getAuthorizationID() {
		if(completed) {
            return userName;
        } else {
            throw new IllegalStateException("PLAIN 认证尚未完成。");
        }
	}

	@Override
	public byte[] unwrap(byte[] incoming, int offset, int len) throws SaslException {
		if(completed) {
            throw new IllegalStateException("PLAIN 不支持");
        } else {
            throw new IllegalStateException("PLAIN 认证未完成");
        }
	}

	@Override
	public byte[] wrap(byte[] outgoing, int offset, int len) throws SaslException {
		if(completed) {
            throw new IllegalStateException("PLAIN 不支持");
        } else {
            throw new IllegalStateException("PLAIN 认证未完成");
        }
	}

	@Override
	public Object getNegotiatedProperty(String propName) {
		if (completed) {
            if (propName.equals(Sasl.QOP)) {
                return "auth";
            } else {
                return null;
            }
        } else {
            throw new IllegalStateException("PLAIN 认证未完成");
        }
	}

	@Override
	public void dispose() throws SaslException {
		password = null;
        userName = null;
        completed = false;
	}

}
