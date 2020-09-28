package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.im

import android.app.Activity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_o2_instant_message.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.bbs.main.BBSMainActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.calendar.CalendarMainActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.clouddrive.CloudDriveActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.clouddrive.v2.CloudDiskActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.cms.index.CMSIndexActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.meeting.main.MeetingMainActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.webview.TaskWebViewActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecycleViewAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.enums.ApplicationEnum
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.flutter.FlutterConnectActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.InstantMessage
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.DateHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.go
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.CircleImageView
import org.json.JSONObject
import org.json.JSONTokener
import java.lang.Exception

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
                    messageTypeEvent(titleText, t)
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

    override fun workIsCompleted(flag: Boolean, workId: String) {
        hideLoadingDialog()
        if (!flag) {
            go<TaskWebViewActivity>(TaskWebViewActivity.start(workId, "", ""))
        }else {
            XToast.toastShort(this, "工作已经结束！")
        }
    }

    private fun messageTypeEvent(textView: TextView, msg: InstantMessage) {
        val type = msg.type
        if (type.startsWith("task_")) {
            if (!type.contains("_delete")) {
                openWork(msg, textView)
            }
        }else if (type.startsWith("taskCompleted_")) {
            if (!type.contains("_delete")) {
                openWork(msg, textView)
            }
        }else if (type.startsWith("read_")) {
            if (!type.contains("_delete")) {
                openWork(msg, textView)
            }
        }else if (type.startsWith("readCompleted_")) {
            if (!type.contains("_delete")) {
                openWork(msg, textView)
            }
        }else if (type.startsWith("review_")||type.startsWith("work_")||type.startsWith("process_")) {
            if (!type.contains("_delete")) {
                openWork(msg, textView)
            }
        }else if (type.startsWith("meeting_")) {
            setLinkStyle(textView) {
                go<MeetingMainActivity>()
            }
        }else if (type.startsWith("attachment_")) {
            setLinkStyle(textView) {
//                go<CloudDriveActivity>()
                go<CloudDiskActivity>()
            }
        }else if (type.startsWith("calendar_")) {
            setLinkStyle(textView) {
                go<CalendarMainActivity>()
            }
        }else if (type.startsWith("cms_")) {
            setLinkStyle(textView) {
                go<CMSIndexActivity>()
            }
        }else if (type.startsWith("bbs_")) {
            setLinkStyle(textView) {
                go<BBSMainActivity>()
            }
        }else if (type.startsWith("mind_")) {
            setLinkStyle(textView) {
                go<FlutterConnectActivity>(FlutterConnectActivity.startFlutterAppWithRoute(ApplicationEnum.MindMap.key))
            }
        }else {
        }
    }

    private fun openWork(msg: InstantMessage, textView: TextView) {
        val json = JSONTokener(msg.body).nextValue()
        if (json is JSONObject) {
            val work = try {json.getString("work")}catch (e: Exception){null}
            if (!TextUtils.isEmpty(work)) {
                setLinkStyle(textView) {
                    //先查询work对象
                    showLoadingDialog()
                    mPresenter.getWorkInfo(work!!)
                }
            }
        }
    }

    private fun setLinkStyle(textView: TextView, openClick:(() -> Unit)) {
        val text = textView.text
        if (!TextUtils.isEmpty(text)) {
            val len = text.length
            val span  = SpannableString(text)
            span.setSpan(ForegroundColorSpan(ContextCompat.getColor(this, R.color.colorPrimary_blue)), 0, len,  Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            span.setSpan(UnderlineSpan(), 0, len , Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            textView.text = span
            textView.setOnClickListener { openClick() }
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
        }else {
            return R.mipmap.app_o2_ai
        }
    }
}
