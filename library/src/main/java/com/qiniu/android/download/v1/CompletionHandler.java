package com.qiniu.android.download.v1;

/**
 * Created by Misty on 16/2/16.
 */
public interface CompletionHandler {

    /**
    * 下载成功后的操作
    */
    public void onCompletion(ResponseInfo info);

    /**
     * 下载失败后的操作ß
     * @param e
     */
    public void onError(Exception e);
}
