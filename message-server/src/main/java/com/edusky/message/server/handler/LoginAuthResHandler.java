package com.edusky.message.server.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.edusky.message.api.MsgType;
import com.edusky.message.api.message.MessageHeader;
import com.edusky.message.api.message.MsgIdentity;
import com.edusky.message.api.message.PushMessage;
import com.edusky.message.api.message.PushMessageContent;
import com.edusky.message.server.ChannelCache;
import com.edusky.message.server.Constant;
import com.edusky.message.server.uitl.HttpUitls;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author tbc on 2017/8/30 11:35:09.
 */
@Slf4j
public class LoginAuthResHandler extends SimpleChannelInboundHandler<PushMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PushMessage msg) throws Exception {
        /*对msg进行验证，可以获取token比对*/
        MessageHeader header = msg.getHeader();
//        log.debug("header={}", header);

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

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String key = ChannelCache.getKey(ctx.channel());
        if (Objects.isNull(key)) {
            throw new RuntimeException("没有此key");
        }
        log.info("通知Channel Key值: " + key + " < --- 停止活动！");
        ChannelCache.remove(ctx.channel());
        String[] split = key.split(":");
        JSONObject jsonObject = new JSONObject();
        log.debug("通知: " + Constant.DOWNLINE + "--->参数openId：" + split[0]);
        jsonObject.put("openId",split[0]);
        jsonObject.put("activeStatus",0);
        String post = HttpUitls.sendPost(Constant.DOWNLINE,jsonObject.toJSONString());
        log.debug("通知请求http返回值：" + post);
    }

    private boolean checkToken(MsgIdentity identity) {
        return true;
    }

}
