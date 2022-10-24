package com.crash.monitor.lib.exception;

import com.crash.monitor.lib.model.SWResponse;

/**
 * 登陆异常类
 * Created by jale on 14-8-28.
 */
public class LoginException extends Exception {

    /**
     * @param msg 异常消息
     */
    public LoginException(String msg) {
        super(msg);
    }

    /**
     *
     * @param response 错误信息
     * @param userId 用户ID
     */
    public LoginException(SWResponse response,String userId) {
        super(response.getMsg() + "，用户ID：" + userId);
    }
}
