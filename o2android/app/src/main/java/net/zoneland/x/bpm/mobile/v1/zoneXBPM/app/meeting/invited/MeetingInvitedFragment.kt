package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.meeting.invited

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.yanzhenjie.recyclerview.swipe.SwipeMenuCreator
import com.yanzhenjie.recyclerview.swipe.SwipeMenuItem
import com.yanzhenjie.recyclerview.swipe.widget.DefaultItemDecoration
import kotlinx.android.synthetic.main.fragment_meeting_invited.*
import net.muliba.changeskin.FancySkinManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPViewPagerFragment
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.meeting.edit.MeetingEditActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecycleViewAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.meeting.MeetingInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.goWithRequestCode
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import org.jetbrains.anko.dip

/**
 * Created by 73419 on 2017/8/16 0016.
 */
class MeetingInvitedFragment : BaseMVPViewPagerFragment<MeetingInvitedFragmentContract.View, MeetingInvitedFragmentContract.Presenter>(),
        MeetingInvitedFragmentContract.View, View.OnClickListener {

    companion object {
        val RECEIVE_INVITE_TYPE = 0
        val ORIGINATOR_INVITE_TYPE = 1
    }

    override var mPresenter: MeetingInvitedFragmentContract.Presenter = MeetingInvitedFragmentPresenter()
    override fun layoutResId(): Int = R.layout.fragment_meeting_invited

    private val receiveInviteList = ArrayList<MeetingInfoJson>()
    private val originatorInviteList = ArrayList<MeetingInfoJson>()
    private var currentType = RECEIVE_INVITE_TYPE

    override fun initUI() {
        sr_meeting_invited_layout.setColorSchemeResources(R.color.z_color_refresh_scuba_blue,
                R.color.z_color_refresh_red, R.color.z_color_refresh_purple, R.color.z_color_refresh_orange)
        sr_meeting_invited_layout.setOnRefreshListener { refreshRecyclerView() }
        ll_receive_invite_title.setOnClickListener(this)
        ll_originator_invite_title.setOnClickListener(this)

        val menuCreator = SwipeMenuCreator { swipeLeftMenu, swipeRightMenu, viewType ->
            val agreeItem = SwipeMenuItem(context)
            agreeItem.height = ViewGroup.LayoutParams.MATCH_PARENT
            agreeItem.width = activity.dip(70)
            agreeItem.text = "同意"
            agreeItem.setTextColor(Color.WHITE)
            agreeItem.textSize = 18
            agreeItem.background = ColorDrawable(ContextCompat.getColor(context, R.color.meeting_agree))
            swipeRightMenu.addMenuItem(agreeItem)
            val rejectItem = SwipeMenuItem(context)
            rejectItem.height = ViewGroup.LayoutParams.MATCH_PARENT
            rejectItem.width = activity.dip(70)
            rejectItem.text = "拒绝"
            rejectItem.setTextColor(Color.WHITE)
            rejectItem.textSize = 18
            rejectItem.background = ColorDrawable(ContextCompat.getColor(context, R.color.meeting_reject))
            swipeRightMenu.addMenuItem(rejectItem)
        }
        receive_invite_list.layoutManager = LinearLayoutManager(activity)
        receive_invite_list.addItemDecoration(DefaultItemDecoration(ContextCompat.getColor(activity, R.color.z_color_split_line_ddd)))
        receive_invite_list.setSwipeMenuCreator(menuCreator)
        receive_invite_list.setSwipeMenuItemClickListener { menuBridge ->
            menuBridge.closeMenu()// 任何操作必须先关闭菜单，否则可能出现Item菜单打开状态错乱。
            val menuPosition = menuBridge.position
            val itemPosition = menuBridge.adapterPosition
            when (menuPosition) {
                0 -> {
                    mPresenter.acceptMeetingInvited(receiveInviteList[itemPosition].id)
                }
                1 -> {
                    mPresenter.rejectMeetingInvited(receiveInviteList[itemPosition].id)
                }
            }
        }
        receive_invite_list.adapter = receiveInviteListAdapter
        originator_invite_list.layoutManager = LinearLayoutManager(context)
        originator_invite_list.adapter = originatorInviteListAdapter
    }

    override fun lazyLoad() {
        mPresenter.getReceiveInviteMeetingList()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ll_receive_invite_title -> {
                currentType = RECEIVE_INVITE_TYPE
                toggleView()
            }
            R.id.ll_originator_invite_title -> {
                currentType = ORIGINATOR_INVITE_TYPE
                toggleView()
            }
        }
    }

    override fun refreshMeetingList() {
        mPresenter.getReceiveInviteMeetingList()
    }

    override fun onError(message: String) {
        XToast.toastShort(activity, message)
    }

    override fun loadReceiveInviteMeetingList(meetingList: List<MeetingInfoJson>) {
        if (currentType == RECEIVE_INVITE_TYPE) {
            originator_invite_list.gone()
            if (meetingList.isNotEmpty()) {
                receive_invite_list.visible()
                ll_empty_meeting.gone()
                receiveInviteList.clear()
                receiveInviteList.addAll(meetingList)
                receiveInviteListAdapter.notifyDataSetChanged()
            } else {
                receive_invite_list.gone()
                ll_empty_meeting.visible()
            }
            sr_meeting_invited_layout.isRefreshing = false
        }
    }

    override fun loadOriginatorMeetingList(meetingList: List<MeetingInfoJson>) {
        if (currentType == ORIGINATOR_INVITE_TYPE) {
            receive_invite_list.gone()
            if (meetingList.isNotEmpty()) {
                originator_invite_list.visible()
                ll_empty_meeting.gone()
                originatorInviteList.clear()
                originatorInviteList.addAll(meetingList)
                originatorInviteListAdapter.notifyDataSetChanged()
            } else {
                originator_invite_list.gone()
                ll_empty_meeting.visible()
            }
            sr_meeting_invited_layout.isRefreshing = false
        }
    }

    private fun refreshRecyclerView() {
        when (currentType) {
            RECEIVE_INVITE_TYPE -> mPresenter.getReceiveInviteMeetingList()
            ORIGINATOR_INVITE_TYPE -> mPresenter.getOriginatorMeetingList()
        }
    }

    private fun toggleView() {
        if (currentType == RECEIVE_INVITE_TYPE) {
            tv_receive_invite_title.setTextColor(FancySkinManager.instance().getColor(activity, R.color.z_color_primary))
            view_receive_invite_title_divider.visible()
            tv_originator_invite_title.setTextColor(FancySkinManager.instance().getColor(activity, R.color.z_color_text_primary))
            view_originator_invite_title_divider.gone()
            mPresenter.getReceiveInviteMeetingList()
        } else {
            tv_originator_invite_title.setTextColor(FancySkinManager.instance().getColor(activity, R.color.z_color_primary))
            view_originator_invite_title_divider.visible()
            tv_receive_invite_title.setTextColor(FancySkinManager.instance().getColor(activity, R.color.z_color_text_primary))
            view_receive_invite_title_divider.gone()
            mPresenter.getOriginatorMeetingList()
        }
    }

    private val originatorInviteListAdapter: CommonRecycleViewAdapter<MeetingInfoJson> by lazy {
        object : CommonRecycleViewAdapter<MeetingInfoJson>(activity, originatorInviteList, R.layout.item_meeting_my_invited_list) {
            override fun convert(holder: CommonRecyclerViewHolder, t: MeetingInfoJson) {
                val time = (t.startTime).substring(11, 19) + "-" + (t.completedTime).substring(11, 19)
                var participants = "参加人: "
                t.invitePersonList
                        .map { it.split("@")[0] }
                        .forEach { participants += "$it " }
                holder.setText(R.id.meeting_card_view_time, time)
                        ?.setText(R.id.meeting_card_view_title, t.subject)
                        ?.setText(R.id.meeting_card_view_participants_id, participants)

                holder.getView<TextView>(R.id.meeting_card_view_room_id).tag = t.id
                mPresenter.asyncLoadRoomName(
                        holder.getView(R.id.meeting_card_view_room_id), t.id, t.room)
                holder.getView<TextView>(R.id.meeting_card_view_originator_id).tag = t.id + "%%%"
                mPresenter.asyncLoadPersonName(
                        holder.getView(R.id.meeting_card_view_originator_id), t.id + "%%%", t.applicant)
                holder.convertView.setOnClickListener {
                    activity.goWithRequestCode<MeetingEditActivity>(MeetingEditActivity.startBundleData(t,
                            holder.getView<TextView>(R.id.meeting_card_view_room_id).text.toString()))
                }
            }
        }
    }

    private val receiveInviteListAdapter: CommonRecycleViewAdapter<MeetingInfoJson> by lazy {
        object : CommonRecycleViewAdapter<MeetingInfoJson>(activity, receiveInviteList, R.layout.item_meeting_invited_list) {
            override fun convert(holder: CommonRecyclerViewHolder?, t: MeetingInfoJson) {
                val time = (t.startTime).substring(11, 19) + "-" + (t.completedTime).substring(11, 19)
                var participants = "参加人: "
                t.invitePersonList
                        .map { it.split("@")[0] }
                        .forEach { participants += "$it " }
                holder?.setText(R.id.meeting_card_view_time, time)
                        ?.setText(R.id.meeting_card_view_title, t.subject)
                        ?.setText(R.id.meeting_card_view_participants_id, participants)

                val room = holder?.getView<TextView>(R.id.meeting_card_view_room_id)
                if (room != null) {
                    room.tag = t.id
                    mPresenter.asyncLoadRoomName(room, t.id, t.room)
                }

                val originator = holder?.getView<TextView>(R.id.meeting_card_view_originator_id)
                if (originator != null) {
                    originator.tag = t.id + "%%%"
                    mPresenter.asyncLoadPersonName(originator, t.id + "%%%", t.applicant)
                }

            }
        }

    }
}