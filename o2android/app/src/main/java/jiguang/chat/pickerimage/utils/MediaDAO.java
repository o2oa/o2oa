package jiguang.chat.pickerimage.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class MediaDAO {
    private static final String TAG = "MediaDAO";

    public static Cursor getAllMediaThumbnails(final Context context) {
        final String[] projection = new String[] {
                MediaStore.Images.Thumbnails._ID,
                MediaStore.Images.Thumbnails.IMAGE_ID,
                MediaStore.Images.Thumbnails.DATA};
        final Uri images = MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI;
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(images, projection, null,
                    null, MediaStore.Images.Thumbnails._ID + " DESC");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getAllMediaThumbnails exception");
        }
        return cursor;
    }

    public static Cursor getAllMediaPhotos(final Context context) {
        final Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(images, null, null,
                    null, MediaStore.Images.Media.DATE_MODIFIED + " DESC");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "getAllMediaPhotos exception");
        }

        return cursor;
    }
}
