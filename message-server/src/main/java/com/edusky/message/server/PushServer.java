package com.edusky.message.server;

import com.edusky.message.api.codec.PushMessageDecoder;
import com.edusky.message.api.codec.PushMessageEncoder;
import com.edusky.message.server.handler.HeartbeatResHandler;
import com.edusky.message.server.handler.LoginAuthResHandler;
import com.edusky.message.server.handler.PushCommandHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
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
import java.net.InetSocketAddress;

/**
 * @author tbc on 2017/8/29 11:43:49.
 */
@Slf4j
class PushServer {
    void bind(InetSocketAddress address) {
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
                                    .addLast("commandHandler", new PushCommandHandler());
                        }
                    });
            // 绑定端口，同步等待成功
            ChannelFuture future = b.bind(address).sync();
            log.info("MessagePush server start ok : {}:{}", address.getHostString(), address.getPort());
            future.channel().closeFuture().sync();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
