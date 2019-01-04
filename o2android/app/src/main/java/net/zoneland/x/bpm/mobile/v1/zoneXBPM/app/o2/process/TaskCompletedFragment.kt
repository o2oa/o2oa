package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.process

import android.graphics.BitmapFactory
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import kotlinx.android.synthetic.main.fragment_todo_task_complete.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2CustomStyle
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPViewPagerFragment
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.SwipeRefreshCommonRecyclerViewAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.TaskCompleteData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.CircleImageView
import org.jetbrains.anko.dip


class TaskCompletedFragment : BaseMVPViewPagerFragment<TaskCompletedContract.View, TaskCompletedContract.Presenter>(), TaskCompletedContract.View {

    override var mPresenter: TaskCompletedContract.Presenter = TaskCompletedPresenter()

    override fun layoutResId(): Int  = R.layout.fragment_todo_task_complete

    companion object {
        val APPLICATION_ID_KEY = "APPLICATION_ID_KEY_TASK_FRAGMENT"
    }

    var application: String = ""
    var lastTaskId: String = ""
    var isRefresh = false
    var isLoading = false
    val taskDatas = ArrayList<TaskCompleteData>()
    val adapter: SwipeRefreshCommonRecyclerViewAdapter<TaskCompleteData> by lazy {
        object : SwipeRefreshCommonRecyclerViewAdapter<TaskCompleteData>(activity, taskDatas, R.layout.item_todo_list) {
            override fun convert(holder: CommonRecyclerViewHolder?, data: TaskCompleteData?) {
                val time = data?.startTime?.substring(0, 10) ?:""
                holder?.setText(R.id.todo_card_view_title_id, data?.title)
                        ?.setText(R.id.todo_card_view_content_id, "【${data?.processName}】")
                        ?.setText(R.id.todo_card_view_node_id, data?.activityName)
                        ?.setText(R.id.todo_card_view_time_id, time)
                val icon = holder?.getView<CircleImageView>(R.id.todo_card_view_icon_id)
                val bitmap = BitmapFactory.decodeFile(O2CustomStyle.processDefaultImagePath(activity))
                icon?.setImageBitmap(bitmap)
                icon?.tag = data?.application
                (activity as TaskCompletedListActivity).loadApplicationIcon(holder?.convertView, data?.application)
            }
        }
    }



    override fun initUI() {
        application = arguments.getString(APPLICATION_ID_KEY) ?: ""
        todo_task_complete_refresh_layout_id.touchSlop =  activity.dip( 70f)
        todo_task_complete_refresh_layout_id.setColorSchemeResources(R.color.z_color_refresh_scuba_blue,
                R.color.z_color_refresh_red, R.color.z_color_refresh_purple, R.color.z_color_refresh_orange)
        todo_task_complete_refresh_layout_id.recyclerViewPageNumber = O2.DEFAULT_PAGE_NUMBER
        todo_task_complete_refresh_layout_id.setOnRefreshListener {
            if (!isLoading && !isRefresh) {
                getDatas(true)
                isRefresh = true
            }
        }
        todo_task_complete_refresh_layout_id.setOnLoadMoreListener {
            if (!isLoading && !isRefresh) {
                if (TextUtils.isEmpty(lastTaskId)) {
                    getDatas(true)
                } else {
                    getDatas(false)
                }
                isLoading = true
            }
        }

        todo_task_complete_list_id.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        todo_task_complete_list_id.adapter = adapter
        adapter.setOnItemClickListener { _, position ->
            (activity as TaskCompletedListActivity).showTaskCompletedWorkFragment(taskDatas[position].id)
        }
    }


    override fun lazyLoad() {
        getDatas(true)
        isRefresh = true
    }


    override fun findTaskCompletedList(list: List<TaskCompleteData>) {
        if (isRefresh) {
            taskDatas.clear()
            taskDatas.addAll(list)
            if (taskDatas.size > 0) {
                lastTaskId = taskDatas[taskDatas.size-1].id
                tv_no_data.gone()
                todo_task_complete_refresh_layout_id.visible()
                adapter.notifyDataSetChanged()
            }else {
                tv_no_data.visible()
                todo_task_complete_refresh_layout_id.gone()
            }
        }else if (isLoading) {
            taskDatas.addAll(list)
            if (taskDatas.size > 0) {
                lastTaskId = taskDatas[taskDatas.size-1].id
                tv_no_data.gone()
                todo_task_complete_refresh_layout_id.visible()
                adapter.notifyDataSetChanged()
            }else {
                tv_no_data.visible()
                todo_task_complete_refresh_layout_id.gone()
            }
        }
        finishAnimation()
    }

    override fun findTaskCompletedListFail() {
        XToast.toastShort(activity, "获取已办列表失败")
        taskDatas.clear()
        adapter.notifyDataSetChanged()
        tv_no_data.visible()
        todo_task_complete_refresh_layout_id.gone()
        finishAnimation()
    }


    private fun finishAnimation() {
        if (isRefresh) {
            todo_task_complete_refresh_layout_id.isRefreshing = false
            isRefresh = false
        }
        if (isLoading) {
            todo_task_complete_refresh_layout_id.setLoading(false)
            isLoading = false
        }
    }

    private fun getDatas(flag: Boolean) {
        if (flag) {
            mPresenter.findTaskCompletedList(application, O2.FIRST_PAGE_TAG, O2.DEFAULT_PAGE_NUMBER)
        }else {
            mPresenter.findTaskCompletedList(application, lastTaskId, O2.DEFAULT_PAGE_NUMBER)
        }
    }
}
