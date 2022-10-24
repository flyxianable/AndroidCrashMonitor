package com.crash.monitor.lib.constants;

/**
 * 远程调用URL
 * Created by jale on 14-6-24.
 */
public class ServiceUrls {


    public final static String SECURITY_SERVER_DOMAIN = "https://security.mrd.xx.com";

    public final static String SERVER_DOMAIN = "http://t.mrd.xx.com"; //上传crash的域名
//    public final static String SERVER_DOMAIN = "http://192.168.207.50";

    public final static String LOCK_DEVICE = "/lockDevice/";

    /**
     * 异常信息收集
     */
    public final static String HANDLE_EXCEPTION = "/crash/handleException";

    /**
     * 设备校验
     */
    public final static String IS_EFFECTIVE = "/validation/isEffective";

    /**
     * 搜集设备信息
     */
    public final static String CEOLLECT_DEVICE_INFO = "/deviceInfo/collection";

    /**
     * 搜集状态信息
     */
    public static final String CEOLLECT_PASS_BACK_INFO = "/deviceInfo/collectPassBackInfo";
}
