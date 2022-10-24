package com.crash.monitor.lib.utils;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

/**
 * 信息提示工具
 * Created by jale on 14-8-13.
 */
public class ToastUtil {

    public static void text(final Context context,final String msg, final int duration) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(context,msg,duration).show();
                Looper.loop();
            }
        }).start();
    }
}
