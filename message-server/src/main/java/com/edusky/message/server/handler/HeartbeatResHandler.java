package com.edusky.message.server.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.edusky.message.api.MsgType;
import com.edusky.message.api.message.PushMessage;
import com.edusky.message.api.message.MessageHeader;
import com.edusky.message.server.ChannelCache;
import com.edusky.message.server.Constant;
import com.edusky.message.server.uitl.HttpUitls;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author tbc on 2017/8/30 14:13:11.
 */
@Slf4j
public class HeartbeatResHandler extends SimpleChannelInboundHandler<PushMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PushMessage msg) throws Exception {
        MessageHeader header = msg.getHeader();
        if (header != null && MsgType.HEARTBEAT_REQ.equals(header.getType())) {
            log.debug("receive heartbeat: {}", ctx.channel().remoteAddress());
            ctx.writeAndFlush(PushMessage.buildHeartbeatResponseEntity());
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        log.info("超过时间未读到消息，中断连接," + ctx.channel().remoteAddress());
        String key = ChannelCache.getKey(ctx.channel());
        if (Objects.isNull(key)) {
            throw new RuntimeException("没有此key");
        }
        ChannelCache.remove(ctx.channel());
        ChannelCache.remove(ctx.channel());
        String[] split = key.split(":");
        JSONObject jsonObject = new JSONObject();
        log.debug("通知: " + Constant.DOWNLINE + "--->参数openId：" + split[0]);
        jsonObject.put("openId",split[0]);
        jsonObject.put("activeStatus",0);
        String post = HttpUitls.sendPost(Constant.DOWNLINE,jsonObject.toJSONString());
        log.info("通知请求http返回值：" + post);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable caught) {
        log.error("异常: {}, {}", caught.getClass(), caught.getMessage());
        ctx.fireExceptionCaught(caught);
    }

}
