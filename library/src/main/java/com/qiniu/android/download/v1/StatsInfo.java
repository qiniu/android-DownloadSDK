package com.qiniu.android.download.v1;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Misty on 16/2/17.
 */
public final class StatsInfo {
    private static final String TAG = "ResponseInfo";

    private String v;//版本
    private String did;//设备ID
    private String os;//操作系统
    private String sysv;//操作系统版本
    private String app;//appname
    private String appv;//appversion

    private List<ResponseInfo> infos;

    public StatsInfo(Context context)
    {
        v = Constants.VERSION;
        did = getDeviceID(context);
        os = "Android";
        sysv = Build.VERSION.RELEASE;
        getAppInfo(context);

        infos = new ArrayList<>();
    }

    private String getDeviceID(Context context)
    {
        String imei = ((TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        Log.i(TAG, "====>imei:" + imei);
        return imei;
    }

    private void getAppInfo(Context context)
    {
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            this.app = info.versionName;
            this.appv = info.versionCode+"";
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            this.app = "no found app name";
        }
    }

    public void addInfo(ResponseInfo info)
    {
        if(info == null)
        {
            return;
        }
        infos.add(info);
    }

    public String toString()
    {
        JSONObject obj = new JSONObject();
        try {
            obj.put("v",v);
            obj.put("did",did);
            obj.put("os",os);
            obj.put("sysv",sysv);
            obj.put("app",app);
            obj.put("appv",appv);

            String reqs = "";
            for(int i = 0 ; i < infos.size(); i++)
            {
                reqs += infos.get(i).toString();
            }
            obj.put("reqs",reqs);

            infos.clear();

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  obj.toString();
    }

}
