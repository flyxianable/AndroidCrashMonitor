package com.crash.monitor.lib.constants;

/**
 * 常量
 * Created by jale on 14-6-24.
 */
public class SWConstants {

    public static final String CHARSET = "UTF-8";

    public static final String DATE_FORMATER = "yyyy-MM-dd HH:mm:ss";

    public static final String DEFAULT_CRASH_FILE_DIR = "/coomrd/crash/";

    public static final String LOGIN_FAIL_LOG_KEY = "LOGIN_FAIL_LOG_KEY";

    public static final String LATEST_LOGIN_USER = "LATEST_LOGIN_USER";

    //Device Info Name

    public static final String DEVICE_NAME = "MODEL";

    /**
     * http请求连接超时时间
     */
    public static final int HTTP_CONNECT_TIMEOUT = 2000;
    /**
     * 数据传输超时时间
     */
    public static final int HTTP_TRANSPORT_TIMEOUT = 20000;

    /**
     * meta-data配置键值
     */

     //最大登录失败次数上限配置
    public static final String MAX_LOGIN_FAIL_TIMES = "MAX_LOGIN_FAIL_TIMES";
     //敏感信息清除工具类
    public static final String SENSITIVE_INFO_CLEANER = "SENSITIVE_INFO_CLEANER";
     //异常文件保存目录
    public static final String CRASH_FILE_DIR = "CRASH_FILE_DIR";


}
