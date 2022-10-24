package com.crash.monitor.lib;

import android.content.Context;
import android.text.TextUtils;

import com.crash.monitor.lib.constants.SWConstants;
import com.crash.monitor.lib.constants.SWResponseCode;
import com.crash.monitor.lib.model.SWResponse;
import com.crash.monitor.lib.service.SecurityService;
import com.crash.monitor.lib.utils.GeneralUtils;
import com.crash.monitor.lib.utils.GeoLocationHolder;

/**
 * 登陆校验工具类
 * Created by jale on 14-7-23.
 */
public class LoginKit {


    private final static String TAG = "LoginUtil";

    /**
     * 最近一次登陆失败的时间
     */
    private static long failStamp = 0;

    /**
     * 半小时内的登陆失败次数
     */
    private static int failTimes = 0;

    /**
     * 半小时内登陆失败次数上限,可以在 Application 的meta-data中配置
     * 键值名：MAX_LOGIN_FAIL_TIMES
     */
    private static Integer maxLoginFailTimes = 3;

    /**
     * 敏感信息清除工具，如果如果在检测到设备被禁用时清除敏感信息就必须实现SensitiveInfoCleaner这个接口并在meta-data中指定它
     * 键值名：SENSITIVE_INFO_CLEANER
     */
    private static SensitiveInfoCleaner sensitiveInfoCleaner = null;

    /**
     * 最近一次登陆的用户ID
     */
    private static String lastLoginUserId = null;

    private static SecurityService securityService;

    private static boolean inited = false;

    private static DBManager dbManager = null;

    /**
     * 登陆动作验证方法，对设备的登陆信息进行一系列的验证
     * <p/>
     * 验证顺序：登陆次数验证 --> 锁定设备验证 --> 账号绑定验证
     * 验证过程中讲调用远程服务器，调用方式采用HTTPS并限定公钥
     * <p/>
     * Update: 登陆失败次数的限制无法只使用一个方法完成，因为无法获知应用登陆的结果
     * <p/>
     * 应用开发人员希望的调用方式是：
     * if(LoginUtil.preLogin(context,userId) {
     * // login action
     * // ...
     * // LoginUtil.setLoginResult(true | false);
     * }
     *
     * @param context Application
     * @param userId  用户名
     * @param sic     异常清理接口实现类
     * @return SWResponse
     */
//    public static SWResponse preLogin(final Context context, final String userId, SensitiveInfoCleaner sic) {
//
//        if(dbManager == null) {
//           dbManager = new DBManager(context);
//        }
//        SWResponse response = null;
//        //首次调用时进行一系列的初始化动作
//        if (!inited) {
//            synchronized (LoginKit.class) {
//                if (!inited) {
//                    //检查应用是否自定义了登陆失败次数限制
//                    String mt = GeneralUtils.getMetaData(context, SWConstants.MAX_LOGIN_FAIL_TIMES);
//                    if (mt != null) {
//                        maxLoginFailTimes = Integer.valueOf(mt);
//                    }
//
//                    //检查应用是否自定义了敏感信息清除接口实现类
//                    String clz = GeneralUtils.getMetaData(context, SWConstants.SENSITIVE_INFO_CLEANER);
//
//                    if (sensitiveInfoCleaner == null) {
//                        if (clz == null) {
//                            Log.d(TAG, "没有配置可用的SENSITIVE_INFO_CLEANER");
//                        } else {
//                            ClassLoader cl = Thread.currentThread().getContextClassLoader();
//                            try {
//                                Class clazz = cl.loadClass(clz);
//                                sensitiveInfoCleaner = (SensitiveInfoCleaner) clazz.newInstance();
//                            } catch (InstantiationException e) {
//                                Log.e(TAG, e.getMessage(), e);
//                            } catch (IllegalAccessException e) {
//                                Log.e(TAG, e.getMessage(), e);
//                            } catch (ClassNotFoundException e) {
//                                Log.e(TAG, e.getMessage(), e);
//                            }
//                        }
//                    }
//                    //初始化安全服务类
//                    securityService = new SecurityService();
//
//                    //上下文初始化
//                    SDKContext.init(context);
//                    inited = true;
//                }
//            }
//        }
//
//        Log.d(TAG, "prepare to login >>>");
//
//
//        if (sic == null) {
//            sic = sensitiveInfoCleaner;
//        }
//
//        //超过失败次数限制时禁止登陆
//        int failTimes = 0;
//        long failStamp = 0;
//        long curr = System.currentTimeMillis();
//        String val = dbManager.query(SWConstants.LOGIN_FAIL_LOG_KEY);
//        if (val != null) {
//            String[] arr = val.split("-");
//            failTimes = Integer.valueOf(arr[1]);
//            failStamp = Long.valueOf(arr[0]);
//            if (failTimes >= maxLoginFailTimes && curr - failStamp < 300 * 1000) {
//                response = new SWResponse(SWResponseCode.FORBIDDEN);
//            }
//        }
//
//        //checkDevice
////        if (checkResponse != null && checkResponse.getCode() == SWResponseCode.BANNED.getCode()) {
////            return checkResponse;
////        } else if (checkResponse != null && lastLoginUserId.equals(userId)) {
////            return checkResponse;
////        } else {
//        if(response == null) {
//            response = checkDevice(userId);
//        }
//
//        if (response == null) {
//            response = new SWResponse(SWResponseCode.NET_OR_SERVER_ERROR);
//        }
//
//        if (response.getCode() != SWResponseCode.SUCCESS.getCode()) {
//            //检测到设备被禁用时清除敏感信息
//            if (response.getCode() == SWResponseCode.BANNED.getCode() && sic != null) {
//                sic.clean();
//            }
//        }
//
//        //save login exception in file
//        if (response.getCode() != SWResponseCode.SUCCESS.getCode()
//                && response.getCode() != SWResponseCode.NET_OR_SERVER_ERROR.getCode()) {
//            ExceptionUtil.saveInFile(context,new LoginException(response,userId), ExceptionType.SECURITY_EXCEPTION.getCode(), "");
//        }
//
//        return response;
////        }
//
//    }

    /**
     * 等同于preLogin(context,userId,null)，此时将调用setSensitiveInfoCleaner设置的清理工具，如果没有则忽略
     *
     * @param context 应用上下文
     * @param userId  用户ID
     * @return SWResponse
     */
//    public static SWResponse preLogin(final Context context, final String userId) {
//        return preLogin(context, userId, null);
//    }

    /**
     * 设置检测到设备禁用时需要调用的清理接口实现类
     *
     * @param sensitiveInfoCleaner 一个SensitiveInfoCleaner接口的实现类
     */
    public static void setSensitiveInfoCleaner(SensitiveInfoCleaner sensitiveInfoCleaner) {
        LoginKit.sensitiveInfoCleaner = sensitiveInfoCleaner;
    }

    /**
     * 应用在执行完登陆动作后，需要调用此方法回传登陆结果
     * preLogin 和 setLoginResult 方法必须一起使用，否则登陆校验的失败次数限制将形同虚设
     *
     * @param context Application
     * @param success 是否登陆成功
     */
    public static void setLoginResult(final Context context, boolean success) {
        DBManager dbManager = new DBManager(context);
        long curr = System.currentTimeMillis();
        if (!success) {
            if (curr - failStamp > 300 * 1000) {
                failStamp = curr;
                failTimes = 1;
            } else {
                failTimes += 1;
            }
            dbManager.add(SWConstants.LOGIN_FAIL_LOG_KEY, failStamp + "-" + failTimes);
        }

////        saveLoginAction2Log(context, success);
    }


    /**
     * 检查当前设备合法性
     * 在无法连接到服务器或服务器异常时（eg：50x），该方法返回值为空（null）
     *
     * @param userId 用户ID
     * @return SWResponse
     */
    private static SWResponse checkDevice(final String userId) {
        SWResponse checkResponse = securityService.checkDevice(userId);
        setLastLoginUserId(userId);
        return checkResponse;
    }

    /**
     * 记录设备此次的登录动作
     * 只有当应用调用了setLoginResult时才会进行记录
     * 此方法是异步执行的
     *
     * @param context 应用上下文
     * @param success 是否登录成功
     */
    private static void saveLoginAction2Log(final Context context, final boolean success) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                securityService.saveLoginAndDeviceInfo(lastLoginUserId, success);
            }
        }).start();
    }

    public static String getLatestLoginUserId() {
    	if(!TextUtils.isEmpty(lastLoginUserId)){
    		
    		return lastLoginUserId;
    	}else{
    		
    		if(dbManager != null){
    			return dbManager.query(SWConstants.LATEST_LOGIN_USER);
    		}
    	}
    	return "";
    }


    /**
     * 保存最近一次尝试登陆的用户的ID到SQLite
     * @param lastLoginUserId 最近的用户ID
     */
    public static void setLastLoginUserId(String lastLoginUserId) {
        LoginKit.lastLoginUserId = lastLoginUserId;
        if(dbManager != null) {
            dbManager.add(SWConstants.LATEST_LOGIN_USER,lastLoginUserId);
        }
    }


    /**
     * 记录设备此次的登录动作
     * 只有当应用调用了setLoginResult时才会进行记录
     * 此方法是异步执行的
     *
     * @param context   应用上下文
     * @param success   登陆结果
     * @param longitude 当前经度
     * @param latitude  当前纬度
     */
    public static void setLoginResult(Context context, final boolean success, final double longitude, final double latitude) {
        DBManager dbManager = new DBManager(context);
        long curr = System.currentTimeMillis();
        if (!success) {
            if (curr - failStamp > 300 * 1000) {
                failStamp = curr;
                failTimes = 1;
            } else {
                failTimes += 1;
            }
            dbManager.add(SWConstants.LOGIN_FAIL_LOG_KEY, failStamp + "-" + failTimes);
        }

        GeoLocationHolder.getInstance().setLocation(latitude, longitude);
        new Thread(new Runnable() {
            @Override
            public void run() {
                securityService.saveLoginAndDeviceInfo(lastLoginUserId, success);
            }
        }).start();
    }
    
    public static SWResponse checkFailedLoginNum(Context context,String userName){
    	SWResponse response = null;
    	if(dbManager == null) {
            dbManager = new DBManager(context);
        }
        //检查应用是否自定义了登陆失败次数限制
        String mt = GeneralUtils.getMetaData(context, SWConstants.MAX_LOGIN_FAIL_TIMES);
        if (mt != null) {
            maxLoginFailTimes = Integer.valueOf(mt);
        }
        //超过失败次数限制时禁止登陆
        int failTimes = 0;
        long failStamp = 0;
        long curr = System.currentTimeMillis();
        String val = dbManager.query(SWConstants.LOGIN_FAIL_LOG_KEY);
        if (val != null) {
            String[] arr = val.split("-");
            failTimes = Integer.valueOf(arr[1]);
            failStamp = Long.valueOf(arr[0]);
            if (failTimes >= maxLoginFailTimes && curr - failStamp < 300 * 1000) {
                response = new SWResponse(SWResponseCode.FORBIDDEN);
            }
        }
        if(response == null){
        	response = new SWResponse(SWResponseCode.SUCCESS);
        }
        setLastLoginUserId(userName);
        return response;
    }
}
