package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.webview

import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.main.IndexPortalFragment
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast

class PortalWebViewActivity : BaseMVPActivity<PortalWebViewContract.View, PortalWebViewContract.Presenter>(), PortalWebViewContract.View  {
    override var mPresenter: PortalWebViewContract.Presenter = PortalWebViewPresenter()

    override fun layoutResId(): Int = R.layout.activity_portal_web_view

    companion object {
        val PORTAL_ID_KEY = "PORTAL_ID_KEY"
        val PORTAL_NAME_KEY = "PORTAL_NAME_KEY"
        fun startPortal(portalId: String, portalName: String): Bundle {
            val bundle = Bundle()
            bundle.putString(PORTAL_ID_KEY, portalId)
            bundle.putString(PORTAL_NAME_KEY, portalName)
            return bundle
        }
    }

    private var portalId: String = ""
    private var portalName: String = ""
    private var portalUrl: String = ""

    private var portalFragment:IndexPortalFragment? = null


    override fun afterSetContentView(savedInstanceState: Bundle?) {
        portalId = intent.extras?.getString(PORTAL_ID_KEY) ?: ""
        portalName = intent.extras?.getString(PORTAL_NAME_KEY) ?: ""
        if (TextUtils.isEmpty(portalId)) {
            XToast.toastShort(this, "缺少参数门户ID！！")
            finish()
        }else {
            setupToolBar(portalName)
            portalUrl = APIAddressHelper.instance().getPortalWebViewUrl(portalId)
            XLog.debug("portal url : $portalUrl")
            portalFragment = IndexPortalFragment.instance(portalId)
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_portal_web_view_content, portalFragment)
            transaction.commit()
            toolbar?.setNavigationIcon(R.mipmap.ic_back_mtrl_white_alpha)
            toolbar?.setNavigationOnClickListener {
                if (portalFragment?.previousPage() == false) {
                    finish()
                }
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (portalFragment?.previousPage() == false) {
                finish()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_scale_in, R.anim.activity_scale_out)
    }
}
