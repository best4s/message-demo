package com.edusky.message.server;

import com.edusky.message.api.codec.PushMessageDecoder;
import com.edusky.message.api.codec.PushMessageEncoder;
import com.edusky.message.api.toolkit.Sleeps;
import com.edusky.message.server.handler.HeartbeatResHandler;
import com.edusky.message.server.handler.LoginAuthResHandler;
import com.edusky.message.server.handler.MyResponseHandler;
import com.edusky.message.server.handler.PushCommandHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @author tbc on 2017/8/29 11:43:49.
 */
@Slf4j
public class PushMessageServer {
    public static void main(String[] args) {
        new PushMessageServer().bind();
        Sleeps.days(99);
    }

    public void bind() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws IOException {
                            ch.pipeline()
                                    .addLast("decoder", new PushMessageDecoder())
                                    .addLast("encoder", new PushMessageEncoder())
                                    .addLast("readTimeoutHandler", new ReadTimeoutHandler(50))
                                    .addLast("loginAuthResHandler", new LoginAuthResHandler())
                                    .addLast("heartbeatResHandler", new HeartbeatResHandler())
                                    .addLast("", new PushCommandHandler());
                        }
                    });

            // 绑定端口，同步等待成功
            b.bind(Constant.HOST, Constant.PORT).sync();
            log.info("MessagePush server start ok : {} : {}", Constant.HOST, Constant.PORT);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

}
