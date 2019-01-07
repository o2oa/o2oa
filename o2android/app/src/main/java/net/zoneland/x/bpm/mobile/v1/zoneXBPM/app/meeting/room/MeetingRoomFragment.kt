package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.meeting.room

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.borax12.materialdaterangepicker.time.RadialPickerLayout
import com.borax12.materialdaterangepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.fragment_meeting_room.*
import net.muliba.changeskin.FancySkinManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPViewPagerFragment
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.meeting.reserve.MeetingRoomDetailActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.group.Group
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.group.GroupRecyclerViewAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.enums.DeviceEnum
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.MeetingRoom
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.DateHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.SystemDialogUtil
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.go
import java.util.*



/**
 * Created by 73419 on 2017/8/17 0017.
 */
class MeetingRoomFragment : BaseMVPViewPagerFragment<MeetingRoomFragmentContract.View,MeetingRoomFragmentContract.Presenter>(),
                MeetingRoomFragmentContract.View,com.borax12.materialdaterangepicker.time.TimePickerDialog.OnTimeSetListener{

    override var mPresenter: MeetingRoomFragmentContract.Presenter = MeetingRoomFragmentPresenter()
    override fun layoutResId(): Int = R.layout.fragment_meeting_room

    val roomList: ArrayList<Group<MeetingRoom.Building, MeetingRoom.Room>> = ArrayList()

    override fun initUI() {
        meeting_room_recycler_view.layoutManager = LinearLayoutManager(activity)
        meeting_room_recycler_view.adapter = adapter
        tv_meeting_room_date.addTextChangedListener(mDateTextWatcher)
        ll_meeting_room_date.setOnClickListener {
            SystemDialogUtil.getDateDialog(getString(R.string.meeting_start_day), tv_meeting_room_date)
        }
        ll_meeting_room_time.setOnClickListener{
            showTimePicker()
        }
    }

    override fun lazyLoad() {
        showLoadingDialog()
        val dayNow = DateHelper.nowByFormate("yyyy-MM-dd")

        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val startHour = if (hour > 9) "$hour:00" else "0$hour:00"
        val endHour = if (hour + 1 > 9) "${hour + 1}:00" else "0${hour + 1}:00"
        tv_meeting_room_start_time.text = startHour
        tv_meeting_room_end_time.text = endHour
        tv_meeting_room_date.text = dayNow
    }

    private fun showTimePicker(){
        val startHour = tv_meeting_room_start_time.text.substring(0,2)
        val startMinute = tv_meeting_room_start_time.text.substring(3,5)
        val endHour = tv_meeting_room_end_time.text.substring(0,2)
        val endMinute = tv_meeting_room_end_time.text.substring(3,5)
        val tpd : TimePickerDialog = TimePickerDialog.newInstance(
                this, Integer.parseInt(startHour), Integer.parseInt(startMinute),true,
                Integer.parseInt(endHour), Integer.parseInt(endMinute))
        tpd.show(activity.fragmentManager,"TimePickerDialog")
    }

    override fun findBuildingList(list: List<Group<MeetingRoom.Building, MeetingRoom.Room>>) {
        hideLoadingDialog()
        roomList.clear()
        roomList.addAll(list)
        adapter.notifyDataSetChanged()
    }

    override fun findError(message: String) {
        hideLoadingDialog()
        XToast.toastShort(activity, message)
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
                if (header.roomNumber<10)
                    holder.setText(R.id.tv_item_meeting_room_list_build_name, header.name+"(0"+header.roomNumber+")")
                else
                    holder.setText(R.id.tv_item_meeting_room_list_build_name, header.name+"("+header.roomNumber+")")
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
                roomName.setTextColor(FancySkinManager.instance().getColor(activity, R.color.z_color_text_hint))
                val res = if (child.available) {
                    if (child.idle) {
                        roomName.setTextColor(FancySkinManager.instance().getColor(activity,R.color.z_color_meeting_room_free))
                        R.mipmap.pic_meeting_room_free
                    } else {
                        roomName.setTextColor(FancySkinManager.instance().getColor(activity,R.color.z_color_primary_dark))
                        R.mipmap.pic_meeting_room_subscribe
                    }
                } else {
                    roomName.setTextColor(FancySkinManager.instance().getColor(activity,R.color.z_color_text_hint))
                    R.mipmap.pic_meeting_room_close
                }

                holder.setText(R.id.tv_meeting_room_name, child.name)
                        .setText(R.id.tv_meeting_room_floor, roomFloorNumber)
                        .setText(R.id.tv_meeting_room_number, roomNumber)
                        .setText(R.id.tv_meeting_list_item_meeting_room, "容纳人数：${child.capacity}人")
                        .setImageViewResource(R.id.iv_meeting_room_status,res)
                        .setText(R.id.tv_meeting_list_size,"${child.meetingList.size} 个")

                setImageGone(holder)
                setImageVisible(deviceResource,holder)
                holder.convertView.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putSerializable(MeetingRoomDetailActivity.ROOM_DETAIL,child)
                    bundle.putIntegerArrayList(MeetingRoomDetailActivity.DEVICE,deviceResource)
                    bundle.putString(MeetingRoomDetailActivity.DATE,tv_meeting_room_date.text.toString())
                    bundle.putString(MeetingRoomDetailActivity.START_TIME,tv_meeting_room_start_time.text.toString())
                    bundle.putString(MeetingRoomDetailActivity.END_TIME,tv_meeting_room_end_time.text.toString())
                    activity.go<MeetingRoomDetailActivity>(bundle)
                }
            }
        }
    }

    override fun onTimeSet(view: RadialPickerLayout?, hourOfDay: Int, minute: Int, hourOfDayEnd: Int, minuteEnd: Int) {
        val hourString = if (hourOfDay < 10) "0$hourOfDay" else "" + hourOfDay
        val minuteString = if (minute < 10) "0$minute" else "" + minute
        val hourStringEnd = if (hourOfDayEnd < 10) "0$hourOfDayEnd" else "" + hourOfDayEnd
        val minuteStringEnd = if (minuteEnd < 10) "0$minuteEnd" else "" + minuteEnd
        tv_meeting_room_start_time.text = "$hourString:$minuteString"
        tv_meeting_room_end_time.text = "$hourStringEnd:$minuteStringEnd"
        val date = tv_meeting_room_date.text
        showLoadingDialog()
        mPresenter.findBuildingListByTime("$date $hourString:$minuteString","$date $hourStringEnd:$minuteStringEnd")
    }

    private var mDateTextWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

        }

        override fun afterTextChanged(s: Editable) {
            showLoadingDialog()
            val startHour = tv_meeting_room_start_time.text
            val completeHour = tv_meeting_room_end_time.text
            if (startHour.isNotBlank()&&completeHour.isNotBlank())
                mPresenter.findBuildingListByTime("$s $startHour", "$s $completeHour")
        }
    }

}