package com.crash.monitor.lib.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;


public class SharePreUtil {

	public static SharedPreferences sharedPreferences;
	public final static String JDSEND_SHARE_PREFERENCE = "JdSendAndroidClient";// 默认shared
	/**
	 * 获取主配置文件
	 */
	public static SharedPreferences getJdSharedPreferences(Context context) {
		if (null == sharedPreferences) {			 
			sharedPreferences = context
					.getSharedPreferences(JDSEND_SHARE_PREFERENCE, Context.MODE_PRIVATE);
		}
		return sharedPreferences;
	}
	
	public static SharedPreferences getGesturePasswordSharedPreferences(Context context){
		return context.getSharedPreferences("Gesturepassword", Context.MODE_PRIVATE);
	}
	
	public static SharedPreferences getLoginRemeberSharedPreferences(Context context){
		return context.getSharedPreferences("login_remeber", Context.MODE_PRIVATE);
	}
	
	/**
	 * 保存access_token
	 * @param access_token
	 */
	public static void setAccessToken(Context context, String access_token){
		String accessToken = getAccessToken(context);
		if (!TextUtils.isEmpty(access_token) && !access_token.equals(accessToken)) {
			getJdSharedPreferences(context).edit().putString("access_token", access_token).commit();
		}
	}
	/**
	 * 获取access_token
	 * @return
	 */
	public static String getAccessToken(Context context){
		return getJdSharedPreferences(context).getString("access_token", "");
	}
	
	//公共的存取方法
	public static void saveStringToSharePreference(Context context, String key, String value){
		SharedPreferences sharedPreferences = SharePreUtil.getJdSharedPreferences(context);
		sharedPreferences.edit().putString(key, value).commit();
	}
	
	public static String getStringToSharePreference(Context context, String key){
		SharedPreferences sharedPreferences = SharePreUtil.getJdSharedPreferences(context);
		return sharedPreferences.getString(key, "");
	}
	
	public static void saveBooleanToSharePreference(Context context, String key, boolean value){
		SharedPreferences sharedPreferences = SharePreUtil.getJdSharedPreferences(context);
		sharedPreferences.edit().putBoolean(key, value).commit();
	}
	
	public static boolean getBooleanToSharePreference(Context context, String key, boolean defaultValue){
		SharedPreferences sharedPreferences = SharePreUtil.getJdSharedPreferences(context);
		return sharedPreferences.getBoolean(key, defaultValue);
	}
	
	public static void saveIntToSharePreference(Context context, String key, int value){
		SharedPreferences sharedPreferences = SharePreUtil.getJdSharedPreferences(context);
		sharedPreferences.edit().putInt(key, value).commit();;
	}
	
	public static int getIntToSharePreference(Context context, String key, int defaultValue){
		SharedPreferences sharedPreferences = SharePreUtil.getJdSharedPreferences(context);
		return sharedPreferences.getInt(key, defaultValue);
	}
	
	public static void saveLongtToSharePreference(Context context, String key, long value){
		SharedPreferences sharedPreferences = SharePreUtil.getJdSharedPreferences(context);
		sharedPreferences.edit().putLong(key, value).commit();
	}
	
	public static long getLongToSharePreference(Context context, String param, long defaultValue){
		SharedPreferences sharedPreferences = SharePreUtil.getJdSharedPreferences(context);
		return sharedPreferences.getLong(param, defaultValue);
	}
}

