package com.example.library.download;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

import okio.BufferedSink;
import okio.GzipSink;
import okio.Okio;

/**
 * Created by Misty on 16/2/17.
 * 拦截器压缩http请求体，许多服务器无法解析
 */
public class GzipRequestInterceptor implements Interceptor {

    private final MediaType JSON = MediaType.parse("application/json");

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        Request compressedRequest = originalRequest.newBuilder()
                .header("Content-Encoding", "gzip")
                .build();
        return chain.proceed(compressedRequest);
    }

    private RequestBody gzip(final RequestBody body) {
        return new RequestBody() {
            @Override public MediaType contentType() {
                return body.contentType();
            }

            @Override public long contentLength() {
                return -1; // 无法知道压缩后的数据大小
            }

            @Override public void writeTo(BufferedSink sink) throws IOException {
                BufferedSink gzipSink = Okio.buffer(new GzipSink(sink));
                body.writeTo(gzipSink);
                gzipSink.close();
            }
        };
    }
}
