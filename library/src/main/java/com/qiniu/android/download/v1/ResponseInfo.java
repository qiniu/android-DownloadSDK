package com.qiniu.android.download.v1;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Misty on 16/2/17.
 */
public final class ResponseInfo {

    private static final String TAG = "ReqInfo";

    public static final int InvalidArgument = -4;
    public static final int Cancelled = -2;

    public static final String ERR_CONNECT_FAILED = "ErrConnectFailed";
    public static final String ERR_BROKEN_CONNECTION = "ErrBrokenConnection";
    public static final String ERR_DOMAIN_NOT_FOUND = "ErrDomainNotFound";
    public static final String ERR_TIME_OUT = "ErrTimeout";
    public static final String ERR_UNKNOWN = "ErrUnknown";

    private String rst;//请求结果
    private String net;//网络类型
    private String dns;//dns
    private String dnse;//dns error
    private String domain;//上传域名
    private String sip;//源IP
    private String ip;//上传IP
    private long pi;//纪录开始下载的时间
    private long dt;//dns查询时间
    private long fnb;//第一次拿到的文件大小
    private long fnbt;//第一次拿到文件的时间
    private long td;//总时长
    private long bd;//文件总长度
    private int code;

    public ResponseInfo(String rst, String net, String domain, String sip, long pi, long td, long bd, int code)
    {
        this.rst = rst;
        this.net = net;
        this.domain = domain;
        this.sip = sip;
        this.pi = pi;
        this.td = td;
        this.bd = bd;
        this.code = code;
    }

    public String getMessage()
    {
        return rst;
    }

    public void setDns(String dns)
    {
        this.dns = dns;
    }

    public void setDnse(String dnse)
    {
        this.dnse = dnse;
    }

    public void setDt(long dt)
    {
        this.dt = dt;
    }

    public void setFnb(long fnb)
    {
        this.fnb = fnb;
    }

    public void setFnbt(long fnbt)
    {
        this.fnbt = fnbt;
    }

    public void setIp(String ip)
    {
        this.ip = ip;
    }

    public static ResponseInfo canceled() {
        return new ResponseInfo("cancel", "", "", "",-1, -1,-1, Cancelled);
    }

    public boolean isOk()
    {
        return rst.equals("success");
    }

    public boolean isCancelled()
    {
        return code == Cancelled;
    }

    public boolean isNotQiniu() {
        return code < 500 && code >= 200 && !rst.equals("success");
    }

    public boolean needRetry()
    {
        return !isCancelled() && (code == 406 || (!rst.equals("success")));
    }

    public String toString()
    {
        JSONObject obj = new JSONObject();
        try {
            obj.put("rst",rst);
            obj.put("net",net);
            obj.put("dns",dns);
            obj.put("dnse",dnse);
            obj.put("domain",domain);
            obj.put("sip",sip);
            obj.put("ip",ip);
            obj.put("pi",System.currentTimeMillis() - pi);
            obj.put("dt",dt);
            obj.put("fnb",fnb);
            obj.put("fnbt",fnbt);
            obj.put("td",td);
            obj.put("bd",bd);
            obj.put("code",code);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  obj.toString();
    }
}
