package com.qiniu.android.download.v1;

import android.os.Environment;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;

import junit.framework.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Misty on 16/2/19.
 */
public class DownloadManagerTest extends AndroidTestCase {
    private static final String TAG = "DownloadManagerTest";

    private String url = "http://7xqbzw.com1.z0.glb.clouddn.com/index_v2";
    private String url1 = "http://7xqbzw.com1.z0.glb.clouddn.com/libfacc128k.mp3";
    private String url2 = "http://7xqbzw.com1.z0.glb.clouddn.com/Rong_IMLib_v2_4_6.jar";
    private String url3 = "http://7xqbzw.com1.z0.glb.clouddn.com/live.png";

    private String destination = Environment.getExternalStorageDirectory().getAbsolutePath();

    private boolean isFinish = false;

    private DownloadManager manager;

    public void setUp() throws Exception {
        super.setUp();
        manager = new DownloadManager(getContext());
    }

    @LargeTest
    public void testDownload() throws Exception {
        RequestInfo requestInfo = new RequestInfo(url1,destination);
        manager.download(requestInfo, null, new CompletionHandler() {
            @Override
            public void onCompletion(ResponseInfo info) {
                isFinish = true;
                Log.e(TAG, "====>onCompletion" + info.toString());
            }

            @Override
            public void onError(Exception e) {
                isFinish = true;
                Log.e(TAG, "====>onError:" + e.toString());
            }
        });

        while (!isFinish)
        {
            Thread.sleep(1000);
        }
    }

    @SmallTest
    public void testNoUrl() throws Exception{

        RequestInfo requestInfo = new RequestInfo("",destination);
        manager.download(requestInfo, null, new CompletionHandler() {
            @Override
            public void onCompletion(ResponseInfo info) {
                isFinish = true;
                Assert.assertEquals("success", info.getMessage());
            }

            @Override
            public void onError(Exception e) {
                isFinish = true;
                Log.e(TAG,"====>onError:" + e.toString());
            }
        });

        while (!isFinish)
        {
            Thread.sleep(1000);
        }
    }

    @SmallTest
    public void testNoDestination() throws Exception
    {
        RequestInfo requestInfo = new RequestInfo(url3,"");
        manager.download(requestInfo, null, new CompletionHandler() {
            @Override
            public void onCompletion(ResponseInfo info) {
                isFinish = true;
                Assert.assertEquals("success", info.getMessage());
            }

            @Override
            public void onError(Exception e) {
                isFinish = true;
                Log.e(TAG,"====>onError:" + e.toString());
            }
        });

        while (!isFinish)
        {
            Thread.sleep(1000);
        }
    }

    @SmallTest
    public void testNoCompletionHandler(){
        RequestInfo requestInfo = new RequestInfo(url1,destination);

        try {
            manager.download(requestInfo, null, null);
        } catch (Exception e) {
            Log.e(TAG,"====>onError:" + e.toString());
        }
    }

    @LargeTest
    public void testHeader() throws Exception{
        Map<String,String> header = new HashMap<>();
        header.put("Range","bytes=0-10000");
        RequestInfo requestInfo = new RequestInfo(url1,destination,header);
        manager.download(requestInfo, null, new CompletionHandler() {
            @Override
            public void onCompletion(ResponseInfo info) {
                isFinish = true;
                Log.e(TAG, "====>onCompletion:" + info.toString());
            }

            @Override
            public void onError(Exception e) {
                isFinish = true;
                Log.e(TAG,"====>onError:" + e.toString());
            }
        });

        while (!isFinish)
        {
            Thread.sleep(1000);
        }
    }

    @LargeTest
    public void testToken() throws Exception{
        RequestInfo requestInfo = new RequestInfo(url2,destination);
        manager.download(requestInfo, null, new CompletionHandler() {
            @Override
            public void onCompletion(ResponseInfo info) {
                isFinish = true;
                Assert.assertEquals("success", info.getMessage());
            }

            @Override
            public void onError(Exception e) {
                isFinish = true;
                Log.e(TAG,"====>onError:" + e.toString());
            }
        });

        while (!isFinish)
        {
            Thread.sleep(1000);
        }
    }

    @LargeTest
    public void testNoToken() throws Exception
    {
        Map<String,String> header = new HashMap<>();
        header.put("Range","bytes=0-10000");
        RequestInfo requestInfo = new RequestInfo(url1,destination,header);
        manager.download(requestInfo, null, new CompletionHandler() {
            @Override
            public void onCompletion(ResponseInfo info) {
                isFinish = true;
                Log.e(TAG, "====>onCompletion:" + info.toString());
            }

            @Override
            public void onError(Exception e) {
                isFinish = true;
                Log.e(TAG,"====>onError:" + e.toString());
            }
        });

        while (!isFinish)
        {
            Thread.sleep(1000);
        }
    }

    @LargeTest
    public void testOption() throws Exception
    {
        DownloadOption option = new DownloadOption.Builder().setAttname("myFileName.txt").builder();
        RequestInfo requestInfo = new RequestInfo(url,destination);
        manager.download(requestInfo, option, new CompletionHandler() {
            @Override
            public void onCompletion(ResponseInfo info) {
                isFinish = true;
                Assert.assertEquals("success", info.getMessage());
            }

            @Override
            public void onError(Exception e) {
                isFinish = true;
                Log.e(TAG,"====>onError:" + e.toString());
            }
        });

        while (!isFinish)
        {
            Thread.sleep(1000);
        }
    }

    @LargeTest
    public void testConfigure() throws Exception{
        Configure configure = new Configure.Builder().setSendResponseInfoTime(1000).builder();
        manager = new DownloadManager(getContext(),configure);

        RequestInfo requestInfo = new RequestInfo(url1,destination);
        manager.download(requestInfo, null, new CompletionHandler() {
            @Override
            public void onCompletion(ResponseInfo info) {
                isFinish = true;
                Log.e(TAG,"====>onCompletion:" + info.toString());
            }

            @Override
            public void onError(Exception e) {
                isFinish = true;
                Log.e(TAG,"====>onError:" + e.toString());
            }
        });

        while (!isFinish)
        {
            Thread.sleep(5000);
        }
    }
}