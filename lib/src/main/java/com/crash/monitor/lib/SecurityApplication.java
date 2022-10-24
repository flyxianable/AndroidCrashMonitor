package com.crash.monitor.lib;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.*;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.crash.monitor.lib.constants.SWConstants;
import com.crash.monitor.lib.constants.SWResponseCode;
import com.crash.monitor.lib.model.SWResponse;
import com.crash.monitor.lib.service.SecurityService;
import com.crash.monitor.lib.utils.DeviceInfoHolder;
import com.crash.monitor.lib.utils.GeoLocationHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * 安全处理SDK，外部接口
 *
 * 一期方案，已不再进行维护
 * Created by jale on 14-6-24.
 */
@Deprecated
public class SecurityApplication extends Application {


    private LoginWare loginWare;

    private final static String TAG = "MRD_SECURITY_APP";

    private List<Activity> activities = new ArrayList<Activity>();

    private SecurityService securityService;

    private boolean collectExceptions = true;

    private boolean collectNetStatus = false;

    private String currentUser = null;

    private DBManager dbManager;

    private SWResponse checkResponse;


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onCreate() {
        super.onCreate();

        final String collectExceptions = getMetaData("collectExceptions");
        if(collectExceptions != null) {
            this.collectExceptions = Boolean.valueOf(collectExceptions);
        }

        if(this.collectExceptions) {
            //初始化异常处理工具
            CrashHandler.getInstance().init(this);
        }

        final String collectNetStatus = getMetaData("collectNetStatus");
        if(collectNetStatus != null) {
            this.collectNetStatus = Boolean.valueOf(collectNetStatus);
        }

        if(this.collectNetStatus) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
            registerReceiver(new NetStatusChangeReceiver(), filter);

        }
        //地理位置
        GeoLocationHolder.getInstance().init(this);
        //设备信息
        DeviceInfoHolder.getInstance().init(this);

        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                activities.add(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                activities.remove(activity);
            }
        });

        /**
         * 注册监听器 在手机连接到wifi时上传收集的数据信息
         */
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(new WifiConnectedReceiver(), filter);

        dbManager = new DBManager(this);
        securityService = new SecurityService();

        final String clz = getMetaData("loginWare");

        if (clz == null) {
            Log.e(TAG, "没有配置可用的securityWare");
        } else {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            try {
                Class clazz = cl.loadClass(clz);
                LoginWare.context = this.getApplicationContext();
                loginWare = (LoginWare) clazz.newInstance();
            } catch (InstantiationException e) {
                Log.e(TAG, e.getMessage(), e);
            } catch (IllegalAccessException e) {
                Log.e(TAG, e.getMessage(), e);
            } catch (ClassNotFoundException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }

        //异步任务 检查设备,上传设备信息
        new DeviceInfoCheckThread().start();
//        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//        AsyncTaskWithGPS asyncTaskWithGPS = new AsyncTaskWithGPS();
//        asyncTaskWithGPS.execute(locationManager);
    }


    /**
     * 上传并校验设备信息
     */
    class DeviceInfoCheckThread extends Thread {

        public Handler mHandler;
        @Override
        public void run() {
            Looper.prepare();
            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    Log.d(TAG,msg.toString());

                        currentUser = dbManager.query(SWConstants.LATEST_LOGIN_USER);
                        if (currentUser != null) {
                            checkResponse = checkDevice(currentUser);
                            if (checkResponse != null && loginWare != null
                                    && checkResponse.getCode() == SWResponseCode.BANNED.getCode()) {
                                loginWare.cleanSensitiveInfo();
                            }else if(loginWare == null) {
//                                Log.e(TAG,"安全SDK初始化错误",new SecurityException("无法初始化" + clz));
                            }
                        }
                }
            };
            GeoLocationHolder.getInstance().getCurrentLocation(Looper.myLooper());

            Looper.loop();

//            Log.d(TAG,GeoLocationHolder.getInstance().getLocation().toString());
        }
    }


    /**
     * 退出应用
     */
    public void exit() {
        for (Activity a : activities) {
            a.finish();
        }
    }


    /**
     *
     * 登陆接口 不要在主线程当中调用这个方法
     * @param userId 用户标识
     * @param pwd 用户密码
     * @param anothers 其他信息
     * @return SWResponse
     */
    public SWResponse login(final String userId,final String pwd, final String ... anothers) {

        Log.d(TAG,"prepare to login >>>");

        //超过失败次数限制 禁止登陆
        int failTimes = 0;
        long failStamp = 0;
        long curr = System.currentTimeMillis();
        String val = dbManager.query(SWConstants.LOGIN_FAIL_LOG_KEY);
        if (val != null) {
            String[] arr = val.split("-");
            failTimes = Integer.valueOf(arr[1]);
            failStamp = Long.valueOf(arr[0]);
            if (failTimes == 5 && curr - failStamp < 3600 * 1000) {
                return new SWResponse(SWResponseCode.FORBIDDEN.getCode(), SWResponseCode.FORBIDDEN.getDesc());
            }
        }

        //checkDevice
        if (checkResponse != null && checkResponse.getCode() == SWResponseCode.BANNED.getCode()) {
            return checkResponse;
        } else if (checkResponse != null && currentUser.equals(userId)) {
            return checkResponse;
        } else {
            SWResponse checkResponse = checkDevice(userId);
            if (checkResponse.getCode() != SWResponseCode.SUCCESS.getCode()) {
                //检测到设备被禁用时清楚敏感信息
                if (checkResponse.getCode() == SWResponseCode.BANNED.getCode()) {
                    loginWare.cleanSensitiveInfo();
                }
                return checkResponse;
            }
        }

        //获取应用自定义的信息安全处理工具类
        if (!loginWare.doLogin(userId,pwd,anothers)) {
            if (curr - failStamp > 1800 * 1000) {
                failStamp = curr;
                failTimes = 1;
            } else {
                failTimes += 1;
            }
            dbManager.add(SWConstants.LOGIN_FAIL_LOG_KEY, failStamp + "-" + failTimes);
            return new SWResponse(SWResponseCode.FAIL.getCode(), SWResponseCode.FAIL.getDesc());
        } else {
            dbManager.add(SWConstants.LATEST_LOGIN_USER, userId);
            return new SWResponse(SWResponseCode.SUCCESS.getCode(), SWResponseCode.SUCCESS.getDesc());
        }


    }


    /**
     * 检查当前设备合法性
     *
     * @param userId 用户ID
     * @return SWResponse
     */
    public SWResponse checkDevice(final String userId) {
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();
        return securityService.checkDevice(userId);
    }

    /**
     * 获取meta信息
     *
     * @param name 属性名字
     * @return 值
     */
    private String getMetaData(final String name) {
        ApplicationInfo info;
        try {
            info = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            assert info != null && info.metaData != null;
            return info.metaData.getString(name);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }


    public SecurityService getSecurityService() {
        return securityService;
    }


}
