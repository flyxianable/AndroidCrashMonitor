package com.crash.monitor.lib.model;

import org.apache.http.NameValuePair;

/**
 * 设备参数存储
 * Created by jale on 14-7-10.
 */
public class DeviceInfo implements NameValuePair {

    private String name;

    private String value;

    public DeviceInfo(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getValue() {
        return this.value;
    }
}
