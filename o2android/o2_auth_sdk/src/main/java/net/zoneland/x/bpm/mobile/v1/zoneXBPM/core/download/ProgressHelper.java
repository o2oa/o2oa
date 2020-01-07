package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.download;


import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

/**
 * Created by FancyLou on 2016/4/21.
 */
public class ProgressHelper {

    private static ProgressHandler mHandler;
    private static DownloadBean bean = new DownloadBean();

    public static void setProgressHander(ProgressHandler hander) {
        mHandler = hander;
    }

    public static OkHttpClient.Builder addProgress(OkHttpClient.Builder builder){
        if (builder == null) {
            builder = new OkHttpClient.Builder();
        }

        final ProgressListener progressListener = new ProgressListener() {
            //该方法在子线程中运行
            @Override
            public void onProgress(long progress, long total, boolean done) {
                //Log.d("progress:",String.format("%d%% done\n",(100 * progress) / total));
                if (mHandler == null){
                    Log.e("ProgressHelper","progress error， handler is null");
                    return;
                }
                bean.setBytesRead(progress);
                bean.setContentLength(total);
                bean.setDone(done);
                mHandler.sendMessage(bean);
            }
        };
        //添加拦截器，自定义ResponseBody，添加下载进度
        builder.networkInterceptors().add(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                okhttp3.Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder().body(
                        new ProgressResponseBody(originalResponse.body(), progressListener))
                        .build();

            }
        });

        return builder;
    }


}
