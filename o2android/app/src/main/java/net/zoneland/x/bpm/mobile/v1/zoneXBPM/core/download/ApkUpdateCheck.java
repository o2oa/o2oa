package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.download;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Looper;
import android.text.TextUtils;

import com.pgyersdk.update.UpdateManagerListener;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.SDCardHelper;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.RetrofitClient;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.service.PgyUpdateApiService;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.AndroidUtils;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.FileExtensionHelper;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by FancyLou on 2016/4/21.
 */
public class ApkUpdateCheck {

    public void downLoadApk(final Activity activity, String url, final String result) {
        //监听下载进度
        final ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setProgressNumberFormat("%1d KB/%2d KB");
        dialog.setTitle("下载");
        dialog.setMessage("正在下载，请稍后...");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);
        dialog.show();

        ProgressHelper.setProgressHander(new DownloadProgressHandler() {
            @Override
            public void onProgress(long progress, long total, boolean done) {
                XLog.debug("是否在主线程中运行" + String.valueOf(Looper.getMainLooper() == Looper.myLooper()));
                XLog.debug(String.format("%d%% done\n",(100 * progress) / total));
                XLog.debug("done --->" + String.valueOf(done));
                dialog.setMax((int) (total/1024));
                dialog.setProgress((int) (progress/1024));
                if(done){
                    dialog.dismiss();
                }
            }
        });
        OkHttpClient.Builder builder = ProgressHelper.addProgress(null);

        PgyUpdateApiService service = RetrofitClient.Companion.instance()
                .updateApiService("http://app.pgyer.com/", builder);
        service.apkDownload(url)
                .subscribeOn(Schedulers.io())
                .map(new Func1<Response<ResponseBody>, String>() {
                    @Override
                    public String call(Response<ResponseBody> responseBodyResponse) {
                        XLog.debug("下载结束， 进入response body 处理函数~~~~~~~~~~~~~~~~~~");
                        String apk = "";
                        try {

                            InputStream inputStream = null;
                            OutputStream outputStream = null;
                            try {
                                String temp = FileExtensionHelper.getXBPMTempFolder();
                                long currentTime = System.currentTimeMillis();
                                apk = temp+ File.separator + "xbpm_"+currentTime+".apk";
                                SDCardHelper.INSTANCE.generateNewFile(apk);
                                File file = new File(apk);

                                byte[] fileReader = new byte[4096];

                                long fileSize = responseBodyResponse.body().contentLength();
                                long fileSizeDownloaded = 0;

                                inputStream = responseBodyResponse.body().byteStream();
                                outputStream = new FileOutputStream(file);

                                while (true) {
                                    int read = inputStream.read(fileReader);
                                    if (read == -1) {
                                        break;
                                    }
                                    outputStream.write(fileReader, 0, read);
                                    fileSizeDownloaded += read;
                                    XLog.debug("file download: " + fileSizeDownloaded + " of " + fileSize);
                                }
                                outputStream.flush();
                                UpdateManagerListener.updateLocalBuildNumber(result);
                                XLog.debug("file download finish ...............");
                                return apk;
                            } catch (IOException e) {
                                XLog.error("生成apk文件出错", e);
                            } finally {
                                if (inputStream != null) {
                                    inputStream.close();
                                }
                                if (outputStream != null) {
                                    outputStream.close();
                                }
                            }
//                            InputStream is = responseBodyResponse.body().byteStream();
//                            String temp = FileExtensionHelper.getXBPMTempFolder();
//                            long currentTime = System.currentTimeMillis();
//                            apk = temp+ File.separator + "xbpm_"+currentTime+".apk";
//                            SDCardHelper.generateNewFile(apk);
//                            File file = new File(apk);
//                            FileOutputStream fos = new FileOutputStream(file);
//                            BufferedInputStream bis = new BufferedInputStream(is);
//                            byte[] buffer = new byte[1024];
//                            int len;
//                            while ((len = bis.read(buffer)) != -1) {
//                                fos.write(buffer, 0, len);
//                                fos.flush();
//                            }
//                            fos.close();
//                            bis.close();
//                            is.close();
                        } catch (Exception e) {
                            XLog.error("生成apk文件出错", e);
                        }
                        return apk;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                   public void onError(Throwable e) {
                        XLog.error("download apk failure", e);
                    }

                    @Override
                    public void onNext(String response) {
                        XLog.info("download, apk url :"+ response);
                        if (!TextUtils.isEmpty(response)){
                            AndroidUtils.INSTANCE.runApp(activity, response);
                        }else {
                            XToast.INSTANCE.toastShort(activity, "下载APK文件出错");
                        }
                    }
                });

    }
}
