package com.crash.monitor.lib.model;

import com.crash.monitor.lib.constants.SWResponseCode;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 安全套件统一返回值
 * Created by jale on 14-6-24.
 */
public class SWResponse {

    /**
     * 服务调用结果标识，详见枚举类SWResponseCode
     */
    private int code;
    /**
     * 结果描述信息
     */
    private String msg;
    private String msgCode;
    private Object data;

    public SWResponse() {
    }

    public SWResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public SWResponse(SWResponseCode sw) {
        this.code = sw.getCode();
        this.msg = sw.getDesc();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsgCode() {
        return msgCode;
    }

    public void setMsgCode(String msgCode) {
        this.msgCode = msgCode;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }


    /**
     * 反序列化一个json
     * @param json json字符串
     * @return SWResponse
     */
    public static SWResponse fromJson(final String json) {

        if(json != null) {
            SWResponse swResponse = new SWResponse();
            try {
                JSONObject jsonObject = new JSONObject(json);
                swResponse.setCode(jsonObject.getInt("code"));
                swResponse.setMsg(jsonObject.getString("msg"));
                swResponse.setMsgCode(jsonObject.getString("msgCode"));
    //            swResponse.setMsgCode(jsonObject.getO("msgCode"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return swResponse;
        }

        return null;
    }

}
