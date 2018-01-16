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

    public static final int READTIMEOUT = 10;
    public static final int WRITETIMEOUT = 11;
    public static final int ALLTIMEOUT = 20;

    public static final String DOWNLINE = "http://127.0.0.1:33060/user/setteacheronlinestatus";

}
