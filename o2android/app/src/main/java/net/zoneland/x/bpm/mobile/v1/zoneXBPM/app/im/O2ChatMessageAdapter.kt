package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.im

import androidx.recyclerview.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.im.IMMessage
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.im.IMMessageBody
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.im.MessageType
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.DateHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.imageloader.O2ImageLoaderManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.CircleImageView
import retrofit2.http.POST


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
        return if (message.createPerson == O2SDKManager.instance().distinguishedName) {
            TEXT_right
        }else {
            TEXT_left
        }
//
//        val body = message.messageBody()
//        if(body != null) {
//            if (body is IMMessageBody.Text) {
//                return if (message.createPerson == O2SDKManager.instance().distinguishedName) {
//                    TEXT_right
//                }else {
//                    TEXT_left
//                }
//            }
//            //其它
//        }
//        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent?.context)
        animation =  AnimationUtils.loadAnimation(parent?.context, R.anim.jmui_rotate)
        return when(viewType) {
            TEXT_left -> CommonRecyclerViewHolder(inflater.inflate(R.layout.item_o2_chat_message_text_left, parent, false))
            else -> CommonRecyclerViewHolder(inflater.inflate(R.layout.item_o2_chat_message_text_right, parent, false))
        }
    }

    override fun getItemCount(): Int  = messages.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        val viewType = getItemViewType(position)
        if (holder is CommonRecyclerViewHolder) {
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
            holder.setText(R.id.tv_o2_chat_message_person_name, name)
                    .setText(R.id.tv_o2_chat_message_time, time)

            if (messageBody != null) {
                when(messageBody.type) {
                    MessageType.text.key -> renderTextMessage(messageBody, holder)
                    MessageType.emoji.key -> renderEmojiMessage(messageBody, holder)
                    MessageType.image.key -> renderImageMessage(messageBody, holder, position)
                    MessageType.audio.key -> renderAudioMessage(messageBody, holder, position)
                    MessageType.location.key -> renderLocationMessage(messageBody, holder)
                }
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

    private fun renderTextMessage(msgBody: IMMessageBody, holder: CommonRecyclerViewHolder) {
        val textMessageView = holder.getView<TextView>(R.id.tv_o2_chat_message_body)
        textMessageView.visible()
        textMessageView.text = msgBody.body
        val emojiMessageView = holder.getView<ImageView>(R.id.image_o2_chat_message_emoji_body)
        emojiMessageView.gone()
        val imageMessageView = holder.getView<ImageView>(R.id.image_o2_chat_message_image_body)
        imageMessageView.gone()
        val audioMessageView = holder.getView<LinearLayout>(R.id.ll_o2_chat_message_audio_body)
        audioMessageView.gone()
        val locationMessageView = holder.getView<RelativeLayout>(R.id.rl_o2_chat_message_location_body)
        locationMessageView.gone()
    }
    private fun renderEmojiMessage(msgBody: IMMessageBody, holder: CommonRecyclerViewHolder) {
        val textMessageView = holder.getView<TextView>(R.id.tv_o2_chat_message_body)
        textMessageView.gone()
        val emojiMessageView = holder.getView<ImageView>(R.id.image_o2_chat_message_emoji_body)
        if (msgBody.body  != null) {
            emojiMessageView.setImageResource(O2IM.emojiResId(msgBody.body!!))
        }
        emojiMessageView.visible()
        val imageMessageView = holder.getView<ImageView>(R.id.image_o2_chat_message_image_body)
        imageMessageView.gone()
        val audioMessageView = holder.getView<LinearLayout>(R.id.ll_o2_chat_message_audio_body)
        audioMessageView.gone()
        val locationMessageView = holder.getView<RelativeLayout>(R.id.rl_o2_chat_message_location_body)
        locationMessageView.gone()
    }
    private fun renderImageMessage(msgBody: IMMessageBody, holder: CommonRecyclerViewHolder,  position: Int) {
        val textMessageView = holder.getView<TextView>(R.id.tv_o2_chat_message_body)
        textMessageView.gone()
        val emojiMessageView = holder.getView<ImageView>(R.id.image_o2_chat_message_emoji_body)
        emojiMessageView.gone()
        val imageMessageView = holder.getView<ImageView>(R.id.image_o2_chat_message_image_body)
        if (!TextUtils.isEmpty(msgBody.fileId)) {
            val url = APIAddressHelper.instance().getImImageDownloadUrlWithWH(msgBody.fileId!!, 144, 192)
            O2ImageLoaderManager.instance().showImage(imageMessageView, url)
        }else if (!TextUtils.isEmpty(msgBody.fileTempPath)) {
            O2ImageLoaderManager.instance().showImage(imageMessageView, msgBody.fileTempPath!!)
        }
        imageMessageView.visible()
        imageMessageView.setOnClickListener { eventListener?.openOriginImage(position, msgBody) }
        val audioMessageView = holder.getView<LinearLayout>(R.id.ll_o2_chat_message_audio_body)
        audioMessageView.gone()
        val locationMessageView = holder.getView<RelativeLayout>(R.id.rl_o2_chat_message_location_body)
        locationMessageView.gone()
    }
    private fun renderAudioMessage(msgBody: IMMessageBody, holder: CommonRecyclerViewHolder, position: Int) {
        val textMessageView = holder.getView<TextView>(R.id.tv_o2_chat_message_body)
        textMessageView.gone()
        val emojiMessageView = holder.getView<ImageView>(R.id.image_o2_chat_message_emoji_body)
        emojiMessageView.gone()
        val imageMessageView = holder.getView<ImageView>(R.id.image_o2_chat_message_image_body)
        imageMessageView.gone()
        val audioMessageView = holder.getView<LinearLayout>(R.id.ll_o2_chat_message_audio_body)
        val durationTv = holder.getView<TextView>(R.id.tv_o2_chat_message_audio_duration)
        durationTv.text = "${msgBody.audioDuration}\""
        audioMessageView.visible()
        audioMessageView.setOnClickListener { eventListener?.playAudio(position, msgBody) }
        val locationMessageView = holder.getView<RelativeLayout>(R.id.rl_o2_chat_message_location_body)
        locationMessageView.gone()
    }
    private fun renderLocationMessage(msgBody: IMMessageBody, holder: CommonRecyclerViewHolder) {
        val textMessageView = holder.getView<TextView>(R.id.tv_o2_chat_message_body)
        textMessageView.gone()
        val emojiMessageView = holder.getView<ImageView>(R.id.image_o2_chat_message_emoji_body)
        emojiMessageView.gone()
        val imageMessageView = holder.getView<ImageView>(R.id.image_o2_chat_message_image_body)
        imageMessageView.gone()
        val audioMessageView = holder.getView<LinearLayout>(R.id.ll_o2_chat_message_audio_body)
        audioMessageView.gone()
        val locationMessageView = holder.getView<RelativeLayout>(R.id.rl_o2_chat_message_location_body)
        val addressTv = holder.getView<TextView>(R.id.tv_o2_chat_message_location_address)
        addressTv.text = msgBody.address
        locationMessageView.visible()
        locationMessageView.setOnClickListener { eventListener?.openLocation(msgBody) }
    }


    interface MessageEventListener {
        //重新发送消息
        fun resendClick(message: IMMessage)
        fun playAudio(position: Int, msgBody: IMMessageBody)
        fun openOriginImage(position: Int, msgBody: IMMessageBody)
        fun openLocation(msgBody: IMMessageBody)
    }
}