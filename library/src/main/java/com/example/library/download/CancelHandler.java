package com.example.library.download;

import java.io.IOException;

/**
 * Created by Misty on 16/2/16.
 */
public interface CancelHandler {
    boolean isCancel();

    class CancellationException extends IOException {
    }
}
