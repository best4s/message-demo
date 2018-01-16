package com.edusky.message.server;

import com.edusky.message.api.message.MsgIdentity;
import com.edusky.message.api.toolkit.Objs;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author tbc on 2017/9/4 14:04:35.
 */
@Slf4j
public class ChannelCache {
    private static Map<String, Channel> map = new ConcurrentHashMap<>();

    private static String getKey(MsgIdentity identity) {
        return identity.getOpenId() + ":" + identity.getDeviceType();
    }

    private static void closeChannel(Channel channel) {
        if (Objs.nonEmpty(channel)) {
            channel.close();
        }
    }

    private static void closeChannel(MsgIdentity identity) {
        closeChannel(map.get(getKey(identity)));
    }

    public static String getKey(Channel channel) {
        return map.entrySet()
                .stream()
                .filter(entry -> entry.getValue() == channel)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList())
                .get(0);
    }

    public static void put(MsgIdentity identity, Channel channel) {
        closeChannel(identity);
        map.put(getKey(identity), channel);
    }

    public static Channel get(MsgIdentity identity) {
        return map.get(getKey(identity));
    }

    public static void remove(MsgIdentity identity) {
        closeChannel(identity);
        map.remove(getKey(identity));
    }

    public static void remove(Channel channel) {
        map.remove(getKey(channel));
        closeChannel(channel);
    }

    public static void flush(MsgIdentity identity, Channel channel) {
        closeChannel(identity);
        map.put(getKey(identity), channel);
    }

    public static boolean contains(MsgIdentity identity) {
        return map.containsKey(getKey(identity));
    }

}
