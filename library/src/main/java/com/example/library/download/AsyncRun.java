package com.example.library.download;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by Misty on 16/2/16.
 */
public final class AsyncRun {
    public static void run(Runnable runnable)
    {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(runnable);
    }
}
