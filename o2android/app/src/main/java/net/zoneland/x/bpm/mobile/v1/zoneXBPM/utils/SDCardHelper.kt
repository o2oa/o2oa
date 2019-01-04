package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils

import android.graphics.Bitmap
import android.os.Environment
import android.text.TextUtils
import java.io.*

/**
 * Created by FancyLou on 2015/10/20.
 */
object SDCardHelper {

    /**
     * 判断SDCard是否存在 [当没有外挂SD卡时，内置ROM也被识别为存在sd卡]
     *
     * @return
     */
    val isSdCardExist: Boolean
        get() = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

    /**
     * 获取SD卡根目录路径
     *
     * @return
     */
    val sdCardPath: String?
        get() {
            val exist = isSdCardExist
            var sdpath: String? = null
            if (exist) {
                sdpath = Environment.getExternalStorageDirectory()
                        .absolutePath
            }
            return sdpath

        }

    /**
     * 写入png文件
     *
     * @param bitmap
     * @param filePath 如：/temp/abc.png
     * @return
     */
    fun bitmapToPNGFile(bitmap: Bitmap, filePath: String): Boolean {
        return bitmapToFile(bitmap, Bitmap.CompressFormat.PNG, filePath)
    }

    /**
     * 写入jpg文件
     *
     * @param bitmap
     * @param filePath 如：/temp/abc.jpg
     * @return
     */
    fun bitmapToJPGFile(bitmap: Bitmap, filePath: String): Boolean {
        return bitmapToFile(bitmap, Bitmap.CompressFormat.JPEG, filePath)
    }

    /**
     * 图片写入文件
     *
     * @param bitmap
     * 图片
     * @param format
     * 类型 png jpg webp
     * @param filePath
     * 文件全路径
     * @return 是否写入成功
     */
    fun bitmapToFile(bitmap: Bitmap?, format: Bitmap.CompressFormat, filePath: String): Boolean {
        var isSuccess = false
        if (bitmap == null) {
            return isSuccess
        }
        val file = File(filePath.substring(0,
                filePath.lastIndexOf(File.separator)))
        if (!file.exists()) {
            file.mkdirs()
        }

        var out: OutputStream? = null
        try {
            out = BufferedOutputStream(FileOutputStream(filePath),
                    8 * 1024)
            isSuccess = bitmap.compress(format, 100, out)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } finally {
            if (out!=null) {
                closeIO(out)
            }

        }
        return isSuccess
    }


    /**
     * 将文件保存到本地
     */
    fun saveFile(fileData: ByteArray, folderPath: String,
                 fileName: String) {
        val folder = File(folderPath)
        if (!folder.exists()) {
            folder.mkdirs()
        }
        val file = File(folderPath, fileName)
        val `is` = ByteArrayInputStream(fileData)
        var os: OutputStream? = null
        if (!file.exists()) {
            try {
                file.createNewFile()
                os = FileOutputStream(file)
                val buffer = ByteArray(1024)
                var len = 0
                do {
                    len = `is`.read(buffer)
                    if (len != -1) {
                        os.write(buffer, 0, len)
                    }
                } while (len !== -1)
                os.flush()
            } catch (e: Exception) {
                throw RuntimeException(
                        SDCardHelper::class.java.javaClass.name, e)
            } finally {
                if (os!=null) {
                    closeIO(`is`, os)
                }
            }
        }
    }

    fun makeDir(filePath: String?):Boolean {
        if (TextUtils.isEmpty(filePath)) {
            return false
        }
        val folder = File(filePath)
        return if (!folder.exists()) {
            folder.mkdirs()
        }else{
            false
        }
    }
    /**
     * 生成一个空文件
     * @param filePath
     * @return
     */
    fun generateNewFile(filePath: String?): Boolean {
        if (filePath == null || "" == filePath) {
            return false
        }
        val folderPath = filePath.substring(0, filePath.lastIndexOf(File.separator))
        XLog.debug("SDCardHelper ,folder:" + folderPath)
        val folder = File(folderPath)
        if (!folder.exists()) {
            folder.mkdirs()
        }
        val file = File(filePath)
        if (!file.exists()) {
            try {
                file.createNewFile()
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }

        }

        return true

    }


    /**
     * 关闭流
     *
     * @param closeables
     */
    fun closeIO(vararg closeables: Closeable) {
        if (null == closeables || closeables.size <= 0) {
            return
        }
        for (cb in closeables) {
            try {
                if (null == cb) {
                    continue
                }
                cb.close()
            } catch (e: IOException) {
                throw RuntimeException(
                        SDCardHelper::class.java.javaClass.name, e)
            }

        }
    }
}
