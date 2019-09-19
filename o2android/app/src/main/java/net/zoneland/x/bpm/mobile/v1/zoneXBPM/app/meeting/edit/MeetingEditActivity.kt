package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.meeting.edit


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
import kotlinx.android.synthetic.main.content_meeting_edit_form.*
import net.muliba.fancyfilepickerlibrary.FilePicker
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.organization.ContactPickerActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecycleViewAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.meeting.MeetingFileInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.meeting.MeetingInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.FileExtensionHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.imageloader.O2ImageLoaderManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.CircleImageView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.O2DialogSupport


class MeetingEditActivity : BaseMVPActivity<MeetingEditContract.View, MeetingEditContract.Presenter>(), MeetingEditContract.View {

    override var mPresenter: MeetingEditContract.Presenter = MeetingEditPresenter()
    override fun layoutResId(): Int = R.layout.activity_meeting_edit_form

    val invitePersonAdd = "添加"
    val invitePersonList = ArrayList<String>()
    val meetingFileList = ArrayList<MeetingFileInfoJson>()

    lateinit var meeting:MeetingInfoJson
    private lateinit var roomName:String

    companion object {
        val MEETING_INFO_KEY = "xbpm.meeting.edit.info"
        val MEETING_INFO_ROOM_NAME_KEY = "xbpm.meeting.edit.room.name"
        val MEETING_FILE_CODE = 1003

        fun startBundleData(info: MeetingInfoJson, roomName:String): Bundle {
            val bundle = Bundle()
            bundle.putSerializable(MEETING_INFO_KEY, info)
            bundle.putString(MEETING_INFO_ROOM_NAME_KEY, roomName)
            return bundle
        }
    }

    //软键盘
    override fun beforeSetContentView() {
        super.beforeSetContentView()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
    }

    override fun afterSetContentView(savedInstanceState: Bundle?) {
        meeting = intent.extras?.getSerializable(MEETING_INFO_KEY) as MeetingInfoJson
        roomName = intent.extras?.getString(MEETING_INFO_ROOM_NAME_KEY) ?: getString(R.string.title_activity_meeting_edit_form)

        if (TextUtils.isEmpty(meeting.id)){
            XToast.toastShort(this, "错误：没有会议对象")
            finish()
            return
        }

        setupToolBar(meeting.subject, true)
        tv_meeting_edit_form_room_name.text = roomName
        edit_meeting_edit_form_name.setText(meeting.subject)
        val startDay = meeting.startTime.substring(0, 10)
        val startTime = meeting.startTime.substring(11, 16)
        val completeTime = meeting.completedTime.substring(11, 16)
        edit_meeting_edit_form_start_day.text = startDay
        edit_meeting_edit_form_start_time.text = startTime
        edit_meeting_edit_form_end_time.text = completeTime
        edit_meeting_edit_form_desc.setText(meeting.description)

        meetingFileList.addAll(meeting.attachmentList)
        recycler_meeting_edit_form_file_list.layoutManager = LinearLayoutManager(this)
        recycler_meeting_edit_form_file_list.adapter = meetingFileAdapter
        meetingFileAdapter.notifyDataSetChanged()

        invitePersonList.addAll(meeting.invitePersonList)
        invitePersonList.add(invitePersonAdd)
        invitePersonAdapter.setOnItemClickListener { _, position ->
            when(position) {
                invitePersonList.size-1 -> {
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
        recycler_meeting_edit_form_invite_person_list.layoutManager = GridLayoutManager(this, 5)
        recycler_meeting_edit_form_invite_person_list.adapter = invitePersonAdapter
        invitePersonAdapter.notifyDataSetChanged()

        //tv_bottom_button_first.setOnClickListener { finish() }
        button_submit_meeting.setOnClickListener { submitForm() }
        iv_meeting_file_add.setOnClickListener {
            _ ->
            FilePicker().withActivity(this).requestCode(MEETING_FILE_CODE)
                    .chooseType(FilePicker.CHOOSE_TYPE_SINGLE)
                    .start()
        }
        meetingFileAdapter.setOnItemClickListener { _, position ->
            showLoadingDialog()
            mPresenter.deleteMeetingFile(meetingFileList[position].id,position)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_meeting_edit, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.menu_meeting_edit_delete -> {
                O2DialogSupport.openConfirmDialog(this, "确定要取消会议？", { _ ->
                    mPresenter.deleteMeeting(meeting.id)
                })
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when(requestCode){

                MEETING_FILE_CODE -> {
                    val result = data?.getStringExtra(FilePicker.FANCY_FILE_PICKER_SINGLE_RESULT_KEY)
                    if (!TextUtils.isEmpty(result)) {
                        XLog.debug("uri path:" + result)
                        showLoadingDialog()
                        mPresenter.saveMeetingFile(result!!,meeting.id)
                    } else {
                        XLog.error("FilePicker 没有返回值！")
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onError(message: String) {
        XToast.toastShort(this, message)
        hideLoadingDialog()
    }

    override fun deleteMeetingFile(position: Int) {
        meetingFileList.removeAt(position)
        meetingFileAdapter.notifyDataSetChanged()
        hideLoadingDialog()
    }

    override fun updateMeetingSuccess() {
        finish()
    }

    override fun deleteMeetingSuccess() {
        finish()
    }

    override fun saveMeetingFileSuccess(fileName: String,fileId: String) {
        val meetingFile = MeetingFileInfoJson()
        meetingFile.name = fileName
        meetingFile.extension = fileName.substringAfterLast('.', "")
        meetingFile.id = fileId
        meetingFileList.add(meetingFile)
        meetingFileAdapter.notifyDataSetChanged()
        hideLoadingDialog()
    }

    private fun chooseInvitePersonCallback(result: List<String>) {
        val allList = ArrayList<String>()
        invitePersonList.remove(invitePersonAdd)
        if (invitePersonList.isNotEmpty()){
            allList.addAll(invitePersonList)
        }
        allList.addAll(result)
        invitePersonList.clear()
        invitePersonList.addAll(allList.distinct())
        invitePersonList.add(invitePersonAdd)
        invitePersonAdapter.notifyDataSetChanged()
    }


    private fun submitForm() {
        val subject = edit_meeting_edit_form_name.text.toString()
        if (TextUtils.isEmpty(subject)) {
            XToast.toastShort(this, "会议名称不能为空！")
            return
        }
        if (invitePersonList.isEmpty()) {
            XToast.toastShort(this, "请选择与会人员！")
            return
        }
        meeting.subject = subject
        meeting.description = edit_meeting_edit_form_desc.text.toString()
        val savePersonList = invitePersonList
        savePersonList.remove(invitePersonAdd)
        meeting.invitePersonList = savePersonList
        meeting.attachmentList = meetingFileList
        mPresenter.updateMeetingInfo(meeting)
    }

    private val invitePersonAdapter: CommonRecycleViewAdapter<String> by lazy {
        object : CommonRecycleViewAdapter<String>(this, invitePersonList, R.layout.item_person_avatar_name) {
            override fun convert(holder: CommonRecyclerViewHolder?, t: String?) {
                if (TextUtils.isEmpty(t)){
                    XLog.error("person id is null!!!!!!")
                    return
                }
                val avatar = holder?.getView<CircleImageView>(R.id.circle_image_avatar)
                avatar?.setImageResource(R.mipmap.contact_icon_avatar)
                val delete = holder?.getView<ImageView>(R.id.delete_people_iv)
                delete?.visibility = View.VISIBLE
                if (avatar!=null) {
                    if (invitePersonAdd==t){
                        avatar.setImageResource(R.mipmap.icon_add_people)
                        delete?.visibility = View.GONE
                    }else {
                        val url = APIAddressHelper.instance().getPersonAvatarUrlWithId(t!!)
                        O2ImageLoaderManager.instance().showImage(avatar, url)
                    }
                }
                val nameTv = holder?.getView<TextView>(R.id.tv_name)
                if (nameTv!=null) {
                    if(invitePersonAdd==t){
                        nameTv.text = t
                    }else{
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

    private val meetingFileAdapter: CommonRecycleViewAdapter<MeetingFileInfoJson> by lazy {
        object : CommonRecycleViewAdapter<MeetingFileInfoJson>(this, meetingFileList, R.layout.item_meeting_file_list) {
            override fun convert(holder: CommonRecyclerViewHolder?, t: MeetingFileInfoJson?) {
                holder?.setText(R.id.meeting_file_list_name_id,t?.name)
                val avatar = holder?.getView<ImageView>(R.id.meeting_file_list_icon_id)
                val id = FileExtensionHelper.getImageResourceByFileExtension(t?.extension)
                avatar?.setImageResource(id)
            }
        }
    }

}
