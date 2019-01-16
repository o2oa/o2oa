package jiguang.chat.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.ImageView;

import java.io.File;

import jiguang.chat.application.JGApplication;


public class VideoThumbnailLoader {

    private static final String TAG = "VideoThumbnailLoader";

    private static VideoThumbnailLoader ins = new VideoThumbnailLoader();

    public static VideoThumbnailLoader getIns(){
        return ins;
    }

    public void display(String fileName, String url, ImageView iv, int width, int height, ThumbnailListener thumbnailListener){
        this.

        new ThumbnailLoadTask(fileName, url,iv,width,height,thumbnailListener)
                .executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);//使用AsyncTask自带的线程池

    }


    private class ThumbnailLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String fileName;
        private String url;
        private ImageView iv;
        private ThumbnailListener thumbnailListener;
        private int width;
        private int height;
        public ThumbnailLoadTask(String fileName, String url, ImageView iv, int width, int height,
                                 ThumbnailListener thumbnailListener) {
            int dot = fileName.lastIndexOf('.');
            if (dot > -1 && dot < fileName.length()) {
                this.fileName = fileName.substring(0, dot);
            }
            this.url = url;
            this.iv = iv;
            this.width = width;
            this.height = height;
            this.thumbnailListener = thumbnailListener;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            /**
             * 注意,由于我们使用了缓存,所以在加载缩略图之前,我们需要去缓存里读取,如果缓存里有,我们则直接获取,如果没有,则去加载.并且加载完成之后记得放入缓存.
             */
            Bitmap bitmap = null;
            if (!TextUtils.isEmpty(url)) {
                    File file = new File(JGApplication.PICTURE_DIR, fileName + ".png");
                    if (file.exists() && file.isFile()) {//去磁盘缓存取
                        bitmap = BitmapFactory.decodeFile(file.getPath());
                        if (null==bitmap) {
                            bitmap = getVideoThumbnail(url, width, height, MediaStore.Video.Thumbnails.MICRO_KIND);
                            NativeImageLoader.getInstance().updateBitmapFromCache(url, bitmap);
                        }
                    } else {
                        bitmap = getVideoThumbnail(url, width, height, MediaStore.Video.Thumbnails.MICRO_KIND);

                        if (null==bitmap) {
                            bitmap = getVideoThumbnail(url, width, height, MediaStore.Video.Thumbnails.MICRO_KIND);
                            //将图片保存到磁盘文件,作为缓存
                            BitmapLoader.saveBitmapToLocal(bitmap, fileName);
                        }

                    }
            }
            return bitmap;
        }



        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            thumbnailListener.onThumbnailLoadCompleted(url, iv, bitmap);//回调
        }
    }

    /**
     * @param videoPath 视频路径
     * @param width
     * @param height
     * @param kind      eg:MediaStore.Video.Thumbnails.MICRO_KIND   MINI_KIND: 512 x 384，MICRO_KIND: 96 x 96
     * @return
     */
    private Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
        // 获取视频的缩略图
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     *  imageloader 的内存缓存的 key 以_ 结尾  截取key比较的时候如果没有加_ 会报错崩溃,所以自己自定义
     * @param filePath 文件地址
     * @return
     */
    private  String getMemoryKey(String filePath) {

        String key ;
        int index = filePath.lastIndexOf("/");
        key = filePath.substring(index + 1, filePath.length())+"_";
        return key;
    }

    //自己定义一个回调,通知外部图片加载完毕
    public interface ThumbnailListener{
        void onThumbnailLoadCompleted(String url, ImageView iv, Bitmap bitmap);
    }
}