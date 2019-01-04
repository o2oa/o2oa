package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.process


import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import kotlinx.android.synthetic.main.activity_task_complete_search.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecycleViewAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.service.PictureLoaderService
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.o2.TaskCompleteData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.CircleImageView




class TaskCompletedSearchActivity : BaseMVPActivity<TaskCompletedSearchContract.View, TaskCompletedSearchContract.Presenter>(), TaskCompletedSearchContract.View {
    override var mPresenter: TaskCompletedSearchContract.Presenter = TaskCompletedSearchPresenter()
    override fun layoutResId(): Int = R.layout.activity_task_complete_search

    var pictureLoaderService: PictureLoaderService? = null
    var lastId: String = ""
    var searchKey: String = ""
    var isRefresh = false
    var isLoading = false
    val resultList = ArrayList<TaskCompleteData>()
    val adapter: CommonRecycleViewAdapter<TaskCompleteData> by lazy {
        object : CommonRecycleViewAdapter<TaskCompleteData>(this, resultList, R.layout.item_todo_list) {
            override fun convert(holder: CommonRecyclerViewHolder?, data: TaskCompleteData?) {
                val time = data?.startTime?.substring(0, 10) ?:""
                holder?.setText(R.id.todo_card_view_title_id, data?.title)
                        ?.setText(R.id.todo_card_view_content_id, "【${data?.processName}】")
                        ?.setText(R.id.todo_card_view_node_id, data?.activityName)
                        ?.setText(R.id.todo_card_view_time_id, time)
                val icon = holder?.getView<CircleImageView>(R.id.todo_card_view_icon_id)
                icon?.setImageResource(R.mipmap.icon_process_app_default)
                icon?.tag = data?.application
                loadApplicationIcon(holder?.convertView, data?.application)
            }
        }
    }

    override fun afterSetContentView(savedInstanceState: Bundle?) {
        val toolBar = findViewById<Toolbar>(R.id.toolbar_task_completed_search_top_bar)
        toolBar?.title = ""
        setSupportActionBar(toolBar)
        toolBar?.setNavigationIcon(R.mipmap.ic_back_mtrl_white_alpha)
        toolBar?.setNavigationOnClickListener { finish() }
        edit_task_completed_search_key.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                if (s?.length == 0) {
                    searchKey = ""
                    cleanResultList()
                } else {
                    searchTaskCompletedOnLine(s)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })
        //SwipeRefreshLayout 下拉刷新控件的颜色 最多4个
        refresh_task_completed_layout.setColorSchemeResources(R.color.z_color_refresh_scuba_blue,
                R.color.z_color_refresh_red, R.color.z_color_refresh_purple, R.color.z_color_refresh_orange)
        refresh_task_completed_layout.recyclerViewPageNumber = O2.DEFAULT_PAGE_NUMBER
        refresh_task_completed_layout.setOnRefreshListener {
            if(!isLoading && !isRefresh){
                searchTaskCompleted(true)
                isRefresh = true
            }
        }
        refresh_task_completed_layout.setOnLoadMoreListener{
            if (!isLoading && !isRefresh) {
                if (TextUtils.isEmpty(lastId)) {
                    searchTaskCompleted(true)
                } else {
                    searchTaskCompleted(false)
                }
                isLoading = true
            }
        }

        adapter.setOnItemClickListener { _, position ->
            showTaskCompletedWorkFragment(resultList[position].id)
        }
        recycler_task_completed_search_list.adapter = adapter
        recycler_task_completed_search_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    override fun onResume() {
        super.onResume()
        pictureLoaderService = PictureLoaderService(this)
    }

    override fun onPause() {
        super.onPause()
        pictureLoaderService?.close()
    }

    override fun searchFail() {
        finishAnimation()
        cleanResultList()
    }

    override fun searchResult(list: List<TaskCompleteData>) {
        if (isRefresh) {
            resultList.clear()
            resultList.addAll(list)
        }else {
            resultList.addAll(list)
        }
        if (resultList.size>0) {
            lastId = resultList[resultList.size-1].id
        }
        adapter.notifyDataSetChanged()
        finishAnimation()
    }

    private var taskCompletedWorkListFragment: TaskCompletedWorkListFragment? = null
    private fun showTaskCompletedWorkFragment(taskId: String) {
        if (taskCompletedWorkListFragment==null) {
            taskCompletedWorkListFragment = TaskCompletedWorkListFragment.createFragmentInstance(taskId)
        }else{
            taskCompletedWorkListFragment?.startLoad(taskId)
        }
        taskCompletedWorkListFragment?.show(supportFragmentManager, TaskCompletedWorkListFragment.TASK_COMPLETED_WORK_LIST_FRAGMENT_TAG)
    }

    private fun searchTaskCompleted(flag: Boolean) {
        if (flag) {
            mPresenter.searchTaskCompleted(O2.FIRST_PAGE_TAG, searchKey)
        }else {
            mPresenter.searchTaskCompleted(lastId, searchKey)
        }
    }

    /**
     * 查询已办
     * @param s
     */
    private fun searchTaskCompletedOnLine(s: Editable?) {
        val key = s.toString()
        if (TextUtils.isEmpty(key)) {
            searchKey = ""
            cleanResultList()
            return
        } else {
            XLog.debug("查询已办 key：" + key)
            searchKey = key
            searchTaskCompleted(true)
        }
    }

    /**
     * 清除列表
     */
    private fun cleanResultList() {
        resultList.clear()
        adapter.notifyDataSetChanged()
    }

    private fun finishAnimation() {
        if (isRefresh) {
            refresh_task_completed_layout.isRefreshing = false
            isRefresh = false
        }
        if (isLoading) {
            refresh_task_completed_layout.setLoading(false)
            isLoading = false
        }
    }

    fun loadApplicationIcon(convertView: View?, application: String?) {
        pictureLoaderService?.loadProcessAppIcon(convertView, application)
    }
}
