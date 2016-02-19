package com.example.library.download;

import com.qiniu.android.dns.DnsManager;
import com.qiniu.android.dns.IResolver;
import com.qiniu.android.dns.NetworkInfo;
import com.qiniu.android.dns.local.AndroidDnsServer;
import com.qiniu.android.dns.local.Resolver;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by Misty on 16/2/16.
 */
public final class Configure {
    private static final String TAG = "Configure";

    /**
     * http协议
     */
    public final Proxy proxy;

    /**
     * 服务器连接超时时间
     */
    public final int connectTimeout;

    /**
     * 服务器响应时间
     */
    public final int responseTimeout;

    /**
     * 下载失败重试次数
     */
    public final int retryMax;

    /**
     * 发送统计数据间隔
     */
    public final long sendResponseInfoTime;

    public final DnsManager dns;

    private Configure(Builder builder)
    {
        this.connectTimeout = builder.connectTimeout;
        this.responseTimeout = builder.responseTimeout;
        this.retryMax = builder.retryMax;

        this.proxy = builder.proxy;
        this.dns = initDns(builder);
        this.sendResponseInfoTime = builder.sendResponseInfoTime;
    }

    private static DnsManager initDns(Builder builder)
    {
        DnsManager d = builder.dns;
        return d;
    }

    public static class Builder{
        private Proxy proxy = null;

        private int connectTimeout = 10;
        private int responseTimeout = 60;
        private int retryMax = 3;
        private DnsManager dns = null;
        private long sendResponseInfoTime;

        public Builder()
        {
            IResolver r1 = AndroidDnsServer.defaultResolver();
            IResolver r2 = null;
            IResolver r3 = null;

            try {
                r2 = new Resolver(InetAddress.getByName("223.6.6.6"));
                r3 = new Resolver(InetAddress.getByName("114.114.115.115"));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            dns = new DnsManager(NetworkInfo.normal, new IResolver[]{r1,r2,r3});

            sendResponseInfoTime = 60 * 1000;
        }

        public Builder setProxy(Proxy proxy) {
            this.proxy = proxy;
            return this;
        }

        public Builder setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder setResponseTimeout(int responseTimeout) {
            this.responseTimeout = responseTimeout;
            return this;
        }

        public Builder setRetryMax(int retryMax) {
            this.retryMax = retryMax;
            return this;
        }

        public Builder setDns(DnsManager dns) {
            this.dns = dns;
            return this;
        }

        public Builder setSendResponseInfoTime(long time) {
            this.sendResponseInfoTime = time;
            return this;
        }

        public Configure builder()
        {
            return new Configure(this);
        }
    }
}
