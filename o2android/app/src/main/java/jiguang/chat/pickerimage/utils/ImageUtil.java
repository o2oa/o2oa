package jiguang.chat.pickerimage.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;
import jiguang.chat.application.JGApplication;

public class ImageUtil {
    public static class ImageSize {
        public int width = 0;
        public int height = 0;

        public ImageSize(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

    public final static float MAX_IMAGE_RATIO = 5f;

    public static Bitmap getDefaultBitmapWhenGetFail() {
        try {
            return getBitmapImmutableCopy(JGApplication.context.getResources(), R.drawable.image_download_failed);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static final Bitmap getBitmapImmutableCopy(Resources res, int id) {
        return getBitmap(res.getDrawable(id)).copy(Config.RGB_565, false);
    }

    public static final Bitmap getBitmap(Drawable dr) {
        if (dr == null) {
            return null;
        }

        if (dr instanceof BitmapDrawable) {
            return ((BitmapDrawable) dr).getBitmap();
        }

        return null;
    }

    public static Bitmap rotateBitmapInNeeded(String path, Bitmap srcBitmap) {
        if (TextUtils.isEmpty(path) || srcBitmap == null) {
            return null;
        }

        ExifInterface localExifInterface;
        try {
            localExifInterface = new ExifInterface(path);
            int rotateInt = localExifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            float rotate = getImageRotate(rotateInt);
            if (rotate != 0) {
                Matrix matrix = new Matrix();
                matrix.postRotate(rotate);
                Bitmap dstBitmap = Bitmap.createBitmap(srcBitmap, 0, 0,
                        srcBitmap.getWidth(), srcBitmap.getHeight(), matrix,
                        false);
                if (dstBitmap == null) {
                    return srcBitmap;
                } else {
                    if (srcBitmap != null && !srcBitmap.isRecycled()) {
                        srcBitmap.recycle();
                    }
                    return dstBitmap;
                }
            } else {
                return srcBitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return srcBitmap;
        }
    }

    /**
     * 获得旋转角度
     *
     * @param rotate
     * @return
     */
    public static float getImageRotate(int rotate) {
        float f;
        if (rotate == 6) {
            f = 90.0F;
        } else if (rotate == 3) {
            f = 180.0F;
        } else if (rotate == 8) {
            f = 270.0F;
        } else {
            f = 0.0F;
        }

        return f;
    }

    public static String makeThumbnail(Context context, File imageFile) {
        String thumbFilePath = StorageUtil.getWritePath(imageFile.getName(),
                StorageType.TYPE_THUMB_IMAGE);
        File thumbFile = AttachmentStore.create(thumbFilePath);
        if (thumbFile == null) {
            return null;
        }

        boolean result = scaleThumbnail(
                imageFile,
                thumbFile,
                getImageMaxEdge(),
                getImageMinEdge(),
                CompressFormat.JPEG,
                60);
        if (!result) {
            AttachmentStore.delete(thumbFilePath);
            return null;
        }

        return thumbFilePath;
    }
    public static int getImageMaxEdge() {
        return (int) (165.0 / 320.0 * ScreenUtil.screenWidth);
    }

    public static int getImageMinEdge() {
        return (int) (76.0 / 320.0 * ScreenUtil.screenWidth);
    }

    public static Boolean scaleThumbnail(File srcFile, File dstFile, int dstMaxWH, int dstMinWH, CompressFormat compressFormat, int quality) {
        Boolean bRet = false;
        Bitmap srcBitmap = null;
        Bitmap dstBitmap = null;
        BufferedOutputStream bos = null;

        try {
            int[] bound = BitmapDecoder.decodeBound(srcFile);
            ImageSize size = getThumbnailDisplaySize(bound[0], bound[1], dstMaxWH, dstMinWH);
            srcBitmap = BitmapDecoder.decodeSampled(srcFile.getPath(), size.width, size.height);

            // 旋转
            ExifInterface localExifInterface = new ExifInterface(srcFile.getAbsolutePath());
            int rotateInt = localExifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            float rotate = getImageRotate(rotateInt);

            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);

            float inSampleSize = 1;

            if (srcBitmap.getWidth() >= dstMinWH && srcBitmap.getHeight() <= dstMaxWH
                    && srcBitmap.getWidth() >= dstMinWH && srcBitmap.getHeight() <= dstMaxWH) {
                //如果第一轮拿到的srcBitmap尺寸都符合要求，不需要再做缩放
            } else {
                if (srcBitmap.getWidth() != size.width || srcBitmap.getHeight() != size.height) {
                    float widthScale = (float) size.width / (float) srcBitmap.getWidth();
                    float heightScale = (float) size.height / (float) srcBitmap.getHeight();

                    if (widthScale >= heightScale) {
                        size.width = srcBitmap.getWidth();
                        size.height /= widthScale;//必定小于srcBitmap.getHeight()
                        inSampleSize = widthScale;
                    } else {
                        size.width /= heightScale;//必定小于srcBitmap.getWidth()
                        size.height = srcBitmap.getHeight();
                        inSampleSize = heightScale;
                    }
                }
            }

            matrix.postScale(inSampleSize, inSampleSize);

            if (rotate == 0 && inSampleSize == 1) {
                dstBitmap = srcBitmap;
            } else {
                dstBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, size.width, size.height, matrix, true);
            }

            bos = new BufferedOutputStream(new FileOutputStream(dstFile));
            dstBitmap.compress(compressFormat, quality, bos);
            bos.flush();
            bRet = true;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (srcBitmap != null && !srcBitmap.isRecycled()) {
                srcBitmap.recycle();
                srcBitmap = null;
            }

            if (dstBitmap != null && !dstBitmap.isRecycled()) {
                dstBitmap.recycle();
                dstBitmap = null;
            }
        }
        return bRet;
    }

    public static ImageSize getThumbnailDisplaySize(float srcWidth, float srcHeight, float dstMaxWH, float dstMinWH) {
        if (srcWidth <= 0 || srcHeight <= 0) { // bounds check
            return new ImageSize((int) dstMinWH, (int) dstMinWH);
        }

        float shorter;
        float longer;
        boolean widthIsShorter;

        //store
        if (srcHeight < srcWidth) {
            shorter = srcHeight;
            longer = srcWidth;
            widthIsShorter = false;
        } else {
            shorter = srcWidth;
            longer = srcHeight;
            widthIsShorter = true;
        }

        if (shorter < dstMinWH) {
            float scale = dstMinWH / shorter;
            shorter = dstMinWH;
            if (longer * scale > dstMaxWH) {
                longer = dstMaxWH;
            } else {
                longer *= scale;
            }
        } else if (longer > dstMaxWH) {
            float scale = dstMaxWH / longer;
            longer = dstMaxWH;
            if (shorter * scale < dstMinWH) {
                shorter = dstMinWH;
            } else {
                shorter *= scale;
            }
        }

        //restore
        if (widthIsShorter) {
            srcWidth = shorter;
            srcHeight = longer;
        } else {
            srcWidth = longer;
            srcHeight = shorter;
        }

        return new ImageSize((int) srcWidth, (int) srcHeight);
    }

    public static File getScaledImageFileWithMD5(File imageFile, String mimeType) {
        String filePath = imageFile.getPath();

        if (!isInvalidPictureFile(mimeType)) {
            return null;
        }

        String tempFilePath = getTempFilePath(FileUtil.getExtensionName(filePath));
        Log.i("ImageUtil", "tempFilePath:"+tempFilePath);
        File tempImageFile = AttachmentStore.create(tempFilePath);
        if (tempImageFile == null) {
            return null;
        }

        CompressFormat compressFormat = CompressFormat.JPEG;
        // 压缩数值由第三方开发者自行决定
        int maxWidth = 720;
        int quality = 60;

        if (ImageUtil.scaleImage(imageFile, tempImageFile, maxWidth, compressFormat, quality)) {
            return tempImageFile;
        } else {
            return null;
        }
    }

    private static String getTempFilePath(String extension) {
        return StorageUtil.getWritePath(
                null,
                "temp_image_" + StringUtil.get36UUID() + "." + extension,
                StorageType.TYPE_TEMP);
    }

    public static Boolean scaleImage(File srcFile, File dstFile, int dstMaxWH, CompressFormat compressFormat, int quality) {
        Boolean success = false;

        try {
            int inSampleSize = SampleSizeUtil.calculateSampleSize(srcFile.getAbsolutePath(), dstMaxWH * dstMaxWH);
            Bitmap srcBitmap = BitmapDecoder.decodeSampled(srcFile.getPath(), inSampleSize);
            if (srcBitmap == null) {
                return success;
            }

            // 旋转
            ExifInterface localExifInterface = new ExifInterface(srcFile.getAbsolutePath());
            int rotateInt = localExifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            float rotate = getImageRotate(rotateInt);

            Bitmap dstBitmap;
            float scale = (float) Math.sqrt(((float) dstMaxWH * (float) dstMaxWH) / ((float) srcBitmap.getWidth() * (float) srcBitmap.getHeight()));
            if (rotate == 0f && scale >= 1) {
                dstBitmap = srcBitmap;
            } else {
                try {
                    Matrix matrix = new Matrix();
                    if (rotate != 0) {
                        matrix.postRotate(rotate);
                    }
                    if (scale < 1) {
                        matrix.postScale(scale, scale);
                    }
                    dstBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, true);
                } catch (OutOfMemoryError e) {
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dstFile));
                    srcBitmap.compress(compressFormat, quality, bos);
                    bos.flush();
                    bos.close();
                    success = true;

                    if (!srcBitmap.isRecycled())
                        srcBitmap.recycle();
                    srcBitmap = null;

                    return success;
                }
            }

            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(dstFile));
            dstBitmap.compress(compressFormat, quality, bos);
            bos.flush();
            bos.close();
            success = true;

            if (!srcBitmap.isRecycled())
                srcBitmap.recycle();
            srcBitmap = null;

            if (!dstBitmap.isRecycled())
                dstBitmap.recycle();
            dstBitmap = null;
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        return success;
    }

    public static ImageSize getThumbnailDisplaySize(int maxSide, int minSide, String imagePath) {
        int[] bound = BitmapDecoder.decodeBound(imagePath);
        ImageSize imageSize = getThumbnailDisplaySize(bound[0], bound[1], maxSide, minSide);
        return imageSize;
    }

    public static int[] getBoundWithLength(int maxSide, Object imageObject, boolean resizeToDefault) {
        int width = -1;
        int height = -1;

        int[] bound;
        if (String.class.isInstance(imageObject)) {
            bound = BitmapDecoder.decodeBound((String) imageObject);
            width = bound[0];
            height = bound[1];
        } else if (Integer.class.isInstance(imageObject)) {
            bound = BitmapDecoder.decodeBound(JGApplication.context.getResources(), (Integer) imageObject);
            width = bound[0];
            height = bound[1];
        } else if (InputStream.class.isInstance(imageObject)) {
            bound = BitmapDecoder.decodeBound((InputStream) imageObject);
            width = bound[0];
            height = bound[1];
        }

        int defaultWidth = maxSide;
        int defaultHeight = maxSide;
        if (width <= 0 || height <= 0) {
            width = defaultWidth;
            height = defaultHeight;
        } else if (resizeToDefault) {
            if (width > height) {
                height = (int) (defaultWidth * ((float) height / (float) width));
                width = defaultWidth;
            } else {
                width = (int) (defaultHeight * ((float) width / (float) height));
                height = defaultHeight;
            }
        }

        return new int[]{width, height};
    }

    /**
     * 下载失败与获取失败时都统一显示默认下载失败图片
     *
     * @return
     */
    public static Bitmap getBitmapFromDrawableRes(int res) {
        try {
            return getBitmapImmutableCopy(JGApplication.context.getResources(), res);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isInvalidPictureFile(String mimeType) {
        String lowerCaseFilepath = mimeType.toLowerCase();
        return (lowerCaseFilepath.contains("jpg") || lowerCaseFilepath.contains("jpeg")
                || lowerCaseFilepath.toLowerCase().contains("png") || lowerCaseFilepath.toLowerCase().contains("bmp") || lowerCaseFilepath
                .toLowerCase().contains("gif"));
    }
}
