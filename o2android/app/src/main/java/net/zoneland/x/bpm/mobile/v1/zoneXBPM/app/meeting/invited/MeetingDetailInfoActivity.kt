package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.meeting.invited

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.content_meeting_detail.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecycleViewAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.meeting.MeetingFileInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.meeting.MeetingInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.AndroidUtils
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.FileExtensionHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.imageloader.O2ImageLoaderManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.CircleImageView
import java.io.File

class MeetingDetailInfoActivity : BaseMVPActivity<MeetingDetailInfoContract.View, MeetingDetailInfoContract.Presenter>() ,
    MeetingDetailInfoContract.View{

    override var mPresenter: MeetingDetailInfoContract.Presenter = MeetingDetailInfoPresenter()
    override fun layoutResId(): Int = R.layout.activity_meeting_detail_info

    private val notAcceptPersonList = ArrayList<String>()
    private val acceptPersonList = ArrayList<String>()
    private val meetingFileList = ArrayList<MeetingFileInfoJson>()

    companion object {
        val meetingDetail = "MEETING_DETAIL_INFO"
    }

    override fun afterSetContentView(savedInstanceState: Bundle?) {
        setupToolBar(getString(R.string.meeting_detail),true,false)

        if (intent.extras?.getSerializable(meetingDetail)  ==null) {
            XToast.toastShort(this, "没有获取到会议详细信息！")
            finish()
        }

        val meetingDetailInfo = intent.extras?.getSerializable(meetingDetail) as MeetingInfoJson
        notAcceptPersonList.addAll(meetingDetailInfo.invitePersonList)
        notAcceptPersonList.removeAll(meetingDetailInfo.acceptPersonList)
        acceptPersonList.addAll(meetingDetailInfo.acceptPersonList)
        edit_meeting_invited_name.text = meetingDetailInfo.subject
        edit_meeting_invited_start_day.text = meetingDetailInfo.startTime.substring(0,10)
        edit_meeting_time.text = meetingDetailInfo.startTime.substring(11,16)+"-"+meetingDetailInfo.completedTime.substring(11,16)
        meeting_people_sum.text = Integer.toString(meetingDetailInfo.invitePersonList.size+1)+"人"
        edit_meeting_create_form_desc.text = meetingDetailInfo.description
        mPresenter.asyncLoadRoomName(edit_meeting_invited_room,meetingDetailInfo.room)

        meetingFileList.addAll(meetingDetailInfo.attachmentList)
        recycler_meeting_form_file_list.layoutManager = LinearLayoutManager(this)
        recycler_meeting_form_file_list.adapter = meetingFileAdapter
        meetingFileAdapter.notifyDataSetChanged()

        meetingFileAdapter.setOnItemClickListener { _, position ->
            showLoadingDialog()
            mPresenter.downloadMeetingFile(meetingFileList[position])
        }

        recycler_meeting_invited_accept_person_list.layoutManager = GridLayoutManager(this, 5)
        recycler_meeting_invited_accept_person_list.adapter = acceptPersonAdapter
        acceptPersonAdapter.notifyDataSetChanged()

        recycler_meeting_invited_not_accept_person_list.layoutManager = GridLayoutManager(this, 5)
        recycler_meeting_invited_not_accept_person_list.adapter = notAcceptPersonAdapter
        notAcceptPersonAdapter.notifyDataSetChanged()

    }

    override fun downloadAttachmentSuccess(file: File?) {
        hideLoadingDialog()
        XLog.debug(file?.name)
        XToast.toastShort(this,"下载成功")
        if (file != null && file.exists()) AndroidUtils.openFileWithDefaultApp(this, file)
    }

    private val acceptPersonAdapter: CommonRecycleViewAdapter<String> by lazy {
        object : CommonRecycleViewAdapter<String>(this, acceptPersonList, R.layout.item_person_avatar_name) {
            override fun convert(holder: CommonRecyclerViewHolder?, t: String?) {
                if (TextUtils.isEmpty(t)){
                    XLog.error("person id is null!!!!!!")
                    return
                }
                val avatar = holder?.getView<CircleImageView>(R.id.circle_image_avatar)
                val delete = holder?.getView<ImageView>(R.id.delete_people_iv)
                delete?.visibility = View.GONE
                val url = APIAddressHelper.instance().getPersonAvatarUrlWithId(t!!)
                O2ImageLoaderManager.instance().showImage(avatar!!, url)
                val nameTv = holder.getView<TextView>(R.id.tv_name)
                if (nameTv!=null) {
                    nameTv.tag = t
                    mPresenter.asyncLoadPersonName(nameTv, t)
                }
            }
        }
    }

    private val notAcceptPersonAdapter: CommonRecycleViewAdapter<String> by lazy {
        object : CommonRecycleViewAdapter<String>(this, notAcceptPersonList, R.layout.item_person_avatar_name) {
            override fun convert(holder: CommonRecyclerViewHolder?, t: String?) {
                if (TextUtils.isEmpty(t)){
                    XLog.error("person id is null!!!!!!")
                    return
                }
                val avatar = holder?.getView<CircleImageView>(R.id.circle_image_avatar)
                val delete = holder?.getView<ImageView>(R.id.delete_people_iv)
                delete?.visibility = View.GONE
                val url = APIAddressHelper.instance().getPersonAvatarUrlWithId(t!!)
                O2ImageLoaderManager.instance().showImage(avatar!!, url)
                val nameTv = holder.getView<TextView>(R.id.tv_name)
                if (nameTv!=null) {
                    nameTv.tag = t
                    mPresenter.asyncLoadPersonName(nameTv, t)
                }
            }
        }
    }

    private val meetingFileAdapter: CommonRecycleViewAdapter<MeetingFileInfoJson> by lazy {
        object : CommonRecycleViewAdapter<MeetingFileInfoJson>(this, meetingFileList, R.layout.item_meeting_file_list) {
            override fun convert(holder: CommonRecyclerViewHolder?, t: MeetingFileInfoJson?) {
                val delete = holder?.getView<ImageView>(R.id.meeting_file_delete)
                delete?.visibility = View.GONE
                holder?.setText(R.id.meeting_file_list_name_id,t?.name)
                val avatar = holder?.getView<ImageView>(R.id.meeting_file_list_icon_id)
                val id = FileExtensionHelper.getImageResourceByFileExtension(t?.extension)
                avatar?.setImageResource(id)
            }
        }
    }

}
