package com.crash.monitor.lib.constants;

/**
 * 返回code值
 * Created by jale on 14-6-24.
 */
public enum ExceptionType {

   APP_EXCEPTION(1,"应用程序崩溃")
    ,SECURITY_EXCEPTION(2,"安全验证异常");

    private int code;
    private String desc;

    ExceptionType(int code, String desc) {
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
