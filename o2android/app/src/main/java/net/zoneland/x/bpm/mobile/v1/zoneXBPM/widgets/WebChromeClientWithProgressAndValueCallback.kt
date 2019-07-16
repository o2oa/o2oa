package net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.ProgressBar
import net.muliba.fancyfilepickerlibrary.PicturePicker
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.FileExtensionHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.FileUtil
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.permission.PermissionRequester
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.O2DialogSupport
import org.jetbrains.anko.dip
import java.io.File

/**
 * Created by fancyLou on 2019-04-29.
 * Copyright © 2019 O2. All rights reserved.
 */


class WebChromeClientWithProgressAndValueCallback private constructor (val activity: Activity?) : WebChromeClient() {

    companion object {
        const val TAKE_FROM_CAMERA_KEY = 100999
        const val TAKE_FROM_PICTURES_KEY = 100998
        fun with(activity: Activity): WebChromeClientWithProgressAndValueCallback =
                WebChromeClientWithProgressAndValueCallback(activity)
        fun with(fragment: Fragment): WebChromeClientWithProgressAndValueCallback =
                WebChromeClientWithProgressAndValueCallback(fragment.activity)
    }

    private var uploadMessageAboveL: ValueCallback<Array<Uri>>? = null
    private var cameraImageUri: Uri? = null

    var progressBar: ProgressBar? = null


    init {
        progressBar = ProgressBar(activity, null, android.R.attr.progressBarStyleHorizontal)
        progressBar?.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, activity?.dip(2)?:10, Gravity.TOP)
        val drawable = ContextCompat.getDrawable(activity, R.drawable.web_view_progress_bar)
        progressBar?.progressDrawable = drawable
        if (activity != null) {
            cameraImageUri = FileUtil.getUriFromFile(activity, File(FileExtensionHelper.getCameraCacheFilePath()))
        }
    }

    override fun onProgressChanged(view: WebView, newProgress: Int) {
        if (newProgress == 100) {
            progressBar?.visibility = View.GONE
        } else {
            if (progressBar?.visibility == View.GONE)
                progressBar?.visibility = View.VISIBLE
            progressBar?.progress = newProgress
        }
        super.onProgressChanged(view, newProgress)
    }


    // For Android >= 5.0
    override fun onShowFileChooser(webView: WebView, filePathCallback: ValueCallback<Array<Uri>>, fileChooserParams: WebChromeClient.FileChooserParams): Boolean {
        XLog.debug("选择文件 5。0。。。。。。。。。。。。。。。。。")
        uploadMessageAboveL = filePathCallback
        showPictureChooseMenu()
        return true
    }


    /**
     * 接收activity返回的数据
     * @return true已经处理 false没有处理
     */
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (resultCode == Activity.RESULT_OK) {
            when(requestCode) {
                TAKE_FROM_PICTURES_KEY -> {
                    //选择照片
                    data?.let {
                        val result = it.extras.getString(PicturePicker.FANCY_PICTURE_PICKER_SINGLE_RESULT_KEY, "")
                        if (!TextUtils.isEmpty(result)) {
                            XLog.debug("照片 path:$result")
                            if (uploadMessageAboveL != null && activity!=null)   {
                                val uri = FileUtil.getUriFromFile(activity, File(result))
                                val list = ArrayList<Uri>()
                                list.add(uri)
                                uploadMessageAboveL?.onReceiveValue(list.toTypedArray())
                            }
                        }
                    }
                    return true
                }
                TAKE_FROM_CAMERA_KEY -> {
                    //拍照
                    XLog.debug("拍照//// ")
                    if (uploadMessageAboveL != null && cameraImageUri!=null)   {
                        val list = ArrayList<Uri>()
                        list.add(cameraImageUri!!)
                        uploadMessageAboveL?.onReceiveValue(list.toTypedArray())
                    }
                    return true
                }
            }
        }
        return false
    }


    private fun showPictureChooseMenu() {
        if (activity != null) {
            BottomSheetMenu(activity)
                    .setTitle("上传照片")
                    .setItem("从相册选择", activity.resources.getColor(R.color.z_color_text_primary)) {
                        takeFromPictures()
                    }
                    .setItem("拍照", activity.resources.getColor(R.color.z_color_text_primary)) {
                        takeFromCamera()
                    }
                    .setCancelButton("取消", activity.resources.getColor(R.color.z_color_text_hint)) {
                        XLog.debug("取消。。。。。")
                        if (uploadMessageAboveL!=null) {
                            uploadMessageAboveL?.onReceiveValue(null)
                        }
                    }
                    .show()
        }else {
            XLog.error("activity 不存在， 无法打开dialog菜单!")
        }

    }


    private fun takeFromPictures() {
        if (activity != null) {
            PicturePicker()
                    .withActivity(activity)
                    .chooseType(PicturePicker.CHOOSE_TYPE_SINGLE)
                    .requestCode(TAKE_FROM_PICTURES_KEY)
                    .start()
        }else {
            XLog.error("activity 不存在， 无法打开图片选择器!")
        }
    }

    private fun takeFromCamera() {
        if (activity != null) {
            PermissionRequester(activity).request(Manifest.permission.CAMERA)
                    .o2Subscribe {
                        onNext { (granted, shouldShowRequestPermissionRationale, deniedPermissions) ->
                            XLog.info("granted:$granted , shouldShowRequest:$shouldShowRequestPermissionRationale, denied:$deniedPermissions")
                            if (!granted) {
                                O2DialogSupport.openAlertDialog(activity, "非常抱歉，相机权限没有开启，无法使用相机！")
                            } else {
                                openCamera()
                            }
                        }
                    }
        }else {
            XLog.error("activity 不存在， 无法打开拍照功能!")
        }
    }
    private fun openCamera() {
        if (activity != null) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            //return-data false 不是直接返回拍照后的照片Bitmap 因为照片太大会传输失败
            intent.putExtra("return-data", false)
            //改用Uri 传递
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
            intent.putExtra("noFaceDetection", true)
            activity.startActivityForResult(intent, TAKE_FROM_CAMERA_KEY)
        }else {
            XLog.error("activity 不存在， 无法打开拍照功能!")
        }
    }


}