package com.example.library.download;

/**
 * Created by Misty on 16/2/16.
 */
public interface DownloadCompletionHandler {

    /**
     * 下载完成
     * @param info
     */
    void onCompletion(ResponseInfo info);
}
