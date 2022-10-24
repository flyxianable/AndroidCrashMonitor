package com.crash.monitor.lib;

import android.content.Context;
import android.os.Environment;
import android.os.Looper;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import com.crash.monitor.lib.constants.SWConstants;
import com.crash.monitor.lib.model.CrashInfo;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 全局异常信息搜集处理工具
 * Created by jale on 14-6-26.
 */
@Deprecated
public class CrashHandler implements Thread.UncaughtExceptionHandler {


    private final static String TAG = "MRD_CRASH_HANDLER";

    private static CrashHandler INSTANCE = new CrashHandler();

    private CrashHandler() {
    }

    //单例
    public static CrashHandler getInstance() {
        return INSTANCE;
    }

    private Context context;

    private SecurityApplication app;

    private Thread.UncaughtExceptionHandler defHandler;

//    private Map<String,String> infoMap =  new HashMap<String, String>();

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    /**
     * 初始化  将自身设置为默认的异常处理器
     *
     * @param sa 程序上下文
     */
    public void init(SecurityApplication sa) {
        this.app = sa;
        this.context = sa.getApplicationContext();
        this.defHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 处理异常信息
     *
     * @param e 异常
     * @return bool
     */
    private boolean handleException(Throwable e) {
        if (e == null) {
            return false;
        }
		try {
			new Thread() {
				@Override
				public void run() {
					Looper.prepare();
					Toast.makeText(context, "抱歉，程序出现异常，即将退出", Toast.LENGTH_LONG)
							.show();
					Looper.loop();
				}
			}.start();
		}catch(Exception ex){
        	ex.printStackTrace();
        }

//        getDeviceInfo();
        saveException(e);
        return true;
    }

    /**
     * 获取设备信息
     */
//    private void getDeviceInfo() {
//        try {
//            PackageManager pm = context.getPackageManager();
//            PackageInfo pi = pm.getPackageInfo(context.getPackageName(),PackageManager.GET_ACTIVITIES);
//
//            if(pi != null) {
//                String versionName = pi.versionName == null ? "N/A" : pi.versionName;
//                String versionCode = pi.versionCode + "";
//
//                infoMap.put("versionName",versionName);
//                infoMap.put("versionCode",versionCode);
//            }
//
//        } catch (PackageManager.NameNotFoundException e) {
//            Log.e(TAG,"An error occured when collect package info");
//        }
//
//        Field[] fields = Build.class.getFields();
//
//        for (Field f : fields) {
//            f.setAccessible(true);
//            try {
//                infoMap.put(f.getName(),f.get(null).toString());
//                Log.d(TAG,f.getName() + "->" + f.get(null));
//            } catch (IllegalAccessException e) {
//                Log.e(TAG,"An error occured when collect crash info");
//            }
//        }
//
//    }


    /**
     * 将异常信息保存到文件
     *
     * @param e 异常信息
     * @return fileName
     */
    private String saveException(Throwable e) {
//        StringBuilder builder = new StringBuilder();
        //
//        Map<String,String> infoMap = DeviceInfoHolder.getInstance().getDeviceInfo();

//        for(Map.Entry<String,String> kv : infoMap.entrySet()) {
//            builder.append(kv.getKey()).append(" = ").append(kv.getValue()).append("\n");
//        }

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        CrashInfo info = new CrashInfo();
        info.setDeviceId(telephonyManager.getDeviceId());
        if (e.getMessage() != null) {
            info.setDigest(e.getMessage());
        } else {
            info.setDigest(e.toString());
        }
//        info.setDeviceInfo(builder.toString());


        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        e.printStackTrace(printWriter);

        Throwable cause = e.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();

        String result = writer.toString();

        info.setDetail(result);
        info.setPackageName(context.getPackageName());

        long timestamp = System.currentTimeMillis();
        String time = sdf.format(new Date());
        String fileName = "crash-" + time + "-" + timestamp + ".log";

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = Environment.getExternalStorageDirectory() + SWConstants.CRASH_FILE_DIR;
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(path + fileName);
                fos.write(info.toJson().getBytes());
                fos.close();

                return fileName;
            } catch (Exception e1) {
                Log.e(TAG, "an error occured while writing file...", e);
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e1) {
                        Log.e(TAG, "an error occured while writing file...", e);
                    }
                }
            }
        }
        return null;

    }


    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && context != null) {
            defHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.e(TAG, "error : ", e);
            }
            //防止主线程自动重启
            app.exit();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(10);
        }

    }

    public void sendCrashInfo2Server() {
        app.getSecurityService().saveCrashInfo();
    }
}
