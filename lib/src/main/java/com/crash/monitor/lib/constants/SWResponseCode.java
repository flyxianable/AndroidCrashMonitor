package com.crash.monitor.lib.constants;

/**
 * 返回code值
 * Created by jale on 14-6-24.
 */
public enum SWResponseCode {

    SUCCESS(0,"success")
    , FAIL(-1,"fail")
    , FORBIDDEN(2,"超过登陆失败次数限制，一小时内禁止登陆")
    , BANNED(3,"当前设备已被禁止登陆")
    , DEVICE_MATCH_FAILED(4,"当前登陆设备与用户绑定设备不匹配")
    , NET_OR_SERVER_ERROR(99,"网络或服务器异常" );


    private int code;
    private String desc;

    SWResponseCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
