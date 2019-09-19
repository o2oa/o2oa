package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.main

import android.Manifest
import android.app.Activity
import android.graphics.BitmapFactory
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.bigkoo.convenientbanner.ConvenientBanner
import io.flutter.app.FlutterActivity
import kotlinx.android.synthetic.main.fragment_main_todo.*
import kotlinx.android.synthetic.main.snippet_shimmer_content.*
import net.muliba.changeskin.FancySkinManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2CustomStyle
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.attendance.main.AttendanceMainActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPViewPagerFragment
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.bbs.main.BBSMainActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.bbs.view.BBSWebViewSubjectActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.calendar.CalendarMainActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.clouddrive.CloudDriveActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.cms.index.CMSIndexActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.cms.view.CMSWebViewActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.meeting.main.MeetingMainActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.ai.O2AIActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.process.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.webview.PortalWebViewActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.webview.TaskWebViewActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecycleViewAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.ConvenientBannerImageHolderView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.enums.ApplicationEnum
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.enums.HotPictureApplicationEnum
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.flutter.FlutterConnectActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.cms.CMSDocumentInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.HotPictureOutData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.TaskData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.persistence.MyAppListObject
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.ToDoFragmentListViewItemVO
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.DateHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.imageloader.O2ImageLoaderManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.imageloader.O2ImageLoaderOptions
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.permission.PermissionRequester
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.zxing.activity.CaptureActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.DividerItemDecoration
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.O2DialogSupport

/**
 * Created by fancy on 2017/6/9.
 */

class IndexFragment : BaseMVPViewPagerFragment<IndexContract.View, IndexContract.Presenter>(), IndexContract.View, View.OnClickListener {

    override var mPresenter: IndexContract.Presenter = IndexPresenter()
    override fun layoutResId(): Int = R.layout.fragment_main_todo

    companion object {
        const val BUSINESS_TYPE_MESSAGE_CENTER = 0//信息中心
        const val BUSINESS_TYPE_WORK_CENTER = 1//工作中心
        const val ALL_APP_ID = "ALL_APP_ID"

        fun go(id: String, activity: Activity, title: String = "") {
            when (id) {
                ApplicationEnum.TASK.key -> activity.go<TaskListActivity>()
                ApplicationEnum.TASKCOMPLETED.key -> activity.go<TaskCompletedListActivity>()
                ApplicationEnum.READ.key -> activity.go<ReadListActivity>()
                ApplicationEnum.READCOMPLETED.key -> activity.go<ReadCompletedListActivity>()
                ApplicationEnum.BBS.key -> activity.go<BBSMainActivity>()
                ApplicationEnum.CMS.key -> activity.go<CMSIndexActivity>()
                ApplicationEnum.YUNPAN.key -> activity.go<CloudDriveActivity>()
                ApplicationEnum.MEETING.key -> activity.go<MeetingMainActivity>()
                ApplicationEnum.ATTENDANCE.key -> activity.go<AttendanceMainActivity>()
                ApplicationEnum.CALENDAR.key -> activity.go<CalendarMainActivity>()
                ApplicationEnum.MindMap.key -> {
                    activity.go<FlutterConnectActivity>(FlutterConnectActivity.startFlutterAppWithRoute(ApplicationEnum.MindMap.key))
                }
                ApplicationEnum.O2AI.key -> {
                    PermissionRequester(activity)
                            .request(Manifest.permission.RECORD_AUDIO)
                            .o2Subscribe {
                                onNext { (granted, shouldShowRequestPermissionRationale, deniedPermissions) ->
                                    XLog.info("granted:$granted , shouldShowRequest:$shouldShowRequestPermissionRationale, denied:$deniedPermissions")
                                    if (!granted) {
                                        O2DialogSupport.openAlertDialog(activity, "非常抱歉，AI助手需要的权限没有开启，无法使用该功能，请到手机应用设置中开启权限！")
                                    } else {
                                        activity.go<O2AIActivity>()
                                    }
                                }
                                onError { e, _ ->
                                    XLog.error("检查权限出错", e)
                                    O2DialogSupport.openAlertDialog(activity, "非常抱歉，AI助手需要的权限没有开启，无法使用该功能，请到手机应用设置中开启权限！")
                                }
                            }

                }
                ALL_APP_ID -> {
//                    activity.go<MyAppActivity>()
                    if (activity is MainActivity) {
                        activity.gotoApp()
                    }
                }
                else -> {
                    //portal 打开
                    activity.go<PortalWebViewActivity>(PortalWebViewActivity.startPortal(id, title))
                }
            }

        }
    }

    lateinit var cBannerView: ConvenientBanner<HotPictureOutData>
    lateinit var mActionBarBackgroundDrawable: Drawable
    val screenWidth: Int by lazy { activity.screenWidth() }
    val cBannerHeight: Int by lazy { screenWidth / 2 }
    val appList = ArrayList<MyAppListObject>()
    val itemDecoration: DividerItemDecoration by lazy { DividerItemDecoration(activity, DividerItemDecoration.VERTICAL_LIST) }
    val itemList = ArrayList<ToDoFragmentListViewItemVO>()
    val taskList = ArrayList<TaskData>()
    val newsList = ArrayList<CMSDocumentInfoJson>()
    val hotPictureList = ArrayList<HotPictureOutData>()
    var currentType = BUSINESS_TYPE_MESSAGE_CENTER
    var isLoadHotPictureList = false
    var isRedPointShow = true
    //load more refresh
    var lastTaskId = ""
    var lastNewsId = ""
    var isRefreshTaskList = false ////是否正在刷新任务
    var isRefreshNewsList = false //是否正在刷新新闻列表
    var isLoadMoreList = false //是否正在加载


    override fun initUI() {
        swipe_refresh_todo_fragment.setColorSchemeResources(R.color.z_color_refresh_scuba_blue,
                R.color.z_color_refresh_red, R.color.z_color_refresh_purple, R.color.z_color_refresh_orange)
        swipe_refresh_todo_fragment.setOnRefreshListener { lazyLoad() }
        XLog.debug("cBanner width:$screenWidth, height:$cBannerHeight")
        mActionBarBackgroundDrawable = ColorDrawable(FancySkinManager.instance().getColor(activity, R.color.z_color_primary))
        mActionBarBackgroundDrawable.alpha = 0 //透明
        relative_todo_main_action_bar.background = mActionBarBackgroundDrawable
        nested_scroll_todo_main_content.setOnScrollChangeListener { _: @ParameterName(name = "v") NestedScrollView?,
                                                                    _: @ParameterName(name = "scrollX") Int,
                                                                    scrollY: @ParameterName(name = "scrollY") Int,
                                                                    _: @ParameterName(name = "oldScrollX") Int,
                                                                    oldScrollY: @ParameterName(name = "oldScrollY") Int ->
            val height: Int = cBannerHeight - relative_todo_main_action_bar.height
            val ratio: Float = Math.min(Math.max(scrollY, 0), height).toFloat().div(height.toFloat())
            val newAlpha: Int = (ratio * 255).toInt()
            mActionBarBackgroundDrawable.alpha = newAlpha
            if (newAlpha >= 250) {
                if (currentType == BUSINESS_TYPE_MESSAGE_CENTER) {
                    tv_todo_fragment_title.text = getString(R.string.tab_todo_new_message_center)
                } else {
                    tv_todo_fragment_title.text = getString(R.string.tab_todo_new_task_center)
                }
            } else {
                tv_todo_fragment_title.text = ""
            }
            if (scrollY > height) {
                image_todo_main_to_top.visible()
            } else {
                image_todo_main_to_top.gone()
            }
            //判断是否滚动到底了 ，然后加载更多数据
            if (oldScrollY < scrollY) {//向下滑动
                determineWhetherScrollToTheBottom(scrollY)
            }
        }
        image_todo_main_to_top.setOnClickListener(this)
        image_todo_fragment_scan_code.setOnClickListener(this)
        tv_todo_fragment_publish.setOnClickListener(this)
        linear_main_todo_new_message_center_button.setOnClickListener(this)
        linear_main_todo_new_task_center_button.setOnClickListener(this)

        initBanner(screenWidth, cBannerHeight)
        initAppList()
        initTaskListView()
    }


    override fun lazyLoad() {
        XLog.info("lazyload......................................")
        mPresenter.loadHotPictureList()
        mPresenter.getMyAppList()
        if (isLoadMoreList) {
            XLog.info("data is loading more.....")
            return
        }
        if (!isRefreshNewsList) {
            isRefreshNewsList = true
            mPresenter.loadNewsList(O2.FIRST_PAGE_TAG)
        }
        if (!isRefreshTaskList) {
            isRefreshTaskList = true
            mPresenter.loadTaskList(O2.FIRST_PAGE_TAG)
        }
    }

    override fun onResume() {
        super.onResume()
        if (isLoadHotPictureList) {
            cBannerView.startTurning(3000)
        }
    }

    override fun onPause() {
        super.onPause()
        if (isLoadHotPictureList) {
            cBannerView.stopTurning()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.image_todo_main_to_top -> nested_scroll_todo_main_content.scrollTo(0, 0)
            R.id.image_todo_fragment_scan_code -> startScan()
            R.id.tv_todo_fragment_publish -> activity.go<StartProcessActivity>()
            R.id.linear_main_todo_new_message_center_button -> {
                currentType = BUSINESS_TYPE_MESSAGE_CENTER
                refreshRecyclerView()
            }
            R.id.linear_main_todo_new_task_center_button -> {
                currentType = BUSINESS_TYPE_WORK_CENTER
                isRedPointShow = false
                refreshRecyclerView()
            }
        }
    }

    private fun startScan() {
        PermissionRequester(activity)
                .request(Manifest.permission.CAMERA)
                .o2Subscribe {
                    onNext { (granted, shouldShowRequestPermissionRationale, deniedPermissions) ->
                        XLog.info("granted:$granted , shouldShowRequest:$shouldShowRequestPermissionRationale, denied:$deniedPermissions")
                        if (!granted) {
                            O2DialogSupport.openAlertDialog(activity, "需要摄像头权限才能进行扫一扫功能！")
                        } else {
                            activity.go<CaptureActivity>()
                        }
                    }
                }
    }


    override fun loadTaskList(list: List<TaskData>) {
        if (isRefreshTaskList) {
            taskList.clear()
            taskList.addAll(list)
            isRefreshTaskList = false
            swipe_refresh_todo_fragment?.isRefreshing = false
        } else if (isLoadMoreList) {
            taskList.addAll(list)
            isLoadMoreList = false
            hiddenObtainMoreDataAnimation()
        }
        if (!taskList.isEmpty()) {
            lastTaskId = taskList[taskList.size - 1].id
        }
        refreshRecyclerView()
    }


    override fun loadTaskListFail() {
        XToast.toastShort(activity, "获取任务列表失败！")
        if (isRefreshTaskList) {
            taskList.clear()
            isRefreshTaskList = false
            swipe_refresh_todo_fragment?.isRefreshing = false
        } else if (isLoadMoreList) {
            isLoadMoreList = false
            hiddenObtainMoreDataAnimation()
        }
        refreshRecyclerView()
    }

    override fun loadNewsList(list: List<CMSDocumentInfoJson>) {
        if (isRefreshNewsList) {
            newsList.clear()
            newsList.addAll(list)
            isRefreshNewsList = false
            swipe_refresh_todo_fragment?.isRefreshing = false
        } else if (isLoadMoreList) {
            newsList.addAll(list)
            isLoadMoreList = false
            hiddenObtainMoreDataAnimation()
        }
        if (!newsList.isEmpty()) {
            lastNewsId = newsList[newsList.size - 1].id
        }
        refreshRecyclerView()
    }

    override fun loadNewsListFail() {
        XToast.toastShort(activity, "获取信息列表失败！")
        if (isRefreshNewsList) {
            newsList.clear()
            isRefreshNewsList = false
            swipe_refresh_todo_fragment?.isRefreshing = false
        } else if (isLoadMoreList) {
            isLoadMoreList = false
            hiddenObtainMoreDataAnimation()
        }
        refreshRecyclerView()
    }

    override fun loadHotPictureList(list: List<HotPictureOutData>) {
        hotPictureList.clear()
        if (list.isEmpty()){
            linear_todo_banner_container?.gone()
            image_todo_banner_noData?.visible()
        }else {
            hotPictureList.addAll(list)
            cBannerView.notifyDataSetChanged()
            isLoadHotPictureList = true
            cBannerView.startTurning(3000)
            linear_todo_banner_container?.visible()
            image_todo_banner_noData?.gone()
        }
    }

    override fun loadHotPictureListFail() {
        hotPictureList.clear()
        isLoadHotPictureList = false
        linear_todo_banner_container?.gone()
        image_todo_banner_noData?.visible()
    }

    private val adapter: CommonRecycleViewAdapter<ToDoFragmentListViewItemVO> by lazy {
        object : CommonRecycleViewAdapter<ToDoFragmentListViewItemVO>(activity, itemList, R.layout.item_todo_fragment_list) {
            override fun convert(holder: CommonRecyclerViewHolder?, t: ToDoFragmentListViewItemVO?) {
                holder?.setText(R.id.tv_todo_fragment_task_title, t?.title ?: "")
                        ?.setText(R.id.tv_todo_fragment_task_type, t?.type ?: "")
                        ?.setText(R.id.tv_todo_fragment_task_date, t?.time ?: "")
                val newIcon = holder?.getView<ImageView>(R.id.image_todo_fragment_task_new)
                if (DateHelper.nowByFormate("yyyy-MM-dd") == t?.time) {
                    newIcon?.visible()
                } else {
                    newIcon?.gone()
                }
            }
        }

    }

    private fun initTaskListView() {
        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        linearLayoutManager.isSmoothScrollbarEnabled = true
        linearLayoutManager.isAutoMeasureEnabled = true
        recycler_todo_main_info_list.layoutManager = linearLayoutManager
        recycler_todo_main_info_list.removeItemDecoration(itemDecoration)
        recycler_todo_main_info_list.addItemDecoration(itemDecoration)
        recycler_todo_main_info_list.setHasFixedSize(true)
        recycler_todo_main_info_list.isNestedScrollingEnabled = false
        recycler_todo_main_info_list.adapter = adapter
        adapter.setOnItemClickListener { _, position ->
            if (itemList[position].businessType == BUSINESS_TYPE_MESSAGE_CENTER) {
                gotoCMSWebView(itemList[position].businessId, itemList[position].title)
            } else {
                startWorkActivity(itemList[position].title, itemList[position].businessId)
            }
        }

    }

    override fun setMyAppList(myAppList: ArrayList<MyAppListObject>) {
        XLog.info("setmy app list ......${myAppList.size}")
        appList.clear()
        appList.addAll(myAppList)
        val app = MyAppListObject()
        app.appTitle = getString(R.string.tab_todo_more)
        app.appId = ALL_APP_ID
        appList.add(app)
        appAdapter.notifyDataSetChanged()
    }

    private fun initAppList() {
        appAdapter.setOnItemClickListener { _, position ->
            IndexFragment.go(appList[position].appId!!, activity, appList[position].appTitle ?: "")
        }
        recycler_todo_main_app_list.adapter = appAdapter
        recycler_todo_main_app_list.layoutManager = GridLayoutManager(activity, 5)

    }

    private val appAdapter: CommonRecycleViewAdapter<MyAppListObject> by lazy {
        object : CommonRecycleViewAdapter<MyAppListObject>(activity, appList, R.layout.item_todofragment_app_list) {
            override fun convert(holder: CommonRecyclerViewHolder?, t: MyAppListObject?) {
                when {
                    ApplicationEnum.isNativeApplication(t?.appId) -> holder?.setImageViewResource(R.id.app_id, ApplicationEnum.getApplicationByKey(t?.appId).iconResId)
                    t?.appId == ALL_APP_ID -> holder?.setImageViewResource(R.id.app_id, R.mipmap.ic_todo_more)
                    else -> {
                        val bitmap = BitmapFactory.decodeFile(O2CustomStyle.processDefaultImagePath(activity))
                        if (bitmap != null) {
                            holder?.setImageViewBitmap(R.id.app_id, bitmap)
                        } else {
                            //holder?.setImageViewResource(R.id.app_id, R.mipmap.process_default)
                            if (t?.appId != null){
                                val portalIconUrl = APIAddressHelper.instance().getPortalIconUrl(t.appId!!)
                                val icon = holder?.getView<ImageView>(R.id.app_id)
                                if (icon !=null) {
                                    O2ImageLoaderManager.instance().showImage(icon, portalIconUrl, O2ImageLoaderOptions(placeHolder = R.mipmap.process_default))
                                }
                            }
                        }
                    }
                }
                holder?.setText(R.id.app_name_id, t?.appTitle)
            }
        }
    }

    private fun initBanner(screenWidth: Int, cBannerHeight: Int) {
        val cBannerLayoutParams = linear_todo_banner_container.layoutParams as LinearLayout.LayoutParams
        cBannerLayoutParams.height = cBannerHeight
        cBannerLayoutParams.width = screenWidth
        linear_todo_banner_container.layoutParams = cBannerLayoutParams
        cBannerView = ConvenientBanner(activity)
        cBannerView.isCanLoop = true
        cBannerView.layoutParams = LinearLayout.LayoutParams(screenWidth, cBannerHeight)
        linear_todo_banner_container.addView(cBannerView)

        val bannerNoDataViewLayoutParams = image_todo_banner_noData.layoutParams as LinearLayout.LayoutParams
        bannerNoDataViewLayoutParams.height = cBannerHeight
        bannerNoDataViewLayoutParams.width = screenWidth
        image_todo_banner_noData.layoutParams = bannerNoDataViewLayoutParams
        cBannerView.setPages({ ConvenientBannerImageHolderView() }, hotPictureList)
                .setPageIndicator(intArrayOf(R.mipmap.x_banner_indicator_blur, R.mipmap.x_banner_indicator_focus))
                .setOnItemClickListener { position ->
                    clickBannerItem(hotPictureList[position])
                }
    }

    private fun clickBannerItem(data: HotPictureOutData) {
        when {
            HotPictureApplicationEnum.BBS.key == data.application -> {
                XLog.debug("打开论坛帖子：" + data.title)
                activity.go<BBSWebViewSubjectActivity>(BBSWebViewSubjectActivity.startBundleData(data.infoId, data.title))
            }
            HotPictureApplicationEnum.CMS.key == data.application -> {
                XLog.debug("点击内容管理：" + data.title)
                gotoCMSWebView(data.infoId, data.title)
            }
            else -> XLog.debug("没有对应的应用：" + data.application)
        }

    }

    private fun determineWhetherScrollToTheBottom(scrollY: @ParameterName(name = "scrollY") Int) {
        if (itemList.size >= O2.DEFAULT_PAGE_NUMBER) {
            val childView = nested_scroll_todo_main_content.getChildAt(0)
            if (childView.measuredHeight <= nested_scroll_todo_main_content.height + scrollY) {
                XLog.debug("到底了。。。。。。")
                toObtainMoreData()
            }
        }

    }

    private fun toObtainMoreData() {
        if (!isLoadMoreList) {
            if (currentType == BUSINESS_TYPE_MESSAGE_CENTER) {
                if (!isRefreshNewsList) {
                    showObtainMoreDataAnimation()
                    isLoadMoreList = true
                    mPresenter.loadNewsList(lastNewsId)
                }
            } else {
                if (!isRefreshTaskList) {
                    showObtainMoreDataAnimation()
                    isLoadMoreList = true
                    mPresenter.loadTaskList(lastTaskId)
                }
            }
        }
    }


    private fun refreshRecyclerView() {
        shimmer_snippet_content?.gone()
        itemList.clear()
        if (currentType == BUSINESS_TYPE_MESSAGE_CENTER) {
            tv_no_data?.text = getString(R.string.recycler_no_data_cool)
            tv_main_todo_new_message_center?.setTextColor(FancySkinManager.instance().getColor(activity, R.color.z_color_primary))
            view_main_todo_new_message_center_divider?.visible()
            tv_main_todo_new_task_center?.setTextColor(FancySkinManager.instance().getColor(activity, R.color.z_color_text_primary))
            view_main_todo_new_task_center_divider?.gone()
            newsList.map {
                itemList.add(it.copyToTodoListItem())
            }
            if (newsList.isEmpty()) {
                tv_no_data?.visible()
                recycler_todo_main_info_list?.gone()
            } else {
                tv_no_data?.gone()
                recycler_todo_main_info_list?.visible()
            }
        } else {
            tv_no_data?.text = getString(R.string.recycler_no_data_wonderful_work)
            tv_main_todo_new_task_center?.setTextColor(FancySkinManager.instance().getColor(activity, R.color.z_color_primary))
            view_main_todo_new_task_center_divider?.visible()
            tv_main_todo_new_message_center?.setTextColor(FancySkinManager.instance().getColor(activity, R.color.z_color_text_primary))
            view_main_todo_new_message_center_divider?.gone()
            taskList.map {
                itemList.add(it.copyToTodoListItem())
            }
            if (taskList.isEmpty()) {
                tv_no_data.visible()
                recycler_todo_main_info_list?.gone()
            } else {
                tv_no_data.gone()
                recycler_todo_main_info_list?.visible()
            }
        }
        image_main_todo_new_task_center_red_point?.gone()
        if (isRedPointShow && taskList.size > 0) {
            image_main_todo_new_task_center_red_point?.visible()
        }


        adapter.notifyDataSetChanged()
    }

    private fun showObtainMoreDataAnimation() {
        linear_todo_main_obtain_more_data_view?.visible()
    }

    private fun hiddenObtainMoreDataAnimation() {
        linear_todo_main_obtain_more_data_view?.gone()
    }


    private fun startWorkActivity(title: String, businessId: String) {
        XLog.debug("goto task work web view page id:$businessId , title: $title")
        val bundle = Bundle()
        bundle.putString(TaskWebViewActivity.WORK_WEB_VIEW_WORK, businessId)
        bundle.putString(TaskWebViewActivity.WORK_WEB_VIEW_TITLE, title)
        (activity as MainActivity).go<TaskWebViewActivity>(bundle)

    }

    private fun gotoCMSWebView(businessId: String, title: String) {
        XLog.debug("goto cms web view page id:$businessId , title: $title")
        activity.go<CMSWebViewActivity>(CMSWebViewActivity.startBundleData(businessId, title))
    }

}