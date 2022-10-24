package com.crash.monitor.lib;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.crash.monitor.lib.utils.ExceptionUtil;
import com.crash.monitor.lib.utils.SharePreUtil;
/**
 * 新的异常捕获类
 * @author liangyanbin
 *
 */
public class ExceptionKitHandler implements Thread.UncaughtExceptionHandler {
	
	private static ExceptionKitHandler instance;
	
    private boolean send2Server;

    private Activity activity;
    
    private Application application;
    
    private final String TAG = "ExceptionKit";

    private Thread.UncaughtExceptionHandler oldHandler;
    
    private ExceptionKitHandler(){
    	
    }
    
    public static ExceptionKitHandler getInstance(){
    	if(instance == null){
    		instance = new ExceptionKitHandler();
    	}
    	return instance;
    }
    
    private Context getContext(){
    	Context context = null;
		if(activity != null && application == null){
			context = activity.getApplicationContext();
		}else{
			context = application.getApplicationContext();
		}
		return context;
    }
    
    public void init(Activity activity, boolean send2Server, boolean isReportCrashForTest) {  
        this.activity = activity;
        this.send2Server = send2Server;
        SDKContext.init(activity.getApplication(), send2Server, isReportCrashForTest);
        if(oldHandler == null){
        	oldHandler = Thread.getDefaultUncaughtExceptionHandler(); 
        }
        Thread.setDefaultUncaughtExceptionHandler(this);  
    }
    
    public void init(Application application , boolean send2Server, boolean isReportCrashForTest) {  
        this.application = application;
        this.send2Server = send2Server;
        SDKContext.init(application.getApplicationContext(), send2Server, isReportCrashForTest);
        if(oldHandler == null){
        	oldHandler = Thread.getDefaultUncaughtExceptionHandler(); 
        }  
        Thread.setDefaultUncaughtExceptionHandler(this);  
    }    


    public Thread.UncaughtExceptionHandler getOldHandler() {
        return oldHandler;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {     
    	
    	ex.printStackTrace();
        if (!handleException(ex) && getContext() != null) {
            oldHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Log.e(TAG, "error : ", e);
            }
            //防止主线程自动重启
//            app.exit();
            //防止多次崩溃 8.12 by lyb
            if(activity != null){
            	//腾讯的定位异常
            	if(ex.getMessage().indexOf("Settings$Global") > -1 || ex.getMessage().indexOf("tencentmap") > -1){
            		SharePreUtil.saveBooleanToSharePreference(activity, "locExp", true);
            	}                	
            	activity.finish(); 
            }
            if(application == null){
            	android.os.Process.killProcess(android.os.Process.myPid());
            	System.exit(0);
            }else{
            	exitApp();
            }
        }

    }
    /**
     * 防止主线程自动重启
     */
	private void exitApp() {
		Intent startMain = new Intent(Intent.ACTION_MAIN);  
        startMain.addCategory(Intent.CATEGORY_HOME);  
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
        application.startActivity(startMain);  
		System.exit(0);  

	}

    /**
     * 处理异常信息
     *
     * @param ex 异常
     * @return bool
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
		
		new Thread() {
			@Override
			public void run() {					
				try {
					Looper.prepare();
					String tip = "抱歉，程序出现异常，即将退出";//
					Toast.makeText(getContext(),
							tip, Toast.LENGTH_LONG).show();
					Log.v("lyb", "crash toast");
					Looper.loop();
				} catch (Throwable e) {
					e.printStackTrace();
				}
				
			}
		}.start();
		
		String actName = "";
		if(activity != null){
			actName = activity.getPackageName() + "."+ activity.getLocalClassName();
		}			

        if (this.send2Server) {
            ExceptionUtil.saveInServer(getContext(), ex, actName);
        } else {
            ExceptionUtil.saveInFile(getContext(), ex, actName);
        }

        return true;
    }
}
