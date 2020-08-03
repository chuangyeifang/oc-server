/**
 * 
 */
package com.oc.core.bs.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oc.util.http.HttpResponses;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

/**
 * @Description:
 * @author chuangyeifang
 * @createDate 2020年1月28日
 * @version v 1.0
 */
@Sharable
public class WrongUrlHandler extends ChannelInboundHandlerAdapter{
	private static final Logger log = LoggerFactory.getLogger(WrongUrlHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
        	FullHttpRequest req = (FullHttpRequest) msg;
        	try {
                Channel channel = ctx.channel();
                QueryStringDecoder queryDecoder = new QueryStringDecoder(req.uri());
                HttpResponses.sendBadRequestError(channel);
                log.warn("Blocked wrong socket.io-context request! url: {}, params: {}, ip: {}", queryDecoder.path(), queryDecoder.parameters(), channel.remoteAddress());
        	} finally {
        		((FullHttpRequest)msg).release();
        	}
        } else {
        	  super.channelRead(ctx, msg);
        }
    }
}
