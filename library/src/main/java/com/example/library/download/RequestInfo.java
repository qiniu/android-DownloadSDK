package com.example.library.download;

import android.util.Log;

import java.util.Map;

/**
 * Created by Misty on 16/2/18.
 */
public final class RequestInfo {
    private static final String TAG = "RequestInfo";

    private String url;
    private DownloadToken token;
    private Map<String,String> header;
    private String destination;

    public RequestInfo(String url,String destination)
    {
        this.url = url;
        this.destination = destination;
    }

    public RequestInfo(String url,String destination, DownloadToken token)
    {
        this.url = url;
        this.destination = destination;
        this.token = token;
    }

    public RequestInfo(String url,String destination,Map<String,String> header)
    {
        this.url = url;
        this.header = header;
        this.destination = destination;
    }

    public RequestInfo(String url,String destination, DownloadToken token,Map<String,String> header)
    {
        this.url = url;
        this.destination = destination;
        this.token = token;
        this.header = header;
    }

    public String getUrl()
    {
        return url;
    }

    public DownloadToken getToken()
    {
        return token;
    }

    public Map<String,String> getHeader()
    {
        return header;
    }

    public String getDestination()
    {
        return destination;
    }

    public String getDownloadUrl()
    {
        if(token != null)
        {
            url += "?" + token.toString();
        }

        return url;
    }

    public String getKey()
    {
        int pos = url.lastIndexOf("/");
        if(pos < 0)
        {
            return null;
        }
        return url.substring(pos + 1, url.length());
    }
}
