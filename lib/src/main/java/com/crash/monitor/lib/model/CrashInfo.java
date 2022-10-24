package com.crash.monitor.lib.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 *
 * 应用程序崩溃时的异常信息
 * Created by jale on 14-6-30.
 */
@Deprecated
public class CrashInfo implements Serializable{

    private long createTime;

    private String packageName;

    private String digest;

    private String detail;

    private String deviceId;

    private String deviceInfo;

    public CrashInfo() {
        this.createTime = System.currentTimeMillis();
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
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

    public String toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("createTime",this.getCreateTime());
            json.put("packageName",this.getPackageName());
            json.put("digest",this.getDigest());
            json.put("detail",this.getDetail());
            json.put("deviceId",this.getDeviceId());
//            json.put("deviceInfo",this.getDeviceInfo());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }

}
