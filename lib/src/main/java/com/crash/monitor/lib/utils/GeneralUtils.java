package com.crash.monitor.lib.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 *
 * 常用工具方法集成
 * Created by jale on 14-7-24.
 */
public class GeneralUtils {

    private final static String TAG = "GeneralUtils";

    /**
     * 获取meta信息
     *
     * @param name 属性名字
     * @param context 应用上下文
     * @return 字符串值
     */
    public static String getMetaData(final Context context,final String name) {
        ApplicationInfo info;
        try {
            info = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            assert info != null && info.metaData != null;
            return info.metaData.getString(name);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }


}
