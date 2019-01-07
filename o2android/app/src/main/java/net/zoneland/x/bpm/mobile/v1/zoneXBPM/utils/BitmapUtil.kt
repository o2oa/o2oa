package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils

import android.content.res.Resources
import android.graphics.*
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_login.*
import java.io.ByteArrayOutputStream
import java.io.InputStream

/**
 * Created by FancyLou on 2015/12/5.
 */
object BitmapUtil {


    private val OPENGL_LARGEST_PX = 4096


    /**
     * Mix two Bitmap as one.
     *
     * @param first
     * @param second
     * @param fromPoint
     * where the second bitmap is painted.
     * @return
     */
    fun mixtureBitmap(first: Bitmap?, second: Bitmap?,
                      fromPoint: PointF?): Bitmap? {
        if (first == null || second == null || fromPoint == null) {
            return null
        }
        val newBitmap = Bitmap.createBitmap(first.width,
                first.height, Bitmap.Config.ARGB_8888)
        val cv = Canvas(newBitmap)
        cv.drawBitmap(first, 0f, 0f, null)
        cv.drawBitmap(second, fromPoint.x, fromPoint.y, null)
        cv.save(Canvas.ALL_SAVE_FLAG)
        cv.restore()
        return newBitmap
    }


    /**
     * 合并水印图片到原图上
     * @param src
     * @param watermark
     * @return
     */
    fun mergeWatermark2SourcePic(src: Bitmap?, watermark: Bitmap?): Bitmap? {
        if (src == null) {
            XLog.error("mergeWatermark2SourcePic src is null!")
            return null
        }
        if (watermark == null) {
            XLog.error("mergeWatermark2SourcePic watermark is null!")
            return src
        }
        val w = src.width
        val h = src.height
        val ww = watermark.width
        val wh = watermark.height
        // create the new blank bitmap
        val newb = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)// 创建一个新的和SRC长度宽度一样的位图
        val cv = Canvas(newb)
        // draw src into
        cv.drawBitmap(src, 0f, 0f, null)// 在 0，0坐标开始画入src
        // draw watermark into
        // 在src的右下角画watermark
        cv.drawBitmap(watermark, (w - ww - 5).toFloat(), (h - wh - 5).toFloat(), null)//设置watermark的位置
        // cv.drawBitmap(watermark, 0, 0, null);
        // save all clip
        cv.save(Canvas.ALL_SAVE_FLAG)// 保存
        // store
        cv.restore()// 存储
        return newb
    }


    /**
     * 图片根据传入的大小进行缩放
     *
     * @param bitmap
     * @param width
     * @param height
     * @return
     */
    fun zoomBitmap(bitmap: Bitmap, width: Int, height: Int): Bitmap {
        val w = bitmap.width
        val h = bitmap.height
        val matrix = Matrix()
        val scaleWidth = width.toFloat() / w
        val scaleHeight = height.toFloat() / h
        matrix.postScale(scaleWidth, scaleHeight)// 利用矩阵进行缩放
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true)
    }

    /**
     * 如果Bitmap 图片的分辨率大于 4096
     * 根据原图的大小比例进行压缩
     *
     * @param bitmap
     * @return
     */
    fun zoomBitmapLess4096(bitmap: Bitmap): Bitmap {
        val w = bitmap.width
        val h = bitmap.height
        var newW = 0
        var newH = 0
        if (w > OPENGL_LARGEST_PX || h > OPENGL_LARGEST_PX) {
            XLog.debug("zoomBitmapLess4096,w:$w,h:$h")
            if (w > h) {
                newW = OPENGL_LARGEST_PX
                val floatH = h.toFloat() * (OPENGL_LARGEST_PX.toFloat() / w.toFloat())
                newH = floatH.toInt()
            } else {
                newH = OPENGL_LARGEST_PX
                val floatW = w.toFloat() * (OPENGL_LARGEST_PX.toFloat() / h.toFloat())
                newW = floatW.toInt()
            }
            XLog.debug("zoomBitmapLess4096, newW:$newW,newH:$newH")
            return zoomBitmap(bitmap, newW, newH)
        }
        return bitmap
    }

    /**
     *
     */
    fun setImageFromFile(filePath: String, into: ImageView) {
        val w = into.width
        val h = into.height
        val bitmap = if (w == 0 || h == 0) {
            BitmapFactory.decodeFile(filePath)
        }else {
            getFitSampleBitmap(filePath, w, h)
        }
        if (bitmap!=null)  {
            into.setImageBitmap(bitmap)
        }

    }


    /**
     * 根据传入宽高 获取图片的Bitmap
     *
     * 防止图片太大OOM
     *
     * @param filePath
     * @param width
     * @param height
     * @return
     */
    fun getFitSampleBitmap(filePath: String, width: Int, height: Int): Bitmap? {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true//true 表示不会生成bitmap对象，只是将图片的相关参数读取到options里面去
        BitmapFactory.decodeFile(filePath, options)
        options.inSampleSize = getFitInSampleSize(width, height, options)//inSampleSize是图片的缩放比例，如 inSampleSize=1 表示decode后图片像素是原图的1/2
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(filePath, options)
    }

    /**
     * 根据传入宽高 获取资源图片的Bitmap
     * 防止图片太大OOM
     * @param resources
     * @param resId
     * @param width
     * @param height
     * @return
     */
    fun getFitSampleBitmap(resources: Resources, resId: Int, width: Int, height: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(resources, resId, options)
        options.inSampleSize = getFitInSampleSize(width, height, options)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeResource(resources, resId, options)
    }

    /**
     * 根据传入宽高 获取图片的Bitmap
     * 防止图片太大OOM
     *
     * @param inputStream
     * @param width
     * @param height
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    fun getFitSampleBitmap(inputStream: InputStream, width: Int, height: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        val bytes = readStream(inputStream)
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
        options.inSampleSize = getFitInSampleSize(width, height, options)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
    }

    /**
     * 获取图片信息 不生存Bitmap
     * @param filePath
     * @return
     */
    fun getImageOptions(filePath: String): BitmapFactory.Options {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true//true 表示不会生成bitmap对象，只是将图片的相关参数读取到options里面去
        BitmapFactory.decodeFile(filePath, options)
        return options
    }


    fun getFitInSampleSize(reqWidth: Int, reqHeight: Int, options: BitmapFactory.Options): Int {
        var inSampleSize = 1
        if (options.outWidth > reqWidth || options.outHeight > reqHeight) {
            val widthRatio = Math.round(options.outWidth.toFloat() / reqWidth.toFloat())
            val heightRatio = Math.round(options.outHeight.toFloat() / reqHeight.toFloat())
            inSampleSize = Math.min(widthRatio, heightRatio)
        }
        return inSampleSize
    }

    /*
     * 从inputStream中获取字节流 数组大小
	 * */
    @Throws(Exception::class)
    fun readStream(inStream: InputStream): ByteArray {
        val outStream = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        var len = 0
        do {
            len = inStream.read(buffer)
            if (len!=-1) {
                outStream.write(buffer, 0, len)
            }
        }while (len != -1)

        outStream.close()
        inStream.close()
        return outStream.toByteArray()
    }
}
