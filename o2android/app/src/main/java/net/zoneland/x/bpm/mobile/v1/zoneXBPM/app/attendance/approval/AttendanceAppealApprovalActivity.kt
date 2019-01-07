package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.attendance.approval


import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.widget.CheckBox
import kotlinx.android.synthetic.main.activity_attendance_appeal_approval.*
import kotlinx.android.synthetic.main.content_attendance_appeal_approval.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecycleViewAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.attendance.AppealInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.DividerItemDecoration


class AttendanceAppealApprovalActivity : BaseMVPActivity<AttendanceAppealApprovalContract.View, AttendanceAppealApprovalContract.Presenter>(),
        AttendanceAppealApprovalContract.View, View.OnClickListener {
    override var mPresenter: AttendanceAppealApprovalContract.Presenter = AttendanceAppealApprovalPresenter()

    override fun layoutResId(): Int = R.layout.activity_attendance_appeal_approval


    var isLoading = false
    var isRefresh = false
    var lastId = ""//分页显示用
    var isEdit = false
    val mSelectedSet = HashSet<String>()

    override fun afterSetContentView(savedInstanceState: Bundle?) {
        toolbar_attendance_appeal_approval_bar.title = ""
        setSupportActionBar(toolbar_attendance_appeal_approval_bar)
        tv_attendance_appeal_approval_top_title.text = getString(R.string.title_activity_attendance_appeal_approval)

        layout_attendance_appeal_approval_refresh.setColorSchemeResources(R.color.z_color_refresh_scuba_blue,
                R.color.z_color_refresh_red, R.color.z_color_refresh_purple, R.color.z_color_refresh_orange)
        layout_attendance_appeal_approval_refresh.setOnRefreshListener {
            if (!isLoading && !isRefresh) {
                loadData(LoadType.REFRESH)
            }
        }
        layout_attendance_appeal_approval_refresh.setOnLoadMoreListener {
            if (!isLoading && !isRefresh) {
                loadData(LoadType.LOADMORE)
            }
        }
        recycler_attendance_appeal_approval_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycler_attendance_appeal_approval_list.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST))
        recycler_attendance_appeal_approval_list.adapter = adapter

        button_attendance_appeal_approval_back.setOnClickListener(this)
        image_attendance_appeal_approval_edit.setOnClickListener(this)
        image_attendance_appeal_approval_close.setOnClickListener(this)
        button_attendance_appeal_approval_choose_all.setOnClickListener(this)
        relative_attendance_appeal_approval_agree_button.setOnClickListener(this)
        relative_attendance_appeal_approval_disagree_button.setOnClickListener(this)

        loadData(LoadType.REFRESH)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isEdit) {
                closeEditBar()
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.button_attendance_appeal_approval_back -> finish()
            R.id.image_attendance_appeal_approval_edit -> if (itemList.size > 0) {
                showEditBar()
            }
            R.id.image_attendance_appeal_approval_close -> closeEditBar()
            R.id.button_attendance_appeal_approval_choose_all -> clickChooseAll(button_attendance_appeal_approval_choose_all.isChecked)
            R.id.relative_attendance_appeal_approval_agree_button -> submitApproval(true)
            R.id.relative_attendance_appeal_approval_disagree_button -> submitApproval(false)
        }
    }

    override fun attendanceAppealList(list: List<AppealInfoJson>) {
        linear_shimmer.gone()
        if (isRefresh) {
            itemList.clear()
            itemList.addAll(list)
        }else if (isLoading) {
            itemList.addAll(list)
        }
        if (itemList.size>0) {
            layout_attendance_appeal_approval_refresh.visible()
            tv_no_data.gone()
            lastId = itemList.last().id
        }else {
            layout_attendance_appeal_approval_refresh.gone()
            tv_no_data.visible()
            lastId = ""
        }
        adapter.notifyDataSetChanged()
        hideLoadingDialog()
        finishLoading()
    }

    override fun approvalAppealFinish() {
        loadData(LoadType.REFRESH)
    }

    private fun submitApproval(isAgree: Boolean) {
        if (mSelectedSet.isEmpty()) {
            XToast.toastShort(this, "请先选择需要审批的数据！")
            return
        }
        XLog.debug("submit set size:${mSelectedSet.size}")
        closeEditBar()
        showLoadingDialog()
        mPresenter.approvalAppeal(mSelectedSet, isAgree)
    }

    private fun clickChooseAll(checked: Boolean) {
        if (isEdit) {
            if (checked) {
                itemList.map { mSelectedSet.add(it.id) }
            } else {
                mSelectedSet.clear()
            }
            adapter.notifyDataSetChanged()
        }
    }

    private fun showEditBar() {
        isEdit = true
        button_attendance_appeal_approval_back.gone()
        image_attendance_appeal_approval_edit.gone()
        button_attendance_appeal_approval_choose_all.visible()
        image_attendance_appeal_approval_close.visible()
        linear_attendance_appeal_approval_bottom_operation_bar.visible()
        mSelectedSet.clear()
        adapter.notifyDataSetChanged()
    }

    private fun closeEditBar() {
        isEdit = false
        button_attendance_appeal_approval_choose_all.gone()
        button_attendance_appeal_approval_choose_all.isChecked = false
        image_attendance_appeal_approval_close.gone()
        linear_attendance_appeal_approval_bottom_operation_bar.gone()
        button_attendance_appeal_approval_back.visible()
        image_attendance_appeal_approval_edit.visible()
        adapter.notifyDataSetChanged()
    }

    private fun loadData(refresh: LoadType) {
        if (refresh.equals(LoadType.LOADMORE)) {
            isLoading = true
        } else {
            lastId = ""
            isRefresh = true
        }
        mPresenter.findAttendanceAppealInfoListByPage(lastId)
    }

    private fun finishLoading() {
        if (isRefresh) {
            layout_attendance_appeal_approval_refresh.isRefreshing = false
            isRefresh = false
        }
        if (isLoading) {
            layout_attendance_appeal_approval_refresh.setLoading(false)
            isLoading = false
        }
    }


    val itemList = ArrayList<AppealInfoJson>()
    val adapter: CommonRecycleViewAdapter<AppealInfoJson> by lazy {
        object : CommonRecycleViewAdapter<AppealInfoJson>(this, itemList, R.layout.item_attendance_appeal_approval_list) {
            override fun convert(holder: CommonRecyclerViewHolder?, t: AppealInfoJson?) {
                if (holder != null && t != null) {
                    var address = t.address
                    var desc = t.appealDescription
                    if (!TextUtils.isEmpty(address)) {
                        address = "地点：$address"
                        desc = " , 事由：$desc"
                    } else {
                        address = ""
                        desc = "事由：$desc"
                    }
                    var reason = t.appealReason
                    if (!TextUtils.isEmpty(t.selfHolidayType)) {
                        reason = "$reason (${t.selfHolidayType})"
                    }

                    holder.setText(R.id.tv_attendance_approval_list_person, t.empName)
                            .setText(R.id.tv_attendance_approval_list_record_day, t.recordDateString)
                            .setText(R.id.tv_attendance_approval_list_reason, reason)
                            .setText(R.id.tv_attendance_approval_list_desc, address + desc)
                    val checkbox = holder.getView<CheckBox>(R.id.checkbox_attendance_approval_list_choose)
                    if (isEdit) {
                        checkbox.visible()
                        checkbox.isChecked = false
                        checkbox.setOnClickListener {
                            val ischeck = checkbox.isChecked
                            toggleSelect(t.id, ischeck)
                        }
                        mSelectedSet.filter { it.equals(t.id) }.map { checkbox.isChecked = true }
                    } else {
                        checkbox.gone()
                        checkbox.isChecked = false
                    }
                }
            }
        }
    }

    private fun toggleSelect(id: String, ischeck: Boolean) {
        if (ischeck) {
            mSelectedSet.add(id)
        } else {
            mSelectedSet.remove(id)
        }
    }

    enum class LoadType {
        REFRESH,
        LOADMORE
    }
}
