package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.bbs.section


import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_bbs_section.*
import kotlinx.android.synthetic.main.content_bbs_section.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.bbs.publish.BBSPublishSubjectActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.bbs.view.BBSWebViewSubjectActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecycleViewAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.bbs.SubjectInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.MiscUtilK
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.go
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.inVisible
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.imageloader.O2ImageLoaderManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.CircleImageView


class BBSSectionActivity : BaseMVPActivity<BBSSectionContract.View, BBSSectionContract.Presenter>(), BBSSectionContract.View {
    override var mPresenter: BBSSectionContract.Presenter = BBSSectionPresenter()
    override fun layoutResId(): Int = R.layout.activity_bbs_section

    companion object {
        val BBS_SECTION_ID = "BBS_SECTION_ID"
        val BBS_SECTION_NAME = "BBS_SECTION_NAME"

        fun startBundleData(sectionId: String, sectionName: String): Bundle {
            val bundle = Bundle()
            bundle.putString(BBS_SECTION_ID, sectionId)
            bundle.putString(BBS_SECTION_NAME, sectionName)
            return bundle
        }
    }

    var isRefresh = true
    var isCollected = false
    var pageNumber = 1
    var sectionId = ""
    var sectionName = ""
    val subjectList = ArrayList<SubjectInfoJson>()
    val adapter: CommonRecycleViewAdapter<SubjectInfoJson> by lazy {
        object : CommonRecycleViewAdapter<SubjectInfoJson>(this, subjectList, R.layout.item_bbs_section_content) {
            override fun convert(holder: CommonRecyclerViewHolder?, t: SubjectInfoJson?) {
                val avatar = holder?.getView<CircleImageView>(R.id.image_item_bbs_section_content_avatar)
                avatar?.setImageResource(R.mipmap.contact_icon_avatar)
                if (avatar != null) {
                    val url = APIAddressHelper.instance().getPersonAvatarUrlWithId(t?.creatorName ?: "")
                    O2ImageLoaderManager.instance().showImage(avatar, url)
                }
                val topIcon = holder?.getView<ImageView>(R.id.image_item_bbs_section_content_top)
                if (t!=null && t.isTopSubject) {
                    topIcon?.visible()
                } else {
                    topIcon?.inVisible()
                }
                holder?.setText(R.id.tv_item_bbs_section_content_title, t?.title ?: "")
                        ?.setText(R.id.tv_item_bbs_section_content_creator, t?.creatorNameShort?:t?.creatorName)
                        ?.setText(R.id.tv_item_bbs_section_content_time, t?.latestReplyTime ?: "")
                        ?.setText(R.id.tv_item_bbs_section_content_view, (t?.viewTotal ?: 0).toString())
                        ?.setText(R.id.tv_item_bbs_section_content_reply, (t?.replyTotal ?: 0).toString())

            }
        }
    }

    override fun afterSetContentView(savedInstanceState: Bundle?) {
        sectionId = intent.extras?.getString(BBS_SECTION_ID) ?: ""
        sectionName = intent.extras?.getString(BBS_SECTION_NAME) ?: ""

        setupToolBar(sectionName, true)

        layout_bbs_section_content_refresh.setColorSchemeResources(R.color.z_color_refresh_scuba_blue,
                R.color.z_color_refresh_red, R.color.z_color_refresh_purple, R.color.z_color_refresh_orange)
        layout_bbs_section_content_refresh.recyclerViewPageNumber = O2.DEFAULT_PAGE_NUMBER
        layout_bbs_section_content_refresh.setOnRefreshListener {
            isRefresh = true
            refreshData()
        }
        layout_bbs_section_content_refresh.setOnLoadMoreListener {
            isRefresh = false
            loadMoreData()
        }
        recycler_bbs_section_content.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        //recycler_bbs_section_content.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST))
        recycler_bbs_section_content.adapter = adapter
        adapter.setOnItemClickListener { _, position ->
            val subject = subjectList[position]
            XLog.debug("subject title:${subject.title}, id:${subject.id}")
            go<BBSWebViewSubjectActivity>(BBSWebViewSubjectActivity.startBundleData(subject.id, subject.title))
        }

        mPresenter.canPublishSubject(sectionId)
        mPresenter.whetherTheSectionHasBeenCollected(sectionId)
    }

    override fun onResume() {
        super.onResume()
        //初始化刷新数据
        MiscUtilK.swipeRefreshLayoutRun(layout_bbs_section_content_refresh, this)
        isRefresh = true
        refreshData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_bbs_section, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        if (isCollected) {
            menu?.findItem(R.id.menu_bbs_collect)?.setIcon(R.mipmap.menu_star_full)
            menu?.findItem(R.id.menu_bbs_collect)?.title = getString(R.string.bbs_section_cancel_collect)
        } else {
            menu?.findItem(R.id.menu_bbs_collect)?.setIcon(R.mipmap.menu_star_empty)
            menu?.findItem(R.id.menu_bbs_collect)?.title = getString(R.string.bbs_section_collect)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_bbs_collect -> {
                mPresenter.collectOrCancelCollectSection(sectionId, isCollected)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun collectOrCancelCollectSectionResponse(result: Boolean, message: String) {
        if (result) {
            isCollected = !isCollected
            refreshCollectMenu()
        }
        XToast.toastShort(this, message)
    }

    override fun hasBeenCollected(result: Boolean) {
        isCollected = result
        refreshCollectMenu()
    }

    override fun loadFail(message: String) {
        finishLoading()
        XToast.toastShort(this, message)
    }

    override fun loadSuccess(list: List<SubjectInfoJson>) {
        finishLoading()
        if (isRefresh) {
            subjectList.clear()
        }
        subjectList.addAll(list)
        adapter.notifyDataSetChanged()
    }

    override fun publishPermission(result: Boolean) {
        if (result) {
            fab_bbs_section_post.visible()
            fab_bbs_section_post.setOnClickListener {
                go<BBSPublishSubjectActivity>(BBSPublishSubjectActivity.startBundleData(sectionId))
            }
        } else {
            fab_bbs_section_post.gone()
        }
    }

    private fun loadMoreData() {
        pageNumber++
        mPresenter.loadSubjectList(sectionId, pageNumber)
    }

    private fun refreshData() {
        pageNumber = 1
        mPresenter.loadSubjectList(sectionId, pageNumber)
    }

    private fun refreshCollectMenu() {
        // getWindow().invalidatePanelMenu(Window.FEATURE_OPTIONS_PANEL);
        invalidateOptionsMenu()
    }

    private fun finishLoading() {
        if (isRefresh) {
            layout_bbs_section_content_refresh.isRefreshing = false
        } else {
            layout_bbs_section_content_refresh.setLoading(false)
        }
    }
}
