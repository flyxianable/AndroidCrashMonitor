package com.crash.monitor.lib;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.util.Log;

/**
 * 监听wifi
 * Created by jale on 14-6-30.
 */
@Deprecated
public class WifiConnectedReceiver extends BroadcastReceiver {


    private final static String TAG = "WifiConnectedReceiver";

//    private ConnectivityManager connectivityManager;


    @Override
    public void onReceive(final Context context, Intent intent) {

//        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        Log.d(TAG,"Network changed!");

        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (null != parcelableExtra) {
                NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                if(networkInfo.isConnectedOrConnecting() && networkInfo.getType() == ConnectivityManager.TYPE_WIFI){
                    //在wifi状态下将信息发送给服务器
                    CrashHandler.getInstance().sendCrashInfo2Server();
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Looper.prepare();
//                            Toast.makeText(context,"WIFI Connected!",Toast.LENGTH_LONG).show();
//                            Looper.loop();
//                        }
//                    }).start();
                }
            }
        }
    }
}
