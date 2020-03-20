package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.im

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.ImageView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.im.IMMessage
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.im.IMMessageBody
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.DateHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.imageloader.O2ImageLoaderManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.CircleImageView


class O2ChatMessageAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TEXT_left = 0
    private val TEXT_right = 1


    private val messages = ArrayList<IMMessage>()
    private var animation: Animation? = null
    var eventListener: MessageEventListener? = null

    fun addPageMessage(list: List<IMMessage>) {
        messages.addAll(0, list)
        notifyDataSetChanged()
    }

    fun addMessage(message: IMMessage) {
        messages.add(message)
        notifyDataSetChanged()
    }
    fun sendMessageSuccess(msgId: String) {
        for ((index, msg) in messages.withIndex()) {
            if (msg.id == msgId) {
                msg.sendStatus = 0
                messages[index] = msg
                notifyItemChanged(index)
                break
            }
        }
    }

    fun sendMessageFail(msgId: String) {
        for((index, msg) in messages.withIndex()) {
            if (msg.id == msgId) {
                msg.sendStatus = 2
                messages[index] = msg
                notifyItemChanged(index)
                break
            }
        }
    }

    fun lastPosition() : Int {
        return messages.size - 1
    }


    override fun getItemViewType(position: Int): Int {
        val message = messages[position]
        val body = message.messageBody()
        if(body != null) {
            if (body is IMMessageBody.Text) {
                return if (message.createPerson == O2SDKManager.instance().distinguishedName) {
                    TEXT_right
                }else {
                    TEXT_left
                }
            }
            //其它
        }
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent?.context)
        animation =  AnimationUtils.loadAnimation(parent?.context, R.anim.jmui_rotate)
        return when(viewType) {
            TEXT_left -> CommonRecyclerViewHolder(inflater.inflate(R.layout.item_o2_chat_message_text_left, parent, false))
            else -> CommonRecyclerViewHolder(inflater.inflate(R.layout.item_o2_chat_message_text_right, parent, false))
        }
    }

    override fun getItemCount(): Int  = messages.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
//        val viewType = getItemViewType(position)
        if (holder != null && holder is CommonRecyclerViewHolder) {
            val message = messages[position]
            val messageBody = message.messageBody()
            val name = if (message.createPerson.isNotEmpty() && message.createPerson.contains("@")) {
                message.createPerson.substring(0, message.createPerson.indexOf("@"))
            }else {
                message.createPerson
            }
            var time = ""
            if (position == 0) {
                time = DateHelper.imChatMessageTime(message.createTime)
            }else {
                val lastTime = messages[position-1].createTime
                val thisTime = message.createTime
                if (DateHelper.imChatTimeBiggerThan1Minute(lastTime, thisTime)) {
                    time = DateHelper.imChatMessageTime(message.createTime)
                }
            }
            if (messageBody!= null && messageBody is IMMessageBody.Text) {
                holder.setText(R.id.tv_o2_chat_message_body, messageBody.body)
                        .setText(R.id.tv_o2_chat_message_person_name, name)
                        .setText(R.id.tv_o2_chat_message_time, time)
            }
            //头像
            val avatar = holder.getView<CircleImageView>(R.id.image_o2_chat_message_avatar)
            val url = APIAddressHelper.instance().getPersonAvatarUrlWithId(message.createPerson)
            O2ImageLoaderManager.instance().showImage(avatar, url)
            //发送loading
            val loading = holder.getView<ImageView>(R.id.image_ot_chat_message_sending)
            loading.visible()
            val sendFailBtn = holder.getView<ImageButton>(R.id.btn_ot_chat_message_resend)
            sendFailBtn.gone()
            animation?.let { loading.startAnimation(it) }
            if (message.sendStatus != 1) {
                loading.clearAnimation()
                loading.gone()
                if (message.sendStatus == 2) {
                    sendFailBtn.visible()
                }
            }
            sendFailBtn.setOnClickListener {
                eventListener?.resendClick(message)
            }
        }
    }


    interface MessageEventListener {
        //重新发送消息
        fun resendClick(message: IMMessage)
    }
}