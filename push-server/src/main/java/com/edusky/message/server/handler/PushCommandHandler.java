package com.edusky.message.server.handler;

import com.edusky.message.api.MsgType;
import com.edusky.message.api.message.MessageHeader;
import com.edusky.message.api.message.MsgIdentity;
import com.edusky.message.api.message.PushMessage;
import com.edusky.message.api.message.PushMessageContent;
import com.edusky.message.api.toolkit.Objs;
import com.edusky.message.server.ChannelCache;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 消息推送处理器
 *
 * @author tbc on 2017/9/4 10:40:43.
 */
@Slf4j
public class PushCommandHandler extends SimpleChannelInboundHandler<PushMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PushMessage msg) throws Exception {
        MessageHeader header = msg.getHeader();
        // 如果消息类型是业务推送请求，则处理
        if (Objs.isEmpty(header) && MsgType.REQ.equals(header.getType())) {
            PushMessageContent content = msg.getBody();
            if (Objs.isEmpty(content)) {
                ctx.channel().writeAndFlush("消息体为空");
            }
            // 发送给谁
            MsgIdentity identity = content.getTo();
            if (Objs.isEmpty(identity)) {

            }
            Channel channel = getChannel(identity);
            if (Objs.isEmpty(channel)) {
                ctx.channel().writeAndFlush("该设备未连接");
            }

            assert channel != null;
            channel.writeAndFlush(content.getContentBody());
        }
    }

    private Channel getChannel(MsgIdentity identity) {
        return ChannelCache.get(identity);
    }
}
