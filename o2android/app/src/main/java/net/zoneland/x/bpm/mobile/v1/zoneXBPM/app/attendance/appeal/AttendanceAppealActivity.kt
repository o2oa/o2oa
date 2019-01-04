package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.attendance.appeal


import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_attendance_appeal.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.enums.AttendanceStatus
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.attendance.AttendanceDetailInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.identity.IdentityJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.SystemDialogUtil
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.text2String
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible


class AttendanceAppealActivity : BaseMVPActivity<AttendanceAppealContract.View, AttendanceAppealContract.Presenter>(),
        AttendanceAppealContract.View, View.OnClickListener {
    override var mPresenter: AttendanceAppealContract.Presenter = AttendanceAppealPresenter()

    override fun layoutResId(): Int = R.layout.activity_attendance_appeal

    companion object {
        val ATTENDANCE_DETAIL_KEY = "attendance_detail_key"

        fun startBundleData(info: AttendanceDetailInfoJson) : Bundle {
            val bundle = Bundle()
            bundle.putSerializable(ATTENDANCE_DETAIL_KEY, info)
            return  bundle
        }
    }

    val APPEAL_REASON = arrayOf("", "临时请假","出差","因公外出","其他")
    val APPEAL_LEAVE_TYPE = arrayOf("", "带薪年休假","带薪病假","带薪福利假","扣薪事假","其他")
    val identityList:ArrayList<IdentityJson> = ArrayList()
    var isChooseIdentity = false
    var chooseIdentity = ""

    var info:AttendanceDetailInfoJson? = null
    var selectReasonIndex = -1//选中的申诉原因
    var selectLeaveTypeIndex = 0//请假类型选择 默认是空
    override fun beforeSetContentView() {
        super.beforeSetContentView()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun afterSetContentView(savedInstanceState: Bundle?) {
        if (intent.extras?.getSerializable(ATTENDANCE_DETAIL_KEY) == null) {
            XToast.toastShort(this, "传入考勤信息为空，无法申诉！")
            finish()
            return
        }
        info =  intent.extras?.getSerializable(ATTENDANCE_DETAIL_KEY) as AttendanceDetailInfoJson

        setupToolBar(getString(R.string.title_activity_attendance_appeal), true)

        tv_attendance_appeal_name.text = info?.empName
        tv_attendance_appeal_record_day.text = info?.recordDateString
        tv_attendance_appeal_on_duty_time.text = info?.onDutyTime
        tv_attendance_appeal_off_duty_time.text = info?.offDutyTime
        var status = ""
        status = when {
            info?.isGetSelfHolidays?:false -> AttendanceStatus.HOLIDAY.label
            info?.isLate?:false -> AttendanceStatus.LATE.label
            info?.isAbsent?: false -> AttendanceStatus.ABSENT.label
            info?.isAbnormalDuty ?: false -> AttendanceStatus.ABNORMALDUTY.label
            info?.isLackOfTime ?: false -> AttendanceStatus.LACKOFTIME.label
            else -> AttendanceStatus.NORMAL.label
        }
        tv_attendance_appeal_status.text = status

        val adapter  = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, APPEAL_REASON)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_attendance_appeal_reason.adapter = adapter
        spinner_attendance_appeal_reason.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                XLog.debug("选择了："+ APPEAL_REASON[position])
                selectReasonIndex = position
                when(position) {
                    0 -> selectReason0()
                    1 -> selectReason1()
                    2 -> selectReason2()
                    3 -> selectReason3()
                    4 -> selectReason4()
                }
            }
        }
        val typeAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, APPEAL_LEAVE_TYPE)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_attendance_appeal_type.adapter = typeAdapter
        spinner_attendance_appeal_type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                XLog.debug("选择了："+ APPEAL_LEAVE_TYPE[position])
                selectLeaveTypeIndex = position
            }
        }

        tv_bottom_button_first.setOnClickListener(this)
        tv_bottom_button_second.setOnClickListener(this)
        edit_attendance_appeal_time_day.setOnClickListener(this)
        edit_attendance_appeal_start_time.setOnClickListener(this)
        edit_attendance_appeal_end_time.setOnClickListener(this)

        mPresenter.getMyIdentity()
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.tv_bottom_button_first -> clickPositiveBtnAppeal()
            R.id.tv_bottom_button_second -> finish()
            R.id.edit_attendance_appeal_time_day -> SystemDialogUtil.getDateDialog("选择日期", edit_attendance_appeal_time_day)
            R.id.edit_attendance_appeal_start_time -> SystemDialogUtil.getTimeDialog("选择开始时间", edit_attendance_appeal_start_time)
            R.id.edit_attendance_appeal_end_time -> SystemDialogUtil.getTimeDialog("选择结束时间", edit_attendance_appeal_end_time)
        }

    }


    override fun myIdentity(list: List<IdentityJson>) {
        XLog.debug("identity:$list")
        if (list!=null && !list.isEmpty()) {
            isChooseIdentity = true
            identityList.clear()
            identityList.addAll(list)
            val array = Array<String>(identityList.size, {index->
                identityList[index].unitName
            })
            val identityAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, array)
            identityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner_attendance_appeal_identity.adapter = identityAdapter
            spinner_attendance_appeal_identity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    chooseIdentity = identityList[position].distinguishedName
                }
            }
            chooseIdentity = identityList[0].distinguishedName
            linear_attendance_appeal_form_identity_line.visible()
        }else {
            linear_attendance_appeal_form_identity_line.gone()
            isChooseIdentity = false
            identityList.clear()
        }
    }

    override fun submitAppeal(flag: Boolean) {
        hideLoadingDialog()
        if (flag) {
            finish()
        }else {
            XToast.toastShort(this, "提交失败！")
        }
    }

    private fun clickPositiveBtnAppeal() {
        when(selectReasonIndex) {
            -1, 0 -> XToast.toastShort(this@AttendanceAppealActivity, "请选择申诉原因！")
            1 -> validateReason1()
            2 -> validateReason2()
            3 -> validateReason3()
            4 -> validateReason4()
        }
    }


    private fun selectReason0() {
        linear_attendance_appeal_form_line7_type.gone()
        linear_attendance_appeal_form_line8_address.gone()
        linear_attendance_appeal_form_line9_startTime.gone()
        linear_attendance_appeal_form_line_time_day.gone()
        linear_attendance_appeal_form_line11_desc.gone()
    }
    private fun selectReason1() {
        linear_attendance_appeal_form_line7_type.visible()
        linear_attendance_appeal_form_line8_address.gone()
        linear_attendance_appeal_form_line9_startTime.visible()
        linear_attendance_appeal_form_line_time_day.visible()
        linear_attendance_appeal_form_line11_desc.gone()
    }
    private fun selectReason2() {
        linear_attendance_appeal_form_line7_type.gone()
        linear_attendance_appeal_form_line8_address.visible()
        linear_attendance_appeal_form_line9_startTime.visible()
        linear_attendance_appeal_form_line_time_day.visible()
        linear_attendance_appeal_form_line11_desc.gone()
    }
    private fun selectReason3() {
        linear_attendance_appeal_form_line7_type.gone()
        linear_attendance_appeal_form_line8_address.visible()
        linear_attendance_appeal_form_line9_startTime.visible()
        linear_attendance_appeal_form_line_time_day.visible()
        linear_attendance_appeal_form_line11_desc.visible()
    }
    private fun selectReason4() {
        linear_attendance_appeal_form_line7_type.gone()
        linear_attendance_appeal_form_line8_address.gone()
        linear_attendance_appeal_form_line9_startTime.gone()
        linear_attendance_appeal_form_line_time_day.gone()
        linear_attendance_appeal_form_line11_desc.visible()
    }


    private fun validateReason1() {
        if (selectLeaveTypeIndex<0) {
            XToast.toastShort(this, "请选择请假类型！")
            return
        }
        validateTimeAndSubmit()
    }


    private fun validateReason2() {
        val address = edit_attendance_appeal_address.text2String()
        if (TextUtils.isEmpty(address)) {
            XToast.toastShort(this, "请输入地址！")
            return
        }
        validateTimeAndSubmit(address)
    }


    private fun validateReason3() {
        val address = edit_attendance_appeal_address.text2String()
        if (TextUtils.isEmpty(address)) {
            XToast.toastShort(this, "请输入地址！")
            return
        }
        val desc = edit_attendance_appeal_desc.text2String()
        if (TextUtils.isEmpty(desc)) {
            XToast.toastShort(this, "请输入事由！")
            return
        }
        validateTimeAndSubmit(address, desc)
    }

    private fun validateReason4() {
        val desc = edit_attendance_appeal_desc.text2String()
        if (TextUtils.isEmpty(desc)) {
            XToast.toastShort(this, "请输入事由！")
            return
        }
        validateTimeAndSubmit(desc=desc)
    }

    private fun validateTimeAndSubmit(address:String = "", desc:String = "") {



        if (TextUtils.isEmpty(address) && !TextUtils.isEmpty(desc)) {
            submitAppealForm("", "", "", desc)
        }else {
            val timeDay = edit_attendance_appeal_time_day.text2String()
            if (TextUtils.isEmpty(timeDay)) {
                XToast.toastShort(this, "请选择日期")
                return
            }
            val startTime = edit_attendance_appeal_start_time.text2String()
            if (TextUtils.isEmpty(startTime)) {
                XToast.toastShort(this, "请选择开始时间")
                return
            }
            val endTime = edit_attendance_appeal_end_time.text2String()
            if (TextUtils.isEmpty(endTime)) {
                XToast.toastShort(this, "请选择结束时间")
                return
            }
            submitAppealForm(address, timeDay+" "+startTime, timeDay+" "+endTime, desc)
        }

    }

    private fun submitAppealForm(address: String, startTime: String, endTime: String, desc: String) {
        XLog.debug("address:$address, starttime:$startTime, endtime:$endTime, desc:$desc, identity:$chooseIdentity")
        if (isChooseIdentity) {
            if (TextUtils.isEmpty(chooseIdentity)) {
                XToast.toastShort(this, "没有选择身份")
                return
            }
        }
        info?.let {
            it.appealReason = (APPEAL_REASON[selectReasonIndex])
            it.selfHolidayType = (APPEAL_LEAVE_TYPE[selectLeaveTypeIndex])
            it.startTime = (startTime)
            it.endTime = (endTime)
            it.address = (address)
            it.appealDescription = (desc)
            it.appealStatus = (0)
            it.identity = chooseIdentity
            showLoadingDialog()
            mPresenter.submitAppeal(it)
        }
    }

}
