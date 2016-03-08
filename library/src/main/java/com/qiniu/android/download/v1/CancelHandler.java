package com.qiniu.android.download.v1;

import java.io.IOException;

/**
 * Created by Misty on 16/2/16.
 */
public interface CancelHandler {
    boolean isCancel();

    class CancellationException extends IOException {
    }
}
