package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.main

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.View
import com.pgyersdk.update.PgyUpdateManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_main_bottom_bar_image.*
import net.muliba.changeskin.FancySkinManager
import net.muliba.fancyfilepickerlibrary.PicturePicker
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2CustomStyle
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.my.ClipAvatarActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.MainActivityFragmentAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.enums.ApplicationEnum
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.service.ClearTempFileJobService
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.service.CollectLogJobService
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.service.PictureLoaderService
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.service.RestartSelfService
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.permission.PermissionRequester
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.O2DialogSupport
import org.jetbrains.anko.doAsync
import java.io.File


/**
 * Created by fancy on 2017/6/8.
 */


class MainActivity : BaseMVPActivity<MainContract.View, MainContract.Presenter>(), MainContract.View, View.OnClickListener {

    val TAKE_FROM_PICTURES_CODE = 1
    val TAKE_FROM_CAMERA_CODE = 2
    val CLIP_AVATAR_ACTIVITY_CODE = 3

    override var mPresenter: MainContract.Presenter = MainPresenter()

    private val fragmentList: ArrayList<Fragment> = ArrayList(5)
    private val fragmentTitles: ArrayList<String> = ArrayList(5)
    private val mCurrentSelectIndexKey = "mCurrentSelectIndexKey"
    private var mCurrentSelectIndex = 2
    private lateinit var cameraImageUri: Uri


    var pictureLoaderService: PictureLoaderService? = null
    private val doubleClickExitHelper: O2DoubleClickExit by lazy { O2DoubleClickExit(this) }
    val adapter: MainActivityFragmentAdapter by lazy { MainActivityFragmentAdapter(fragmentList, fragmentTitles, supportFragmentManager) }

    override fun layoutResId(): Int {
        return R.layout.activity_main
    }

    override fun beforeSetContentView() {
        super.beforeSetContentView()
        setTheme(R.style.XBPMTheme_NoActionBar)
    }
    override fun afterSetContentView(savedInstanceState: Bundle?) {
        mCurrentSelectIndex = savedInstanceState?.getInt(mCurrentSelectIndexKey, 2) ?: 2
        setupToolBar(getString(R.string.app_name))

        XLog.info("main activity init..............")
        val indexType = O2SDKManager.instance().prefs().getString(O2CustomStyle.INDEX_TYPE_PREF_KEY, O2CustomStyle.INDEX_TYPE_DEFAULT)
        val indexId = O2SDKManager.instance().prefs().getString(O2CustomStyle.INDEX_ID_PREF_KEY, "")
        XLog.info("main activity isIndex $indexType..............")

        val newsFragment = NewsFragment()
        fragmentList.add(newsFragment)
        fragmentTitles.add(getString(R.string.tab_message))

        val contactFragment = NewContactFragment()
        fragmentList.add(contactFragment)
        fragmentTitles.add(getString(R.string.tab_contact))

        val indexName = getString(R.string.tab_todo)
        if (indexType == O2CustomStyle.INDEX_TYPE_DEFAULT || TextUtils.isEmpty(indexId)) {
            val indexFragment = IndexFragment()
            fragmentList.add(indexFragment)
            fragmentTitles.add(indexName)
        } else {
            val indexFragment = IndexPortalFragment.instance(indexId)
            fragmentList.add(indexFragment)
            fragmentTitles.add(indexName)
        }

        val appFragment = AppFragment()
        fragmentList.add(appFragment)
        fragmentTitles.add(getString(R.string.tab_app))

        val settingFragment = SettingsFragment()
        fragmentList.add(settingFragment)
        fragmentTitles.add(getString(R.string.tab_settings))

        content_fragmentView_id.adapter = adapter
        content_fragmentView_id.offscreenPageLimit = 5
        content_fragmentView_id.addOnPageChangeListener {
            onPageSelected { position ->
                selectTab(position)
            }
        }

        fab_main_start_ai.setOnClickListener(this)
        icon_main_bottom_news.setOnClickListener(this)
        icon_main_bottom_app.setOnClickListener(this)
//        icon_main_bottom_index_blur.setOnClickListener(this)
        icon_main_bottom_index.setOnClickListener(this)
        icon_main_bottom_contact.setOnClickListener(this)
        icon_main_bottom_setting.setOnClickListener(this)

        selectTab(mCurrentSelectIndex)

        //初始化拍照地址等
        SDCardHelper.generateNewFile(FileExtensionHelper.getCameraCacheFilePath())

    }

    override fun o2AIEnable(enable: Boolean) {
        XLog.info("O2AI enable: $enable")
//        if (enable) {
//            icon_main_bottom_center_gap.visible()
//            fab_main_start_ai.visible()
//        }else {
//            icon_main_bottom_center_gap.gone()
//            fab_main_start_ai.gone()
//        }
    }

    override fun onResume() {
        super.onResume()
        pictureLoaderService = PictureLoaderService(this)
        changeBottomIcon(mCurrentSelectIndex)
        calDpi()
        //register scheduler job
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            registerSchedulerJob()
        }

        val unit = O2SDKManager.instance().prefs().getString(O2.PRE_CENTER_HOST_KEY, "")
        if (!TextUtils.isEmpty(unit) && unit == "demo.o2oa.io") {
            val day = O2SDKManager.instance().prefs().getString(O2.PRE_DEMO_ALERT_REMIND_DAY, "")
            val today = DateHelper.nowByFormate("yyyy-MM-dd")
            if (day != today) {
                val demoDialog = DemoAlertFragment()
                demoDialog.isCancelable = true
                demoDialog.show(supportFragmentManager, "demo")
                O2SDKManager.instance().prefs().edit {
                    putString(O2.PRE_DEMO_ALERT_REMIND_DAY, today)
                }
            }
        }
    }


    override fun onPause() {
        super.onPause()
        pictureLoaderService?.close()
        PgyUpdateManager.unregister()//点击太快 进入main后马上跳到别的Activity去有可能会出错 需要这个unregister
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putInt(mCurrentSelectIndexKey, mCurrentSelectIndex)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        mCurrentSelectIndex = savedInstanceState?.getInt(mCurrentSelectIndexKey) ?: 2
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return if (mCurrentSelectIndex == 2 && fragmentList[2] is IndexPortalFragment) {
                if ((fragmentList[2] as IndexPortalFragment).previousPage()) {
                    true
                } else {
                    doubleClickExitHelper.onKeyDown(keyCode, event)
                }
            } else {
                doubleClickExitHelper.onKeyDown(keyCode, event)
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            fragmentList.map { it.onActivityResult(requestCode, resultCode, data) }
            when (requestCode) {
                TAKE_FROM_CAMERA_CODE -> startClipAvatar(cameraImageUri)

                TAKE_FROM_PICTURES_CODE -> {
                    XLog.debug("choose from pictures ...")
                    data?.let {
                        val result = it.extras.getString(PicturePicker.FANCY_PICTURE_PICKER_SINGLE_RESULT_KEY, "")
                        if (!TextUtils.isEmpty(result)) {
                            val uri = Uri.fromFile(File(result))
                            startClipAvatar(uri)
                        }
                    }
                }

                CLIP_AVATAR_ACTIVITY_CODE -> {
                    data?.let {
                        val url = it.extras.getString("clipAvatarFilePath")
                        XLog.debug("back Myinfo avatar uri : $url ")
                        if (content_fragmentView_id.currentItem == 3 && fragmentList[3] is MyFragment) {
                            (fragmentList[3] as MyFragment).modifyAvatar2Remote(url)
                        }

                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
//            R.id.icon_main_bottom_index_blur -> selectTab(0)
            R.id.icon_main_bottom_news -> selectTab(0)
            R.id.icon_main_bottom_contact -> selectTab(1)
            R.id.icon_main_bottom_index -> {
                if (fragmentList[2] is IndexPortalFragment) {
                    (fragmentList[2] as IndexPortalFragment).loadWebview()
                }
                selectTab(2)
            }
            R.id.icon_main_bottom_app -> selectTab(3)
            R.id.icon_main_bottom_setting -> selectTab(4)
            R.id.fab_main_start_ai -> startAi()
        }
    }

    //刷新ActionBar的菜单按钮 应用页面使用
    fun refreshMenu() {
        invalidateOptionsMenu()
    }
    //跳转到应用页面 首页使用
    fun gotoApp() {
        selectTab(3)
    }

    private fun startAi() {
        IndexFragment.go(ApplicationEnum.O2AI.key, this)
    }


    private fun selectTab(i: Int) {
        changePageView(i)
        changeBottomIcon(i)
        mCurrentSelectIndex = i
    }

    fun takeFromPictures() {
        PicturePicker()
                .withActivity(this)
                .chooseType(PicturePicker.CHOOSE_TYPE_SINGLE)
                .requestCode(TAKE_FROM_PICTURES_CODE)
                .start()
    }

    fun takeFromCamera() {
        PermissionRequester(this).request(Manifest.permission.CAMERA)
                .o2Subscribe {
                    onNext { (granted, shouldShowRequestPermissionRationale, deniedPermissions) ->
                        XLog.info("granted:$granted , shouldShowRequest:$shouldShowRequestPermissionRationale, denied:$deniedPermissions")
                        if (!granted) {
                            O2DialogSupport.openAlertDialog(this@MainActivity, "非常抱歉，相机权限没有开启，无法使用相机！")
                        } else {
                            openCamera()
                        }
                    }
                    onError { e, _ ->
                        XLog.error("检查权限出错", e)
                    }
                }
    }

    private fun startClipAvatar(pictureUri: Uri) {
        goWithRequestCode<ClipAvatarActivity>(ClipAvatarActivity.startWithBundle(pictureUri), CLIP_AVATAR_ACTIVITY_CODE)
    }

    private fun openCamera() {
        XLog.info("openCamera")
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //return-data false 不是直接返回拍照后的照片Bitmap 因为照片太大会传输失败
        intent.putExtra("return-data", false)
        //改用Uri 传递
        cameraImageUri = FileUtil.getUriFromFile(this@MainActivity, File(FileExtensionHelper.getCameraCacheFilePath()))
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri)
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString())
        intent.putExtra("noFaceDetection", true)
        startActivityForResult(intent, TAKE_FROM_CAMERA_CODE)
    }


    private fun changeBottomIcon(i: Int) {
        resetBottomBtnAlpha()
        when (i) {

            0 -> {
                image_icon_main_bottom_news.setImageDrawable(FancySkinManager.instance().getDrawable(this, R.mipmap.icon_main_news_red))
                tv_icon_main_bottom_news.setTextColor(FancySkinManager.instance().getColor(this, R.color.z_color_primary))
            }
            1 -> {
                image_icon_main_bottom_contact.setImageDrawable(FancySkinManager.instance().getDrawable(this, R.mipmap.icon_main_contact_red))
                tv_icon_main_bottom_contact.setTextColor(FancySkinManager.instance().getColor(this, R.color.z_color_primary))
            }
            2 -> {
                val path = O2CustomStyle.indexMenuLogoFocusImagePath(this)
                if (!TextUtils.isEmpty(path)) {
                    BitmapUtil.setImageFromFile(path!!, icon_main_bottom_index)
                }else {
                    icon_main_bottom_index.setImageResource(R.mipmap.index_bottom_menu_logo_focus)
                }
            }
            3 -> {
                image_icon_main_bottom_app.setImageDrawable(FancySkinManager.instance().getDrawable(this, R.mipmap.icon_main_app_red))
                tv_icon_main_bottom_app.setTextColor(FancySkinManager.instance().getColor(this, R.color.z_color_primary))
            }
            4 -> {
                image_icon_main_bottom_setting.setImageDrawable(FancySkinManager.instance().getDrawable(this, R.mipmap.icon_main_setting_red))
                tv_icon_main_bottom_setting.setTextColor(FancySkinManager.instance().getColor(this, R.color.z_color_primary))
            }
        }

    }

    private fun changePageView(position: Int) {
        content_fragmentView_id.setCurrentItem(position, false)
        when (position) {
            0 -> resetToolBar(getString(R.string.tab_message))
            1 -> resetToolBar(getString(R.string.tab_contact))
            2 -> setIndexToolBar()
            3 -> resetToolBar(getString(R.string.tab_contact))
            4 -> resetToolBar(getString(R.string.tab_settings))
        }

    }

    private fun resetToolBar(string: String?) {
        app_bar_layout_main_head.visible()
        toolbar?.navigationIcon = null
        toolbarTitle?.text = string
    }

    private fun setIndexToolBar() {
        app_bar_layout_main_head.gone()
    }

    private fun resetBottomBtnAlpha() {
        image_icon_main_bottom_news.setImageDrawable(FancySkinManager.instance().getDrawable(this, R.mipmap.icon_main_news))
        tv_icon_main_bottom_news.setTextColor(FancySkinManager.instance().getColor(this, R.color.z_color_text_primary))
        image_icon_main_bottom_contact.setImageDrawable(FancySkinManager.instance().getDrawable(this, R.mipmap.icon_main_contact))
        tv_icon_main_bottom_contact.setTextColor(FancySkinManager.instance().getColor(this, R.color.z_color_text_primary))
        val path = O2CustomStyle.indexMenuLogoBlurImagePath(this)
        if (!TextUtils.isEmpty(path)) {
            BitmapUtil.setImageFromFile(path!!, icon_main_bottom_index)
        }else {
            icon_main_bottom_index.setImageResource(R.mipmap.index_bottom_menu_logo_blur)
        }
        image_icon_main_bottom_app.setImageDrawable(FancySkinManager.instance().getDrawable(this, R.mipmap.icon_main_app))
        tv_icon_main_bottom_app.setTextColor(FancySkinManager.instance().getColor(this, R.color.z_color_text_primary))
        image_icon_main_bottom_setting.setImageDrawable(FancySkinManager.instance().getDrawable(this, R.mipmap.icon_main_setting))
        tv_icon_main_bottom_setting.setTextColor(FancySkinManager.instance().getColor(this, R.color.z_color_text_primary))
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun registerSchedulerJob() {
        val componentName = ComponentName(this, ClearTempFileJobService::class.java)
        val jobInfo = JobInfo.Builder(O2.O2_CLEAR_TEMP_FILE_JOB_ID, componentName)
                .setPersisted(true)//手机重启之后是否继续
                .setRequiresCharging(true)//充电的时候才执行
                .setPeriodic(24 * 60 * 60 * 1000)
                .build()
        val collectLogComponent = ComponentName(this, CollectLogJobService::class.java)
        val jobCollectLog = JobInfo.Builder(O2.O2_COLLECT_LOG_JOB_ID, collectLogComponent)
                .setPersisted(true)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(1000 * 60 * 60 * 12)
                .build()

        val jobScheduler = applicationContext.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val result = jobScheduler.schedule(jobInfo)
        val result2 = jobScheduler.schedule(jobCollectLog)
        XLog.info("jobScheduler result:$result, result2:$result2")
    }


    /**
     * 存储下手机分辨率
     */
    private fun calDpi() {
        val dm = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(dm)
        val width = dm.widthPixels
        val height = dm.heightPixels
        doAsync {
            O2SDKManager.instance().prefs().edit {
                putString(O2.PRE_DEVICE_DPI_KEY, "$width*$height")
            }
            XLog.debug("storage success, width:$width, height:$height")
        }
    }

    fun changeIndexFragment() {
        XLog.info("changeIndexFragment  ..................")
        val isIndex = O2SDKManager.instance().prefs().getBoolean(O2.O2_INDEX_OR_PORTAL, true)
        XLog.info("changeIndexFragment  $isIndex..................")
        O2SDKManager.instance().prefs().edit {
            XLog.info("edit index..................")
            putBoolean(O2.O2_INDEX_OR_PORTAL, !isIndex)
        }
        restartAppSelf(this)
    }

    /**
     * 重启应用
     */
    private fun restartAppSelf(context: Context) {
        val intent = Intent(context, RestartSelfService::class.java)
        intent.putExtra(RestartSelfService.RESTART_PACKAGE_NAME_EXTRA_NAME, context.packageName)
        context.startService(intent)

        android.os.Process.killProcess(android.os.Process.myPid())
    }

}