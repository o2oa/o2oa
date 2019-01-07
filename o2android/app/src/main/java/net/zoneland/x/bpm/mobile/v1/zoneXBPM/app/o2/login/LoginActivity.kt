package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.login

import android.Manifest
import android.content.Context
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.media.AudioManager
import android.media.MediaPlayer
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.text.InputType
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.view.inputmethod.InputMethodManager
import com.facepp.demo.util.*
import com.megvii.facepp.sdk.Facepp
import com.megvii.licensemanager.sdk.LicenseManager
import kotlinx.android.synthetic.main.activity_login.*
import net.muliba.changeskin.FancySkinManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.bind.BindPhoneActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.main.MainActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.AuthenticationInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.permission.PermissionRequester
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.CountDownButtonHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.O2DialogSupport
import java.io.IOException
import java.nio.FloatBuffer
import java.util.*
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


/**
 * Created by fancy o7/6/8.
 */

class LoginActivity: BaseMVPActivity<LoginContract.View, LoginContract.Presenter>(),
        LoginContract.View, View.OnClickListener,
        Camera.PreviewCallback, GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener{

    override var mPresenter: LoginContract.Presenter = LoginPresenter()

    override fun beforeSetContentView() {
        super.beforeSetContentView()
        setTheme(R.style.XBPMTheme_NoActionBar)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)//去掉信息栏
        Screen.initialize(this)
    }

    companion object {
        const val REQUEST_PHONE_KEY = "REQUEST_PHONE_KEY"
        fun startBundleData(phone:String): Bundle {
            val bundle = Bundle()
            bundle.putString(REQUEST_PHONE_KEY, phone)
            return bundle
        }
    }

    private val countDownHelper: CountDownButtonHelper by lazy {
        CountDownButtonHelper(button_login_phone_code,
                getString(R.string.login_button_code),
                60,
                1)
    }
    private val scale0 by lazy {
        ScaleAnimation(1f,0f,1f,1f,
            Animation.RELATIVE_TO_PARENT,0.5f,
                Animation.RELATIVE_TO_PARENT,0.5f)
    }
    private val scale1 by lazy {
        ScaleAnimation(0f,1f,1f,1f,
                Animation.RELATIVE_TO_PARENT,0.5f,
                Animation.RELATIVE_TO_PARENT,0.5f)
    }
    private var receivePhone = ""

    //播放声音
    private var mediaPlayer: MediaPlayer? = null
    private var playBeep: Boolean = false
    //facepp 相关的参数
    private var faceppLicenseIsOK: Boolean = false
    private var is106Points: Boolean = false
    private var isBackCamera:Boolean = false
    private var isOneFaceTrackig:Boolean = false
    private var trackModel: String? = null
    private var mGlSurfaceView: GLSurfaceView? = null
    private var mICamera: ICamera? = null
    private var mCamera: Camera? = null
    private val mHandlerThread = HandlerThread("facepp")
    private var mHandler: Handler? = null
    private var facepp: Facepp? = null
    private var min_face_size = 200
    private var detection_interval = 25
    private var sensorUtil: SensorEventUtil? = null
    private var carmeraImgData: ByteArray? = null
    private var Angle: Int = 0
    private var mTextureID = -1
    private var mSurface: SurfaceTexture? = null
    private var mCameraMatrix: CameraMatrix? = null
    private var mPointsMatrix: PointsMatrix? = null
    private val mMVPMatrix = FloatArray(16)
    private val mProjMatrix = FloatArray(16)
    private val mVMatrix = FloatArray(16)
    private var pitch: Float = 0.toFloat()
    private var yaw:Float = 0.toFloat()
    private var roll:Float = 0.toFloat()
    private var rotation = Angle


    override fun afterSetContentView(savedInstanceState: Bundle?) {
        receivePhone = intent.extras?.getString(REQUEST_PHONE_KEY) ?: ""
        setDefaultLogo()
        login_edit_username_id.setText(receivePhone)
        tv_login_copyright.text = getString(R.string.copy_right).plus(" ")
                .plus(DateHelper.nowByFormate("yyyy")).plus(" ")
                .plus(getString(R.string.app_name_about)).plus(" ")
                .plus(getString(R.string.reserved))
        login_edit_username_id.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                image_login_icon_name.setImageDrawable(FancySkinManager.instance().getDrawable(this, R.mipmap.icon_user_focus))
                view_login_username_bottom.setBackgroundColor(FancySkinManager.instance().getColor(this, R.color.z_color_input_line_focus))
            }else {
                image_login_icon_name.setImageDrawable(FancySkinManager.instance().getDrawable(this, R.mipmap.icon_user_normal))
                view_login_username_bottom.setBackgroundColor(FancySkinManager.instance().getColor(this, R.color.z_color_input_line_blur))
            }
        }
        login_edit_password_id.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                image_login_icon_password.setImageDrawable(FancySkinManager.instance().getDrawable(this, R.mipmap.icon_verification_code_focus))
                view_login_password_bottom.setBackgroundColor(FancySkinManager.instance().getColor(this, R.color.z_color_input_line_focus))
            }else {
                image_login_icon_password.setImageDrawable(FancySkinManager.instance().getDrawable(this, R.mipmap.icon_verification_code_normal))
                view_login_password_bottom.setBackgroundColor(FancySkinManager.instance().getColor(this, R.color.z_color_input_line_blur))
            }
        }

        facePPAuthCheck()
        initFaceppUI()

        btn_login_submit.setOnClickListener(this)
        btn_login_facepp.setOnClickListener(this)
        login_facepp_back.setOnClickListener(this)


        if (BuildConfig.InnerServer) {
            login_edit_password_id.setHint(R.string.activity_login_password)
            login_edit_password_id.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            button_login_phone_code.gone()
            tv_rebind_btn.gone()
        }else {
            val unit = O2SDKManager.instance().prefs().getString(O2.PRE_CENTER_HOST_KEY, "")
            if ("dev.o2oa.io" == unit || "dev.o2oa.net" == unit || "dev.o2server.io" == unit) {
                btn_login_facepp.visible()
            }else {
                btn_login_facepp.gone()
            }
            login_edit_password_id.setHint(R.string.login_code)
            login_edit_password_id.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_NORMAL
            button_login_phone_code.visible()
            button_login_phone_code.setOnClickListener(this)
            tv_rebind_btn.visible()
            tv_rebind_btn.setOnClickListener(this)
        }

        initScaleAnimation()
    }


    override fun layoutResId(): Int = R.layout.activity_login


    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        event?.let {
            if (it.keyCode == KeyEvent.KEYCODE_ENTER && it.action == KeyEvent.ACTION_DOWN) {
                if (login_edit_password_id.hasFocus()) {
                    /*隐藏软键盘*/
                    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    if (inputMethodManager.isActive) {
                        inputMethodManager.hideSoftInputFromWindow(this@LoginActivity.currentFocus!!.windowToken, 0)
                    }
                    submitLogin()
                    return true
                }
            }
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onResume() {
        super.onResume()

        playBeep = true
        val audioService = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (audioService.ringerMode != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false
        }
        initBeepSound()
    }

    override fun onStop() {
        super.onStop()
        stopCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownHelper.destroy()
        mHandler?.post { facepp?.release() }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_login_submit -> {
                submitLogin()
            }
            R.id.button_login_phone_code -> {
                getVerificationCode()
            }
            R.id.tv_rebind_btn -> {
                O2DialogSupport.openConfirmDialog(this@LoginActivity, "确定要重新绑定手机号码吗？", { _ ->
                    reBindService()
                })
            }
            R.id.login_facepp_back -> {
                login_facepp.startAnimation(scale0)
            }
            R.id.btn_login_facepp -> {
                clickStartFaceRecognize()
            }
        }
    }

    private fun clickStartFaceRecognize() {
        PermissionRequester(this).request(Manifest.permission.CAMERA)
                .o2Subscribe {
                    onNext {(granted, shouldShowRequestPermissionRationale, deniedPermissions) ->
                       XLog.debug("granted:$granted, show:$shouldShowRequestPermissionRationale, deniedList:$deniedPermissions")
                        if (granted) {
                            if (faceppLicenseIsOK) {
                                login_main.startAnimation(scale0)
                            }else {
                                XToast.toastShort(this@LoginActivity, "非常抱歉，今日的试用次数已经用完了！")
                            }
                        }else {
                            O2DialogSupport.openAlertDialog(this@LoginActivity,
                                    "非常抱歉，摄像头权限没有开启，马上去设置",
                                    { AndroidUtils.gotoSettingApplication(this@LoginActivity) })
                        }
                    }
                    onError { e, isNetworkError ->
                        XLog.error("检查权限异常，$isNetworkError", e)
                        XToast.toastShort(this@LoginActivity, "非常抱歉，无法启动摄像头！")
                    }
                }
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)// 清除屏幕和深度缓存
        val mtx = FloatArray(16)
        mSurface?.getTransformMatrix(mtx)
        mCameraMatrix?.draw(mtx)
        // Set the camera position (View matrix)
        Matrix.setLookAtM(mVMatrix, 0, 0f, 0f, -3f, 0f, 0f, 0f, 0f, 1f, 0f)
        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0)
        mPointsMatrix?.draw(mMVPMatrix)
        mSurface?.updateTexImage()// 更新image，会调用onFrameAvailable方法
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        XLog.debug("onSurfaceChanged.....w:$width, h:$height")
        // 设置画面的大小
        GLES20.glViewport(0, 0, width, height)
        var ratio = 1f // 这样OpenGL就可以按照屏幕框来画了，不是一个正方形了
        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1f, 1f, 3f, 7f)
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        XLog.debug("onSurfaceCreated...............")
        // 黑色背景
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        surfaceInit()
    }

    override fun onPreviewFrame(imgData: ByteArray?, camera: Camera?) {
        XLog.debug("onPreviewFrame...............这是获取每帧数据的地方。。")
        //检测操作放到主线程，防止贴点延迟
        val width = mICamera?.cameraWidth ?: 640
        val height = mICamera?.cameraHeight ?: 480

        val orientation = sensorUtil?.orientation
        //0.4.7之前（包括）jni把所有角度的点算到竖直的坐标，所以外面画点需要再调整回来，才能与其他角度适配
        //目前getLandmarkOrigin会获得原始的坐标，所以只需要横屏适配好其他的角度就不用适配了，因为texture和preview的角度关系是固定的
        when (orientation) {
            0 -> rotation = Angle
            1 -> rotation = 0
            2 -> rotation = 180
            3 -> rotation = 360 - Angle
        }

        setConfig(rotation)

        val faces = facepp?.detect(imgData, width, height, Facepp.IMAGEMODE_NV21)
        if (faces != null) {
            XLog.debug("faces size." + faces.size)
            val pointsOpengl = ArrayList<ArrayList<*>>()
            if (faces.isNotEmpty()) {
                for (c in faces.indices) {
                    if (is106Points)
                        facepp?.getLandmarkRaw(faces[c], Facepp.FPP_GET_LANDMARK106)
                    else
                        facepp?.getLandmarkRaw(faces[c], Facepp.FPP_GET_LANDMARK81)

                    pitch = faces[c].pitch
                    yaw = faces[c].yaw
                    roll = faces[c].roll


                    //0.4.7之前（包括）jni把所有角度的点算到竖直的坐标，所以外面画点需要再调整回来，才能与其他角度适配
                    //目前getLandmarkOrigin会获得原始的坐标，所以只需要横屏适配好其他的角度就不用适配了，因为texture和preview的角度关系是固定的
                    val triangleVBList = ArrayList<FloatBuffer>()
                    for (i in faces[c].points.indices) {
                        var x = faces[c].points[i].x / width * 2 - 1
                        if (isBackCamera)
                            x = -x
                        val y = faces[c].points[i].y / height * 2 - 1
                        val pointf = floatArrayOf(y, x, 0.0f)
                        val fb = mCameraMatrix?.floatBufferUtil(pointf)
                        if (fb != null) {
                            triangleVBList.add(fb)
                        }
                    }
                    pointsOpengl.add(triangleVBList)
                }

                //开始在线识别
                val data = imgData!!
                mPresenter.searchFace(data, faces, mICamera!!, isBackCamera)

            } else {
                pitch = 0.0f
                yaw = 0.0f
                roll = 0.0f
            }

            /**
             * 画人脸特征点
             */
            if (mPointsMatrix!=null) {
                synchronized(mPointsMatrix!!) {
                    mPointsMatrix!!.bottomVertexBuffer = null
                    mPointsMatrix!!.points = pointsOpengl
//                mPointsMatrix.faceRects = rectsOpengl
                }
            }
        }

    }

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        XLog.debug("onFrameAvailable。。。。。。。。。。。。。。")
        mGlSurfaceView?.requestRender()
    }

    override fun loginSuccess(data: AuthenticationInfoJson) {
        if (login_facepp.visibility == View.VISIBLE) {
            playBeepSound()
        }
        hideLoadingDialog()
        O2SDKManager.instance().setCurrentPersonData(data)
        O2App.instance._JMLoginInner()
        goThenKill<MainActivity>()
    }

    override fun loginFail() {
        XToast.toastShort(this,  "登录失败, 请检查您的验证码是否输入正确！")
        hideLoadingDialog()
    }

    override fun getCodeError() {
        XToast.toastShort(this, "获取手机验证码失败，请检查您输入的用户名是否正确！")
    }

    private fun submitLogin() {
        val credential = login_edit_username_id.text.toString()
        val code = login_edit_password_id.text.toString()
        if (TextUtils.isEmpty(credential)) {
            XToast.toastShort(this, "用户名或手机号码 不能为空！")
            return
        }
        if (TextUtils.isEmpty(code)) {
            val label = if(BuildConfig.InnerServer){
                getString(R.string.login_code)
            }else {
                getString(R.string.activity_login_password)
            }
            XToast.toastShort(this, "$label 不能为空！")
            return
        }
        showLoadingDialog()
        if (BuildConfig.InnerServer) {
            mPresenter.loginByPassword(credential, code)
        }else {
            mPresenter.login(credential, code)
        }
    }


    private fun getVerificationCode() {
        val credential = login_edit_username_id.text.toString()
        if (TextUtils.isEmpty(credential)) {
            XToast.toastShort(this, "请输入用户名！")
            return
        }
        // 发送验证码
        mPresenter.getVerificationCode(credential)
        countDownHelper.start()
        //焦点跳转到验证码上面
        login_edit_password_id.isFocusable = true
        login_edit_password_id.isFocusableInTouchMode = true
        login_edit_password_id.requestFocus()
        login_edit_password_id.requestFocusFromTouch()
    }

    private fun reBindService() {
        O2SDKManager.instance().clearBindUnit()
        goThenKill<BindPhoneActivity>()
    }



    /**
     * 设置logo
     */
    private fun setDefaultLogo() {
        val path = O2CustomStyle.loginAvatarImagePath(this@LoginActivity)
        if (!TextUtils.isEmpty(path)) {
            BitmapUtil.setImageFromFile(path!!, image_login_logo)
        }
    }
    /**
     * 切换动画
     */
    private fun initScaleAnimation() {
        scale0.duration = 500
        scale1.duration = 500
        scale0.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                if (login_main.visibility == View.VISIBLE) {
                    login_main.animation = null
                    login_main.gone()
                    login_facepp.visible()
                    login_facepp.startAnimation(scale1)
                } else {
                    login_facepp.animation = null
                    login_facepp.gone()
                    login_main.visible()
                    login_main.startAnimation(scale1)
                }
            }

            override fun onAnimationStart(animation: Animation?) {
            }
        })
        scale1.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                //判断是开启人脸识别还是关闭 这个可以在 onAnimationEnd中进行
                if (login_main.visibility == View.VISIBLE) {
                    stopCamera()
                }else {
                    startCamera()
                }
            }

            override fun onAnimationStart(animation: Animation?) {
            }
        })
    }


    //刷脸登录///////////////////////////////////////////////////////////



    /**
     * 开启摄像头
     */
    private fun startCamera() {
        ConUtil.acquireWakeLock(this)
        //设置相机 比如分辨率
        mCamera = mICamera?.openCamera(isBackCamera, this)
        if (mCamera != null) {
            Angle = 360 - (mICamera?.Angle?:0)
            if (isBackCamera)
                Angle = (mICamera?.Angle?:0)

            val params = mICamera?.layoutParam
            mGlSurfaceView?.layoutParams = params

            val width = mICamera?.cameraWidth
            val height = mICamera?.cameraHeight

            val left = 0
            val top = 0

            val errorCode = facepp?.init(this, ConUtil.getFileContent(this, R.raw.megviifacepp_0_5_2_model), if (isOneFaceTrackig) 1 else 0)
            XLog.info("facepp init errorCode:$errorCode")

            val faceppConfig = facepp?.faceppConfig
            faceppConfig?.interval = detection_interval
            faceppConfig?.minFaceSize = min_face_size
            faceppConfig?.roi_left = left
            faceppConfig?.roi_top = top
            faceppConfig?.roi_right = width
            faceppConfig?.roi_bottom = height
            val array = resources.getStringArray(R.array.login_facepp_trackig_mode_array)
            when (trackModel) {
                array[0] -> faceppConfig?.detectionMode = Facepp.FaceppConfig.DETECTION_MODE_TRACKING_FAST
                array[1] -> faceppConfig?.detectionMode = Facepp.FaceppConfig.DETECTION_MODE_TRACKING_ROBUST
                array[2] -> faceppConfig?.detectionMode = Facepp.FaceppConfig.MG_FPP_DETECTIONMODE_TRACK_RECT
            }
            facepp?.faceppConfig = faceppConfig

            val version = Facepp.getVersion()
            XLog.debug("Facepp:version:$version")

            //启动
            mICamera?.startPreview(mSurface)// 设置预览容器
            mICamera?.actionDetect(this)

        } else {
            XToast.toastShort(this, "打开相机失败")
        }
    }

    /**
     * 关闭摄像头
     */
    private fun stopCamera() {
        ConUtil.releaseWakeLock()
        mICamera?.closeCamera()
        mCamera = null
    }

    private fun surfaceInit() {
        mTextureID = OpenGLUtil.createTextureID()
        mSurface = SurfaceTexture(mTextureID)
        // 这个接口就干了这么一件事，当有数据上来后会进到onFrameAvailable方法
        mSurface?.setOnFrameAvailableListener(this)// 设置照相机有数据时进入
        mCameraMatrix = CameraMatrix(mTextureID)
        mPointsMatrix = PointsMatrix(false)
        mPointsMatrix?.isShowFaceRect = false
    }

    private fun setConfig(rotation: Int) {
        val faceppConfig = facepp?.faceppConfig
        if (faceppConfig != null && faceppConfig.rotation != rotation) {
            faceppConfig.rotation = rotation
            facepp!!.faceppConfig = faceppConfig
        }
    }

    /**
     * 初始化 人脸识别相关的参数
     */
    private fun initFaceppUI() {
        // 人脸识别参数设置
        is106Points = false // 81 还 106
        isBackCamera = false //是否后置摄像头 默认用前置
        isOneFaceTrackig = true //单脸跟踪
        trackModel = "Fast" //3种模式： Fast Robust Tracking_Rect
        min_face_size = 40 // 33 --- 2147483647
        detection_interval = 30 //毫秒

        facepp = Facepp()
        sensorUtil = SensorEventUtil(this)
        mHandlerThread.start()
        mHandler = Handler(mHandlerThread.looper)

        mGlSurfaceView = findViewById(R.id.login_facepp_surfaceview)
        mGlSurfaceView?.setEGLContextClientVersion(2)// 创建一个OpenGL ES 2.0
        // context
        mGlSurfaceView?.setRenderer(this)// 设置渲染器进入gl
        // RENDERMODE_CONTINUOUSLY不停渲染
        // RENDERMODE_WHEN_DIRTY懒惰渲染，需要手动调用 glSurfaceView.requestRender() 才会进行更新
        mGlSurfaceView?.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY// 设置渲染器模式
        mGlSurfaceView?.setOnClickListener { autoFocus() }
        mICamera = ICamera()
    }

    /**
     * 自动对焦
     */
    private fun autoFocus() {
        if (mCamera != null && isBackCamera) {
            mCamera?.cancelAutoFocus()
            val parameters = mCamera?.parameters
            parameters?.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
            mCamera?.parameters = parameters
            mCamera?.autoFocus(null)
        }
    }


    private fun facePPAuthCheck() {
        val licenseManager = LicenseManager(this)
        val uuid = ConUtil.getUUIDString(this)
        val apiName = Facepp.getApiName()
        licenseManager.setAuthTimeBufferMillis(0)
        licenseManager.takeLicenseFromNetwork(Util.CN_LICENSE_URL, uuid, Util.API_KEY, Util.API_SECRET, apiName,
                "1", object : LicenseManager.TakeLicenseCallback {
            override fun onSuccess() {
                authState(true, 0, "")
            }

            override fun onFailed(i: Int, bytes: ByteArray?) =
                    if (TextUtils.isEmpty(Util.API_KEY) || TextUtils.isEmpty(Util.API_SECRET)) {
                        if (!ConUtil.isReadKey(this@LoginActivity)) {
                            authState(false, 1001, "")
                        } else {
                            authState(false, 1001, "")
                        }
                    } else {
                        var msg = ""
                        if (bytes != null && bytes.isNotEmpty()) {
                            msg = String(bytes)
                        }
                        authState(false, i, msg)
                    }
        })
    }

    private fun authState(isSuccess: Boolean, code: Int, message: String) {
        XLog.info("Facepp  License  check $isSuccess, code:$code, message:$message")
        faceppLicenseIsOK = isSuccess
    }



    ///////////////play media////////////
    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private val beepListener = MediaPlayer.OnCompletionListener { mediaPlayer -> mediaPlayer.seekTo(0) }

    private fun initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            volumeControlStream = AudioManager.STREAM_MUSIC
            mediaPlayer = MediaPlayer()
            mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer?.setOnCompletionListener(beepListener)
            val file = resources.openRawResourceFd(
                    R.raw.beep)
            try {
                mediaPlayer?.setDataSource(file.fileDescriptor,
                        file.startOffset, file.length)
                file.close()
                mediaPlayer?.setVolume(0.90f, 0.90f)
                mediaPlayer?.prepare()
            } catch (e: IOException) {
                mediaPlayer = null
            }
        }
    }

    private fun playBeepSound() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer?.start()
        }
    }



}