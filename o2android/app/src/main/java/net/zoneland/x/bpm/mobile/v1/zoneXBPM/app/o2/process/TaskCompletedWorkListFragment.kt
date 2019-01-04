package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.process

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_task_completed_work_list.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.webview.TaskWebViewActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecycleViewAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.TaskCompleteInfoDataWithControl
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.Work
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.WorkCompleted
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.WorkVO
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.go
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible

/**
 * Created by fancyLou on 02/05/2018.
 * Copyright © 2018 O2. All rights reserved.
 */

class TaskCompletedWorkListFragment : DialogFragment(), TaskCompletedWorkListContract.View {

    var mPresenter: TaskCompletedWorkListPresenter = TaskCompletedWorkListPresenter()

    var taskId: String? = null
    private val mWorkList: ArrayList<WorkVO> = ArrayList()
    private val adapter: CommonRecycleViewAdapter<WorkVO> by lazy {
        object : CommonRecycleViewAdapter<WorkVO>(activity, mWorkList, R.layout.item_task_completed_work_list) {
            override fun convert(holder: CommonRecyclerViewHolder?, t: WorkVO?) {
                if (t != null) {
                    val content = if (t is Work) {
                        "文件于 ${t.startTime} 流转至 ${t.activityName} "
                    } else if (t is WorkCompleted) {
                        "${t.title} 文件于 ${t.completedTime} 流转完成"
                    } else {
                        ""
                    }
                    holder?.setText(R.id.tv_item_task_completed_work_list_content, content)

                }
            }
        }
    }


    companion object {
        val TASK_COMPLETED_WORK_LIST_FRAGMENT_TAG = "TaskCompletedWorkListFragment"
        val TASKCOMPLETED_ID_KEY = ""
        fun createFragmentInstance(taskId: String): TaskCompletedWorkListFragment {
            val fragment = TaskCompletedWorkListFragment()
            val arguments = Bundle()
            arguments.putString(TASKCOMPLETED_ID_KEY, taskId)
            fragment.arguments = arguments
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(android.app.DialogFragment.STYLE_NO_FRAME, R.style.customStyleDialogStyle) //NO_FRAME就是dialog无边框，0指的是默认系统Theme
        mPresenter.attachView(this)
        taskId = arguments.getString(TASKCOMPLETED_ID_KEY)
    }

    override fun onStart() {
        super.onStart()
        val window = dialog.window
        val metrics = resources.displayMetrics
        val width = metrics.widthPixels //DialogSearch的宽
        val height = (metrics.heightPixels * 0.85).toInt() //DialogSearch的宽
        window!!.setLayout(width, height)
        window.setGravity(Gravity.BOTTOM)
        window.setWindowAnimations(R.style.DialogEmptyAnimation)//取消过渡动画 , 使DialogSearch的出现更加平滑
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_task_completed_work_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        image_fragment_task_completed_work_list_close.setOnClickListener {
            closeSelf()
        }

        recyclerView_fragment_task_completed_work_list.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recyclerView_fragment_task_completed_work_list.adapter = adapter
        adapter.setOnItemClickListener { _, position ->
            val work = mWorkList[position]
            if (work is Work) {

                activity.go<TaskWebViewActivity>(TaskWebViewActivity.start(work.id ?: "", "", work.title
                        	                        ?: ""))
                closeSelf()
            }
            if (work is WorkCompleted) {
                activity.go<TaskWebViewActivity>(TaskWebViewActivity.start("", work.id ?: "",work.title
                        	                        ?: ""))
                closeSelf()
            }
        }
        if (!TextUtils.isEmpty(taskId)) {
            circleProgressBar_task_completed_work.visible()
            mPresenter.loadTaskCompleteInfo(taskId!!)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mPresenter.detachView()
    }


    override fun loadWorkCompletedInfo(info: TaskCompleteInfoDataWithControl) {
        circleProgressBar_task_completed_work.gone()
        tv_fragment_task_completed_work_list_application?.text = info.applicationName ?: ""
        tv_fragment_task_completed_work_list_title?.text = formatTitle(info.title, info.processName)
        tv_fragment_task_completed_work_list_node?.text = info.activityName ?: ""
        if (activity is TaskCompletedSearchActivity) {
            (activity as TaskCompletedSearchActivity).loadApplicationIcon(image_fragment_task_completed_work_list_logo, info.application)
        } else if (activity is TaskCompletedListActivity) {
            (activity as TaskCompletedListActivity).loadApplicationIcon(image_fragment_task_completed_work_list_logo, info.application)
        }
        changeListData(info.workList, info.workCompletedList)
    }


    override fun loadWorkCompletedInfoFail() {
        XToast.toastShort(activity, "获取已办详细数据异常！")
        closeSelf()
    }


    fun startLoad(taskId: String) {
        circleProgressBar_task_completed_work.visible()
        this.taskId = taskId
        mPresenter.loadTaskCompleteInfo(taskId)
    }


    private fun changeListData(workList: List<Work>?, workCompletedList: List<WorkCompleted>?) {
        mWorkList.clear()
        workList?.map {
            mWorkList.add(it)
        }
        workCompletedList?.map {
            mWorkList.add(it)
        }
        adapter.notifyDataSetChanged()

    }

    private fun formatTitle(title: String?, processName: String?): CharSequence? = "[$processName]$title"

    private fun closeSelf() {
        dismiss()
    }
}

