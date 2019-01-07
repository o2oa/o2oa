package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.cms.application

import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import kotlinx.android.synthetic.main.fragment_cms_category.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.SwipeRefreshCommonRecyclerViewAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.DateHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.CircleImageView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPViewPagerFragment
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.cms.view.CMSWebViewActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.cms.CMSCategoryInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.cms.CMSDocumentInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.go
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.imageloader.O2ImageLoaderManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.DividerItemDecoration
import org.jetbrains.anko.dip


class CMSCategoryFragment : BaseMVPViewPagerFragment<CMSCategoryContract.View, CMSCategoryContract.Presenter>(), CMSCategoryContract.View {

    override var mPresenter: CMSCategoryContract.Presenter = CMSCategoryPresenter()

    override fun layoutResId(): Int = R.layout.fragment_cms_category

    companion object {
        val CMS_CATEGORY_OBJECT_KEY = "CMS_CATEGORY_OBJECT_KEY"
    }


    var info: CMSCategoryInfoJson? = null
    var lastId:String = ""
    var isRefresh = false//是否正在刷新
    var isLoading = false//是否正在加载
    val documentList = ArrayList<CMSDocumentInfoJson>()
    val adapter: SwipeRefreshCommonRecyclerViewAdapter<CMSDocumentInfoJson> by lazy {
        object : SwipeRefreshCommonRecyclerViewAdapter<CMSDocumentInfoJson>(activity, documentList, R.layout.item_cms_category_document_list) {
            override fun convert(holder: CommonRecyclerViewHolder?, t: CMSDocumentInfoJson?) {
                val publishTime = DateHelper.convertStringToDate(t?.publishTime)
                holder?.setText(R.id.tv_item_cms_category_document_name, t?.title?:"")
                        ?.setText(R.id.tv_item_cms_category_document_time, DateHelper.friendlyTime(publishTime))
                val avatar = holder?.getView<CircleImageView>(R.id.image_item_cms_category_document_creator)
                avatar?.setImageResource(R.mipmap.contact_icon_avatar)
                if (t?.creatorPerson != null && avatar!=null) {
                    val url = APIAddressHelper.instance().getPersonAvatarUrlWithId(t?.creatorPerson)
                    O2ImageLoaderManager.instance().showImage(avatar, url)
                }
            }
        }
    }

    override fun initUI() {
        info = arguments.getSerializable(CMS_CATEGORY_OBJECT_KEY) as CMSCategoryInfoJson

        if (info == null) {
            XLog.error("没有接收到分类对象。。。。。。")
            tv_no_data.visible()
            refresh_cms_category_layout.gone()
            return
        }

        refresh_cms_category_layout.touchSlop = activity.dip(70f)
        refresh_cms_category_layout.setColorSchemeResources(R.color.z_color_refresh_scuba_blue,
                R.color.z_color_refresh_red, R.color.z_color_refresh_purple, R.color.z_color_refresh_orange)
        refresh_cms_category_layout.recyclerViewPageNumber = O2.DEFAULT_PAGE_NUMBER
        refresh_cms_category_layout.setOnRefreshListener {
            if (!isLoading && !isRefresh) {
                getDatas(true)
                isRefresh = true
            }
        }
        refresh_cms_category_layout.setOnLoadMoreListener {
            if (!isLoading && !isRefresh) {
                if (TextUtils.isEmpty(lastId)) {
                    getDatas(true)
                } else {
                    getDatas(false)
                }
                isLoading = true
            }
        }

        recycler_cms_category_document_list.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recycler_cms_category_document_list.adapter = adapter
        recycler_cms_category_document_list.addItemDecoration(DividerItemDecoration(activity, DividerItemDecoration.VERTICAL_LIST))
        adapter.setOnItemClickListener { view, position ->
            val document = documentList[position]
            activity.go<CMSWebViewActivity>(CMSWebViewActivity.startBundleData(document.id, document.title))
        }

    }

    override fun lazyLoad() {
        getDatas(true)
        isRefresh = true
        XLog.debug("lazyLoad finish")
    }

    override fun loadFail() {
        XToast.toastShort(activity, "获取数据异常，请检查网络情况！")
        linear_shimmer_content.gone()
        tv_no_data.visible()
        refresh_cms_category_layout.gone()
        documentList.clear()
        adapter.notifyDataSetChanged()
        finishAnimation()
    }

    override fun loadSuccess(list: List<CMSDocumentInfoJson>) {
        linear_shimmer_content.gone()
        if (isRefresh) {
            documentList.clear()
            documentList.addAll(list)
        }else if (isLoading) {
            documentList.addAll(list)
        }
        if (documentList.size> 0) {
            tv_no_data.gone()
            refresh_cms_category_layout.visible()
            lastId = documentList[documentList.size - 1].id
        }else {
            refresh_cms_category_layout.gone()
            tv_no_data.visible()
        }
        adapter.notifyDataSetChanged()
        finishAnimation()
    }

    private fun getDatas(b: Boolean) {
        if (info == null) {
            XToast.toastShort(activity, "数据对象为空,无法查询数据")
            return
        }
        if (b) {//刷新
            mPresenter.findDocumentByPage(info!!.id, O2.FIRST_PAGE_TAG)
        }else{//加载
            mPresenter.findDocumentByPage(info!!.id, lastId)
        }
    }

    private fun finishAnimation() {
        if (isRefresh) {
            refresh_cms_category_layout.isRefreshing = false
            isRefresh = false
        }
        if (isLoading) {
            refresh_cms_category_layout.setLoading(false)
            isLoading = false
        }
    }

}
