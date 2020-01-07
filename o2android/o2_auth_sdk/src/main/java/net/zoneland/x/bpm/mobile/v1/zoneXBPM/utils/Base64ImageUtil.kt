package net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils

import android.text.TextUtils
import android.util.Base64
import android.util.Log
import java.io.*
import java.util.*


/**
 *
 *
 * Created by FancyLou on 2015/10/20.
 */
object Base64ImageUtil {

    private val LOG_TAG = "Base64ImageUtil"


    /**
     * 把userId进行DES加密
     * 人脸识别的时候 sso登录用的
     * 加密方式：
     *      userId#time组合DES加密，然后将字符串里面的 “+”换成“-”、“/”换成“_”、“=”换成“”
     */
    fun ssoUserIdDesCode(userId: String): String {
        val time = Date().time
        val code = "$userId#$time"
        Log.d(LOG_TAG,"初始code:$code")
        val key = "xplatform" //人脸识别登录的DES加密key
        val sutil = CryptDES.getInstance(key)
        var encode = ""
        try {
            encode = sutil.encryptBase64(code)
            Log.d(LOG_TAG,"加密后code:$encode")
            encode = encode.replace("+", "-")
            encode = encode.replace("/", "_")
            encode = encode.replace("=", "")
            Log.d(LOG_TAG,"替换特殊字符后的code:$encode")
        }catch (e: Exception) {
            Log.e(LOG_TAG,"加密失败", e)
        }
        return encode
    }


    /**
     * 解码base64编码的字符串，生成图片
     *
     *
     * @param imageFilePath  图片全路径 如：/temp/abc.png
     * @param base64Str base64编码后的字符串
     * @return
     */
    fun generateImage(imageFilePath: String?, base64Str: String?): Boolean {
        if (base64Str == null || "" == base64Str) {
            Log.e(LOG_TAG, "base64Str为空无法生成图片")
            return false
        }
        if (imageFilePath == null || "" == imageFilePath) {
            Log.e(LOG_TAG, "图片路径为空无法生成图片")
            return false
        }

        try {
            //Base64解码
            val b = Base64.decode(base64Str.toByteArray(), Base64.DEFAULT)
            b.indices
                    .filter { b[it] < 0 }
                    .forEach {
                        //调整异常数据
                        b[it].plus(256)
                    }
            //生成图片
            val out = FileOutputStream(imageFilePath)
            out.write(b)
            out.flush()
            out.close()
            return true
        } catch (e: Exception) {
            Log.e(LOG_TAG, "解码base64失败", e)
        }

        return false

    }

    fun generateBase642Inputstream(base64Str: String?): InputStream? {

        if (base64Str == null || "" == base64Str) {
            Log.e(LOG_TAG, "base64Str为空无法生成图片")
            return null
        }

        try {
            val b = Base64.decode(base64Str.toByteArray(), Base64.DEFAULT)
            b.indices
                    .filter { b[it] < 0 }
                    .forEach {
                        //调整异常数据
                        b[it].plus(256)
                    }
            return ByteArrayInputStream(b)
        } catch (e: Exception) {
            Log.e(LOG_TAG, "解码base64失败", e)
        }

        return null
    }


    /**
     * 将图片编码为base64字符串
     *
     * @param imageFilePath
     * @return
     */
    fun generateImage2Base64Str(imageFilePath: String?): String? {
        if (imageFilePath == null || "" == imageFilePath) {
            Log.e(LOG_TAG, "图片路径为空无法转化")
            return null
        }

        var `in`: InputStream? = null
        var data: ByteArray? = null
        try {
            val file = File(imageFilePath)
            if (!file.exists()) {
                Log.e(LOG_TAG, "该路径下的图片不存在，imageFilePath=" + imageFilePath)
                return null
            }
            `in` = FileInputStream(file)
            data = ByteArray(`in`.available())
            `in`.read(data)
            `in`.close()
        } catch (e: Exception) {
            Log.e(LOG_TAG, "读取图片失败", e)
        }

        return String(Base64.encode(data, Base64.DEFAULT))
    }


    /**
     * 将中文转化为base64编码
     * @param chinese
     * @return
     */
    fun generateChinese2Base64Str(chinese: String): String? {
        if (TextUtils.isEmpty(chinese)) {
            Log.e(LOG_TAG, "内容为空无法转化")
            return null
        }
        var data: ByteArray? = null
        try {
            data = chinese.toByteArray()
            return String(Base64.encode(data, Base64.DEFAULT))
        } catch (e: Exception) {
            Log.e(LOG_TAG, "转化失败", e)
        }

        return null
    }


}
