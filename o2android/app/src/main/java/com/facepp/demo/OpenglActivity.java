package com.facepp.demo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PreviewCallback;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facepp.demo.util.CameraMatrix;
import com.facepp.demo.util.ConUtil;
import com.facepp.demo.util.ICamera;
import com.facepp.demo.util.OpenGLUtil;
import com.facepp.demo.util.PointsMatrix;
import com.facepp.demo.util.Screen;
import com.facepp.demo.util.SensorEventUtil;
import com.megvii.facepp.sdk.Facepp;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.face.FaceResult;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.face.FaceSearchData;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.face.FaceSearchResponse;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.O2AlertDialogBuilder;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.O2AlertIconEnum;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.O2DialogSupport;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class OpenglActivity extends Activity
        implements PreviewCallback, Renderer, SurfaceTexture.OnFrameAvailableListener {

    private boolean is106Points, isBackCamera, isOneFaceTrackig;
    private String trackModel;
    private GLSurfaceView mGlSurfaceView;
    private ICamera mICamera;
    private Camera mCamera;
    private HandlerThread mHandlerThread = new HandlerThread("facepp");
    private Handler mHandler;
    private Facepp facepp;
    private int min_face_size = 200;
    private int detection_interval = 25;
    private SensorEventUtil sensorUtil;
    private byte[] carmeraImgData;


    private TextView debugPrinttext;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Screen.initialize(this);
        setContentView(R.layout.activity_facepp_opengl);

        init();

        ConUtil.toggleHideyBar(this);
    }

    private void init() {
        // 人脸识别参数设置
        is106Points = false; // 81 还 106
        isBackCamera = false; //是否后置摄像头 默认用前置
        isOneFaceTrackig = true; //单脸跟踪
        trackModel = "Fast"; //3种模式： Fast Robust Tracking_Rect
        min_face_size = 40; // 33 --- 2147483647
        detection_interval = 30; //毫秒

        facepp = new Facepp();
        sensorUtil = new SensorEventUtil(this);
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());

        mGlSurfaceView = findViewById(R.id.opengl_layout_surfaceview);
        mGlSurfaceView.setEGLContextClientVersion(2);// 创建一个OpenGL ES 2.0
        // context
        mGlSurfaceView.setRenderer(this);// 设置渲染器进入gl
        // RENDERMODE_CONTINUOUSLY不停渲染
        // RENDERMODE_WHEN_DIRTY懒惰渲染，需要手动调用 glSurfaceView.requestRender() 才会进行更新
        mGlSurfaceView.setRenderMode(mGlSurfaceView.RENDERMODE_WHEN_DIRTY);// 设置渲染器模式
        mGlSurfaceView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                autoFocus();
            }
        });

        mICamera = new ICamera();

        debugPrinttext = findViewById(R.id.opengl_layout_debugPrinttext);
        ImageView imgBack = findViewById(R.id.opengl_back);
        imgBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void autoFocus() {
        if (mCamera != null && isBackCamera) {
            mCamera.cancelAutoFocus();
            Parameters parameters = mCamera.getParameters();
            parameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
            mCamera.setParameters(parameters);
            mCamera.autoFocus(null);
        }
    }

    private int Angle;

    @Override
    protected void onResume() {
        super.onResume();
        ConUtil.acquireWakeLock(this);
        startTime = System.currentTimeMillis();
        //设置相机 比如分辨率
        mCamera = mICamera.openCamera(isBackCamera, this);
        if (mCamera != null) {
            Angle = 360 - mICamera.Angle;
            if (isBackCamera)
                Angle = mICamera.Angle;

            RelativeLayout.LayoutParams layout_params = mICamera.getLayoutParam();
            mGlSurfaceView.setLayoutParams(layout_params);

            int width = mICamera.cameraWidth;
            int height = mICamera.cameraHeight;

            int left = 0;
            int top = 0;

            String errorCode = facepp.init(this, ConUtil.getFileContent(this, R.raw.megviifacepp_0_5_2_model), isOneFaceTrackig ? 1 : 0);

            Log.i("OPenGl", "errorCode:" + errorCode);
            //sdk内部其他api已经处理好，可以不判断
            if (errorCode != null) {
                Intent intent = new Intent();
                intent.putExtra("errorcode", errorCode);
                setResult(101, intent);
                finish();
                return;
            }

            Facepp.FaceppConfig faceppConfig = facepp.getFaceppConfig();
            faceppConfig.interval = detection_interval;
            faceppConfig.minFaceSize = min_face_size;
            faceppConfig.roi_left = left;
            faceppConfig.roi_top = top;
            faceppConfig.roi_right = width;
            faceppConfig.roi_bottom = height;
            String[] array = getResources().getStringArray(R.array.login_facepp_trackig_mode_array);
            if (trackModel.equals(array[0]))
                faceppConfig.detectionMode = Facepp.FaceppConfig.DETECTION_MODE_TRACKING_FAST;
            else if (trackModel.equals(array[1]))
                faceppConfig.detectionMode = Facepp.FaceppConfig.DETECTION_MODE_TRACKING_ROBUST;
            else if (trackModel.equals(array[2])) {
                faceppConfig.detectionMode = Facepp.FaceppConfig.MG_FPP_DETECTIONMODE_TRACK_RECT;
//                isShowFaceRect = true;
            }


            facepp.setFaceppConfig(faceppConfig);

            String version = Facepp.getVersion();
            Log.d("ceshi", "onResume:version:" + version);
        } else {
            O2DialogSupport.INSTANCE.openAlertDialog(this, "打开相机失败", new Function1<O2AlertDialogBuilder.O2Dialog, Unit>() {

                @Override
                public Unit invoke(O2AlertDialogBuilder.O2Dialog o2Dialog) {
                    finish();
                    return null;
                }
            }, O2AlertIconEnum.FAILURE);
        }
    }

    private void setConfig(int rotation) {
        Facepp.FaceppConfig faceppConfig = facepp.getFaceppConfig();
        if (faceppConfig.rotation != rotation) {
            faceppConfig.rotation = rotation;
            facepp.setFaceppConfig(faceppConfig);
        }
    }

    boolean isSuccess = false;
    float pitch, yaw, roll;
    long startTime;
    int rotation = Angle;
    long matrixTime;


    @Override
    public void onPreviewFrame(final byte[] imgData, final Camera camera) {
        Log.e("Fancy", "onPreviewFrame ...............");
        if (!faceSwitch) {
            Log.e("Fancy", "结束。。。。。。。。。。");
            return;
        }

        //检测操作放到主线程，防止贴点延迟
        int width = mICamera.cameraWidth;
        int height = mICamera.cameraHeight;

        final int orientation = sensorUtil.orientation;
        if (orientation == 0)
            rotation = Angle;
        else if (orientation == 1)
            rotation = 0;
        else if (orientation == 2)
            rotation = 180;
        else if (orientation == 3)
            rotation = 360 - Angle;


        setConfig(rotation);

        final Facepp.Face[] faces = facepp.detect(imgData, width, height, Facepp.IMAGEMODE_NV21);
        if (faces != null) {
            Log.e("Fancy", "faces size." + faces.length);
            long actionMaticsTime = System.currentTimeMillis();
            ArrayList<ArrayList> pointsOpengl = new ArrayList<ArrayList>();
            ArrayList<FloatBuffer> rectsOpengl = new ArrayList<FloatBuffer>();
            if (faces.length > 0) {
                for (int c = 0; c < faces.length; c++) {

                    if (is106Points)
                        facepp.getLandmarkRaw(faces[c], Facepp.FPP_GET_LANDMARK106);
                    else
                        facepp.getLandmarkRaw(faces[c], Facepp.FPP_GET_LANDMARK81);

                    final Facepp.Face face = faces[c];
                    pitch = faces[c].pitch;
                    yaw = faces[c].yaw;
                    roll = faces[c].roll;


                    //0.4.7之前（包括）jni把所有角度的点算到竖直的坐标，所以外面画点需要再调整回来，才能与其他角度适配
                    //目前getLandmarkOrigin会获得原始的坐标，所以只需要横屏适配好其他的角度就不用适配了，因为texture和preview的角度关系是固定的
                    ArrayList<FloatBuffer> triangleVBList = new ArrayList<>();
                    for (int i = 0; i < faces[c].points.length; i++) {
                        float x = (faces[c].points[i].x / width) * 2 - 1;
                        if (isBackCamera)
                            x = -x;
                        float y = (faces[c].points[i].y / height) * 2 - 1;
                        float[] pointf = new float[]{y, x, 0.0f};
                        FloatBuffer fb = mCameraMatrix.floatBufferUtil(pointf);
                        triangleVBList.add(fb);
                    }

                    pointsOpengl.add(triangleVBList);
//
//                    if (mPointsMatrix.isShowFaceRect) {
//                        facepp.getRect(faces[c]);
//                        FloatBuffer buffer = calRectPostion(faces[c].rect, mICamera.cameraWidth, mICamera.cameraHeight);
//                        rectsOpengl.add(buffer);
//                    }

                }
            } else {
                pitch = 0.0f;
                yaw = 0.0f;
                roll = 0.0f;
            }

            matrixTime = System.currentTimeMillis() - actionMaticsTime;

        }

        if (isSuccess)
            return;
        isSuccess = true;

        //设置图片数据
        carmeraImgData = imgData;

//        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
//                Log.e("Fancy", "handle ...faces size."+faces.length);
//                if (faces.length > 0) {
////                    List<String> filepaths = saveFaceAsFile(OpenglActivity.this, faces, mICamera, carmeraImgData, isBackCamera);
////                    if (filepaths!= null && !filepaths.isEmpty()) {
////                        FaceSearchResponse response = OkHttpUtil.getInstance().searchFaceFromServer(filepaths.get(0));
////                        List<FaceSearchResult> list = response.getData().getResults();
////                        if (list!=null && !list.isEmpty()) {
////                            FaceSearchResult resut  = list.get(0);
////                            final String userid = resut.getUser_id();
////                            final double c = resut.getConfidence();
////                            faceSwitch = false;
////                            runOnUiThread(new Runnable() {
////                                @Override
////                                public void run() {
////                                    debugPrinttext.setText("userId: "+userid+", confidence:"+c);
////                                }
////                            });
////
////                        }else {
////                            Log.e("Fancy", "没有识别到。。。。");
////                        }
////                    }else  {
////                        Log.e("Fancy" ,"没有生成图片。。");
////                    }
//
//                }
//
//                isSuccess = false;
//
//            }
//        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        ConUtil.releaseWakeLock();
        mICamera.closeCamera();
        mCamera = null;


        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                facepp.release();
            }
        });

    }

    private int mTextureID = -1;
    private SurfaceTexture mSurface;
    private CameraMatrix mCameraMatrix;
    private PointsMatrix mPointsMatrix;

    private boolean faceSwitch = true;

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        Log.e("Fancy", "onFrameAvailable");
        mGlSurfaceView.requestRender();
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // 黑色背景
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        surfaceInit();
    }

    private void surfaceInit() {
        mTextureID = OpenGLUtil.createTextureID();

        mSurface = new SurfaceTexture(mTextureID);

        // 这个接口就干了这么一件事，当有数据上来后会进到onFrameAvailable方法
        mSurface.setOnFrameAvailableListener(this);// 设置照相机有数据时进入
        mCameraMatrix = new CameraMatrix(mTextureID);
        mPointsMatrix = new PointsMatrix(false);
        mPointsMatrix.isShowFaceRect = false;
        mICamera.startPreview(mSurface);// 设置预览容器
        mICamera.actionDetect(this);
    }


    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // 设置画面的大小
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;
        ratio = 1; // 这样OpenGL就可以按照屏幕框来画了，不是一个正方形了

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
        // Matrix.perspectiveM(mProjMatrix, 0, 0.382f, ratio, 3, 700);

    }

    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjMatrix = new float[16];
    private final float[] mVMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];

    @Override
    public void onDrawFrame(GL10 gl) {

        XLog.info("这个方法是否有调用 需要关注。。。。。。。。。。。。。。");

        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);// 清除屏幕和深度缓存
        float[] mtx = new float[16];
        mSurface.getTransformMatrix(mtx);
        mCameraMatrix.draw(mtx);
        // Set the camera position (View matrix)
        Matrix.setLookAtM(mVMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1f, 0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);

        mPointsMatrix.draw(mMVPMatrix);


        mSurface.updateTexImage();// 更新image，会调用onFrameAvailable方法


    }


    private FloatBuffer calRectPostion(Rect rect, float width, float height) {
        float top = 1 - (rect.top * 1.0f / height) * 2;
        float left = (rect.left * 1.0f / width) * 2 - 1;
        float right = (rect.right * 1.0f / width) * 2 - 1;
        float bottom = 1 - (rect.bottom * 1.0f / height) * 2;

        // 左上角
        float x1 = -top;
        float y1 = left;

        // 右下角
        float x2 = -bottom;
        float y2 = right;

        if (isBackCamera) {
            y1 = -y1;
            y2 = -y2;
        }

        float[] tempFace = {
                x1, y2, 0.0f,
                x1, y1, 0.0f,
                x2, y1, 0.0f,
                x2, y2, 0.0f,
        };

        FloatBuffer buffer = mCameraMatrix.floatBufferUtil(tempFace);
        return buffer;
    }

    /**
     * 保存图片 上传到服务器验证。。。
     *
     * @param activity
     * @param faces
     * @param mICamera
     * @param carmeraImgData
     * @param isBackCamera
     * @return
     */
    private List<String> saveFaceAsFile(OpenglActivity activity, Facepp.Face[] faces, ICamera mICamera, byte[] carmeraImgData, boolean isBackCamera) {
        List<String> imgs = new ArrayList<>();
        for (int i = 0; i < faces.length; i++) {
            Facepp.Face face = faces[i];
            Rect rect = face.rect;
            Bitmap bitmap = mICamera.getBitMapWithRect(carmeraImgData, mICamera.mCamera, !isBackCamera, rect);
            if (bitmap != null) {
                String filePath = ConUtil.saveBitmap(activity, bitmap);
                Log.e("FANCY", "file path:" + filePath);
                imgs.add(filePath);
            }
        }
        return imgs;
    }


}
