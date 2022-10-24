package com.crash.monitor.lib.utils;

import android.util.Log;
import com.crash.monitor.lib.constants.SWConstants;
import org.apache.http.NameValuePair;

import javax.net.ssl.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.List;

/**
 * http请求处理工具类
 * Created by jale on 14-6-24.
 */
public class HttpsUtil {

    private final static String TAG = "HttpsClient";

    /**
     * GET方式的HTTPS请求
     * @param path request url
     * @return response message
     */
    public static String get(final String path) {

        InputStreamReader instream = null;
        BufferedReader br = null;
        try {
            TrustManager tm[] = {new PubKeyManager()};
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tm, null);

            URL url = new URL(path);

            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            //连接超时时间设置
            connection.setConnectTimeout(SWConstants.HTTP_CONNECT_TIMEOUT);
            //数据传输超时设置
            connection.setReadTimeout(SWConstants.HTTP_TRANSPORT_TIMEOUT);

            connection.setSSLSocketFactory(context.getSocketFactory());
            instream = new InputStreamReader(connection.getInputStream());
            br = new BufferedReader(instream);
            String temp;
            StringBuilder builder = new StringBuilder();
            while ((temp = br.readLine()) != null) {
                builder.append(temp);
            }
            return builder.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch(Exception e){
        	e.printStackTrace();
        } finally {
            try {
                if (instream != null) {
                    instream.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        return null;
    }

    /**
     *
     * 使用post方式传参的https请求
     * @param serverURL 服务器url
     * @param params 请求参数
     * @return 返回信息
     */
    public static String post(final String serverURL,final List<NameValuePair> params) {
        InputStreamReader instream = null;
        BufferedReader br = null;
        try {
            TrustManager tm[] = {new PubKeyManager()};
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tm, null);

            URL url = new URL(serverURL);

            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            //连接超时时间设置
            connection.setConnectTimeout(SWConstants.HTTP_CONNECT_TIMEOUT);
            //数据传输超时设置
            connection.setReadTimeout(SWConstants.HTTP_TRANSPORT_TIMEOUT);

            connection.setRequestMethod("POST");
            if(params != null) {
                for(NameValuePair nvp : params) {
                    connection.setRequestProperty(nvp.getName(),nvp.getName());
                }
            }

            connection.setSSLSocketFactory(context.getSocketFactory());
            instream = new InputStreamReader(connection.getInputStream());
            br = new BufferedReader(instream);
            String temp;
            StringBuilder builder = new StringBuilder();
            while ((temp = br.readLine()) != null) {
                builder.append(temp);
            }
            return builder.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch(Exception e){
        	e.printStackTrace();
        } finally {
            try {
                if (instream != null) {
                    instream.close();
                }
                if (br != null) {
                    br.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        return null;
    }


    /**
     * *
     * <p/>
     * 对公钥进行认证 防止中间人攻击
     * <p/>
     * 为了进一步提高安全性，还需要对APP进行抗反编译的相关工作
     */
    static class PubKeyManager implements X509TrustManager {

        private final static String PUB_KEY = "30820122300d06092a864886f70d01010105000382010f003082010a0282010100c65f2410eaa486ed501a5c2063b777ce26ceb47a5e91140fd250e25cf8f9d10bfcd2213548c024b82d48daed7420049c9623b7a24131c64f63b5409a0e18c56a9b45206981e5503e58a136e1c7edcefe42e107b6cdd5a98e5198c277236308d95fde70adb9fd52a87c29670c5a0e38349f24d2b1adffdd0fec83a37f69211f0d6927d2d46fcb22ac1e903675e584a06e7937f20540b7c28a7ba369151ab12c8c492d99ebaba4c18cb75e93a65ed210156ac1a689223923e23b6cc7fc9832504ee4ca24699f28073fb7c2fd984ed41df2c026cd9780a0e86e2bbbb583e243bee89fe0da80e4182912d8cb3ebda6d734d9b9f4e36787ea381380475d34c5897d1b0203010001";

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            if (chain == null) {
                throw new IllegalArgumentException("checkServerTrusted: X509Certificate array is null");
            }

            if (!(chain.length > 0)) {
                throw new IllegalArgumentException("checkServerTrusted: X509Certificate is empty");
            }

            if (!(null != authType && authType.equalsIgnoreCase("RSA"))) {
                throw new CertificateException("checkServerTrusted: AuthType is not RSA");
            }

            //SSL/TLS checks
            try {
                TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
                tmf.init((KeyStore) null);

                for (TrustManager trustManager : tmf.getTrustManagers()) {
                    ((X509TrustManager) trustManager).checkServerTrusted(chain, authType);
                }
            } catch (Exception e) {
                throw new CertificateException(e);
            }

            RSAPublicKey pubkey = (RSAPublicKey) chain[0].getPublicKey();
            String encoded = new BigInteger(1, pubkey.getEncoded()).toString(16);

            Log.d(TAG, encoded);

            final boolean expected = PUB_KEY.equalsIgnoreCase(encoded);
            if (!expected) {
                throw new CertificateException("checkServerTrusted: Expected public key: "
                        + PUB_KEY + ", got public key:" + encoded);
            }
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}

