package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.cache;

import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * LruCache 工具类
 *
 * 内存缓存
 *
 * Created by FancyLou on 2015/12/11.
 */
public class LruCacheHelper {

    private LruCacheHelper(){}

    private static LruCache<String, Bitmap> mCache;

    /** 初始化LruCache。 */
    public static void openCache(int maxSize) {
        mCache = new LruCache<String, Bitmap>((int) maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
    }

    /** 把图片写入缓存。 */
    public static void dump(String key, Bitmap value) {
        mCache.put(key, value);
    }

    /** 从缓存中读取图片数据。 */
    public static Bitmap load(String key) {
        return mCache.get(key);
    }

    /**
     * 删除某一条缓存
     * @param key
     */
    public static void remove(String key) {
        mCache.remove(key);
    }

    public static void closeCache() {
//        mCache.evictAll();//会清空缓存
        // 暂时没事干。
    }
}
