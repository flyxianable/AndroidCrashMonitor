package com.crash.monitor.lib;

/**
 * 安全校验异常类
 * Created by jale on 14-7-3.
 */
public class SecurityException extends Throwable {

    public SecurityException(String detailMessage) {
        super(detailMessage);
    }
}
