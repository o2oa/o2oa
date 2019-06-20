package jiguang.chat.utils.photovideo.takevideo.camera;

import android.content.Context;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;

import jiguang.chat.utils.photovideo.takevideo.utils.SPUtils;


final class CameraUtils {
    /**
     * 摄像机闪光灯状态
     */
    static final String CAMERA_FLASH = "camera_flash";
    /**
     * 摄像机前后置状态
     */
    static final String CAMERA_AROUND = "camera_around";

    private CameraUtils() {}

    /**
     * 获取相机闪光灯状态
     * @return 0为自动,1为打开,其他为关闭
     */
    static int getCameraFlash(Context context) {
        return (int) SPUtils.get(context, CAMERA_FLASH, 0);
    }

    /**
     * 设置相机闪光状态
     * @param flash
     */
    static void setCameraFlash(Context context, int flash) {
        SPUtils.put(context, CAMERA_FLASH, flash);
    }

    /**
     * 获取摄像头是否为前置或后
     *
     * @param context
     * @return 0为后置,1为前置
     */
    static int getCameraFacing(Context context, int defaultId) {
        return (int) SPUtils.get(context, CAMERA_AROUND, defaultId);
    }

    /**
     * 设置摄像头前置或后
     *
     * @param context
     * @param around
     */
    static void setCameraFacing(Context context, int around) {
        SPUtils.put(context, CAMERA_AROUND, around );
    }

    /**
     * 摄像机是否支持前置拍照
     * @return
     */
    static boolean isSupportFrontCamera() {
        final int cameraCount = Camera.getNumberOfCameras();
        Camera.CameraInfo info = new Camera.CameraInfo();
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, info);
            if (info.facing == 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否支持闪光
     * @param context
     * @return
     */
    static boolean isSupportFlashCamera(Context context) {
        FeatureInfo[] features = context.getPackageManager().getSystemAvailableFeatures();
        for(FeatureInfo info : features) {
            if(PackageManager.FEATURE_CAMERA_FLASH.equals(info.name))
                return true;
        }
        return false;
    }

}
