package com.crash.monitor.lib.utils;

import com.crash.monitor.lib.constants.SWConstants;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 *
 * 压缩工具
 * Created by jale on 14-8-5.
 */
public class GZipUtil {


    private final static String ZIP_STR_CHARSET = "ISO-8859-1";

    /**
     * 对字符串进行压缩
     * 字符串本身采用UTF-8编码，压缩后的字符串使用ISO-8859-1进行编码
     *
     * @param str 压缩之前的字符串
     * @return 压缩之后的的字符串
     */
    public static String gzip(final String str) {

        if(str == null || str.length() == 0) {
            return  str;
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            gzipOutputStream.write(str.getBytes(SWConstants.CHARSET));
            gzipOutputStream.close();
            return byteArrayOutputStream.toString(ZIP_STR_CHARSET);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return str;
    }


    /**
     * 解压缩
     * 首先使用ISO-8859-1编码获取byte流，再用UTF-8编码还原字符串
     *
     * @param str 压缩后的字符串
     * @return 解压完成的字符串
     */
    public static String unGzip(final String str) {
        if(str == null || str.length() == 0) {
            return  str;
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(str.getBytes(ZIP_STR_CHARSET));
            GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);

            byte[] buff = new byte[1024];
            int n;
            while ((n = gzipInputStream.read(buff)) > 0) {
                byteArrayOutputStream.write(buff,0,n);
            }

            return byteArrayOutputStream.toString(SWConstants.CHARSET);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }


//    public static void main(String args[]) {
//        String x = "中发动房间打扫房间打扫房间爱的是附近的司法局爱的色放加大食品费建安大姐夫V大房间爱的是福建大厦覅敬爱的";
//        String zx = gzip(x);
//        System.out.println(zx);
//        System.out.println(unGzip(zx));
//    }

}
