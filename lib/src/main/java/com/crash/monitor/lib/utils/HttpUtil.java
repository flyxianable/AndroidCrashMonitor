package com.crash.monitor.lib.utils;

import android.util.Log;
import com.crash.monitor.lib.constants.SWConstants;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * http请求工具类
 * Created by jale on 14-7-9.
 */
public class HttpUtil {

    private final static String TAG = "HttpUtil";

    /**
     * 通过post方式调用服务端
     *
     * @param url    要调用的服务url
     * @param params 参数，名值对
     * @return response
     */
    public static String post(final String url, final List<NameValuePair> params) throws IOException {

        Log.d(TAG,"执行HTTP请求："  + url);
        Log.d(TAG,"[参数列表]：");
        if(params != null) {
            for(NameValuePair nvp : params) {
                Log.d(TAG,"-------------> " + nvp.getName() + " : " + nvp.getValue());
            }
        }

        HttpPost httpRequest = new HttpPost(url);
        String msg = null;
        // 设置字符集
        try {
            HttpEntity httpentity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
            httpRequest.setEntity(httpentity);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        HttpClient httpclient = new DefaultHttpClient();
        /**
         * httpClient必须设置合理的超时时间，否则服务端无法返回状态码时，当前线程会一直阻塞
         */
        httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, SWConstants.HTTP_CONNECT_TIMEOUT);//连接时间2s
        httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, SWConstants.HTTP_TRANSPORT_TIMEOUT);//数据传输时间20s
        HttpResponse httpResponse;
        httpResponse = httpclient.execute(httpRequest);
        if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            msg = EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);
        }
        httpRequest.abort();
        httpclient.getConnectionManager().shutdown();

        return msg;
    }
    public static String httpGetData(String url, String param,String charSet) throws Exception{
        String result = "";
        BufferedReader in = null;
        URLConnection conn = null;
        URL realUrl = null;
        String urlName = url + "?" + param;
        try {
            realUrl = new URL(urlName);
            conn = realUrl.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.connect();
            if(charSet!=null && charSet.length()>0){
                in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(),charSet));
            }else{
                in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
            }
            String line = "";
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            throw e;
        }
        finally {
            try {
                realUrl=null;
                conn=null;
                if (in != null) {
                    in.close();
                }
            } catch (Exception ex) {
            }
        }
        return result;
    }

}
