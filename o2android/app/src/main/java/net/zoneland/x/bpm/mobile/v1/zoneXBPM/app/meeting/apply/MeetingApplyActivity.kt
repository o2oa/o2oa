package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.meeting.apply

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.borax12.materialdaterangepicker.time.RadialPickerLayout
import com.borax12.materialdaterangepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.content_meeting_create_form.*
import net.muliba.fancyfilepickerlibrary.FilePicker
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.meeting.room.MeetingRoomChooseActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.organization.ContactPickerActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecycleViewAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.meeting.MeetingInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.goWithRequestCode
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.imageloader.O2ImageLoaderManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.CircleImageView
import java.io.File
import java.util.*


class MeetingApplyActivity : BaseMVPActivity<MeetingApplyContract.View, MeetingApplyContract.Presenter>(),
        MeetingApplyContract.View, View.OnClickListener, TimePickerDialog.OnTimeSetListener{

    override var mPresenter: MeetingApplyContract.Presenter = MeetingApplyPresenter()
    override fun layoutResId(): Int = R.layout.activity_meeting_create_form

    val invitePersonAdd = "添加"
    val invitePersonList = ArrayList<String>()
    val fileList = ArrayList<String>()
    private val fileIdList = ArrayList<String>()
    private var addFile = ""
    private var meetingId = ""
    private var roomId: String = ""

    companion object {
        val MEETING_CHOOSE_ROOM = 1001
        val MEETING_FILE_CODE = 1003
    }

    //软键盘
    override fun beforeSetContentView() {
        super.beforeSetContentView()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun afterSetContentView(savedInstanceState: Bundle?) {
        setupToolBar(getString(R.string.title_activity_meeting_create_form), true)

        val dayNow = DateHelper.nowByFormate("yyyy-MM-dd")
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val startHour = if (hour > 9) "$hour:00" else "0$hour:00"
        val endHour = if (hour + 1 > 9) "${hour + 1}:00" else "0${hour + 1}:00"

        edit_meeting_create_form_start_day.text = dayNow
        edit_meeting_create_form_start_time.text = startHour
        edit_meeting_create_form_end_time.text = endHour

        //edit_meeting_create_form_end_time.setOnClickListener(this)
        //edit_meeting_create_form_start_time.setOnClickListener(this)
        ll_meeting_time.setOnClickListener(this)
        edit_meeting_create_form_start_day.setOnClickListener(this)
        rl_choose_room.setOnClickListener(this)
        //button_submit_meeting.setOnClickListener(this)
        iv_meeting_file_add.setOnClickListener(this)

        invitePersonAdapter.setOnItemClickListener { _, position ->
            when (position) {
                invitePersonList.size - 1 -> {
                    val bundle = ContactPickerActivity.startPickerBundle(
                            arrayListOf("personPicker"),
                            multiple = true)
                    contactPicker(bundle) { result ->
                        if (result != null) {
                            val users = result.users.map { it.distinguishedName }
                            XLog.debug("choose invite person, list:$users,")
                            chooseInvitePersonCallback(users)
                        }
                    }
                }
                else -> {
                    invitePersonList.removeAt(position)
                    invitePersonAdapter.notifyDataSetChanged()
                }
            }
        }

        recycler_meeting_create_form_invite_person_list.adapter = invitePersonAdapter
        recycler_meeting_create_form_invite_person_list.layoutManager = GridLayoutManager(this, 5)
        invitePersonList.add(invitePersonAdd)
        invitePersonAdapter.notifyDataSetChanged()

        recycler_meeting_create_form_file_list.adapter = meetingFileAdapter
        recycler_meeting_create_form_file_list.layoutManager = LinearLayoutManager(this)
        meetingFileAdapter.setOnItemClickListener { _, position ->
            showLoadingDialog()
            mPresenter.deleteMeetingFile(fileIdList[position],position)
        }

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            //R.id.edit_meeting_create_form_end_time -> ViewTools.getInstance().getTimeDialog(getString(R.string.meeting_end_time), edit_meeting_create_form_end_time)
            //R.id.edit_meeting_create_form_start_time -> ViewTools.getInstance().getTimeDialog(getString(R.string.meeting_start_time), edit_meeting_create_form_start_time)
            R.id.edit_meeting_create_form_start_day -> SystemDialogUtil.getDateDialog(getString(R.string.meeting_start_day), edit_meeting_create_form_start_day)
            //R.id.edit_meeting_create_form_start_day -> showDatePicker()
            R.id.ll_meeting_time -> showTimePicker()
            R.id.rl_choose_room -> chooseMeetingRoom()
            R.id.iv_meeting_file_add -> {
                FilePicker().withActivity(this).requestCode(MEETING_FILE_CODE)
                        .chooseType(FilePicker.CHOOSE_TYPE_SINGLE)
                        .start()
            }
            /*R.id.button_submit_meeting -> {
                val subject = edit_meeting_create_form_name.text.toString()
                if (TextUtils.isEmpty(subject)) {
                    XToast.toastShort(this, "会议名称不能为空！")
                    return
                }
                val startDay = edit_meeting_create_form_start_day.text.toString()
                val startTime = edit_meeting_create_form_start_time.text.toString()
                val endTime = edit_meeting_create_form_end_time.text.toString()
                if (TextUtils.isEmpty(startDay) || TextUtils.isEmpty(startTime) || TextUtils.isEmpty(endTime)) {
                    XToast.toastShort(this, "会议时间不能为空！")
                    return
                }
                if (TextUtils.isEmpty(roomId)) {
                    XToast.toastShort(this, "请选择会议室！")
                    return
                }
                if (invitePersonList.isEmpty()) {
                    XToast.toastShort(this, "请选择与会人员！")
                    return
                }
                val info = MeetingInfoJson()
                info.subject = subject
                info.startTime = "$startDay $startTime:00"
                info.completedTime = "$startDay $endTime:00"
                info.description = edit_meeting_create_form_desc.text.toString()
                val savePersonList = invitePersonList
                savePersonList.remove(invitePersonAdd)
                info.invitePersonList = savePersonList
                info.room = roomId
                if (TextUtils.isEmpty(meetingId)) {
                    mPresenter.saveMeetingNoFile(info)
                } else {
                    info.id = meetingId
                    mPresenter.updateMeetingInfo(info,meetingId)
                }
            }*/

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_meeting_apply,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_meeting_main_accept -> {
                val subject = edit_meeting_create_form_name.text.toString()
                if (TextUtils.isEmpty(subject)) {
                    XToast.toastShort(this, "会议名称不能为空！")
                    return true
                }
                val startDay = edit_meeting_create_form_start_day.text.toString()
                val startTime = edit_meeting_create_form_start_time.text.toString()
                val endTime = edit_meeting_create_form_end_time.text.toString()
                if (TextUtils.isEmpty(startDay) || TextUtils.isEmpty(startTime) || TextUtils.isEmpty(endTime)) {
                    XToast.toastShort(this, "会议时间不能为空！")
                    return super.onOptionsItemSelected(item)
                }
                if (TextUtils.isEmpty(roomId)) {
                    XToast.toastShort(this, "请选择会议室！")
                    return true
                }
                if (invitePersonList.isEmpty()) {
                    XToast.toastShort(this, "请选择与会人员！")
                    return true
                }
                val info = MeetingInfoJson()
                info.subject = subject
                info.startTime = "$startDay $startTime:00"
                info.completedTime = "$startDay $endTime:00"
                info.description = edit_meeting_create_form_desc.text.toString()
                val savePersonList = invitePersonList
                savePersonList.remove(invitePersonAdd)
                info.invitePersonList = savePersonList
                info.room = roomId
                if (TextUtils.isEmpty(meetingId)) {
                    mPresenter.saveMeetingNoFile(info)
                } else {
                    info.id = meetingId
                    mPresenter.updateMeetingInfo(info,meetingId)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {

                MEETING_CHOOSE_ROOM -> {
                    val resultRoomName = data?.getStringExtra(MeetingRoomChooseActivity.RESULT_ROOM_NAME_KEY) ?: ""
                    val resultRoomId = data?.getStringExtra(MeetingRoomChooseActivity.RESULT_ROOM_ID_KEY) ?: ""
                    XLog.debug("choose room, id:$resultRoomId, name:$resultRoomName")
                    if (!TextUtils.isEmpty(resultRoomId) && !TextUtils.isEmpty(resultRoomName)) {
                        edit_meeting_create_form_room.text = resultRoomName
                        roomId = resultRoomId
                    }
                }
                MEETING_FILE_CODE -> {
                    val result = data?.getStringExtra(FilePicker.FANCY_FILE_PICKER_SINGLE_RESULT_KEY)
                    if (!TextUtils.isEmpty(result)) {
                        XLog.debug("uri path:" + result)
                        showLoadingDialog()
                        addFile = result!!
                        updateFileList()
                    } else {
                        XLog.error("FilePicker 没有返回值！")
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun updateMeetingSuccess() {
        finish()
    }

    override fun saveMeetingFileSuccess(id: String) {
        fileIdList.add(id)
        hideLoadingDialog()
        XLog.debug("上传成功："+id)
    }

    override fun saveMeetingSuccess(id : String,fileId : String) {
        meetingId = id
        fileIdList.add(fileId)
        hideLoadingDialog()
        //finish()
    }

    override fun deleteMeetingFile(fileId: String,position: Int) {
        fileList.removeAt(position)
        fileIdList.remove(fileId)
        meetingFileAdapter.notifyDataSetChanged()
        hideLoadingDialog()
    }

    override fun doMeetingFail(message: String) {
        hideLoadingDialog()
        XToast.toastShort(this, message)
    }


    override fun onTimeSet(view: RadialPickerLayout?, hourOfDay: Int, minute: Int, hourOfDayEnd: Int, minuteEnd: Int) {
        val hourString = if (hourOfDay < 10) "0" + hourOfDay else "" + hourOfDay
        val minuteString = if (minute < 10) "0" + minute else "" + minute
        val hourStringEnd = if (hourOfDayEnd < 10) "0" + hourOfDayEnd else "" + hourOfDayEnd
        val minuteStringEnd = if (minuteEnd < 10) "0" + minuteEnd else "" + minuteEnd
        edit_meeting_create_form_start_time.text = "$hourString:$minuteString"
        edit_meeting_create_form_end_time.text = "$hourStringEnd:$minuteStringEnd"
    }

    private fun updateFileList() {
        if (!fileList.contains(addFile)){
            fileList.add(addFile)
            submitForm()
            meetingFileAdapter.notifyDataSetChanged()
        }
        //hideLoadingDialog()
    }

    private fun chooseMeetingRoom() {
        val startDay = edit_meeting_create_form_start_day.text.toString()
        val startTime = edit_meeting_create_form_start_time.text.toString()
        val endTime = edit_meeting_create_form_end_time.text.toString()
        if (TextUtils.isEmpty(startDay) || TextUtils.isEmpty(startTime) || TextUtils.isEmpty(endTime)) {
            XToast.toastShort(this, "请先选择会议时间！")
            return
        }
        goWithRequestCode<MeetingRoomChooseActivity>(MeetingRoomChooseActivity.startBundleData("$startDay $startTime", "$startDay $endTime"), MEETING_CHOOSE_ROOM)
    }

    private fun showTimePicker(){
        val startHour = edit_meeting_create_form_start_time.text.substring(0,2)
        val startMinute = edit_meeting_create_form_start_time.text.substring(3,5)
        val endHour = edit_meeting_create_form_end_time.text.substring(0,2)
        val endMinute = edit_meeting_create_form_end_time.text.substring(3,5)
        val tpd : TimePickerDialog = TimePickerDialog.newInstance(
                this, Integer.parseInt(startHour), Integer.parseInt(startMinute),true,
                Integer.parseInt(endHour), Integer.parseInt(endMinute))
        tpd.show(fragmentManager,"TimePickerDialog")
    }

    private fun submitForm() {
        if (!TextUtils.isEmpty(meetingId)) {
            mPresenter.saveMeetingFile(addFile,meetingId)
        } else {
            val subject = edit_meeting_create_form_name.text.toString()
            if (TextUtils.isEmpty(subject)) {
                XToast.toastShort(this, "会议名称不能为空！")
                return
            }
            val startDay = edit_meeting_create_form_start_day.text.toString()
            val startTime = edit_meeting_create_form_start_time.text.toString()
            val endTime = edit_meeting_create_form_end_time.text.toString()
            if (TextUtils.isEmpty(startDay) || TextUtils.isEmpty(startTime) || TextUtils.isEmpty(endTime)) {
                XToast.toastShort(this, "会议时间不能为空！")
                return
            }
            if (TextUtils.isEmpty(roomId)) {
                XToast.toastShort(this, "请选择会议室！")
                return
            }
            if (invitePersonList.isEmpty()) {
                XToast.toastShort(this, "请选择与会人员！")
                return
            }
            val info = MeetingInfoJson()
            info.subject = subject
            info.startTime = "$startDay $startTime:00"
            info.completedTime = "$startDay $endTime:00"
            info.description = edit_meeting_create_form_desc.text.toString()
            val savePersonList = invitePersonList
            savePersonList.remove(invitePersonAdd)
            info.invitePersonList = savePersonList
            info.room = roomId
            mPresenter.saveMeeting(info,addFile)
        }
    }

    private fun chooseInvitePersonCallback(result: List<String>) {
        val allList = ArrayList<String>()
        invitePersonList.remove(invitePersonAdd)
        if (invitePersonList.isNotEmpty()) {
            allList.addAll(invitePersonList)
        }
        allList.addAll(result)
        invitePersonList.clear()
        invitePersonList.addAll(allList.distinct())
        invitePersonList.add(invitePersonAdd)
        invitePersonAdapter.notifyDataSetChanged()

    }

    private val invitePersonAdapter: CommonRecycleViewAdapter<String> by lazy {
        object : CommonRecycleViewAdapter<String>(this, invitePersonList, R.layout.item_person_avatar_name) {
            override fun convert(holder: CommonRecyclerViewHolder?, t: String?) {
                if (TextUtils.isEmpty(t)) {
                    XLog.error("person id is null!!!!!!")
                    return
                }
                val avatar = holder?.getView<CircleImageView>(R.id.circle_image_avatar)
                val delete = holder?.getView<ImageView>(R.id.delete_people_iv)
                delete?.visibility = View.VISIBLE
                avatar?.setImageResource(R.mipmap.contact_icon_avatar)
                if (avatar != null) {
                    if (invitePersonAdd == t) {
                        avatar.setImageResource(R.mipmap.icon_add_people)
                        delete?.visibility = View.GONE
                    } else {
                        val url = APIAddressHelper.instance().getPersonAvatarUrlWithId(t!!)
                        O2ImageLoaderManager.instance().showImage(avatar, url)
                    }
                }
                val nameTv = holder?.getView<TextView>(R.id.tv_name)
                if (nameTv != null) {
                    if (invitePersonAdd == t) {
                        nameTv.text = t
                    } else {
                        if (t != null && t.contains("@")) {
                            nameTv.text = t.split("@").first()
                        }else {
                            nameTv.text = t
                        }
//                        mPresenter.asyncLoadPersonName(nameTv, t!!)
                    }
                }
            }
        }
    }

    private val meetingFileAdapter: CommonRecycleViewAdapter<String> by lazy {
        object : CommonRecycleViewAdapter<String>(this, fileList, R.layout.item_meeting_file_list) {
            override fun convert(holder: CommonRecyclerViewHolder?, t: String?) {
                if (TextUtils.isEmpty(t)) {
                    XLog.error("person id is null!!!!!!")
                    return
                }

                val nameTv = holder?.getView<TextView>(R.id.meeting_file_list_name_id)
                val file = File(t)
                val avatar = holder?.getView<ImageView>(R.id.meeting_file_list_icon_id)
                val id = FileExtensionHelper.getImageResourceByFileExtension(file.extension)
                avatar?.setImageResource(id)
                nameTv!!.text = file.name

            }
        }
    }
}
