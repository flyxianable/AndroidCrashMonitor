package com.crash.monitor.lib;

/**
 *
 * 隐私信息清除接口
 * 如果应用需要在检测到设备禁用时清除敏感信息，需要实现此接口并在Application的meta-data中指定自己的实现类
 *
 * Created by jale on 14-6-30.
 */
public interface SensitiveInfoCleaner {

    /**
     * 删除敏感信息
     *
     * 应用程序需要自己来实现删除敏感信息的细节
     */
     void clean();

}
