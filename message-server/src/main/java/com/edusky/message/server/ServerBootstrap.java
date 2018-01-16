package com.edusky.message.server;

import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Objects;

/**
 * @author tbc on 2017/9/5 10:05:39.
 */
@Slf4j
public class ServerBootstrap {
    public static void main(String[] args) {
        String host = Constant.HOST;
        int port = Constant.PORT;
        if (Objects.nonNull(args) && args.length > 0) {
            host = args[0];
            if (args.length > 1) {
                port = Integer.valueOf(args[1]);
            }
        }
        new PushServer().bind(
                new InetSocketAddress(host, port)
        );
    }
}
