package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.meeting.room


import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_meeting_room.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.group.Group
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.group.GroupRecyclerViewAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.enums.DeviceEnum
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.MeetingRoom
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast


class MeetingRoomChooseActivity : BaseMVPActivity<MeetingRoomChooseContract.View, MeetingRoomChooseContract.Presenter>(), MeetingRoomChooseContract.View {

    override var mPresenter: MeetingRoomChooseContract.Presenter = MeetingRoomChoosePresenter()
    override fun layoutResId(): Int = R.layout.activity_meeting_room

    val roomList: ArrayList<Group<MeetingRoom.Building, MeetingRoom.Room>> = ArrayList()
    lateinit var startTime: String
    lateinit var endTime: String

    companion object {
        val START_TIME_KEY = "START_TIME_KEY"
        val END_TIME_KEY = "END_TIME_KEY"
        val RESULT_ROOM_NAME_KEY = "RESULT_ROOM_NAME_KEY"
        val RESULT_ROOM_ID_KEY = "RESULT_ROOM_ID_KEY"

        fun startBundleData(startTime: String, endTime: String): Bundle {
            val bundle = Bundle()
            bundle.putString(START_TIME_KEY, startTime)
            bundle.putString(END_TIME_KEY, endTime)
            return bundle
        }
    }

    override fun afterSetContentView(savedInstanceState: Bundle?) {
        startTime = intent?.extras?.getString(START_TIME_KEY) ?: ""
        endTime = intent?.extras?.getString(END_TIME_KEY) ?: ""
        if (TextUtils.isEmpty(startTime) || TextUtils.isEmpty(endTime)) {
            XToast.toastShort(this, "传入参数为空！！")
            finish()
            return
        }

        //val showTime = endTime.substring(11, endTime.length)
        tv_meeting_room_date.text = startTime.substring(0,10)
        tv_meeting_room_time.text = startTime.substring(11,16)+"-"+endTime.substring(11,16)

        setupToolBar(getString(R.string.title_activity_meeting_create), true)

        recycler_meeting_room_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycler_meeting_room_list.adapter = adapter

        showLoadingDialog()
        mPresenter.findBuildingListByTime(startTime, endTime)
    }


    override fun findBuildingList(list: List<Group<MeetingRoom.Building, MeetingRoom.Room>>) {
        hideLoadingDialog()
        roomList.clear()
        roomList.addAll(list)
        adapter.notifyDataSetChanged()
    }

    override fun findError(message: String) {
        hideLoadingDialog()
        XToast.toastShort(this, message)
    }

    private fun chooseRoom(room: MeetingRoom.Room) {
        val bundle = Bundle()
        bundle.putString(RESULT_ROOM_ID_KEY, room.id)
        bundle.putString(RESULT_ROOM_NAME_KEY, room.name)
        intent.putExtras(bundle)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun setImageGone(holder : CommonRecyclerViewHolder){
        holder.getView<ImageView>(R.id.iv_icon_wifi).visibility = View.GONE
        holder.getView<ImageView>(R.id.iv_icon_tv).visibility = View.GONE
        holder.getView<ImageView>(R.id.iv_icon_projector).visibility = View.GONE
        holder.getView<ImageView>(R.id.iv_icon_video).visibility = View.GONE
        holder.getView<ImageView>(R.id.iv_icon_camera).visibility = View.GONE
        holder.getView<ImageView>(R.id.iv_icon_phone).visibility = View.GONE
        holder.getView<ImageView>(R.id.iv_icon_board).visibility = View.GONE
    }

    private fun setImageVisible(list : ArrayList<Int>,holder : CommonRecyclerViewHolder){
        if (list.contains(R.mipmap.icon__wifi)) holder.getView<ImageView>(R.id.iv_icon_wifi).visibility = View.VISIBLE
        if (list.contains(R.mipmap.icon__tv)) holder.getView<ImageView>(R.id.iv_icon_tv).visibility = View.VISIBLE
        if (list.contains(R.mipmap.icon__projector)) holder.getView<ImageView>(R.id.iv_icon_projector).visibility = View.VISIBLE
        if (list.contains(R.mipmap.icon__video)) holder.getView<ImageView>(R.id.iv_icon_video).visibility = View.VISIBLE
        if (list.contains(R.mipmap.icon__camera)) holder.getView<ImageView>(R.id.iv_icon_camera).visibility = View.VISIBLE
        if (list.contains(R.mipmap.icon__phone)) holder.getView<ImageView>(R.id.iv_icon_phone).visibility = View.VISIBLE
        if (list.contains(R.mipmap.icon__board)) holder.getView<ImageView>(R.id.iv_icon_board).visibility = View.VISIBLE
    }

    val adapter: GroupRecyclerViewAdapter<MeetingRoom.Building, MeetingRoom.Room> by lazy {
        object : GroupRecyclerViewAdapter<MeetingRoom.Building, MeetingRoom.Room>(roomList,
                R.layout.item_meeting_room_list_title, R.layout.item_meeting_room_view) {
            override fun onBindHeaderViewHolder(holder: CommonRecyclerViewHolder, header: MeetingRoom.Building, position: Int) {
                holder.setText(R.id.tv_item_meeting_room_list_build_name, header.name)
            }

            override fun onBindChildViewHolder(holder: CommonRecyclerViewHolder, child: MeetingRoom.Room, position: Int) {
                val deviceResource = ArrayList<Int>()
                child.device.split("#").map{  s ->
                    deviceResource.add(DeviceEnum.getResourceByKey(s))
                }
                val roomFloorNumber = "楼层：${child.floor}"
                var roomNumber = ""
                if (!TextUtils.isEmpty(child.roomNumber)) {
                    roomNumber =  " 房间：${child.roomNumber}"
                }
                val roomName = holder.getView<TextView>(R.id.tv_meeting_room_name)
                roomName.setTextColor(ContextCompat.getColor(this@MeetingRoomChooseActivity, R.color.z_color_text_hint))
                val res = if (child.available) {
                    if (child.idle) {
                        roomName.setTextColor(Color.parseColor("#FF66CC80"))
                        R.mipmap.pic_meeting_room_free
                    } else {
                        roomName.setTextColor(Color.parseColor("#FFFB4747"))
                        R.mipmap.pic_meeting_room_subscribe
                    }
                } else {
                    roomName.setTextColor(Color.parseColor("#999999"))
                    R.mipmap.pic_meeting_room_close
                }

                holder.setText(R.id.tv_meeting_room_name, child.name)
                        .setText(R.id.tv_meeting_room_floor, roomFloorNumber)
                        .setText(R.id.tv_meeting_room_number, roomNumber)
                        .setText(R.id.tv_meeting_list_item_meeting_room, "容纳人数：${child.capacity}人")
                        .setImageViewResource(R.id.iv_meeting_room_status,res)

                setImageGone(holder)
                setImageVisible(deviceResource,holder)
                holder.convertView.setOnClickListener {
                    if (child.available) {
                        if (child.idle) {
                            chooseRoom(child)
                        }else {
                            XLog.info("会议室使用中")
                            XToast.toastShort(this@MeetingRoomChooseActivity, "会议室使用中，请选择其他会议室或其他时间段！")
                        }
                    }else {
                        XLog.info("会议室关闭无法使用")
                        XToast.toastShort(this@MeetingRoomChooseActivity, "会议室关闭无法使用，请选择其他会议室！")
                    }
                }
            }
        }
    }
}
