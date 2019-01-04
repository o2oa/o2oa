package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.process


import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import kotlinx.android.synthetic.main.activity_read_complete.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2CustomStyle
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.webview.TaskWebViewActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.SwipeRefreshCommonRecyclerViewAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.service.PictureLoaderService
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.ReadCompleteData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.MiscUtilK
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.go
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.CircleImageView
import org.jetbrains.anko.dip


class ReadCompletedListActivity : BaseMVPActivity<ReadCompletedListContract.View, ReadCompletedListContract.Presenter>(), ReadCompletedListContract.View {
    override var mPresenter: ReadCompletedListContract.Presenter = ReadCompletedListPresenter()


    override fun layoutResId(): Int = R.layout.activity_read_complete

    var pictureLoaderService: PictureLoaderService? = null
    var application: String = "-1"
    var lastTaskId: String = ""
    var isRefresh = false
    var isLoading = false
    val itemList = ArrayList<ReadCompleteData>()
    val adapter: SwipeRefreshCommonRecyclerViewAdapter<ReadCompleteData> by lazy {
        object : SwipeRefreshCommonRecyclerViewAdapter<ReadCompleteData>(this, itemList, R.layout.item_todo_list) {
            override fun convert(holder: CommonRecyclerViewHolder?, data: ReadCompleteData?) {
                val time = data?.startTime?.substring(0, 10) ?:""
                holder?.setText(R.id.todo_card_view_title_id, data?.title)
                        ?.setText(R.id.todo_card_view_content_id, "【${data?.processName}】")
                        ?.setText(R.id.todo_card_view_node_id, data?.activityName)
                        ?.setText(R.id.todo_card_view_time_id, time)
                val icon = holder?.getView<CircleImageView>(R.id.todo_card_view_icon_id)
                val bitmap = BitmapFactory.decodeFile(O2CustomStyle.processDefaultImagePath(this@ReadCompletedListActivity))
                icon?.setImageBitmap(bitmap)
                icon?.tag = data?.application
                loadApplicationIcon(holder?.convertView, data?.application)
            }
        }
    }

    override fun afterSetContentView(savedInstanceState: Bundle?) {
        setupToolBar(getString(R.string.tab_todo_read_complete), true)

        todo_read_complete_refresh_layout_id.touchSlop = dip( 70f)
        todo_read_complete_refresh_layout_id.setColorSchemeResources(R.color.z_color_refresh_scuba_blue,
                R.color.z_color_refresh_red, R.color.z_color_refresh_purple, R.color.z_color_refresh_orange)
        todo_read_complete_refresh_layout_id.recyclerViewPageNumber = O2.DEFAULT_PAGE_NUMBER
        todo_read_complete_refresh_layout_id.setOnRefreshListener{
            if(!isLoading && !isRefresh){
                getDatas(true)
                isRefresh = true
            }
        }
        todo_read_complete_refresh_layout_id.setOnLoadMoreListener {
            if(!isLoading && !isRefresh){
                if (TextUtils.isEmpty(lastTaskId)) {
                    getDatas(true)
                } else {
                    getDatas(false)
                }
                isLoading = true
            }
        }

        todo_read_complete_list_id.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        todo_read_complete_list_id.adapter = adapter
        adapter.setOnItemClickListener { _, position ->
            go<TaskWebViewActivity>(TaskWebViewActivity.start(itemList[position].work,
                    	                    itemList[position].workCompleted, itemList[position].title))
        }

        //初始化刷新数据
        getDatas(true)
        isRefresh = true
        MiscUtilK.swipeRefreshLayoutRun(todo_read_complete_refresh_layout_id, this)
    }

    override fun onResume() {
        super.onResume()
        pictureLoaderService = PictureLoaderService(this)
    }


    override fun onPause() {
        super.onPause()
        pictureLoaderService?.close()
    }

    override fun returnReadCompletedList(list: List<ReadCompleteData>) {
        if (isRefresh) {
            itemList.clear()
            itemList.addAll(list)
            if (itemList.size>0) {
                lastTaskId = itemList[itemList.size-1].id
                tv_no_data.gone()
                todo_read_complete_refresh_layout_id.visible()
            }else {
                todo_read_complete_refresh_layout_id.gone()
                tv_no_data.visible()
            }
            adapter.notifyDataSetChanged()
        }else if (isLoading) {
            itemList.addAll(list)
            if (itemList.size>0) {
                lastTaskId = itemList[itemList.size-1].id
                tv_no_data.gone()
                todo_read_complete_refresh_layout_id.visible()
            }else {
                todo_read_complete_refresh_layout_id.gone()
                tv_no_data.visible()
            }
            adapter.notifyDataSetChanged()
        }
        finishAnimation()
    }

    override fun finishLoading() {
        finishAnimation()
    }

    private fun finishAnimation() {
        if (isRefresh) {
            todo_read_complete_refresh_layout_id.isRefreshing = false
            isRefresh = false
        }
        if (isLoading) {
            todo_read_complete_refresh_layout_id.setLoading(false)
            isLoading = false
        }
    }

    private fun getDatas(b: Boolean) {
        if (b) {
            mPresenter.findReadCompletedList(application, O2.FIRST_PAGE_TAG, O2.DEFAULT_PAGE_NUMBER)
        }else {
            mPresenter.findReadCompletedList(application, lastTaskId, O2.DEFAULT_PAGE_NUMBER)
        }
    }

    private fun loadApplicationIcon(convertView: View?, application: String?) {
        pictureLoaderService?.loadProcessAppIcon(convertView, application)
    }

}
