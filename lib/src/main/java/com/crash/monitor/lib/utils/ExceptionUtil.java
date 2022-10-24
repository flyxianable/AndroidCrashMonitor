package com.crash.monitor.lib.utils;

import android.content.Context;
import android.os.*;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.crash.monitor.lib.LoginKit;
import com.crash.monitor.lib.SDKContext;
import com.crash.monitor.lib.constants.ExceptionType;
import com.crash.monitor.lib.constants.SWConstants;
import com.crash.monitor.lib.constants.SWResponseCode;
import com.crash.monitor.lib.model.DeviceInfo;
import com.crash.monitor.lib.model.ExceptionInfo;
import com.crash.monitor.lib.model.SWResponse;
import com.crash.monitor.lib.service.SecurityService;
import org.apache.http.NameValuePair;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 异常信息保存工具
 * <p/>
 * 根据应用开发人员的建议，所有接口都以静态方法的形式提供。
 * 应用程序自行选择异常信息的保存方式，以及异常信息记录文件的上传时机，本工具只提供相关接口。
 * <p/>
 * Created by jale on 14-7-23.
 */
public class ExceptionUtil {


    public final static String TAG = "ExceptionUtil";


    /**
     * 将异常信息保存在本地文件中
     *
     * @param applicationContext app上下文
     * @param e                  异常
     */
    public static void saveInFile(final Context applicationContext, final Throwable e, String actName) {
        saveInFile(applicationContext,e,ExceptionType.APP_EXCEPTION.getCode(), actName);
    }

    /**
     * 将异常信息发送到服务器
     *
     * 该方法将启动一个子线程
     *
     * @param applicationContext apps上下文
     * @param e                  异常
     */
    public static void saveInServer(final Context applicationContext, final Throwable e, String actName) {
        saveInServer(applicationContext, e, ExceptionType.APP_EXCEPTION.getCode(), actName);
    }


    /**
     * 将异常信息发送到服务器
     *
     * 该方法将启动一个子线程
     *
     * @param applicationContext apps上下文
     * @param e                  异常
     * @param code               异常类型
     */
    public static void saveInServer(final Context applicationContext, final Throwable e, int code, final String actName) {
        SDKContext.init(applicationContext);
        final ExceptionInfo info = createException(e, code, actName);

        if(info != null) {
            new Thread(new Runnable() {
                @Override
                protected Object clone() throws CloneNotSupportedException {
                    return super.clone();
                }

                @Override
                public void run() {
                    SecurityService securityService = new SecurityService();
                    try {
                        securityService.saveExceptionInfo(info);
                    } catch (Exception e1) {
                        Log.d(TAG,"网络异常");
                        //当上传到服务器出错时，将异常信息暂时存到本地
                        saveInFile(applicationContext,e, actName);
                    }
                }
            }).start();
        }
    }

    /**
     * 将所有保存在本地的异常信息上传到服务器
     *
     * 如果存在异常记录文件，该方法将启动一个子线程来逐个将信息上传至服务器，上传成功之后异常文件将被清除。
     */
    public static void sendAll2Server(final Context context) {

        SDKContext.init(context);
        final SecurityService securityService = new SecurityService();

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = Environment.getExternalStorageDirectory() + SDKContext.getCrashFileDirectory();
            File dir = new File(path);
            if (dir.exists()) {
                final File[] files = dir.listFiles();
                if (files != null && files.length > 0) {

                    Toast.makeText(context,"正在上传应用异常记录", Toast.LENGTH_LONG).show();
                    //任务处理器
                    final Handler fileHandler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            Bundle data = msg.getData();
                            if(data != null) {
                                int code = data.getInt("code");
                                String message = data.getString("msg");
                                String filePath = data.getString("file");
                                if(code == SWResponseCode.SUCCESS.getCode()) {
                                    message = "文件上传成功";
                                }
                                if(message != null && filePath != null) {
                                    Toast.makeText(context,message + " : " + filePath, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    };

                    new Thread(new Runnable() {

//                        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                        @Override
                        public void run() {
                            List<NameValuePair> params;

                            Looper.prepare();


                            for (File file : files) {
                                FileInputStream fis = null;
                                try {
                                    fis = new FileInputStream(file);
                                    BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                                    StringBuilder jsonBuilder = new StringBuilder();
                                    String temp;
                                    while ((temp = br.readLine()) != null) {
                                        jsonBuilder.append(temp);
                                    }

                                    ExceptionInfo info = ExceptionInfo.fromJson(jsonBuilder.toString());

                                    SWResponse response = securityService.saveExceptionInfo(info);
                                    //delete after upload success
                                    if (response.getCode() == SWResponseCode.SUCCESS.getCode() && file.exists()) {
                                        file.delete();
                                    }
                                    //send msg to UI thread
                                    Message message = Message.obtain();
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("code",response.getCode());
                                    bundle.putString("msg",response.getMsg());
                                    bundle.putString("file",file.getAbsolutePath());
                                    message.setData(bundle);
                                    fileHandler.sendMessage(message);

                                } catch (FileNotFoundException e) {
                                    Log.e(TAG, e.getMessage(), e);
                                } catch (IOException e) {
                                    Log.e(TAG, e.getMessage(), e);
                                }catch(Exception e){
                                	Log.e(TAG, e.getMessage(), e);
                                } finally {
                                    if (fis != null) {
                                        try {
                                            fis.close();
                                        } catch (Exception e) {
                                            Log.e(TAG, e.getMessage(), e);
                                        }
                                    }
                                }
                            }
                        }
                    }).start();
                }

            }
        }
    }


    /**
     * 将异常信息保存到文件
     *
     * @param e 异常信息
     * @param code 异常类型
     * @return fileName
     */
    private static ExceptionInfo createException(Throwable e, int code, String actName) {

        ExceptionInfo info = new ExceptionInfo();

        DeviceInfoHolder deviceInfoHolder = DeviceInfoHolder.getInstance();
        GeoLocationHolder geoLocationHolder = GeoLocationHolder.getInstance();

    
        info.setDeviceId(deviceInfoHolder.getDeviceId().getValue());
        if (e.getMessage() != null) {
            info.setExceptionMsg(e.getMessage());
        } else {
            info.setExceptionMsg(e.toString());
        }
        info.setDeviceName(deviceInfoHolder.getDeviceInfo(SWConstants.DEVICE_NAME));

        DeviceInfo lat = geoLocationHolder.getDeviceLat();
        DeviceInfo lon = geoLocationHolder.getDeviceLon();
        if (lat != null && lon != null) {
            info.setDeviceLat(lat.getValue());
            info.setDeviceLon(lon.getValue());
        }

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
        
        if(!TextUtils.isEmpty(actName)){
        	result = "className:" + actName + " ; exp:" + result;
        }
        
        info.setTotalexceptionMsg(result);
        info.setExceptionType(code + "");
        info.setPackageName(deviceInfoHolder.getPackageName().getValue());
        info.setAppVersion(deviceInfoHolder.getAppVersion().getValue());//add by lyb at 2015.03.06
        info.setAppName(deviceInfoHolder.getAppName().getValue());
        info.setOsName(deviceInfoHolder.getOsName().getValue());
        info.setOsVersion(deviceInfoHolder.getOsVersion().getValue());
        info.setUserName(LoginKit.getLatestLoginUserId());
        info.setIpAddress(deviceInfoHolder.getIpAddress().getValue());
        info.setDeviceScreen(deviceInfoHolder.getDeviceScreen().getValue());
        return info;


    }

    /**
     * 保存异常信息，如果当前连接是wifi，异常会被直接发送到服务器而不存为文件
     *
     * @param context 应用上下文
     * @param e 异常
     * @param code 异常类型
     */
    public static void saveInFile(Context context, Throwable e, int code, String actName) {
        SDKContext.init(context);

        if(SDKContext.isWifiConnected()) {
            //如果是wifi连接状态，则直接上传到服务器，不存文件
            saveInServer(context, e, code, actName);
            //将其他异常信息一并上传
            sendAll2Server(context);
            return;
        }

        ExceptionInfo info = createException(e,code, actName);
        SimpleDateFormat sdf = new SimpleDateFormat(SWConstants.DATE_FORMATER);

        long timestamp = System.currentTimeMillis();
//        String time = sdf.format(new Date()); //del by lyb at 2015.03.06
        String fileName = "crash_"  + timestamp + ".log";

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String path = Environment.getExternalStorageDirectory() + SDKContext.getCrashFileDirectory();
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }           
           
            FileOutputStream fos = null;
            try {
            	File file = new File(path + fileName);
				if (!file.exists()) {
					file.createNewFile();
				}
                fos = new FileOutputStream(path + fileName);
                fos.write(info.toJson().getBytes());
                fos.close();

            } catch (Exception e1) {
                Log.e(TAG, "an error occured while writing file...", e);
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (Exception e1) {
                        Log.e(TAG, "an error occured while writing file...", e);
                    }
                }
            }
        }
    }
}
