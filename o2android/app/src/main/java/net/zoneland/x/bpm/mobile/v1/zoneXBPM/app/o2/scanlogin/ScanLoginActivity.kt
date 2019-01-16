package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.scanlogin


import android.os.Bundle
import android.text.TextUtils
import kotlinx.android.synthetic.main.activity_scan_login.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.AndroidUtils
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.StringUtil
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible


class ScanLoginActivity : BaseMVPActivity<ScanLoginContract.View, ScanLoginContract.Presenter>(), ScanLoginContract.View {
    override var mPresenter: ScanLoginContract.Presenter = ScanLoginPresenter()

    override fun layoutResId(): Int = R.layout.activity_scan_login

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
                    title = getString(R.string.scan_login_confirm_title)
                    activity_scan_login.visible()
                    tv_scan_login_text_content.gone()
                }else{
                    gotoDefaultBrowser()
                }
            }else{
                gotoDefaultBrowser()
            }
        }else{
            activity_scan_login.gone()
            tv_scan_login_text_content.text = result
            tv_scan_login_text_content.visible()
            title = getString(R.string.scan_login_title)
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
