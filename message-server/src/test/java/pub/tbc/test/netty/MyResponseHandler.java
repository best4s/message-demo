package pub.tbc.test.netty;

import com.edusky.message.api.message.MyPushMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author tbc on 2017/8/31 15:37:04.
 */
@Slf4j
public class MyResponseHandler extends SimpleChannelInboundHandler {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("receive message : {}", msg);
        MyPushMessage message = (MyPushMessage) msg;
        if (message != null) {
            switch (message.getFlag()) {
                case 0:
                    login(ctx);
                    break;
                case 2:
                    heartbeat(ctx);
                    break;
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("异常： {} - {}", cause.getClass(), cause.getMessage());
        cause.printStackTrace();
        ctx.fireExceptionCaught(cause);
    }

    private void login(ChannelHandlerContext ctx) {
        log.info("收到认证消息...");
        ctx.writeAndFlush(MyPushMessage.builder().flag((byte) 1).build());
    }

    private void heartbeat(ChannelHandlerContext ctx) {
        log.info("收到心跳消息...");
        ctx.writeAndFlush(MyPushMessage.builder().flag((byte) 3).build());
    }
}
