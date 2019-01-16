package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.meeting.reserve

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_meeting_room_detail.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.meeting.apply.MeetingApplyActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecycleViewAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.meeting.BuildingInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.meeting.MeetingInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.MeetingRoom
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.go
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone

class MeetingRoomDetailActivity : BaseMVPActivity<MeetingRoomDetailContract.View, MeetingRoomDetailContract.Presenter>(), MeetingRoomDetailContract.View {

    override var mPresenter: MeetingRoomDetailContract.Presenter = MeetingRoomDetailPresenter()
    override fun layoutResId(): Int = R.layout.activity_meeting_room_detail

    val meetingList = ArrayList<MeetingInfoJson>()

    companion object {
        val ROOM_DETAIL = "roomMessage"
        val DEVICE = "device"
        val DATE = "date"
        val START_TIME = "start_time"
        val END_TIME = "end_time"
    }

    override fun afterSetContentView(savedInstanceState: Bundle?) {
        setupToolBar(getString(R.string.title_activity_meeting), true, false)
        if (intent.extras?.getSerializable(ROOM_DETAIL) == null) {
            XToast.toastShort(this, "没有获取到会议室详细信息！")
            finish()
        }else {
            val roomDetail = intent.extras?.getSerializable(ROOM_DETAIL) as MeetingRoom.Room
            val device = if (intent.extras?.getIntegerArrayList(DEVICE) == null) {
                ArrayList()
            } else {
                intent.extras?.getIntegerArrayList(DEVICE) as ArrayList<Int>
            }
            val date = intent.extras?.getString(DATE) ?: ""
            val startTime = intent.extras?.getString(START_TIME) ?: ""
            val endTime = intent.extras?.getString(END_TIME) ?: ""
            meeting_detail_recycler_view.layoutManager = LinearLayoutManager(this)
            meeting_detail_recycler_view.adapter = adapter
            setImageVisible(device)
            tv_meeting_room_date.text = date
            tv_meeting_room_start_time.text = startTime
            tv_meeting_room_end_time.text = endTime
            tv_meeting_room_name.text = roomDetail.name
            mPresenter.getBuildingDetailById(roomDetail.building)
            tv_meeting_room_floor.text = "楼层：${roomDetail.floor}"
            tv_meeting_room_number.text = "房间：${roomDetail.roomNumber}"
            tv_meeting_list_item_meeting_room.text = "容纳人数：${roomDetail.capacity}人"
            meetingList.addAll(roomDetail.meetingList)
        }
    }

    override fun getBuildingName(buildingInfoJson: BuildingInfoJson) {
        if (buildingInfoJson.roomList.isEmpty() || buildingInfoJson.roomList.size > 9)
            meeting_building_name.text = buildingInfoJson.name + "(" + buildingInfoJson.roomList.size + ")"
        else
            meeting_building_name.text = buildingInfoJson.name + "(0" + buildingInfoJson.roomList.size + ")"

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_meeting_room_detail, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_meeting_edit_delete -> {
                val bundle = Bundle()
                go<MeetingApplyActivity>(bundle)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setImageVisible(list: ArrayList<Int>) {
        if (list.contains(R.mipmap.icon__wifi)) iv_icon_wifi.visibility = View.VISIBLE
        if (list.contains(R.mipmap.icon__tv)) iv_icon_tv.visibility = View.VISIBLE
        if (list.contains(R.mipmap.icon__projector)) iv_icon_projector.visibility = View.VISIBLE
        if (list.contains(R.mipmap.icon__video)) iv_icon_video.visibility = View.VISIBLE
        if (list.contains(R.mipmap.icon__camera)) iv_icon_camera.visibility = View.VISIBLE
        if (list.contains(R.mipmap.icon__phone)) iv_icon_phone.visibility = View.VISIBLE
        if (list.contains(R.mipmap.icon__board)) iv_icon_board.visibility = View.VISIBLE
    }

    private val adapter: CommonRecycleViewAdapter<MeetingInfoJson> by lazy {
        object : CommonRecycleViewAdapter<MeetingInfoJson>(this, meetingList, R.layout.item_meeting_list_view) {
            override fun convert(holder: CommonRecyclerViewHolder?, t: MeetingInfoJson?) {
                val time = (t?.startTime)?.substring(11, 16) + "-" + (t?.completedTime)?.substring(11, 16)

                holder?.getView<ImageView>(R.id.image_meeting_list_item_icon)?.gone()
                holder?.setText(R.id.tv_meeting_list_item_time, time)
                        ?.setText(R.id.tv_meeting_list_item_title, t!!.subject)
                        ?.setText(R.id.tv_meeting_list_item_meeting_participants, "参加人：")

                holder!!.getView<TextView>(R.id.tv_meeting_list_item_meeting_originator)!!.tag = t!!.id + "%%%"
                mPresenter.asyncLoadPersonName(
                        holder.getView(R.id.tv_meeting_list_item_meeting_originator), t.id + "%%%", t.applicant)

                holder.let {
                    holder.getView<TextView>(R.id.tv_meeting_list_item_meeting_room).tag = t.id
                    mPresenter.asyncLoadRoomName(
                            holder.getView(R.id.tv_meeting_list_item_meeting_room), t.id, t.room)
                }

                holder.getView<TextView>(R.id.tv_meeting_list_item_meeting_participants).tag = t.id
                for (participants: String in t.invitePersonList) {
                    mPresenter.asyncLoadPersonName(
                            holder.getView(R.id.tv_meeting_list_item_meeting_participants), t.id, participants)
                }
            }
        }
    }

}
