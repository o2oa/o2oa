package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.clouddrive.viewer

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.TextUtils
import android.view.ViewGroup
import android.widget.ImageView
import kotlinx.android.synthetic.main.fragment_picture_viewer.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPViewPagerFragment
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.enums.APIDistributeTypeEnum
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import rx.Observable
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File


class CloudDrivePictureViewerFragment : BaseMVPViewPagerFragment<CloudDrivePictureViewerContract.View, CloudDrivePictureViewerContract.Presenter>(), CloudDrivePictureViewerContract.View {

    override var mPresenter: CloudDrivePictureViewerContract.Presenter = CloudDrivePictureViewerPresenter()

    override fun layoutResId(): Int = R.layout.fragment_picture_viewer

    companion object {
        val FILE_ID_KEY = "FILE_ID_KEY"
        val FILE_NAME_KEY = "FILE_NAME_KEY"
    }

    var fileId = ""
    var fileName = ""

    override fun initUI() {
        fileId = arguments?.getString(FILE_ID_KEY) ?: ""
        fileName = arguments?.getString(FILE_NAME_KEY) ?: ""
        if (TextUtils.isEmpty(fileId) || TextUtils.isEmpty(fileName)) {
            XToast.toastShort(activity, "没有传入必要的参数，无法展现图片！")
        }else {
            circle_progress_fragment_picture_view.visible()
            zoomImage_fragment_picture_view.visible()

            val path = FileExtensionHelper.getXBPMTempFolder()+ File.separator + fileName
            XLog.debug("file path $path")
            val downloadUrl = APIAddressHelper.instance()
                    .getCommonDownloadUrl(APIDistributeTypeEnum.x_file_assemble_control, "jaxrs/attachment/$fileId/download/stream")
            O2FileDownloadHelper.download(downloadUrl, path)
                    .subscribeOn(Schedulers.io())
                    .flatMap {
                        var bitmap:Bitmap? = null
                        //压缩
                        val options = BitmapFactory.Options()
                        options.inJustDecodeBounds = true
                        val imageSize = getImageViewWidthAndHeight(zoomImage_fragment_picture_view)
                        val  newW = imageSize.width
                        val   newH = imageSize.height
                        XLog.debug("zoomBitmap, newW:$newW,newH:$newH")
                        options.inSampleSize = BitmapUtil.getFitInSampleSize(newW, newH, options)
                        options.inJustDecodeBounds = false
                        bitmap = BitmapFactory.decodeFile(path, options)
                        Observable.just(bitmap)
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Observer<Bitmap> {
                        override fun onError(e: Throwable?) {
                            XLog.error("的丁大丁大", e)
                            circle_progress_fragment_picture_view?.gone()
                        }

                        override fun onNext(bitmap: Bitmap?) {
                            if (bitmap!=null) {
                                zoomImage_fragment_picture_view?.setImageBitmap(bitmap)
                            }
                            circle_progress_fragment_picture_view?.gone()
                        }

                        override fun onCompleted() {
                        }

                    })

        }
    }

    override fun lazyLoad() {
    }


    private fun getImageViewWidthAndHeight(imageView: ImageView): ImageSize {
        val displayMetrics = imageView.context.resources.displayMetrics
        val params = imageView.layoutParams

        var width = if (params.width == ViewGroup.LayoutParams.WRAP_CONTENT)
            0
        else
            imageView.width // Get actual image width

        if (width <= 0)
            width = params.width // Get layout width parameter
        if (width <= 0)
            width = getImageViewFieldValue(imageView, "mMaxWidth") // Check
        // maxWidth
        // parameter
        if (width <= 0)
            width = displayMetrics.widthPixels
        var height = if (params.height == ViewGroup.LayoutParams.WRAP_CONTENT)
            0
        else
            imageView.height // Get actual image height
        if (height <= 0)
            height = params.height // Get layout height parameter
        if (height <= 0)
            height = getImageViewFieldValue(imageView, "mMaxHeight") // Check
        // maxHeight
        // parameter
        if (height <= 0)
            height = displayMetrics.heightPixels

        return ImageSize(width, height)

    }

    private fun getImageViewFieldValue(ob: ImageView, fieldName: String): Int {
        var value = 0
        try {
            val field = ImageView::class.java.getDeclaredField(fieldName)
            field.isAccessible = true
            val fieldValue = field.get(ob) as Int
            if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
                value = fieldValue

            }
        } catch (e: Exception) {
        }

        return value
    }


    data class ImageSize(var width:Int, var height:Int)
}
