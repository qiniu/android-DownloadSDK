package com.example.library.download;

import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Misty on 16/2/17.
 */
public final class DownloadToken {
    private static final String TAG = "DownloadToken";

    private String token;
    private String expires;

    private DownloadToken(String token, String expires) {
        this.token = token;
        this.expires = expires;
    }

    public static DownloadToken parse(String token){
        String[] t;

        try {
            t = token.split(":");
        }catch (Exception e)
        {
            return null;
        }
        if(t.length != 2)
        {
            return null;
        }

        byte[] dtoken = UrlSafeBase64.decode(t[1]);
        Log.e(TAG,"====>dtoken:" + dtoken);

        String stoken = new String(dtoken);
        String expires = null;

        if(stoken.indexOf("e=") > 0)
        {
            expires = stoken.substring(stoken.indexOf("e=") + 1 , stoken.length());
        }
        else
        {
            return null;
        }

        return new DownloadToken(token,expires);
    }

    public String toString()
    {
        return "e="+expires + "&token=" + token;
    }
}