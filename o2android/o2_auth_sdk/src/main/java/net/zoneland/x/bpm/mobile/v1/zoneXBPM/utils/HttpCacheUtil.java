package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2;

import java.io.File;
import java.util.Date;

import okhttp3.Cache;

/**
 * Created by fancy on 2017/3/22.
 */

public class HttpCacheUtil {

    private static final String TAG = "HttpCacheUtil";
    /**
     * 缓存大小 10M
     */
    private static final long CACHESIZE = 1024 * 1024 * 10L;


    /**
     *  Delete the files older than numDays days from the application cache
     *  0 means all files.
     * @param context
     * @param numDays
     */
    public static void clearCache(final Context context, final int numDays) {
        Log.i(TAG, String.format("Starting cache prune, deleting files older than %d days", numDays));
        int numDeletedFiles = clearCacheFolder(context.getCacheDir(), numDays);
        Log.i(TAG, String.format("Cache pruning completed, %d files deleted", numDeletedFiles));
        Toast.makeText(context, "缓存已经清除！", Toast.LENGTH_SHORT).show();
    }


    /**
     * 获取OKHTTP cache 对象
     * @param context
     * @return
     */
    public static Cache getOkHttpCacheInstance(Context context) {
        File cacheFile = new File(context.getCacheDir(), O2.INSTANCE.getHTTP_CACHE_FOLDER());
        Cache cache = new Cache(cacheFile, CACHESIZE);
        return cache;
    }


    //helper method for clearCache() , recursive
//returns number of deleted files
    private static int clearCacheFolder(final File dir, final int numDays) {
        int deletedFiles = 0;
        if (dir!= null && dir.isDirectory()) {
            Log.i(TAG,"cache dir:"+dir.getAbsolutePath());
            try {
                for (File child:dir.listFiles()) {

                    //first delete subdirectories recursively
                    if (child.isDirectory()) {
                        deletedFiles += clearCacheFolder(child, numDays);
                    }

                    //then delete the files and subdirectories in this dir
                    //only empty directories can be deleted, so subdirs have been done first
                    if (child.lastModified() <= new Date().getTime() - numDays * DateUtils.DAY_IN_MILLIS) {
                        if (child.delete()) {
                            deletedFiles++;
                        }
                    }
                }
            }
            catch(Exception e) {
                Log.e(TAG,String.format("Failed to clean the cache, error %s", e.getMessage()));
            }
        }
        return deletedFiles;
    }


}
