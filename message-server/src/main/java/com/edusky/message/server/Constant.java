package com.edusky.message.server;

import lombok.extern.slf4j.Slf4j;

/**
 * @author tbc on 2017/8/30 15:57:04.
 */
@Slf4j
public class Constant {
    public static String HOST = "0.0.0.0";
    public static int PORT = 7007;

    public static int DISCONNECT_TIME = 5 * 3;

    public static final int READTIMEOUT = 8;
    public static final int WRITETIMEOUT = 9;
    public static final int ALLTIMEOUT = 15;

    public static final String DOWNLINE = "http://47.95.195.29:33060/user/setteacheronlinestatus";

}
