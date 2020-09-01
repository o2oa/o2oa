package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.im.fm

import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.wugang.activityresult.library.ActivityResult
import kotlinx.android.synthetic.main.fragment_o2_im_conversation.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPViewPagerFragment
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.im.O2ChatActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.im.O2IM
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.im.O2InstantMessageActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.main.MainActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.organization.ContactPickerActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecycleViewAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.InstantMessage
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.im.IMConversationInfo
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.im.IMMessage
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.im.MessageType
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.ContactPickerResult
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.DateHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.imageloader.O2ImageLoaderManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.CircleImageView


class O2IMConversationFragment : BaseMVPViewPagerFragment<O2IMConversationContract.View, O2IMConversationContract.Presenter>(),
        O2IMConversationContract.View {
    override var mPresenter: O2IMConversationContract.Presenter = O2IMConversationPresenter()

    override fun layoutResId(): Int = R.layout.fragment_o2_im_conversation
    private val instantList = ArrayList<InstantMessage>()
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
                    }else if(O2IM.conversation_type_group == t.type) {
                        holder.setText(R.id.tv_o2_im_con_title, t.title )
                                .setImageViewResource(R.id.image_o2_im_con_avatar, R.mipmap.group_default)
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
                        when(lastMessageBody?.type) {
                            MessageType.emoji.key -> {
                                val image = holder.getView<ImageView>(R.id.tv_o2_im_con_last_message_emoji)
                                image.setImageResource(O2IM.emojiResId(lastMessageBody.body!!))
                                image.visible()
                                val text = holder.getView<TextView>(R.id.tv_o2_im_con_last_message)
                                text.gone()
                            }
                            MessageType.text.key -> {
                                val image = holder.getView<ImageView>(R.id.tv_o2_im_con_last_message_emoji)
                                image.gone()
                                val text = holder.getView<TextView>(R.id.tv_o2_im_con_last_message)
                                text.text = lastMessageBody.body
                                text.visible()
                            }
                            else -> {
                                val image = holder.getView<ImageView>(R.id.tv_o2_im_con_last_message_emoji)
                                image.gone()
                                val text = holder.getView<TextView>(R.id.tv_o2_im_con_last_message)
                                text.text = lastMessageBody?.body
                                text.visible()
                            }
                        }

                        holder.setText(R.id.tv_o2_im_con_last_message_time, DateHelper.friendlyTime(lastTime))

                    }
                }
            }
        }
    }

    override fun initUI() {
        rv_o2_im_conversation.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        rv_o2_im_conversation.adapter = adapter
        adapter.setOnItemClickListener { _, position ->
            O2ChatActivity.startChat(activity!!, cList[position].id!!)
        }
        ll_o2_instant_message.setOnClickListener {
            if (instantList.isNotEmpty()) {
                O2InstantMessageActivity.openInstantActivity(instantList, activity!!)
            }
        }
    }


    override fun lazyLoad() {
        XLog.debug("lazy load im conversation。。。。。。。。。。")
        mPresenter.getMyConversationList()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_news_tribe_create, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId) {
            R.id.menu_single_create -> {
                openCreateSingleConversation()
                return true
            }
            R.id.menu_tribe_create -> {
                openCreateTribeConversation()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun myConversationList(list: List<IMConversationInfo>) {
        if (list.isEmpty()) {
            tv_null_conversation.visible()
            ll_o2_im_message_list.gone()
            setUnreadNumber(0)
        } else {
            tv_null_conversation.gone()
            ll_o2_im_message_list.visible()
            cList.clear()
            cList.addAll(list)
            adapter.notifyDataSetChanged()
            var allnumbers = 0
            list.forEach { con ->
                val number = con.unreadNumber ?: 0
                allnumbers += number
            }
            setUnreadNumber(allnumbers)
        }
        mPresenter.getMyInstantMessageList()
    }


    override fun myInstantMessageList(instantList: List<InstantMessage>) {
         if (instantList.isNotEmpty()) {
             this.instantList.clear()
             this.instantList.addAll(instantList)
             ll_o2_instant_message.visible()
             val lastMsg = instantList.last()
             val lastTime = DateHelper.convertStringToDate(lastMsg.createTime)
             tv_o2_in_con_last_message_time.text = DateHelper.friendlyTime(lastTime)
             tv_o2_in_con_last_message.text = lastMsg.title
         }else {
             ll_o2_instant_message.gone()
         }
    }

    override fun createConvSuccess(conv: IMConversationInfo) {
        if (!cList.any { it.id == conv.id }) {
            cList.add(conv)
            adapter.notifyDataSetChanged()
        }
        O2ChatActivity.startChat(activity!!, conv.id!!)
    }

    override fun createConvFail(message: String) {
        XToast.toastShort(activity, message)
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


    private fun setUnreadNumber(number: Int) {
        if (activity is MainActivity) {
            (activity as MainActivity).refreshUnreadNumber(number)
        }
    }

    private fun openCreateSingleConversation() {
        ActivityResult.of(activity!!)
                .className(ContactPickerActivity::class.java)
                .params(ContactPickerActivity.startPickerBundle(pickerModes = arrayListOf(ContactPickerActivity.personPicker), multiple = false))
                .greenChannel().forResult { _, data ->
                    val result = data?.getParcelableExtra<ContactPickerResult>(ContactPickerActivity.CONTACT_PICKED_RESULT)
                    if (result != null && result.users.isNotEmpty()) {
                        createSingleConversation(result.users[0].distinguishedName)
                    }else {
                        XLog.debug("没有选择人员！！！！")
                    }
                }
    }

    private fun createSingleConversation(user: String) {
        mPresenter.createConversation("single", arrayListOf(user))
    }

    private fun openCreateTribeConversation() {
        ActivityResult.of(activity!!)
                .className(ContactPickerActivity::class.java)
                .params(ContactPickerActivity.startPickerBundle(pickerModes = arrayListOf(ContactPickerActivity.personPicker), multiple = true))
                .greenChannel().forResult { _, data ->
                    val result = data?.getParcelableExtra<ContactPickerResult>(ContactPickerActivity.CONTACT_PICKED_RESULT)
                    if (result != null && result.users.isNotEmpty()) {
                        val a = arrayListOf<String>()
                        a.addAll(result.users.map { it.distinguishedName })
                        mPresenter.createConversation("group",  a)
                    }else {
                        XLog.debug("没有选择人员！！！！")
                    }
                }
    }
}