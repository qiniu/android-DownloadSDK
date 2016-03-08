package com.qiniu.android.download.v1;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.qiniu.android.dns.DnsManager;
import com.qiniu.android.dns.Domain;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Dns;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by Misty on 16/2/17.
 */
public final class Client {
    private static final String TAG = "Client";

    public static final String ContentTypeHeader = "Content-Type";
    public static final String DefaultMime = "application/octet-stream";
    public static final String JsonMime = "application/json";
    public static final String FormMime = "application/x-www-form-urlencoded";

    private OkHttpClient client;
    private DnsManager dnsManager;
    private Context context;

    private StatsInfo infos;

    private boolean isFirst = true;
    private long fnb;
    private long fnbt;
    private long time;

    public Client(Context context) {
        this(context, null, 10, 30, null, 60 * 1000);
    }

    public Client(Context context, Proxy proxy, int connectTimeout, int responseTimeout, final DnsManager dns, long time) {
        client = new OkHttpClient();
        this.context = context;
        this.time = time;

        //统计下载数据,并开启定时发送功能;
        infos = new StatsInfo(context);
        sendResInfo();

        if (proxy != null) {
            client.setProxy(proxy.toProxy());
        }

        if (dns != null) {
            this.dnsManager = dns;
            client.setDns(new Dns() {
                @Override
                public List<InetAddress> lookup(String hostname) throws UnknownHostException {
                    InetAddress[] ips;
                    try {
                        ips = dns.queryInetAdress(new Domain(hostname));
                    } catch (IOException e) {
                        e.printStackTrace();
                        throw new UnknownHostException(e.getMessage());
                    }
                    if (ips == null) {
                        throw new UnknownHostException(hostname + " resolve failed");
                    }
                    List<InetAddress> l = new ArrayList<>();
                    Collections.addAll(l, ips);
                    return l;
                }
            });
        }
        client.networkInterceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                Response response = chain.proceed(request);
                IpTag tag = new IpTag();
                String ip = chain.connection().getSocket().getRemoteSocketAddress().toString();
                tag.ip = ip;
                return response;
            }
        });

        client.setConnectTimeout(connectTimeout, TimeUnit.SECONDS);
        client.setReadTimeout(responseTimeout, TimeUnit.SECONDS);
        client.setWriteTimeout(0, TimeUnit.SECONDS);
    }

    public void asyncGet(final RequestInfo requestInfo, final DownloadOption option, final DownloadCompletionHandler completionHandler) {
        String url = requestInfo.getDownloadUrl();

        final Request.Builder requestBuilder = new Request.Builder().url(url);
        if (requestInfo.getHeader() != null) {
            Map<String, String> header = requestInfo.getHeader();
            for (String key : header.keySet()) {
                requestBuilder.addHeader(key, header.get(key));
            }
        }

        final long startT = System.currentTimeMillis();
        client.newCall(requestBuilder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e(TAG, "====>this is client async get onFailure:" + e.toString());
                long td = System.currentTimeMillis() - startT;
                URL u = request.url();
                String status = getErrorType(e);

                ResponseInfo info = new ResponseInfo(status, null, u.getHost(), getSip(), startT, td, -1, -1);
                infos.addInfo(info);

                completionHandler.onCompletion(info);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                long td = System.currentTimeMillis() - startT;
                URL req = response.request().url();

                if(response.isSuccessful())
                {
                    long contentLength = response.body().contentLength();

                    MediaType contentType = response.body().contentType();
                    String filename = option.attname == null ? requestInfo.getKey() : option.attname;

                    writeToFile(requestInfo.getDestination(),getFileName(filename,contentType.toString()),response);


                    ResponseInfo info = new ResponseInfo("success", getNet(context), req.getHost(), getSip(), startT, td, contentLength, response.code());
                    getIp(info);
                    queryDns(info, req.getHost());
                    if (isFirst) {
                        info.setFnb(contentLength);
                        info.setFnbt(td);
                        fnb = contentLength;
                        fnbt = td;
                        isFirst = false;
                    } else {
                        info.setFnb(fnb);
                        info.setFnbt(fnbt);
                    }

                    infos.addInfo(info);

                    completionHandler.onCompletion(info);
                }
                else
                {
                    ResponseInfo info = new ResponseInfo(response.message(), null, req.getHost(), getSip(), startT, td, -1, -1);
                    infos.addInfo(info);

                    completionHandler.onCompletion(info);
                }
            }
        });
    }

    //取消
    public void cancle() {

    }

    //暂停
    public void stop() {

    }

    //重新开始
    public void resume() {

    }

    private void writeToFile(String path, String filename, Response response) {
        Log.e(TAG,"=====>writeToFile name:" + filename);
        File file = new File(path, filename);
        InputStream is = null;
        FileOutputStream fos = null;
        int len = 0;
        byte[] buf = new byte[1024 * 4];

        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            is = response.body().byteStream();
            fos = new FileOutputStream(file);
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0,len);
            }
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null)
                    is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getFileName(String key, String mimeType) {
        if(mimeType == null || mimeType.equals(""))
        {
            return key;
        }

        int p = mimeType.lastIndexOf("/");
        if(p < 0)
        {
            return key;
        }
        else
        {
            mimeType = mimeType.substring(p+1,mimeType.length());
        }

        int k = key.lastIndexOf(".");
        if(k < 0)
        {
            return key + "." + mimeType;
        }
        else
        {
            String keyMimeType = key.substring(k + 1, key.length());
            if(keyMimeType.equalsIgnoreCase(mimeType))
            {
                return key;
            }
            else
            {
                return key + "." + mimeType;
            }
        }
    }

    private String getErrorType(IOException e) {
        String statusCode = ResponseInfo.ERR_UNKNOWN;
        String msg = e.getMessage();
        if (e instanceof UnknownHostException) {
            statusCode = ResponseInfo.ERR_DOMAIN_NOT_FOUND;
        } else if (e instanceof SocketTimeoutException) {
            statusCode = ResponseInfo.ERR_TIME_OUT;
        } else if (e instanceof ConnectException) {
            statusCode = ResponseInfo.ERR_CONNECT_FAILED;
        } else if (msg != null && msg.indexOf("Broken pipe") == 0) {
            statusCode = ResponseInfo.ERR_BROKEN_CONNECTION;
        }

        return statusCode;
    }

    /**
     * 获取网络状态
     *
     * @param context
     * @return
     */
    private String getNet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null && info.isConnectedOrConnecting()) {
            switch (info.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    return "WiFi";
                case ConnectivityManager.TYPE_MOBILE:
                    switch (info.getSubtype()) {
                        case TelephonyManager.NETWORK_TYPE_GPRS: //联通2g
                        case TelephonyManager.NETWORK_TYPE_CDMA: //电信2g
                        case TelephonyManager.NETWORK_TYPE_EDGE: //移动2g
                        case TelephonyManager.NETWORK_TYPE_1xRTT:
                        case TelephonyManager.NETWORK_TYPE_IDEN:
                            return "2G";
                        case TelephonyManager.NETWORK_TYPE_EVDO_A: //电信3g
                        case TelephonyManager.NETWORK_TYPE_UMTS:
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                        case TelephonyManager.NETWORK_TYPE_HSDPA:
                        case TelephonyManager.NETWORK_TYPE_HSUPA:
                        case TelephonyManager.NETWORK_TYPE_HSPA:
                        case TelephonyManager.NETWORK_TYPE_EVDO_B:
                        case TelephonyManager.NETWORK_TYPE_EHRPD:
                        case TelephonyManager.NETWORK_TYPE_HSPAP:
                            return "3G";
                        case TelephonyManager.NETWORK_TYPE_LTE:
                            return "4G";
                        default:
                            return "unknown";
                    }
                default:
                    return "unknown";
            }
        }
        return "no_network";
    }

    /**
     * 获取本地IP
     *
     * @return
     */
    private String getSip() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> ipAddr = intf.getInetAddresses(); ipAddr
                        .hasMoreElements(); ) {
                    InetAddress inetAddress = ipAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取服务器ip
     *
     * @param info
     */
    private void getIp(final ResponseInfo info) {
        OkHttpClient client1 = client.clone();
        Request request = new Request.Builder().url(Constants.SERVICE).build();

        try {
            Response response = client1.newCall(request).execute();
            if(response.isSuccessful())
            {
                String str = response.body().string();
                response.body().close();
                try {
                    JSONObject obj = new JSONObject(str);
                    String ip = obj.optString("ip");
                    info.setIp(ip);
                } catch (JSONException e) {
                    Log.e(TAG, "====>get service ip is error:" + e.toString());
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void queryDns(ResponseInfo info, String host) {
        try {
            long start = System.currentTimeMillis();
            String[] ip = dnsManager.query(new Domain(host));
            long duration = System.currentTimeMillis() - start;
            info.setDt(duration);
            if (ip.length == 0) {
                info.setDnse("1");
            } else {
                info.setDns(ip[0]);
                info.setDnse("0");
            }
        } catch (IOException e) {
            e.printStackTrace();
            info.setDnse("1");
        }
    }

    private void sendResInfo() {
        Timer mTimer = new Timer();
        TimerTask mTimerTask = new TimerTask() {
            @Override
            public void run() {
                startSendResInfo();
            }
        };
        //开始一个定时任务
        mTimer.schedule(mTimerTask, time, time);
    }

    private void startSendResInfo() {
        OkHttpClient client1 = client.clone();
        client1.networkInterceptors().add(new GzipRequestInterceptor());

        MediaType mediaType = MediaType.parse(JsonMime);
        RequestBody body = RequestBody.create(mediaType, infos.toString());
        Request request = new Request.Builder().url(Constants.SERVICE).post(body).build();
        client1.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
//                Log.e(TAG, "====>start send responseinfo failed and error:" + e.toString());
            }

            @Override
            public void onResponse(Response response) throws IOException {
//                Log.e(TAG, "====>start send responseinfo success!");
            }
        });
    }

    private static class IpTag {
        public String ip = null;
    }
}
