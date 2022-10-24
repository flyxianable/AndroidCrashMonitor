package com.crash.monitor.lib;

import android.content.Context;

/**
 *
 * 登陆套件
 * Created by jale on 14-7-24.
 */
@Deprecated
public abstract class LoginWare {

    public static Context context;

    public abstract void cleanSensitiveInfo();

    public abstract boolean doLogin(String userId, String pwd, String[] anothers);
}
