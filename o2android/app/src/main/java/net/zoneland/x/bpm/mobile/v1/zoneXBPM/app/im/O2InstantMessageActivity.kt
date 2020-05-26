package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.im

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_o2_instant_message.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecycleViewAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.InstantMessage
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.DateHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.go
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.CircleImageView

class O2InstantMessageActivity : BaseMVPActivity<O2InstantMessageContract.View, O2InstantMessageContract.Presenter>(), O2InstantMessageContract.View {
    override var mPresenter: O2InstantMessageContract.Presenter = O2InstantMessagePresenter()


    override fun layoutResId(): Int = R.layout.activity_o2_instant_message


    companion object {
        val messageListKey = "messageListKey"
        fun openInstantActivity(instantList: ArrayList<InstantMessage>, activity: Activity) {
            val bundle = Bundle()
            bundle.putParcelableArrayList(messageListKey, instantList)
            activity.go<O2InstantMessageActivity>(bundle)
        }
    }

    private val instantList = ArrayList<InstantMessage>()
    private val adapter: CommonRecycleViewAdapter<InstantMessage> by lazy {
        object : CommonRecycleViewAdapter<InstantMessage>(this, instantList, R.layout.item_o2_chat_message_text_left) {
            override fun convert(holder: CommonRecyclerViewHolder?, t: InstantMessage?) {
                if (t != null && holder!= null) {
                    val avatar = holder.getView<CircleImageView>(R.id.image_o2_chat_message_avatar)
                    avatar.setImageResource(messageTypeAvatar(t.type))
                    val titleText = holder.getView<TextView>(R.id.tv_o2_chat_message_body)
                    titleText.text = t.title
                    titleText.visible()
                    val time = DateHelper.imChatMessageTime(t.createTime)
                    holder.setText(R.id.tv_o2_chat_message_time, time)
                }

            }

        }
    }


    override fun afterSetContentView(savedInstanceState: Bundle?) {
        setupToolBar("通知消息", setupBackButton = true)
        val inList = intent?.extras?.getParcelableArrayList<InstantMessage>(messageListKey)
        if (inList != null && inList.isNotEmpty()) {
            instantList.clear()
            instantList.addAll(inList)
        }

        rv_o2_instant_messages.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_o2_instant_messages.adapter = adapter
        if (instantList.isNotEmpty()) {
            rv_o2_instant_messages.scrollToPosition(instantList.size - 1)
        }
    }


    private fun messageTypeAvatar(type: String) : Int {
        if (type.startsWith("task_")) {
            return R.mipmap.ic_todo_task
        }else if (type.startsWith("taskCompleted_")) {
            return R.mipmap.ic_todo_task_completed
        }else if (type.startsWith("read_")) {
            return R.mipmap.ic_todo_read
        }else if (type.startsWith("readCompleted_")) {
            return R.mipmap.ic_todo_read_completed
        }else if (type.startsWith("review_")||type.startsWith("work_")||type.startsWith("process_")) {
            return R.mipmap.ic_todo_task
        }else if (type.startsWith("meeting_")) {
            return R.mipmap.app_meeting
        }else if (type.startsWith("attachment_")) {
            return R.mipmap.app_yunpan
        }else if (type.startsWith("calendar_")) {
            return R.mipmap.app_calendar
        }else if (type.startsWith("cms_")) {
            return R.mipmap.app_cms
        }else if (type.startsWith("bbs_")) {
            return R.mipmap.app_bbs
        }else if (type.startsWith("mind_")) {
            return R.mipmap.app_mind_map
        }else if (type.startsWith("attachment_")) {
            return R.mipmap.app_attendance
        }else {
            return R.mipmap.app_o2_ai
        }
    }
}
