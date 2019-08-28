package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.scanlogin


import android.os.Bundle
import android.text.TextUtils
import kotlinx.android.synthetic.main.activity_scan_login.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.RetrofitClient
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.AndroidUtils
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.StringUtil
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import java.io.IOException


class ScanLoginActivity : BaseMVPActivity<ScanLoginContract.View, ScanLoginContract.Presenter>(), ScanLoginContract.View {
    override var mPresenter: ScanLoginContract.Presenter = ScanLoginPresenter()

    override fun layoutResId(): Int = net.zoneland.x.bpm.mobile.v1.zoneXBPM.R.layout.activity_scan_login

    companion object {
        val SCAN_RESULT_KEY = "scan_result_key"
    }

    private var result = ""
    private var meta = ""
    private var title = ""

    override fun afterSetContentView(savedInstanceState: Bundle?) {
        result =  intent.extras?.getString(SCAN_RESULT_KEY) ?: ""
        if (TextUtils.isEmpty(result)) {
            XToast.toastShort(this, "没有扫描到任何信息！")
            finish()
            return
        }
        XLog.debug("scan result: $result")
        parseResult()

        setupToolBar(title)
        button_scan_login_confirm.setOnClickListener{ mPresenter.confirmWebLogin(meta) }
        tv_scan_login_cancel.setOnClickListener { finish() }
    }

    override fun confirmSuccess() {
        XToast.toastShort(this, "登录成功！")
        finish()
    }

    override fun confirmFail() {
        XToast.toastShort(this, "登录失败！")
        finish()
    }

    private fun parseResult() {
        if (StringUtil.isUrl(result)) {
            if (result.contains("pgyer.com")) {
                parseMeta()
                if (!TextUtils.isEmpty(meta)) {
                    title = getString(net.zoneland.x.bpm.mobile.v1.zoneXBPM.R.string.scan_login_confirm_title)
                    activity_scan_login.visible()
                    tv_scan_login_text_content.gone()
                }else{
                    gotoDefaultBrowser()
                }
            }else if (result.contains("x_meeting_assemble_control") && result.contains("/checkin")){
                meetingCheckin(result)//会议签到
            }else{
                gotoDefaultBrowser()
            }
        }else{
            activity_scan_login.gone()
            tv_scan_login_text_content.text = result
            tv_scan_login_text_content.visible()
            title = getString(net.zoneland.x.bpm.mobile.v1.zoneXBPM.R.string.scan_login_title)
        }
    }

    private fun  meetingCheckin(url: String) {
        XLog.debug("会议签到：$url")
        val request = Request.Builder().get().url(url).build()
        val client = RetrofitClient.instance().getO2HttpClient()
        if (client != null) {
            val call = client.newCall(request)
            call.enqueue(object : Callback{
                override fun onFailure(call: Call, e: IOException) {
                    XLog.error("", e)
                    runOnUiThread {
                        XToast.toastShort(this@ScanLoginActivity, "签到失败")
                        finish()
                    }

                }

                override fun onResponse(call: Call, response: Response) {
                    val result = response.body()?.string()
                    XLog.debug(result)
                    runOnUiThread {
                        XToast.toastShort(this@ScanLoginActivity, "签到成功")
                        finish()
                    }

                }

            })
        }
    }

    private fun gotoDefaultBrowser() {
        AndroidUtils.runDefaultBrowser(this, result)
        finish()
    }

    private fun parseMeta() {
        try {
            val str = result.trim().toLowerCase()
            val array = str.split("?")
            XLog.debug("$array")
            if (array.size > 1) {
                val paramArray = array[1].split("&")
                paramArray.filter { "meta".equals(it.split("=")[0]) }.map { meta = it.split("=")[1] }
            }
        }catch (e: Exception){XLog.error("", e)}
    }
}
