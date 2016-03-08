package com.qiniu.android.download.v1;

import android.content.Context;

/**
 * Created by Misty on 16/2/17.
 */
public final class DownloadManager {
    private static final String TAG = "DownloadManager";

    private Client client;

    public DownloadManager(Context context)
    {
        this(context, new Configure.Builder().builder());
    }

    public DownloadManager(Context context, Configure configure)
    {
        this.client = new Client(context,configure.proxy,configure.connectTimeout,configure.responseTimeout,configure.dns, configure.sendResponseInfoTime);
    }

    private static boolean areInvalidArg(RequestInfo requestInfo, final CompletionHandler completionHandler) {

        if (completionHandler == null) {
            throw new IllegalArgumentException("no CompletionHandler");
        }

        String message = null;
        if(requestInfo.getUrl() == null || requestInfo.getUrl().equals(""))
        {
            message = "no download url";
        }
        if(requestInfo.getDestination() == null || requestInfo.getDestination().equals(""))
        {
            message = "destination is null";
        }

        if (message != null) {
            final String m = message;
            AsyncRun.run(new Runnable() {
                @Override
                public void run() {
                    completionHandler.onError(new Exception(m));
                }
            });
            return true;
        }
        return false;
    }

    /**
     * 下载
     * @param requestInfo 下载需要的参数
     * @param option 下载配置
     * @param completionHandler 下载完成后的操作
     */
    public void download(final RequestInfo requestInfo, final DownloadOption option, final CompletionHandler completionHandler)
    {
        if(areInvalidArg(requestInfo,completionHandler))
        {
            return;
        }
        AsyncRun.run(new Runnable() {
            @Override
            public void run() {
                FormDownloader.download(requestInfo, option, client, completionHandler);
            }
        });
    }
}