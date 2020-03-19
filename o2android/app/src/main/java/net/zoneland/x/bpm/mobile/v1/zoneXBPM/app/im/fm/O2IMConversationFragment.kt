package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.im.fm

import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuInflater
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_o2_im_conversation.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPViewPagerFragment
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.im.O2ChatActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.im.O2IM
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecycleViewAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.im.IMConversationInfo
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.im.IMMessage
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.im.IMMessageBody
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.DateHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.imageloader.O2ImageLoaderManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.CircleImageView


class O2IMConversationFragment : BaseMVPViewPagerFragment<O2IMConversationContract.View, O2IMConversationContract.Presenter>(),
        O2IMConversationContract.View {
    override var mPresenter: O2IMConversationContract.Presenter = O2IMConversationPresenter()

    override fun layoutResId(): Int = R.layout.fragment_o2_im_conversation
    private val cList = ArrayList<IMConversationInfo>()
    private val adapter: CommonRecycleViewAdapter<IMConversationInfo> by lazy {
        object : CommonRecycleViewAdapter<IMConversationInfo>(activity, cList,
                R.layout.item_o2_im_conversation) {
            override fun convert(holder: CommonRecyclerViewHolder?, t: IMConversationInfo?) {
                if (holder != null && t != null) {
                    if (t.type == O2IM.conversation_type_single) {
                        //头像
                        val person = t.personList.firstOrNull { it != O2SDKManager.instance().distinguishedName }
                        if (person != null) {
                            val url = APIAddressHelper.instance().getPersonAvatarUrlWithId(person)
                            val avatar = holder.getView<CircleImageView>(R.id.image_o2_im_con_avatar)
                            O2ImageLoaderManager.instance().showImage(avatar, url)
                            val name = person.substring(0, person.indexOf("@"))
                            holder.setText(R.id.tv_o2_im_con_title, name)
                        }
                    }
                    val unread = holder.getView<TextView>(R.id.tv_o2_im_con_unread_number)
                    if (t.unreadNumber > 0) {
                        unread.text = "${t.unreadNumber}"
                        unread.visible()
                    }else {
                        unread.gone()
                    }
                    val lastMessage = t.lastMessage
                    if (lastMessage != null) {
                        val lastTime = DateHelper.convertStringToDate(lastMessage.createTime)
                        val lastMessageBody = lastMessage.messageBody()
                        var lastMessageText = ""
                        if (lastMessageBody != null) {
                            lastMessageText = when(lastMessageBody) {
                                is IMMessageBody.Text -> {lastMessageBody.body}
                                else -> "" //其它消息类型 转化成文本
                            }
                        }
                        holder.setText(R.id.tv_o2_im_con_last_message_time, DateHelper.friendlyTime(lastTime))
                                .setText(R.id.tv_o2_im_con_last_message, lastMessageText)
                    }
                }
            }
        }
    }

    override fun initUI() {
        rv_o2_im_conversation.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        rv_o2_im_conversation.adapter = adapter
        adapter.setOnItemClickListener { _, position ->
            O2ChatActivity.startChat(activity, cList[position].id)
        }
    }


    override fun lazyLoad() {
        mPresenter.getMyConversationList()
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_news_tribe_create, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun myConversationList(list: List<IMConversationInfo>) {
        if (list.isEmpty()) {
            tv_null_conversation.visible()
            rv_o2_im_conversation.gone()
        } else {
            tv_null_conversation.gone()
            rv_o2_im_conversation.visible()
            cList.clear()
            cList.addAll(list)
            adapter.notifyDataSetChanged()
        }
    }

    fun receiveMessageFromWebsocket(message: IMMessage) {
        for ((index, imConversationInfo) in cList.withIndex()) {
            if (imConversationInfo.id == message.conversationId) {
                imConversationInfo.lastMessage = message
                imConversationInfo.unreadNumber = imConversationInfo.unreadNumber + 1
                cList[index] = imConversationInfo
                adapter.notifyDataSetChanged()
                break
            }
        }
    }
}