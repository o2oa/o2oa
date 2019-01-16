package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * DiskLruCache 缓存帮助类
 *
 * 磁盘缓存
 *
 * Created by FancyLou on 2015/12/11.
 */
public class DiskLruCacheHelper {

    private DiskLruCacheHelper(){}

    private static DiskLruCache mCache;

    /** 打开DiskLruCache。 */
    public static void openCache(Context context, int appVersion, int maxSize) {
        try {
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                    || !Environment.isExternalStorageRemovable()) {
                mCache = DiskLruCache.open(context.getExternalCacheDir(), appVersion, 1, maxSize);
            } else {
                mCache = DiskLruCache.open(context.getCacheDir(), appVersion, 1, maxSize);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    /**
     * 写入缓存
     *
     * @param bitmap
     * @param keyCache
     * @throws Exception
     */
    public static void dump(Bitmap bitmap, String keyCache) throws Exception {
        if (mCache == null) throw new IllegalStateException("Must call openCache() first!");

        String key = MD5Util.getMD5String(keyCache);
        DiskLruCache.Editor editor = mCache.edit(key);

        if (editor != null) {
            OutputStream outputStream = editor.newOutputStream(0);
            boolean success = bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

            if (success) {
                editor.commit();
            } else {
                editor.abort();
            }
        }
    }

    /**
     * 读取缓存
     * @param keyCache
     * @return
     * @throws Exception
     */
    public static Bitmap load(String keyCache) throws Exception {
        if (mCache == null) throw new IllegalStateException("Must call openCache() first!");

        String key = MD5Util.getMD5String(keyCache);
        DiskLruCache.Snapshot snapshot = mCache.get(key);

        if (snapshot != null) {
            InputStream inputStream = snapshot.getInputStream(0);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            return bitmap;
        }

        return null;
    }

    /**
     * 删除某一条缓存
     * @param keyCache
     * @throws Exception
     */
    public static void remove(String keyCache) throws Exception {
        if (mCache == null) throw new IllegalStateException("Must call openCache() first!");
        String key = MD5Util.getMD5String(keyCache);
        mCache.remove(key);
    }

    /**
     * 删除全部缓存
     * @throws Exception
     */
    public static void deleteAllCache() throws Exception {
        if (mCache == null) throw new IllegalStateException("Must call openCache() first!");
        mCache.delete();
    }

    /**
     * 检查缓存是否存在
     * @param keyCache
     * @return
     */
    public static boolean hasCache(String keyCache) {
        try {
            return mCache.get(MD5Util.getMD5String(keyCache)) != null;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static void syncLog() {
        try {
            mCache.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }

    /** 关闭DiskLruCache。 */
    public static void closeCache() {
        syncLog();
    }
}
