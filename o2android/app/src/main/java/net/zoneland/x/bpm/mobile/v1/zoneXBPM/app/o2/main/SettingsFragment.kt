package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.main

import android.Manifest
import android.text.TextUtils
import android.view.View
import com.pgyersdk.feedback.PgyFeedback
import com.pgyersdk.views.PgyerDialog
import kotlinx.android.synthetic.main.fragment_main_settings.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPViewPagerFragment
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.about.AboutActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.login.LoginActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.notice.NoticeSettingActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.organization.ContactPickerActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.security.AccountSecurityActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.skin.SkinManagerActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.BitmapUtil
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.HttpCacheUtil
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.permission.PermissionRequester
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.AndroidShareDialog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.BottomSheetMenu
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.O2AlertIconEnum
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.O2DialogSupport


/**
 * Created by fancy on 2017/6/9.
 */


class SettingsFragment : BaseMVPViewPagerFragment<SettingsContract.View, SettingsContract.Presenter>(), SettingsContract.View, View.OnClickListener {

    override var mPresenter: SettingsContract.Presenter = SettingsPresenter()

    override fun lazyLoad() {

    }

    override fun layoutResId(): Int {
        return R.layout.fragment_main_settings
    }

    override fun initUI() {
        setting_button_account_security_id.setOnClickListener(this)
        setting_button_skin.setOnClickListener(this)
        setting_button_about_id.setOnClickListener(this)
        setting_button_remind_setting_id.setOnClickListener(this)
        setting_button_common_set_id.setOnClickListener(this)
        if (BuildConfig.InnerServer) {
            id_setting_button_customer_service_split.gone()
            setting_button_customer_service_id.gone()
        }else {
            id_setting_button_customer_service_split.visible()
            setting_button_customer_service_id.visible()
            setting_button_customer_service_id.setOnClickListener(this)
        }

        setting_button_feedback_id.setOnClickListener(this)
        myInfo_logout_btn_id.setOnClickListener(this)

        val path = O2CustomStyle.setupAboutImagePath(activity)
        if (!TextUtils.isEmpty(path)) {
            BitmapUtil.setImageFromFile(path!!, setting_image_about_icon)
        }

    }

    val shareDialog: AndroidShareDialog by lazy { AndroidShareDialog(activity, O2.O2_DOWNLOAD_URL, null) }
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.setting_button_account_security_id -> activity.go<AccountSecurityActivity>()
            R.id.setting_button_skin -> activity.go<SkinManagerActivity>()
            R.id.setting_button_remind_setting_id -> activity.go<NoticeSettingActivity>()
            R.id.setting_button_common_set_id -> {
                O2DialogSupport.openConfirmDialog(activity, "确认要清除缓存吗？", {
                    HttpCacheUtil.clearCache(activity, 0)
                }, O2AlertIconEnum.CLEAR)
            }
            R.id.setting_button_customer_service_id -> shareDialog.show()
            R.id.setting_button_feedback_id -> startFeedBack()
            R.id.setting_button_about_id -> activity.go<AboutActivity>()
            R.id.myInfo_logout_btn_id -> logout()


        }
    }

    override fun logoutSuccess() {
        logoutThenJump2Login()
    }

    override fun logoutFail() {
        logoutThenJump2Login()
    }

    override fun cleanOver() {
        XToast.toastShort(activity, "clean Ok!")
    }

    private fun logout() {
        XLog.debug("acti ity: ${activity == null}")
        O2DialogSupport.openConfirmDialog(activity, "确定要退出登录吗？", {
            mPresenter.logout()
        })
    }

    private fun startFeedBack() {
        PermissionRequester(activity)
                .request(Manifest.permission.RECORD_AUDIO)
                .o2Subscribe {
                    onNext { (granted, shouldShowRequestPermissionRationale, deniedPermissions) ->
                        XLog.info("granted:$granted , shouldShowRequest:$shouldShowRequestPermissionRationale, denied:$deniedPermissions")
                        if (!granted) {
                            O2DialogSupport.openAlertDialog(activity, "需要麦克风权限才能进行语音反馈!")
                        } else {
                            PgyerDialog.setDialogTitleBackgroundColor("#FB4747")
                            PgyFeedback.getInstance().showDialog(activity)
                        }
                    }
                    onError { e, _ ->
                        XLog.error("麦克风权限验证异常", e)
                    }
                }
    }

    private fun logoutThenJump2Login() {
        O2SDKManager.instance().logoutCleanCurrentPerson()
        O2App.instance._JMLogout()
        activity.goAndClearBefore<LoginActivity>()
    }
}