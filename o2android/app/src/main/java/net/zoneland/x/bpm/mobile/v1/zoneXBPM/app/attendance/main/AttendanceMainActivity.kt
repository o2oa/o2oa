package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.attendance.main


import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_attendance_main.*
import net.muliba.changeskin.FancySkinManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.attendance.approval.AttendanceAppealApprovalActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.attendance.list.AttendanceListActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.attendance.setting.AttendanceLocationSettingActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonFragmentPagerAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.addOnPageChangeListener
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.go
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.o2Subscribe
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.permission.PermissionRequester
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.O2DialogSupport


class AttendanceMainActivity : BaseMVPActivity<AttendanceMainContract.View, AttendanceMainContract.Presenter>(), AttendanceMainContract.View {
    override var mPresenter: AttendanceMainContract.Presenter = AttendanceMainPresenter()
    override fun layoutResId(): Int = R.layout.activity_attendance_main

    var locationEnable: Boolean = false
    private var isAttendanceAdmin: Boolean = false

    private val fragmentList: ArrayList<Fragment> by lazy { arrayListOf<Fragment>(AttendanceCheckInFragment(), AttendanceStatisticFragment()) }
    private val titleList: ArrayList<String> by lazy { arrayListOf<String>(getString(R.string.attendance_check_in_title), getString(R.string.title_activity_attendance_chart)) }
    override fun afterSetContentView(savedInstanceState: Bundle?) {
        setupToolBar(getString(R.string.attendance_check_in_title), true, true)

        view_pager_attendance_main_content.adapter = CommonFragmentPagerAdapter(supportFragmentManager, fragmentList, titleList)
        view_pager_attendance_main_content.addOnPageChangeListener {
            onPageSelected { position ->
                selected(position)
            }
        }

        linear_attendance_main_check_in_tab.setOnClickListener {
            view_pager_attendance_main_content.currentItem = 0
            selected(0)
        }
        linear_attendance_main_statistic_tab.setOnClickListener {
            view_pager_attendance_main_content.currentItem = 1
            selected(1)
        }

        view_pager_attendance_main_content.currentItem = 0
        selected(0)

        mPresenter.loadAttendanceAdmin()

    }

    override fun onResume() {
        super.onResume()
        checkPermission()
    }

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.menu_attendance_main_normal, menu)
//        return super.onCreateOptionsMenu(menu)
//    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()
        if (isAttendanceAdmin) {
            menuInflater.inflate(R.menu.menu_attendance_main_admin, menu)
        }
//        else {
//            menuInflater.inflate(R.menu.menu_attendance_main_normal, menu)
//        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item?.itemId) {
            R.id.menu_attendance_list -> {
                XLog.debug("click menu attendance list")
                go<AttendanceListActivity>()
                true
            }
            R.id.menu_attendance_appeal_approval -> {
                XLog.debug("click menu attendance approval list")
                go<AttendanceAppealApprovalActivity>()
                true
            }
            R.id.menu_attendance_location_setting -> {
                XLog.debug("click menu attendance location setting")
                go<AttendanceLocationSettingActivity>()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_scale_in, R.anim.activity_scale_out)
    }

    override fun isAttendanceAdmin(flag: Boolean) {
        isAttendanceAdmin = flag
        XLog.debug("is admin : $flag")
        invalidateOptionsMenu()
    }

    fun checkPermission() {
        PermissionRequester(this).request(Manifest.permission.ACCESS_FINE_LOCATION)
                .o2Subscribe {
                    onNext {  (granted, shouldShowRequestPermissionRationale, deniedPermissions) ->
                        if (!granted){
                            O2DialogSupport.openAlertDialog(this@AttendanceMainActivity, "需要定位权限, 去设置", { permissionSetting() })
                        }else{
                            locationEnable = true
                        }

                    }
                    onError { e, _ ->
                        XLog.error( "检查权限出错", e)
                    }
                }
    }

    private fun permissionSetting() {
        val packageUri = Uri.parse("package:$packageName")
        startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageUri))
    }


    private fun selected(position: Int) {
        when (position) {
            0 -> {
                image_attendance_main_check_in_tab_icon.setImageDrawable(FancySkinManager.instance().getDrawable(this, R.mipmap.attendance_check_in_red))
                tv_attendance_main_check_in_tab_name.setTextColor(FancySkinManager.instance().getColor(this@AttendanceMainActivity, R.color.z_color_primary))
                image_attendance_main_statistic_tab_icon.setImageDrawable(FancySkinManager.instance().getDrawable(this,R.mipmap.attendance_statistic_gray))
                tv_attendance_main_statistic_tab_name.setTextColor(FancySkinManager.instance().getColor(this@AttendanceMainActivity, R.color.z_color_text_primary))
            }
            1 -> {
                image_attendance_main_check_in_tab_icon.setImageDrawable(FancySkinManager.instance().getDrawable(this,R.mipmap.attendance_check_in_gray))
                tv_attendance_main_check_in_tab_name.setTextColor(FancySkinManager.instance().getColor(this@AttendanceMainActivity, R.color.z_color_text_primary))
                image_attendance_main_statistic_tab_icon.setImageDrawable(FancySkinManager.instance().getDrawable(this, R.mipmap.attendance_statistic_red))
                tv_attendance_main_statistic_tab_name.setTextColor(FancySkinManager.instance().getColor(this@AttendanceMainActivity, R.color.z_color_primary))
            }
        }
        toolbarTitle?.text = titleList[position]
    }
}
