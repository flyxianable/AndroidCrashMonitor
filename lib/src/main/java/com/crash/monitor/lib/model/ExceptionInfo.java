package com.crash.monitor.lib.model;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.crash.monitor.lib.LoginKit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 异常信息
 * Created by jale on 14-6-30.
 */
public class ExceptionInfo implements Serializable {

    private long createTimeL;

    private String deviceId;

    private String packageName;
    
    private String appVersion; //版本号。add lyb at 2015.3.6

    public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	private String deviceName;

    private String deviceLon;

    private String deviceLat;

    private String userName;

    private String osName;
    
    private String osVersion;
    
    private String deviceScreen;
    
    private String appName;

    private String exceptionType;

    private String exceptionMsg;

    private String totalexceptionMsg;

    private String ipAddress;

    private String deviceInfo;

    public ExceptionInfo() {
        this.createTimeL = System.currentTimeMillis();
    }

    public long getCreateTimeL() {
        return createTimeL;
    }

    public void setCreateTimeL(long createTimeL) {
        this.createTimeL = createTimeL;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
    

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceLon() {
        return deviceLon;
    }

    public void setDeviceLon(String deviceLon) {
        this.deviceLon = deviceLon;
    }

    public String getDeviceLat() {
        return deviceLat;
    }

    public void setDeviceLat(String deviceLat) {
        this.deviceLat = deviceLat;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
    }

    public String getExceptionMsg() {
        return exceptionMsg;
    }

    public void setExceptionMsg(String exceptionMsg) {
        this.exceptionMsg = exceptionMsg;
    }

    public String getTotalexceptionMsg() {
        return totalexceptionMsg;
    }

    public void setTotalexceptionMsg(String totalexceptionMsg) {
        this.totalexceptionMsg = totalexceptionMsg;
    }

    public String getOsVersion() {
		return osVersion;
	}

	public void setOsVersion(String osVersion) {
		this.osVersion = osVersion;
	}

	public String getDeviceScreen() {
		return deviceScreen;
	}

	public void setDeviceScreen(String deviceScreen) {
		this.deviceScreen = deviceScreen;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	/**
     * 从
     *
     * @param jsonStr 根据给出的json字符串构造出响应的ExceptionInfo对象
     * @return ExceptionInfo
     */
    public static ExceptionInfo fromJson(final String jsonStr) {

        ExceptionInfo info = new ExceptionInfo();

        try {
            JSONObject json = new JSONObject(jsonStr);
            info.setDeviceId(getJsonValue(json, "deviceId"));
            info.setCreateTimeL(json.getLong("createTimeL"));
            info.setPackageName(getJsonValue(json, "packageName"));
            info.setDeviceName(getJsonValue(json, "deviceName"));

            info.setDeviceLon(getJsonValue(json, "deviceLon"));
            info.setDeviceLat(getJsonValue(json, "deviceLat"));
//            info.setUserName(getJsonValue(json, "userName"));
            info.setUserName(LoginKit.getLatestLoginUserId()); //redo by lyb at 2015.03.06

            info.setOsName(getJsonValue(json, "osName"));
            info.setAppName(getJsonValue(json, "appName"));
            info.setExceptionMsg(getJsonValue(json, "exceptionMsg"));
            info.setExceptionType(getJsonValue(json, "exceptionType"));
            info.setTotalexceptionMsg(getJsonValue(json, "totalexceptionMsg"));

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return info;
    }

    private static String getJsonValue(final JSONObject json,final String k) throws JSONException {
        return json.has(k) ? json.getString(k) : "";
    }

    /**
     * 将当前对象转为json字符串
     *
     * @return json
     */
    public String toJson() {

        JSONObject json = new JSONObject();

        try {
            json.put("createTimeL", this.getCreateTimeL());
            json.put("packageName", this.getPackageName());
            json.put("deviceId", this.getDeviceId());
            json.put("deviceName", this.getDeviceName());

            if (this.getDeviceLat() != null) {
                json.put("deviceLat", this.getDeviceLat());
            }
            if (this.getDeviceLon() != null) {
                json.put("deviceLon", this.getDeviceLon());
            }
            if (this.getUserName() != null) {
                json.put("userName", this.getUserName());
            }

            json.put("osName", this.getOsName());
            json.put("appName", this.getAppName());
            json.put("exceptionType", this.getExceptionType());
            json.put("exceptionMsg", this.getExceptionMsg());
            json.put("totalexceptionMsg", this.getTotalexceptionMsg());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

    /**
     * 根据当前对象构造用于进行http请求的参数
     *
     * @return List<NameValuePair>
     */
    public List<NameValuePair> toParams() {
        List<NameValuePair> list = new ArrayList<NameValuePair>();

        list.add(new DeviceInfo("createTimeL", this.getCreateTimeL() + ""));
        list.add(new DeviceInfo("packageName", this.getPackageName()));
        list.add(new DeviceInfo("deviceId", this.getDeviceId()));
        list.add(new DeviceInfo("deviceName", this.getDeviceName()));

        if (this.getDeviceLat() != null) {
            list.add(new DeviceInfo("deviceLat", this.getDeviceLat()));
        }
        if (this.getDeviceLon() != null) {
            list.add(new DeviceInfo("deviceLon", this.getDeviceLon()));
        }
        if (this.getUserName() != null) {
        	//add versionName by lyb at 2015.03.06
            list.add(new DeviceInfo("userName", this.getUserName()+ ";versionName:" + getAppVersion()));
        }

        list.add(new DeviceInfo("osName", this.getOsName()));
        list.add(new DeviceInfo("appName", this.getAppName()));
        list.add(new DeviceInfo("exceptionType", this.getExceptionType()));
        list.add(new DeviceInfo("exceptionMsg", this.getExceptionMsg()));
        list.add(new DeviceInfo("totalexceptionMsg", this.getTotalexceptionMsg()));
        list.add(new DeviceInfo("osVersion", this.getOsVersion()));
        list.add(new DeviceInfo("ipAddress", this.getIpAddress()));
        list.add(new DeviceInfo("deviceScreen", this.getDeviceScreen()));

        return list;
    }


}
