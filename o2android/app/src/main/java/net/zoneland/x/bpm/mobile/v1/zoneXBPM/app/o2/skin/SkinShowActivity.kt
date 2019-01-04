package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.skin

import android.os.Bundle
import android.os.Looper
import android.support.v7.app.AppCompatActivity
import com.bigkoo.convenientbanner.ConvenientBanner
import kotlinx.android.synthetic.main.activity_skin_show.*
import net.muliba.changeskin.FancySkinManager
import net.muliba.changeskin.callback.PluginSkinChangingListener
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.main.MainActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.ConvenientBannerSkinHolderView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.RetrofitClient
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.download.DownloadProgressHandler
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.goAndClearBefore
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.O2ProgressButton
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.O2DialogSupport
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream

class SkinShowActivity : AppCompatActivity() {
    private val puppyList = arrayListOf(R.mipmap.puppy2018_show_1, R.mipmap.puppy2018_show_2, R.mipmap.puppy2018_show_3,
            R.mipmap.puppy2018_show_4, R.mipmap.puppy2018_show_5)
    private var cBanner: ConvenientBanner<Int>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_skin_show)
        // 沉浸式状态栏
        ImmersedStatusBarUtils.setImmersedStatusBar(this)

        toolbar_snippet_top_bar.title = ""
        setSupportActionBar(toolbar_snippet_top_bar)
        tv_snippet_top_title.text = getString(R.string.skin_puppy2018)
        toolbar_snippet_top_bar.setNavigationIcon(R.mipmap.ic_back_mtrl_white_alpha)
        toolbar_snippet_top_bar.setNavigationOnClickListener { finish() }
        cBanner = findViewById(R.id.convenientBanner)
        cBanner?.setPages({ ConvenientBannerSkinHolderView() }, puppyList)
                ?.setPageIndicator(intArrayOf(R.mipmap.x_banner_indicator_blur, R.mipmap.x_banner_indicator_focus))

        button_skin_show_use.setOnClickListener {
            changeSkinConfirm {
                val filePath = FileExtensionHelper.generateSkinFilePath("puppy2018.apk")
                val outFile = File(filePath)
                if (outFile.exists()){
                    applySkin("net.zoneland.o2.skin.puppy2018", "", filePath)
                }else {
                    button_skin_show_use.text = getString(R.string.skin_show_downloading)
                    button_skin_show_use.isEnabled = false
                    startDownLoad()
                }
            }
        }
        button_skin_show_use.addFinishListener(object : O2ProgressButton.ProgressFinishListener {
            override fun onFinish() {
                XLog.info("finish。。。。。。。。。。。。。。。。。。。")
            }
        })
        checkUsed()
    }

    private fun changeSkinConfirm(callBack:()->Unit) {
        O2DialogSupport.openConfirmDialog(this, "确认要切换皮肤吗，切换会重启应用", { _->
            callBack.invoke()
        })
    }

    private fun checkUsed(){
        val cPackage = FancySkinManager.instance().currentSkinPackageName()
        val cPath = FancySkinManager.instance().currentSkinPath()
        val cSuffix = FancySkinManager.instance().currentSkinSuffix()
        val filePath = FileExtensionHelper.generateSkinFilePath("puppy2018.apk")
        if (cPackage=="net.zoneland.o2.skin.puppy2018" && cSuffix=="" && cPath == filePath) {
            button_skin_show_use.text = getString(R.string.skin_manager_using)
            button_skin_show_use.isEnabled = false
        }else {
            button_skin_show_use.text = getString(R.string.skin_show_use)
            button_skin_show_use.isEnabled = true
        }
    }


    private fun startDownLoad() {
        val url = APIAddressHelper.instance().getDownloadPuppy2018SkinUrl()
        XLog.info("download url : $url")
        val filePath = FileExtensionHelper.generateSkinFilePath("puppy2018.apk")
        SDCardHelper.generateNewFile(filePath)
        val outFile = File(filePath)
        var isDownFileSuccess = false
        RetrofitClient.instance().skinDownloadService(url, object : DownloadProgressHandler() {
            override fun onProgress(progress: Long, total: Long, done: Boolean) {
                XLog.debug("$progress $total, $done")
                XLog.debug("是否在主线程中运行" + (Looper.getMainLooper() == Looper.myLooper()).toString())
                val myP = 100 * progress / total
                XLog.debug(String.format("%d%% done\n", myP))
                XLog.debug("done --->" + done.toString())
                button_skin_show_use.setProgress(myP.toInt())
            }
        }).skinDownload("puppy2018.apk")
                .subscribeOn(Schedulers.io())
                .flatMap { response ->
                    try {
                        val input = DataInputStream(response.body()?.byteStream())
                        val output = DataOutputStream(FileOutputStream(outFile))
                        val buffer = ByteArray(4096)
                        var count = 0
                        do {
                            count = input.read(buffer)
                            if (count > 0) {
                                output.write(buffer, 0, count)
                            }
                        } while (count > 0)
                        output.close()
                        input.close()
                        isDownFileSuccess = true
                    } catch (e: Exception) {
                        XLog.error("download file fail", e)
                        isDownFileSuccess = false
                    }
                    Observable.just(isDownFileSuccess)
                }.observeOn(AndroidSchedulers.mainThread())
                .o2Subscribe {
                    onNext { res ->
                        if (res) {
                            XLog.info("下载文件成功，应用皮肤。。。。。。。")
                            applySkin("net.zoneland.o2.skin.puppy2018", "", filePath)
                        }else {
                            downloadSkinFail(outFile)
                        }
                    }
                    onError { e, isNetworkError ->
                        XLog.error("下载皮肤失败！", e)
                        downloadSkinFail(outFile)
                    }
                }

    }

    private fun downloadSkinFail(outFile: File) {
        XToast.toastShort(this@SkinShowActivity, "皮肤下载失败！！！")
        try {
            XLog.info("删除没有下载成功的皮肤文件！！！！！！！！！！！！！！！")
            outFile.delete()
        } catch (e: Exception) {
            XLog.error("删除皮肤失败！", e)
        }
        button_skin_show_use.text = getString(R.string.skin_show_use)
        button_skin_show_use.isEnabled = true
    }


    private fun applySkin(packageName: String, suffix: String, path: String) {
        XLog.info("skin path: $path")
        FancySkinManager.instance().changeSkin(path, packageName, suffix, object : PluginSkinChangingListener {
            override fun onCompleted() {
                XLog.info("skin change onCompleted.............")
                XToast.toastShort(this@SkinShowActivity, "切换皮肤成功！")
                button_skin_show_use.text = getString(R.string.skin_manager_using)
                button_skin_show_use.isEnabled = false
                goAndClearBefore<MainActivity>()
            }

            override fun onError(e: Exception) {
                XLog.error("skin change onError.............", e)
                button_skin_show_use.text =  getString(R.string.skin_show_use)
                button_skin_show_use.isEnabled = true
            }

            override fun onStart() {
                XLog.info("skin change onStart.............")
                button_skin_show_use.text = getString(R.string.skin_show_installing)
            }
        })
    }

}
