
//
//import android.annotation.TargetApi;
//import android.app.Activity;
//import android.app.Application;
//import android.content.ComponentName;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Looper;
//import android.util.Log;
//import android.widget.Toast;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * 异常信息保存工具
// * <p/>
// * 根据应用开发人员的建议，所有接口都以静态方法的形式提供。
// * 应用程序自行选择异常信息的保存方式，以及异常信息记录文件的上传时机，本工具只提供相关接口。
// * <p/>
// * Created by jale on 14-7-23.
// */
//public class ExceptionKit {
//
//    private final String TAG = "ExceptionKit";
//
//    private boolean inited = false;
//
//
////	private Map<ComponentName, ExceptionHandler> configMap = new HashMap<ComponentName, ExceptionHandler>();
//	
//	private static ExceptionKit instance;
//	
//	private ExceptionKit(){
//		
//	}
//	
//	public static ExceptionKit getInstance(){
//		if(instance == null){
//			instance = new ExceptionKit();
//		}
//		return instance;
//	}
//
//    /**
//     * 设置要监视的Activity对象
//     *
//     * 注意事项：1）4.0之前的版本无法对Activity进行有效监控，一旦调用了setActivity，运行在此线程的所有模块的
//     *          异常收集方式都将被修改。在4.0之后的版本上，当Activity被销毁时，异常收集方式将会重置到调用
//     *          此方法之前的状态
//     *          2）当设备没有外部存储时，只能选择即时上传已异常信息
//     *
//     * @param activity    要监视的Activity对象
//     * @param send2Server 当异常发生时，是否立即将信息上传至服务器
//     *                    true:上传至服务器
//     *                    false:保存到本地文件系统中
//     */
//    public void setActivity(final Activity activity, boolean send2Server) {
//
//        Log.d(TAG, "bind exception handler : " + activity.getComponentName().getClassName());
//        //上下文初始化
//        SDKContext.init(activity.getApplication(), false, false);
//        init(activity.getApplication());
//
//        ExceptionHandler exceptionHandler = new ExceptionHandler(activity, send2Server, Thread.getDefaultUncaughtExceptionHandler());
////        configMap.put(activity.getComponentName(), exceptionHandler);
//        Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);
//
//    }
//    
//    public void setActivity(final Activity activity, boolean send2Server, boolean isReportCrashForTest) {
//    	Log.d(TAG, "bind exception handler : " + activity.getComponentName().getClassName());
//        //上下文初始化
//        SDKContext.init(activity.getApplication(), false, isReportCrashForTest);
//        init(activity.getApplication());
//
//        ExceptionHandler exceptionHandler = new ExceptionHandler(activity, send2Server, Thread.getDefaultUncaughtExceptionHandler());
////        configMap.put(activity.getComponentName(), exceptionHandler);
//        Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);
//    }
//    
//    public void setApplication(final Application application, boolean send2Server, boolean isReportCrashForTest) {
//    	
//        //上下文初始化
//        SDKContext.init(application, false, isReportCrashForTest);
//        init(application);
//
//        ExceptionHandler exceptionHandler = new ExceptionHandler(application, send2Server, Thread.getDefaultUncaughtExceptionHandler());
////        configMap.put(activity.getComponentName(), exceptionHandler);
//        Thread.setDefaultUncaughtExceptionHandler(exceptionHandler);
//    }
//
//    /**
//     * 等同于调用 setActivity(activity,false)，即将异常信息存到本地文件
//     *
//     * @param activity 要监视的Activity对象
//     */
//    public void setActivity(final Activity activity) {
//        setActivity(activity, false);
//    }   
//
//
//
//    /**
//     * 将所有记录在本地文件系统中的异常信息传到服务器，并删除本地文件
//     *
//     * @param context 应用上下文
//     */
//    public void flush(final Context context) {
//        ExceptionUtil.sendAll2Server(context);
//    }
//
//
//    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
//    private void init(final Application app) {
//        if (!inited) {
//            synchronized (ExceptionKit.class) {
//                if (!inited) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
//
//                        app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
//                            @Override
//                            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
//
//                            }
//
//                            @Override
//                            public void onActivityStarted(Activity activity) {
//
//                            }
//
//                            @Override
//                            public void onActivityResumed(Activity activity) {
//
//                            }
//
//                            @Override
//                            public void onActivityPaused(Activity activity) {
//
//                            }
//
//                            @Override
//                            public void onActivityStopped(Activity activity) {
//
//                            }
//
//                            @Override
//                            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
//
//                            }
//
//                            @Override
//                            public void onActivityDestroyed(Activity activity) {
//
//                                //检查当前的Activity是否设置自定义的ExceptionHandler并恢复默认设置
//
//                                ExceptionHandler exceptionHandler = configMap.get(activity.getComponentName());
//                                if (exceptionHandler != null) {
//                                    Thread.setDefaultUncaughtExceptionHandler(exceptionHandler.getOldHandler());
//                                }
//
//                                Log.d(TAG, "unbind exception handler : " + activity.getComponentName().getClassName());
//                            }
//                        });
//                    }
//                    inited = true;
//                }
//            }
//        }
//
//    }
//
//    /**
//     * 异常处理类
//     */
//     class ExceptionHandler implements Thread.UncaughtExceptionHandler {
//
//        private boolean send2Server;
//
//        private Activity activity;
//        
//        private Application application;
//
//        private Thread.UncaughtExceptionHandler oldHandler;
//        
//        private Context getContext(){
//        	Context context = null;
//    		if(activity != null && application == null){
//    			context = activity.getApplicationContext();
//    		}else{
//    			context = application.getApplicationContext();
//    		}
//    		return context;
//        }
//
//
//        public ExceptionHandler(Activity activity, boolean send2Server, Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler) {
//            this.activity = activity;
//            this.send2Server = send2Server;
//            this.oldHandler = defaultUncaughtExceptionHandler;
//        }
//        
//        public ExceptionHandler(Application application, boolean send2Server, Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler) {
//            this.application = application;
//            this.send2Server = send2Server;
//            this.oldHandler = defaultUncaughtExceptionHandler;
//        }
//
//        public Thread.UncaughtExceptionHandler getOldHandler() {
//            return oldHandler;
//        }
//
//        @Override
//        public void uncaughtException(Thread thread, Throwable ex) {     
//        	
//        	ex.printStackTrace();
//            if (!handleException(ex) && getContext() != null) {
//                oldHandler.uncaughtException(thread, ex);
//            } else {
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    Log.e(TAG, "error : ", e);
//                }
//                //防止主线程自动重启
////                app.exit();
//                //防止多次崩溃 8.12 by lyb
//                if(activity != null){
//                	//腾讯的定位异常
//                	if(ex.getMessage().indexOf("Settings$Global") > -1 || ex.getMessage().indexOf("tencentmap") > -1){
//                		SharePreUtil.saveBooleanToSharePreference(activity, "locExp", true);
//                	}                	
//                	activity.finish(); 
//                }
//                if(application == null){
//                	android.os.Process.killProcess(android.os.Process.myPid());
//                	System.exit(0);
//                }else{
//                	exitApp();
//                }
//            }
//
//        }
//        /**
//         * 防止主线程自动重启
//         */
//		private void exitApp() {
//			Intent startMain = new Intent(Intent.ACTION_MAIN);  
//            startMain.addCategory(Intent.CATEGORY_HOME);  
//            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
//            application.startActivity(startMain);  
//			System.exit(0);  
//
//		}
//
//        /**
//         * 处理异常信息
//         *
//         * @param ex 异常
//         * @return bool
//         */
//        private boolean handleException(Throwable ex) {
//            if (ex == null) {
//                return false;
//            }
//			
//			new Thread() {
//				@Override
//				public void run() {					
//					try {
//						Looper.prepare();
//						String tip = "抱歉，程序出现异常，即将退出";//
//						Toast.makeText(getContext(),
//								tip, Toast.LENGTH_LONG).show();
//						Log.v("lyb", "crash toast");
//						Looper.loop();
//					} catch (Throwable e) {
//						e.printStackTrace();
//					}
//					
//				}
//			}.start();
//			
//			String actName = "";
//			if(activity != null){
//				actName = activity.getPackageName() + "."+ activity.getLocalClassName();
//			}			
//
//            if (this.send2Server) {
//                ExceptionUtil.saveInServer(getContext(), ex, actName);
//            } else {
//                ExceptionUtil.saveInFile(getContext(), ex, actName);
//            }
//
//            return true;
//        }
//    }
//
//}
