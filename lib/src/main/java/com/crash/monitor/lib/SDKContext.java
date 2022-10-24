package com.crash.monitor.lib;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.crash.monitor.lib.constants.SWConstants;
import com.crash.monitor.lib.utils.DeviceInfoHolder;
import com.crash.monitor.lib.utils.GeneralUtils;
import com.crash.monitor.lib.utils.GeoLocationHolder;

/**
 *
 * SDK上下文，负责对执行环境进行初始化，保存全局环境变量
 * Created by jale on 14-7-25.
 */
public class SDKContext {


    /**
     * 是否已经初始化完成
     */
    public static volatile boolean inited = false;



    /**
     * 唯一实例
     */
    public static SDKContext instance;

    /**
     * 应用程序上下文
     */
    static Context context;

    /**
     * 异常记录文件保存路径
     */
    private static String CRASH_FILE_PATH;

    /**
     * 是否连接了WIFI
     */
    private static boolean isWifi;

    private SDKContext() {

    }

    public static SDKContext getInstance() {
        if (instance == null) {
            synchronized (SDKContext.class) {
                if (instance == null) {
                    instance = new SDKContext();
                }
            }
        }
        return instance;
    }



    /**
     *
     * 初始化操作，获取并保存设备信息、位置信息供后续的方法调用
     *
     * @param context 应用程序上下文
     */
    public static void init(Context context) {
        if(!inited) {
            synchronized (SDKContext.class) {
                if(!inited) {
                    SDKContext.context = context;
                    //地理位置
                    GeoLocationHolder.getInstance().init(context);
                    //设备信息
                    DeviceInfoHolder.getInstance().init(context);

                    //网络状况
                    ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    isWifi = wifi.isAvailable();

                    if(CRASH_FILE_PATH == null) {
                        synchronized (ExceptionKitHandler.class) {
                            if(CRASH_FILE_PATH == null) {
                                CRASH_FILE_PATH = GeneralUtils.getMetaData(context, SWConstants.CRASH_FILE_DIR);
                                if(CRASH_FILE_PATH == null) {
                                    CRASH_FILE_PATH = SWConstants.DEFAULT_CRASH_FILE_DIR;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    /**
     * 初始化操作，获取并保存设备信息、位置信息供后续的方法调用
     * @param context
     * @param isAllowLocation 是否允许定位获取经纬度信息
     */
    public static void init(Context context, boolean isAllowLocation, boolean isReportCrashForTest) {
        if(!inited) {
            synchronized (SDKContext.class) {
                if(!inited) {
                    SDKContext.context = context;
                    //地理位置
                    if(isAllowLocation){
                    	GeoLocationHolder.getInstance().init(context);
                    }
                    //设备信息
                    DeviceInfoHolder.getInstance().init(context, isReportCrashForTest);

                    //网络状况
                    ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    isWifi = wifi.isAvailable();

                    if(CRASH_FILE_PATH == null) {
                        synchronized (ExceptionKitHandler.class) {
                            if(CRASH_FILE_PATH == null) {
                                CRASH_FILE_PATH = GeneralUtils.getMetaData(context, SWConstants.CRASH_FILE_DIR);
                                if(CRASH_FILE_PATH == null) {
                                    CRASH_FILE_PATH = SWConstants.DEFAULT_CRASH_FILE_DIR;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static String getCrashFileDirectory() {
        return CRASH_FILE_PATH;
    }


    public static boolean isWifiConnected() {
        return inited && isWifi;
    }
}
