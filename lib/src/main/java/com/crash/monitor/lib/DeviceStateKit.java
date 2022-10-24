package com.crash.monitor.lib;

import android.content.Context;
import com.crash.monitor.lib.service.SecurityService;
import com.crash.monitor.lib.utils.DeviceInfoHolder;

/**
 *
 * 设备状态信息收集
 * Created by jale on 14-8-18.
 */
public class DeviceStateKit {

    /**
     * 回传设备信息
     * @param context 应用上下文
     * @param longitude 设备经度
     * @param latitude 设备纬度
     */
    public static void sendLocationInfo(final Context context,final double latitude, final double longitude) {
        DeviceInfoHolder deviceInfoHolder = DeviceInfoHolder.getInstance();
        deviceInfoHolder.init(context);
        SecurityService securityService = new SecurityService();
        securityService.sendDeviceStatus(longitude,latitude,deviceInfoHolder);
    }

}
