package com.facepp.demo.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.util.Log;
import android.view.Surface;
import android.widget.RelativeLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * 照相机工具类
 */
public class ICamera {

	public Camera mCamera;
	public int cameraWidth;
	public int cameraHeight;
	public int cameraId = 1;// 前置摄像头
	public int Angle;

	public ICamera() {
	}

	/**
	 * 打开相机
	 */
	public Camera openCamera(boolean isBackCamera, Activity activity) {
		try {
			if (isBackCamera)
				cameraId = 0;
			else
				cameraId = 1;

//			int width = 640;
//			int height = 480;
//
//			if (resolutionMap != null) {
//				width = resolutionMap.get("width");
//				height = resolutionMap.get("height");
//			}

			mCamera = Camera.open(cameraId);
			CameraInfo cameraInfo = new CameraInfo();
			Camera.getCameraInfo(cameraId, cameraInfo);
			Camera.Parameters params = mCamera.getParameters();
			// Camera.Size bestPreviewSize = calBestPreviewSize(
			// mCamera.getParameters(), Screen.mWidth, Screen.mHeight);
			// 根据传入的计算预览的大小
//			Camera.Size bestPreviewSize = calBestPreviewSize(
//					mCamera.getParameters(), width, height);
			// 取最大的分辨率的那个
			Camera.Size bestPreviewSize = calBigestPreviewSize(mCamera.getParameters());
			cameraWidth = bestPreviewSize.width;
			cameraHeight = bestPreviewSize.height;
			params.setPreviewSize(cameraWidth, cameraHeight);
			Angle = getCameraAngle(activity);
			Log.w("ceshi", "Angle==" + Angle);
			Log.i("Fancy", "camera size width = " + cameraWidth + ", height = " + cameraHeight);
			// mCamera.setDisplayOrientation(Angle);
			mCamera.setParameters(params);
			return mCamera;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean isBackCamera(){
		return cameraId==1?false:true;
	}

	// 通过屏幕参数、相机预览尺寸计算布局参数
	public RelativeLayout.LayoutParams getLayoutParam() {
		float scale = cameraWidth * 1.0f / cameraHeight;

		int layout_width = Screen.mWidth;
		int layout_height = (int) (layout_width * scale);

		if (Screen.mWidth >= Screen.mHeight) {
			layout_height = Screen.mHeight;
			layout_width = (int) (layout_height / scale);
		}

		RelativeLayout.LayoutParams layout_params = new RelativeLayout.LayoutParams(
				layout_width, layout_height);
		layout_params.addRule(RelativeLayout.CENTER_HORIZONTAL);// 设置照相机水平居中
		layout_params.addRule(RelativeLayout.CENTER_VERTICAL);//垂直居中

		return layout_params;
	}

	/**
	 * 开始检测脸
	 */
	public void actionDetect(Camera.PreviewCallback mActivity) {
		if (mCamera != null) {
			mCamera.setPreviewCallback(mActivity);
		}
	}

	public void startPreview(SurfaceTexture surfaceTexture) {
		if (mCamera != null) {
			try {
				mCamera.setPreviewTexture(surfaceTexture);
				mCamera.startPreview();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void closeCamera() {
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.setPreviewCallback(null);
			mCamera.release();
			mCamera = null;
		}
	}

	public static ArrayList<HashMap<String, Integer>> getCameraPreviewSize(
			int cameraId) {
		ArrayList<HashMap<String, Integer>> size = new ArrayList<HashMap<String, Integer>>();
		Camera camera = null;
		try {
			camera = Camera.open(cameraId);
			if (camera == null)
				camera = Camera.open(0);

			List<Camera.Size> allSupportedSize = camera.getParameters()
					.getSupportedPreviewSizes();
			for (Camera.Size tmpSize : allSupportedSize) {
				if (tmpSize.width > tmpSize.height) {
					HashMap<String, Integer> map = new HashMap<String, Integer>();
                    map.put("width", tmpSize.width);
                    map.put("height", tmpSize.height);
                    if (tmpSize.width==640&&tmpSize.height==480){
                        size.add(map);
                    }
                    if (tmpSize.width==960&&tmpSize.height==540){
                        size.add(map);
                    }
                    if (tmpSize.width==1280&&tmpSize.height==720){
                        size.add(map);
                    }
                    if (tmpSize.width==1920&&tmpSize.height==1080){
                        size.add(map);
                    }

				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (camera != null) {
				camera.stopPreview();
				camera.setPreviewCallback(null);
				camera.release();
				camera = null;
			}
		}

		return size;
	}

	/**
	 * 通过传入的宽高算出最接近于宽高值的相机大小
	 */
	private Camera.Size calBestPreviewSize(Camera.Parameters camPara,
										   final int width, final int height) {
		List<Camera.Size> allSupportedSize = camPara.getSupportedPreviewSizes();
		ArrayList<Camera.Size> widthLargerSize = new ArrayList<Camera.Size>();
		for (Camera.Size tmpSize : allSupportedSize) {
			Log.w("ceshi", "tmpSize.width===" + tmpSize.width
					+ ", tmpSize.height===" + tmpSize.height);
			if (tmpSize.width > tmpSize.height) {
				widthLargerSize.add(tmpSize);
			}
		}

		Collections.sort(widthLargerSize, new Comparator<Camera.Size>() {
			@Override
			public int compare(Camera.Size lhs, Camera.Size rhs) {
				int off_one = Math.abs(lhs.width * lhs.height - width * height);
				int off_two = Math.abs(rhs.width * rhs.height - width * height);
				return off_one - off_two;
			}
		});

		return widthLargerSize.get(0);
	}

	private Camera.Size calBigestPreviewSize(Camera.Parameters camPara) {
		List<Camera.Size> allSupportedSize = camPara.getSupportedPreviewSizes();
		ArrayList<Camera.Size> widthLargerSize = new ArrayList<Camera.Size>();
		for (Camera.Size tmpSize : allSupportedSize) {

			if (tmpSize.width > tmpSize.height) {
				widthLargerSize.add(tmpSize);
			}
		}

		Collections.sort(widthLargerSize, new Comparator<Camera.Size>() {
			@Override
			public int compare(Camera.Size lhs, Camera.Size rhs) {
				int off_one = lhs.width * lhs.height;
				int off_two = rhs.width * rhs.height;
				return off_two - off_one;
			}
		});

		return widthLargerSize.get(0);
	}

	/**
	 * 打开前置或后置摄像头
	 */
	public Camera getCameraSafely(int cameraId) {
		Camera camera = null;
		try {
			camera = Camera.open(cameraId);
		} catch (Exception e) {
			camera = null;
		}
		return camera;
	}

	public Bitmap getBitMap(byte[] data,  boolean mIsFrontalCamera){
		return getBitMap(data, mCamera, mIsFrontalCamera);
	}

	public Bitmap getBitMap(byte[] data, Camera camera, boolean mIsFrontalCamera) {
		int width = camera.getParameters().getPreviewSize().width;
		int height = camera.getParameters().getPreviewSize().height;
		YuvImage yuvImage = new YuvImage(data, camera.getParameters()
				.getPreviewFormat(), width, height, null);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		yuvImage.compressToJpeg(new Rect(0, 0, width, height), 80,
				byteArrayOutputStream);
		byte[] jpegData = byteArrayOutputStream.toByteArray();
		// 获取照相后的bitmap
		Bitmap tmpBitmap = BitmapFactory.decodeByteArray(jpegData, 0,
				jpegData.length);
		Matrix matrix = new Matrix();
		matrix.reset();
		if (mIsFrontalCamera) {
			matrix.setRotate(-90);
		} else {
			matrix.setRotate(90);
		}
		tmpBitmap = Bitmap.createBitmap(tmpBitmap, 0, 0, tmpBitmap.getWidth(),
				tmpBitmap.getHeight(), matrix, true);
		tmpBitmap = tmpBitmap.copy(Bitmap.Config.ARGB_8888, true);

		int hight = tmpBitmap.getHeight() > tmpBitmap.getWidth() ? tmpBitmap
				.getHeight() : tmpBitmap.getWidth();

		float scale = hight / 800.0f;

		if (scale > 1) {
			tmpBitmap = Bitmap.createScaledBitmap(tmpBitmap,
					(int) (tmpBitmap.getWidth() / scale),
					(int) (tmpBitmap.getHeight() / scale), false);
		}
		return tmpBitmap;
	}



	public Bitmap getBitMapWithRect(byte[] data, Camera camera, boolean mIsFrontalCamera,Rect rect) {
		int width = camera.getParameters().getPreviewSize().width;
		int height = camera.getParameters().getPreviewSize().height;
		YuvImage yuvImage = new YuvImage(data, camera.getParameters()
				.getPreviewFormat(), width, height, null);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		yuvImage.compressToJpeg(new Rect(0, 0, width, height), 80,
				byteArrayOutputStream);

		byte[] jpegData = byteArrayOutputStream.toByteArray();
		// 获取照相后的bitmap
		Bitmap tmpBitmap = BitmapFactory.decodeByteArray(jpegData, 0,
				jpegData.length);

//		Log.e("xie", "getbitmap width"+tmpBitmap.getWidth()+"rect="+rect );
		if (rect.top<0){
			rect.top=0;
		}
		if (rect.top>tmpBitmap.getHeight()){
			rect.top=tmpBitmap.getHeight();
		}
		if (rect.left<0){
			rect.left=0;
		}
		if (rect.left>tmpBitmap.getWidth()){
			rect.left=tmpBitmap.getWidth();
		}
		int widthRect=rect.right-rect.left;
		if(rect.right>tmpBitmap.getWidth()){
			widthRect=tmpBitmap.getWidth()-rect.left;

		}
		int heightRect=rect.bottom-rect.top;
		if(rect.bottom>tmpBitmap.getHeight()){
			heightRect=tmpBitmap.getHeight()-rect.top;
		}
//		Log.i("xie","xie rect"+rect+"wid"+widthRect+"height"+heightRect);
		tmpBitmap = Bitmap.createBitmap(tmpBitmap, rect.left, rect.top, widthRect,
				heightRect);

		Matrix matrix = new Matrix();
		matrix.reset();
		if (mIsFrontalCamera) {
			matrix.setRotate(-90);
		} else {
			matrix.setRotate(90);
		}
		tmpBitmap = Bitmap.createBitmap(tmpBitmap, 0, 0, tmpBitmap.getWidth(),
				tmpBitmap.getHeight(), matrix, true);
//		Log.e("xie", "getbitmap temp"+tmpBitmap.getWidth()+"asdhe "+tmpBitmap.getHeight() );
		tmpBitmap = tmpBitmap.copy(Bitmap.Config.ARGB_8888, true);

		int hight = tmpBitmap.getHeight() > tmpBitmap.getWidth() ? tmpBitmap
				.getHeight() : tmpBitmap.getWidth();

		float scale = hight / 800.0f;

		if (scale > 1) {
			tmpBitmap = Bitmap.createScaledBitmap(tmpBitmap,
					(int) (tmpBitmap.getWidth() / scale),
					(int) (tmpBitmap.getHeight() / scale), false);
		}
		return tmpBitmap;
	}

	/**
	 * 获取照相机旋转角度
	 */
	public int getCameraAngle(Activity activity) {
		int rotateAngle = 90;
		CameraInfo info = new CameraInfo();
		Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();
		int degrees = 0;
		switch (rotation) {
			case Surface.ROTATION_0:
				degrees = 0;
				break;
			case Surface.ROTATION_90:
				degrees = 90;
				break;
			case Surface.ROTATION_180:
				degrees = 180;
				break;
			case Surface.ROTATION_270:
				degrees = 270;
				break;
		}

		if (info.facing == CameraInfo.CAMERA_FACING_FRONT) {
			rotateAngle = (info.orientation + degrees) % 360;
			rotateAngle = (360 - rotateAngle) % 360; // compensate the mirror
		} else { // back-facing
			rotateAngle = (info.orientation - degrees + 360) % 360;
		}
		return rotateAngle;
	}
}