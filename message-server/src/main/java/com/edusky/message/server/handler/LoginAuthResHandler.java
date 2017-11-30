package com.edusky.message.server.handler;

import com.edusky.message.api.MsgType;
import com.edusky.message.api.message.MessageHeader;
import com.edusky.message.api.message.MsgIdentity;
import com.edusky.message.api.message.PushMessage;
import com.edusky.message.api.message.PushMessageContent;
import com.edusky.message.server.ChannelCache;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author tbc on 2017/8/30 11:35:09.
 */
@Slf4j
public class LoginAuthResHandler extends SimpleChannelInboundHandler<PushMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PushMessage msg) throws Exception {
        /*对msg进行验证，可以获取token比对*/
        MessageHeader header = msg.getHeader();
        log.debug("header={}", header);

        assert header != null;
        // 握手请求信息，处理，其它，透传
        if (MsgType.LOGIN_REQ.equals(header.getType())) {
            PushMessageContent messageContent = msg.getBody();
            MsgIdentity identity = messageContent.getFrom();
            log.info("连接认证请求, 来自: {}, IP: {}", identity, ctx.channel().remoteAddress());
            //1. 验证token,通过则加入缓存（若存在，关掉旧连接，并刷新连接缓存
            if (checkToken(identity)) {
                ChannelCache.flush(identity, ctx.channel());
            }

            ctx.writeAndFlush(PushMessage.buildAuthResponseEntity());
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private boolean checkToken(MsgIdentity identity) {
        return true;
    }

}
