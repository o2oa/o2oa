package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.security


import android.os.Bundle
import kotlinx.android.synthetic.main.activity_account_security.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.bind.BindPhoneActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.my.MyInfoActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.O2DialogSupport


class AccountSecurityActivity : BaseMVPActivity<AccountSecurityContract.View, AccountSecurityContract.Presenter>(), AccountSecurityContract.View {
    override var mPresenter: AccountSecurityContract.Presenter = AccountSecurityPresenter()
    override fun layoutResId(): Int  = R.layout.activity_account_security
    override fun afterSetContentView(savedInstanceState: Bundle?) {
        setupToolBar(getString(R.string.title_activity_account_security), true)

        account_name_id.text = O2SDKManager.instance().cName
        account_change_mobile_label_id.text = O2SDKManager.instance().prefs().getString(O2.PRE_BIND_PHONE_KEY, "")

        if (BuildConfig.InnerServer) {
            account_change_mobile_id.inVisible()
        }else {
            account_change_mobile_id.visible()
            account_change_mobile_id.setOnClickListener { changeMobile() }
        }

        rl_account_security_name_btn.setOnClickListener {
            go<MyInfoActivity>()
        }

        val unitName = O2SDKManager.instance().prefs().getString(O2.PRE_BIND_UNIT_KEY, "")
        tv_account_security_unit_name.text = "当前绑定服务器：$unitName"
    }

    override fun logoutSuccess() {
        O2SDKManager.instance().logoutCleanCurrentPerson()
        O2SDKManager.instance().clearBindUnit()
        O2App.instance._JMLogout()

        goAndClearBefore<BindPhoneActivity>()
    }


    private fun changeMobile() {
        O2DialogSupport.openConfirmDialog(this, "确定要重新绑定手机号码吗,该操作会清空当前登录信息，需要重新登录？", {
            val deviceId = O2SDKManager.instance().prefs().getString(O2.PRE_BIND_PHONE_TOKEN_KEY, "")
            mPresenter.logout(deviceId)
        })
    }



}
