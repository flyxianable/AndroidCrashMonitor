package com.crash.monitor.lib.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import com.crash.monitor.lib.model.DeviceInfo;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 设备信息采集工具
 * Created by jale on 14-7-9.
 */
public class DeviceInfoHolder {


    private final static String TAG = "DeviceInfoHolder";

    private Map<String, String> infoMap = new HashMap<String, String>();

    private DeviceInfo packageName;

    private DeviceInfo appName;

    private DeviceInfo deviceId;

    private DeviceInfo appVersion;

    private DeviceInfo versionCode;

    private DeviceInfo ipAddress;
    
    private DeviceInfo deviceScreen;
    
    private DeviceInfo osName;
    
    private DeviceInfo osVersion;

//    private Context context;

    private static DeviceInfoHolder instance;

    private static boolean inited = false;

    private DeviceInfoHolder() {

    }

    public static DeviceInfoHolder getInstance() {
        if (instance == null) {
            synchronized (DeviceInfoHolder.class) {
                if (instance == null) {
                    instance = new DeviceInfoHolder();
                }
            }
        }
        return instance;
    }

    /**
     *
     * 初始化 获取设备信息
     * @param context 应用上下文
     */
    public void init(final Context context) {

        if(inited)
            return;

//        this.context = context;
//        getDeviceInfo();
        try {
            PackageManager pm = context.getPackageManager();

            assert pm != null;
            //包名
            setPackageName(new DeviceInfo("packageName",context.getPackageName()));
            //应用名
            String appName = (String)pm.getApplicationLabel(pm.getApplicationInfo(context.getPackageName(), 0));
            setAppName((new DeviceInfo("appName",appName)));
            //屏幕分辨率
            DisplayMetrics dm = new DisplayMetrics();
            WindowManager windowMgr = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
            windowMgr.getDefaultDisplay().getMetrics(dm);
            int width = dm.widthPixels;
            int height = dm.heightPixels;
            Log.v("width", "width = " + width);
            setDeviceScreen(new DeviceInfo("deviceScreen", "" + width + "x" + height));
            //版本号
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "N/A" : pi.versionName;
                String versionCode = pi.versionCode + "";

                setAppVersion(new DeviceInfo("appVersion", versionName));
                setVersionCode(new DeviceInfo("codeVersion", versionCode));
            }
            //ip地址
            String ip = getLocalIpAddress();
            
            if(ip == null){
                //获取wifi服务
                WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
                //判断wifi是否开启
                if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);  
                }
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();     
                int ipAddress = wifiInfo.getIpAddress(); 
                ip = intToIp(ipAddress); 
            }
            
            if(ip != null) {
                setIpAddress(new DeviceInfo("ipAddress",ip));
            }
            //操作系统名
//            String osName = android.os.Build.PRODUCT;

            setOsName(new DeviceInfo("osName", "android")); //redo by lyb at 2015.03.06
            //操作系统版本
            String osVersion = Build.VERSION.RELEASE;
            setOsVersion(new DeviceInfo("osVersion", osVersion));

        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "An error occured when collect package info");
        }

        Field[] fields = Build.class.getFields();

        for (Field f : fields) {
            f.setAccessible(true);
            try {
                infoMap.put(f.getName(), f.get(null).toString());
                Log.d(TAG, f.getName() + "->" + f.get(null));
            } catch (IllegalAccessException e) {
                Log.e(TAG, "An error occured when collect crash info");
            }
        }

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        //Set DeviceId
        setDeviceId(new DeviceInfo("deviceId",telephonyManager.getDeviceId()));

        inited = true;
    }
    
    /**
    *
    * 初始化 获取设备信息
    * @param context 应用上下文
    */
   public void init(final Context context, boolean isReportCrashForTest) {

       if(inited)
           return;

//       this.context = context;
//       getDeviceInfo();
       try {
           PackageManager pm = context.getPackageManager();

           assert pm != null;
           //包名
           setPackageName(new DeviceInfo("packageName",context.getPackageName()));
           //应用名
           String appName = (String)pm.getApplicationLabel(pm.getApplicationInfo(context.getPackageName(), 0));
           setAppName((new DeviceInfo("appName",appName)));
           //屏幕分辨率
           DisplayMetrics dm = new DisplayMetrics();
           WindowManager windowMgr = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
           windowMgr.getDefaultDisplay().getMetrics(dm);
           int width = dm.widthPixels;
           int height = dm.heightPixels;
           Log.v("width", "width = " + width);
           setDeviceScreen(new DeviceInfo("deviceScreen", "" + width + "x" + height));
           //版本号
           PackageInfo pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
           if (pi != null) {
               String versionName = pi.versionName == null ? "N/A" : pi.versionName;
               String versionCode = pi.versionCode + "";

               setAppVersion(new DeviceInfo("appVersion", versionName));
               setVersionCode(new DeviceInfo("codeVersion", versionCode));
           }
           //ip地址
           String ip = getLocalIpAddress();
           
           if(ip == null){
               //获取wifi服务
               WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
               //判断wifi是否开启
               if (!wifiManager.isWifiEnabled()) {
               wifiManager.setWifiEnabled(true);  
               }
               WifiInfo wifiInfo = wifiManager.getConnectionInfo();     
               int ipAddress = wifiInfo.getIpAddress(); 
               ip = intToIp(ipAddress); 
           }
           
           if(ip != null) {
               setIpAddress(new DeviceInfo("ipAddress",ip));
           }
           //操作系统名
//           String osName = android.os.Build.PRODUCT;
           String test = "";
           if(isReportCrashForTest){
           	test = "线下测试";
           }else{
           	test = "线上运行";
           }
           setOsName(new DeviceInfo("osName", "android" + test)); //redo by lyb at 2015.03.06
           //操作系统版本
           String osVersion = Build.VERSION.RELEASE;
           setOsVersion(new DeviceInfo("osVersion", osVersion));

       } catch (PackageManager.NameNotFoundException e) {
           Log.e(TAG, "An error occured when collect package info");
       }

       Field[] fields = Build.class.getFields();

       for (Field f : fields) {
           f.setAccessible(true);
           try {
               infoMap.put(f.getName(), f.get(null).toString());
               Log.d(TAG, f.getName() + "->" + f.get(null));
           } catch (IllegalAccessException e) {
               Log.e(TAG, "An error occured when collect crash info");
           }
       }

       TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

       //Set DeviceId
       setDeviceId(new DeviceInfo("deviceId",telephonyManager.getDeviceId()));

       inited = true;
   }

    /**
     * 获取设备信息
     * @param key 参数名称
     * @return 值
     */
    public String getDeviceInfo(final String key) {
        return infoMap.get(key);
    }

    public DeviceInfo getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(DeviceInfo deviceId) {
        this.deviceId = deviceId;
    }

    public DeviceInfo getPackageName() {
        return packageName;
    }

    public void setPackageName(DeviceInfo packageName) {
        this.packageName = packageName;
    }

    public DeviceInfo getAppName() {
        return appName;
    }

    public void setAppName(DeviceInfo appName) {
        this.appName = appName;
    }

    public DeviceInfo getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(DeviceInfo appVersion) {
        this.appVersion = appVersion;
    }

    public DeviceInfo getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(DeviceInfo versionCode) {
        this.versionCode = versionCode;
    }
    

    public DeviceInfo getDeviceScreen() {
		return deviceScreen;
	}

	public void setDeviceScreen(DeviceInfo deviceScreen) {
		this.deviceScreen = deviceScreen;
	}

	public DeviceInfo getOsName() {
		return osName;
	}

	public void setOsName(DeviceInfo osName) {
		this.osName = osName;
	}

	public DeviceInfo getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(DeviceInfo osVersion) {
		this.osVersion = osVersion;
	}

	/**
     * 获取当前设备的IP地址
     * @return ip字符串
     */
    private String getLocalIpAddress() {
        try {
        	String ip = "";
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) { // 不加isLinkLocalAddress 在4.0下获取到的是IPV6地址
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(TAG, ex.toString());
        }
        return null;
    }

    private String intToIp(int i) {     
        
        return (i & 0xFF ) + "." +     
      ((i >> 8 ) & 0xFF) + "." +     
      ((i >> 16 ) & 0xFF) + "." +     
      ( i >> 24 & 0xFF) ;
   } 
    
    public DeviceInfo getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(DeviceInfo ipAddress) {
        this.ipAddress = ipAddress;
    }
}
