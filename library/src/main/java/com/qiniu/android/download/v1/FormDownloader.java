package com.qiniu.android.download.v1;

import android.util.Log;

/**
 * Created by Misty on 16/2/16.
 */
public final class FormDownloader {
    private static final String TAG = "FormDownloader";

    /**
     * 下载
     * @param requestInfo
     * @param downloadOption
     * @param client
     * @param completionHandler
     */
    public static void download(final RequestInfo requestInfo, DownloadOption downloadOption, final Client client, final CompletionHandler completionHandler)
    {
        //option,如果没有设置,使用默认option
        final DownloadOption option = downloadOption != null ? downloadOption : new DownloadOption.Builder().builder();

        DownloadCompletionHandler downloadCompletionHandler = new DownloadCompletionHandler() {
            @Override
            public void onCompletion(ResponseInfo info) {
                if(info != null && info.isOk()) {
                    Log.i(TAG,"====>download ok");
                    completionHandler.onCompletion(info);
                }
                else if(info != null && info.isCancelled())
                {
                    Log.i(TAG,"====>download cancelled");
                    completionHandler.onError(new Exception("download cancelled!"));
                }
                else if(info != null && (info.needRetry() || info.isNotQiniu()))
                {
                    Log.e(TAG,"====>download retry");
                    DownloadCompletionHandler retry = new DownloadCompletionHandler() {
                        @Override
                        public void onCompletion(ResponseInfo info) {
                            if(info.isOk())
                            {
                                completionHandler.onCompletion(info);
                            }
                            else
                            {
                                completionHandler.onError(new Exception(info.getMessage()));
                            }
                        }
                    };

                    client.asyncGet(requestInfo,option,retry);
                }
                else
                {
                    Log.e(TAG,"====>no response info");
                    completionHandler.onError(new Exception("no error info"));
                }
            }
        };

        client.asyncGet(requestInfo,option,downloadCompletionHandler);
    }
}
