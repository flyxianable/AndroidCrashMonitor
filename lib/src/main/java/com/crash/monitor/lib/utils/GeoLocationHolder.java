package com.crash.monitor.lib.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import com.crash.monitor.lib.model.DeviceInfo;

/**
 * 设备地理位置获取工具
 * Created by jale on 14-7-8.
 */
public class GeoLocationHolder {

    private final static String TAG = "GeoLocationHolder";

    private Context context;

    private Location location;
    //经度
    private DeviceInfo deviceLon;
    //纬度
    private DeviceInfo deviceLat;

    /**
     * 当前使用的地理位置Provider
     */
    private String provider;

    private long timestamp = 0l;


    private GeoLocationHolder() {
    }

    private static GeoLocationHolder instance = null;

    public static GeoLocationHolder getInstance() {
        if(instance == null) {
            synchronized (GeoLocationHolder.class) {
                if(instance == null) {
                    instance = new GeoLocationHolder();
                }
            }
        }
        return instance;
    }


    public void setLocation(Location location) {
        this.location = location;
        if(location != null) {
            this.deviceLat = new DeviceInfo("deviceLat",Double.valueOf(location.getLatitude()).toString());
            this.deviceLon = new DeviceInfo("deviceLon",Double.valueOf(location.getLongitude()).toString());
        }
    }

    /**
     * 初始化
     * @param context app上下文
     */
    public void init(Context context) {    	
        try {
			LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			    provider = LocationManager.GPS_PROVIDER;
			} else if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			    provider = LocationManager.NETWORK_PROVIDER;
			}
			this.context = context;
			new GeoUpdateThread().start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * 获取设备位置
     * 有可能获取不到位置，获取当前provider最近以此定位的位置
     * @return location
     */
    private boolean checkLocation() {
//        long curr = System.currentTimeMillis();
        if(location == null && provider != null) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            setLocation(locationManager.getLastKnownLocation(provider));
        }

        return location != null;
    }

    /**
     * 获取最新的位置
     * @param looper 回调线程
     */
    public void getCurrentLocation(final Looper looper) {
        reg(looper);
    }

    /**
     * 注册位置监听
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private void reg(final Looper looper) {
    	try{
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if(provider != null) {
            if(looper == null) {
                locationManager.requestLocationUpdates(provider, 20 * 1000, 1000, new GeoLocationListener());
            }else {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
                    locationManager.requestSingleUpdate(provider, new GeoLocationListener(true), looper);
                }else {
                    locationManager.requestLocationUpdates(provider, 20 * 1000, 1000, new GeoLocationListener());
                }
            }
        }
    	}catch(Exception e){
    		e.printStackTrace();
    	}

    }


    public DeviceInfo getDeviceLon() {
        return deviceLon;
    }

    public void setDeviceLon(DeviceInfo deviceLon) {
        this.deviceLon = deviceLon;
    }

    public DeviceInfo getDeviceLat() {
        return deviceLat;
    }

    public void setDeviceLat(DeviceInfo deviceLat) {
        this.deviceLat = deviceLat;
    }
    /**
     * 设置经纬度
     * @param latitude
     * @param longitude
     */
    public void setLocation(double latitude, double longitude) {
        this.deviceLat = new DeviceInfo("deviceLat",Double.valueOf(latitude).toString());
        this.deviceLon = new DeviceInfo("deviceLon",Double.valueOf(longitude).toString());
    }


    /**
     * 位置监听器
     */
    class GeoLocationListener implements LocationListener {

        private boolean isMainLooper = true;

        GeoLocationListener(boolean isMainLooper) {
            this.isMainLooper = isMainLooper;
        }

        GeoLocationListener() {
        }



        @Override
        public void onLocationChanged(Location loc) {
            setLocation(loc);

//            setDeviceLat(new DeviceInfo("deviceLat",Double.valueOf(loc.getLatitude()).toString()));
//            setDeviceLon(new DeviceInfo("deviceLon",Double.valueOf(loc.getLongitude()).toString()));

            if(!isMainLooper) {
                Looper.myLooper().quit();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    class GeoUpdateThread extends Thread {

        @Override
        public void run() {
            Looper.prepare();

            GeoLocationHolder.getInstance().getCurrentLocation(Looper.myLooper());

            Looper.loop();

        }
    }


}
