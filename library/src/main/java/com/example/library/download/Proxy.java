package com.example.library.download;

/**
 * Created by Misty on 16/2/17.
 */
public final class Proxy {
    /**
     * 服务器域名或IP
     */
    private final String hostAddress;
    /**
     * 端口号
     */
    private final int port;
    /**
     * 用户名
     */
    private final String user;
    /**
     * 密码
     */
    private final String password;

    public Proxy(String hostAddress, int port, String user, String password)
    {
        this.hostAddress = hostAddress;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    public java.net.Proxy toProxy()
    {
        return null;
    }

}
