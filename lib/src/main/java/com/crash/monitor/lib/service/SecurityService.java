package com.crash.monitor.lib.service;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.crash.monitor.lib.constants.ExceptionType;
import com.crash.monitor.lib.constants.SWConstants;
import com.crash.monitor.lib.constants.SWResponseCode;
import com.crash.monitor.lib.constants.ServiceUrls;
import com.crash.monitor.lib.model.DeviceInfo;
import com.crash.monitor.lib.model.ExceptionInfo;
import com.crash.monitor.lib.model.SWResponse;
import com.crash.monitor.lib.utils.DeviceInfoHolder;
import com.crash.monitor.lib.utils.GZipUtil;
import com.crash.monitor.lib.utils.GeoLocationHolder;
import com.crash.monitor.lib.utils.HttpUtil;
import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 与服务端交互接口
 * Created by jale on 14-7-2.
 */
public class SecurityService {


    private final static String TAG = "SecurityService";

    /**
     * 校验设备是否合法
     * 测试时使用http方式与服务器通信，测试完成上线时必须要修改为https的方式
     *
     * @param userId 登陆用户的ID
     * @return SWResponse
     */
    public SWResponse checkDevice(final String userId) {

        final List<NameValuePair> params = new ArrayList<NameValuePair>();
        DeviceInfoHolder deviceInfoHolder = DeviceInfoHolder.getInstance();
        params.add(deviceInfoHolder.getDeviceId());
        params.add(deviceInfoHolder.getPackageName());
        params.add(new DeviceInfo("deviceName", deviceInfoHolder.getDeviceInfo(SWConstants.DEVICE_NAME)));
        params.add(new DeviceInfo("userName", userId));

        String msg;
        try {
            msg = HttpUtil.post(ServiceUrls.SERVER_DOMAIN + ServiceUrls.IS_EFFECTIVE, params);
        } catch (Exception e) {
            return new SWResponse(SWResponseCode.NET_OR_SERVER_ERROR);
        }
        return SWResponse.fromJson(msg);

    }


    /**
     * 将异常信息上传至服务器
     * 不论位置信息是否能取到 都进行回传
     */
    @Deprecated
    public void saveCrashInfo() {

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = Environment.getExternalStorageDirectory() + SWConstants.CRASH_FILE_DIR;
            File dir = new File(path);
            if (dir.exists()) {
                final File[] files = dir.listFiles();
                if (files != null && files.length > 0) {
                    new Thread(new Runnable() {

                        @Override
                        public void run() {

                            DeviceInfoHolder deviceInfoHolder = DeviceInfoHolder.getInstance();
                            GeoLocationHolder geoLocationHolder = GeoLocationHolder.getInstance();
                            final List<NameValuePair> params = new ArrayList<NameValuePair>();

                            params.add(deviceInfoHolder.getAppName());
                            params.add(deviceInfoHolder.getPackageName());
                            params.add(deviceInfoHolder.getDeviceId());

                            DeviceInfo lat = GeoLocationHolder.getInstance().getDeviceLat();
                            DeviceInfo lon = GeoLocationHolder.getInstance().getDeviceLon();
                            if (lat != null && lon != null) {
                                params.add(lat);
                                params.add(lon);
                            }
                            params.add(new DeviceInfo("deviceName", deviceInfoHolder.getDeviceInfo(SWConstants.DEVICE_NAME)));
                            params.add(new DeviceInfo("osName", "Android " + Build.VERSION.RELEASE));

                            final DeviceInfo exceptionMsg = new DeviceInfo("exceptionMsg", null);
                            final DeviceInfo totalexceptionMsg = new DeviceInfo("totalexceptionMsg", null);
                            final DeviceInfo exceptionType = new DeviceInfo("exceptionType", Integer.valueOf(ExceptionType.APP_EXCEPTION.getCode()).toString());
                            params.add(exceptionMsg);
                            params.add(exceptionType);
                            params.add(totalexceptionMsg);

                            for (File file : files) {
                                FileInputStream fis = null;
                                try {
                                    fis = new FileInputStream(file);
                                    BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                                    StringBuilder jsonBuilder = new StringBuilder();
                                    String temp;
                                    while ((temp = br.readLine()) != null) {
                                        jsonBuilder.append(temp);
                                    }
                                    JSONObject jsonObject = new JSONObject(jsonBuilder.toString());
                                    exceptionMsg.setValue(jsonObject.getString("digest"));
                                    totalexceptionMsg.setValue(jsonObject.getString("detail"));

                                    String msg = HttpUtil.post(ServiceUrls.SERVER_DOMAIN + ServiceUrls.HANDLE_EXCEPTION, params);
                                    //delete after upload success
                                    if(msg != null) {
                                        SWResponse response = SWResponse.fromJson(msg);
                                        if (response.getCode() == SWResponseCode.SUCCESS.getCode()) {
                                            file.delete();
                                        }
                                    }

                                } catch (FileNotFoundException e) {
                                    Log.e(TAG, e.getMessage(), e);
                                } catch (IOException e) {
                                    Log.e(TAG, e.getMessage(), e);
                                } catch (JSONException e) {
                                    Log.e(TAG, e.getMessage(), e);
                                }catch(Exception e){
                                	Log.e(TAG, e.getMessage(), e);
                                } finally {
                                    if (fis != null) {
                                        try {
                                            fis.close();
                                        } catch (Exception e) {
                                            Log.e(TAG, e.getMessage(), e);
                                        }
                                    }
                                }
                            }
                        }
                    }).start();
                }

            }
        }

    }

    /**
     * 上传异常信息
     *
     * @param info 异常对象
     */
    public SWResponse saveExceptionInfo(final ExceptionInfo info) throws IOException {

        if (info != null) {

            String totalMsg = info.getTotalexceptionMsg();
            if(totalMsg != null) {
                info.setTotalexceptionMsg(GZipUtil.gzip(totalMsg));
            }

            String msg = HttpUtil.post(ServiceUrls.SERVER_DOMAIN + ServiceUrls.HANDLE_EXCEPTION, info.toParams());
            return SWResponse.fromJson(msg);
        }
        return null;
    }

    /**
     * 保存设备信息及登录信息
     *
     * @param userId  用户ID
     * @param success 是否登录成功
     */
    @TargetApi(Build.VERSION_CODES.DONUT)
    public void saveLoginAndDeviceInfo(String userId, boolean success) {
        final List<NameValuePair> params = new ArrayList<NameValuePair>();
        DeviceInfoHolder deviceInfoHolder = DeviceInfoHolder.getInstance();
        params.add(deviceInfoHolder.getDeviceId());
        params.add(deviceInfoHolder.getPackageName());
        params.add(new DeviceInfo("deviceInfo.deviceMode", deviceInfoHolder.getDeviceInfo(SWConstants.DEVICE_NAME)));
        params.add(new DeviceInfo("userName", userId));
        params.add(new DeviceInfo("deviceInfo.osName", Build.VERSION.RELEASE));
        params.add(new DeviceInfo("deviceInfo.osVersion", Build.VERSION.CODENAME + "||" + Build.VERSION.RELEASE));
        params.add(deviceInfoHolder.getAppName());
        params.add(deviceInfoHolder.getAppVersion());
        params.add(deviceInfoHolder.getVersionCode());

        DeviceInfo lat = GeoLocationHolder.getInstance().getDeviceLat();
        DeviceInfo lon = GeoLocationHolder.getInstance().getDeviceLon();
        if (lat != null && lon != null) {
            params.add(lat);
            params.add(lon);
        }

        params.add(new DeviceInfo("loginSuccess", success + ""));

        params.add(deviceInfoHolder.getIpAddress());
        String msg = null;
        try {
            msg = HttpUtil.post(ServiceUrls.SERVER_DOMAIN + ServiceUrls.CEOLLECT_DEVICE_INFO, params);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SWResponse response = SWResponse.fromJson(msg);

        System.out.print(response.getCode());
        System.out.print(response.getMsg());
    }

    /**
     * 发送当前设备的状态信息到服务器
     * @param longitude 设备所处位置的经度
     * @param latitude 设备所处位置的纬度
     * @param deviceInfoHolder 设备信息
     */
    public void sendDeviceStatus(final double longitude,final double latitude, final DeviceInfoHolder deviceInfoHolder) {
        sendDeviceStatus(longitude,latitude,deviceInfoHolder,null);
    }


    /**
     * 发送当前设备的状态信息到服务器
     * @param longitude 设备所处位置的经度
     * @param latitude 设备所处位置的纬度
     * @param deviceInfoHolder 设备信息
     * @param userId 用户ID
     */
    public void sendDeviceStatus(final double longitude,final double latitude, final DeviceInfoHolder deviceInfoHolder,final String userId) {
        Log.d(TAG,"开始上传设备位置信息");

        new Thread(new Runnable() {

            @Override
            public void run() {

                final List<NameValuePair> params = new ArrayList<NameValuePair>();

                params.add(deviceInfoHolder.getAppName());
                params.add(deviceInfoHolder.getPackageName());
                params.add(deviceInfoHolder.getDeviceId());
                params.add(deviceInfoHolder.getIpAddress());
                params.add(deviceInfoHolder.getAppVersion());
                params.add(deviceInfoHolder.getVersionCode());

                params.add(new DeviceInfo("deviceLat",Double.valueOf(latitude).toString()));
                params.add(new DeviceInfo("deviceLon",Double.valueOf(longitude).toString()));

                if(userId != null) {
                    params.add(new DeviceInfo("userName",userId));
                }

                try {
                    String msg = HttpUtil.post(ServiceUrls.SERVER_DOMAIN + ServiceUrls.CEOLLECT_PASS_BACK_INFO, params);
                    Log.d(TAG,"上传设备状态信息完成,Response:" + SWResponse.fromJson(msg).getMsg());
                } catch (Exception e) {
                    Log.e(TAG,"上传设备信息失败",e);
                }

            }
        }).start();
    }

}
