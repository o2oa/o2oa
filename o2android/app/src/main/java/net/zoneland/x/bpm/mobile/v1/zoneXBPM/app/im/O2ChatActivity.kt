package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.im

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.activity_o2_chat.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecycleViewAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.im.IMConversationInfo
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.im.IMMessage
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.im.IMMessageBody
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.DateHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.go
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import java.util.*


class O2ChatActivity : BaseMVPActivity<O2ChatContract.View, O2ChatContract.Presenter>(), O2ChatContract.View {

    companion object {
        const val con_id_key = "con_id_key"
        fun startChat(activity: Activity, conversationId: String) {
            val bundle = Bundle()
            bundle.putString(con_id_key, conversationId)
            activity.go<O2ChatActivity>(bundle)
        }
    }


    override var mPresenter: O2ChatContract.Presenter = O2ChatPresenter()

    override fun layoutResId(): Int = R.layout.activity_o2_chat



    private val adapter: O2ChatMessageAdapter by lazy { O2ChatMessageAdapter() }
    private val emojiList = O2IM.im_emoji_hashMap.keys.toList().sortedBy { it }
    private val emojiAdapter : CommonRecycleViewAdapter<String> by lazy {
        object : CommonRecycleViewAdapter<String>(this, emojiList, R.layout.item_o2_im_chat_emoji) {
            override fun convert(holder: CommonRecyclerViewHolder?, t: String?) {
                if (t != null) {
                    holder?.setImageViewResource(R.id.image_item_o2_im_chat_emoji, O2IM.emojiResId(t))
                }
            }
        }
    }

    //
    private val defaultTitle = "聊天界面"
    private var page = 0

    private var conversationId = ""

    private var conversationInfo: IMConversationInfo? = null


    private var mKeyboardHeight = 150 // 输入法默认高度为400



    override fun afterSetContentView(savedInstanceState: Bundle?) {
        // 起初的布局可自动调整大小
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE or WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)


        setupToolBar(defaultTitle, setupBackButton = true)

        conversationId = intent.getStringExtra(con_id_key) ?: ""
        if (TextUtils.isEmpty(conversationId)) {
            XToast.toastShort(this, "缺少参数！")
            finish()
        }
        //消息列表初始化
        rv_o2_chat_messages.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_o2_chat_messages.adapter = adapter
        adapter.eventListener = object : O2ChatMessageAdapter.MessageEventListener {
            override fun resendClick(message: IMMessage) {
                mPresenter.sendTextMessage(message)//重新发送
            }
        }
        //输入法切换的时候滚动到底部
        cl_o2_chat_outside.addOnLayoutChangeListener { _, _, _, _, bottom, _, _, _, oldBottom ->
            if (bottom < oldBottom) {
                scroll2Bottom()
            }
        }

        //表情初始化
        rv_o2_chat_emoji_box.layoutManager = GridLayoutManager(this, 10)
        rv_o2_chat_emoji_box.adapter = emojiAdapter
        emojiAdapter.setOnItemClickListener { _, position ->
            val key = emojiList[position]
            XLog.debug(key)
            newEmojiMessage(key)
            //更新阅读时间
            mPresenter.readConversation(conversationId)
        }



        initListener()

        getPageData()

        registerBroadcast()
    }



    override fun onDestroy() {
        super.onDestroy()
        if (mReceiver != null) {
            unregisterReceiver(mReceiver)
        }
    }

    override fun conversationInfo(info: IMConversationInfo) {
        conversationInfo = info
        //
        var title = defaultTitle
        if (O2IM.conversation_type_single == conversationInfo?.type) {
            val persons = conversationInfo?.personList
            if (persons != null && persons.isNotEmpty()) {
                val person = persons.firstOrNull { it != O2SDKManager.instance().distinguishedName }
                if (person != null) {
                    title = person.substring(0, person.indexOf("@"))
                }
            }
        }else if(O2IM.conversation_type_group == conversationInfo?.type) {
            title = conversationInfo?.title ?: defaultTitle
        }
        updateToolbarTitle(title)
    }

    override fun conversationGetFail() {
        XToast.toastShort(this, "获取会话信息异常！")
        finish()
    }

    override fun backPageMessages(list: List<IMMessage>) {
        if(list.isNotEmpty()) {
            page++
            adapter.addPageMessage(list)
        }
        //第一次 滚动到底部
        if (page == 1) {
            scroll2Bottom()
        }
    }

    override fun sendMessageSuccess(id: String) {
        //消息前面的loading消失
        adapter.sendMessageSuccess(id)
    }

    override fun sendFail(id: String) {
        //消息前面的loading消失 变成重发按钮
        adapter.sendMessageFail(id)
    }

    /**
     * 监听
     */
    private fun initListener() {
        et_o2_chat_input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null && !TextUtils.isEmpty(s)) {
                    btn_o2_chat_send.visible()
                    btn_o2_chat_emotion.gone()
                }else {
                    btn_o2_chat_emotion.visible()
                    btn_o2_chat_send.gone()
                }
            }
        })
        et_o2_chat_input.setOnClickListener {
            rv_o2_chat_emoji_box_out.postDelayed({
                rv_o2_chat_emoji_box.gone()
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
            }, 250)
        }
        rv_o2_chat_emoji_box_out.setKeyboardListener { isActive, keyboardHeight ->
            if (isActive) { // 输入法打开
                if (mKeyboardHeight != keyboardHeight) { // 键盘发生改变时才设置emojiView的高度，因为会触发onGlobalLayoutChanged，导致onKeyboardStateChanged再次被调用
                    mKeyboardHeight = keyboardHeight
                    initEmojiView() // 每次输入法弹起时，设置emojiView的高度为键盘的高度，以便下次emojiView弹出时刚好等于键盘高度
                }
                if (rv_o2_chat_emoji_box.visibility == View.VISIBLE) { // 表情打开状态下
                    rv_o2_chat_emoji_box.gone()
                }
            }
        }
        btn_o2_chat_emotion.setOnClickListener {
            if (rv_o2_chat_emoji_box_out.isKeyboardActive) { //输入法激活时
                if(rv_o2_chat_emoji_box.visibility == View.GONE) {
                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING) //  不改变布局，隐藏键盘，emojiView弹出
                    val imm =  it.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(et_o2_chat_input.applicationWindowToken, 0)
                    rv_o2_chat_emoji_box.visibility = View.VISIBLE
                }else {
                    rv_o2_chat_emoji_box.visibility = View.GONE
                    val imm =  it.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(et_o2_chat_input.applicationWindowToken, 0)
                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                }
            }else {
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                if(rv_o2_chat_emoji_box.visibility == View.GONE) {
                    rv_o2_chat_emoji_box.visibility = View.VISIBLE
                }else {
                    rv_o2_chat_emoji_box.visibility = View.GONE
                }
            }

        }
        btn_o2_chat_send.setOnClickListener {
            sendTextMessage()
        }
    }
    // 设置表情栏的高度
    private fun initEmojiView() {
        val layoutParams = rv_o2_chat_emoji_box.layoutParams
        layoutParams.height = mKeyboardHeight
        rv_o2_chat_emoji_box.layoutParams = layoutParams
    }


    private fun getPageData() {
        mPresenter.getMessage(page + 1, conversationId)
        //更新阅读时间
        mPresenter.readConversation(conversationId)
    }

    private fun scroll2Bottom() {
        rv_o2_chat_messages.scrollToPosition(adapter.lastPosition())
    }

    /**
     * 发送消息
     */
    private fun sendTextMessage() {
        val text = et_o2_chat_input.text.toString()
        if (!TextUtils.isEmpty(text)) {
            et_o2_chat_input.setText("")
            newTextMessage(text)
        }
        //更新阅读时间
        mPresenter.readConversation(conversationId)
    }

    /**
     * 创建文本消息 并发送
     */
    private fun newTextMessage(text: String) {
        val time = DateHelper.now()
        val body = IMMessageBody.Text(text)
        val bodyJson = O2SDKManager.instance().gson.toJson(body)
        XLog.debug("body: $bodyJson")
        val uuid = UUID.randomUUID().toString()
        val message = IMMessage(uuid, conversationId, bodyJson,
                O2SDKManager.instance().distinguishedName, time, 1)
        adapter.addMessage(message)
        mPresenter.sendTextMessage(message)//发送到服务器
        scroll2Bottom()
    }

    /**
     * 创建表情消息
     */
    private fun newEmojiMessage(emoji: String) {
        val time = DateHelper.now()
        val body = IMMessageBody.Emoji(emoji)
        val bodyJson = O2SDKManager.instance().gson.toJson(body)
        XLog.debug("body: $bodyJson")
        val uuid = UUID.randomUUID().toString()
        val message = IMMessage(uuid, conversationId, bodyJson,
                O2SDKManager.instance().distinguishedName, time, 1)
        adapter.addMessage(message)
        mPresenter.sendTextMessage(message)//发送到服务器
        scroll2Bottom()
    }

    /**
     * 接收到消息
     */
    private fun receiveMessage(message: IMMessage) {
        adapter.addMessage(message)
        scroll2Bottom()
        //更新阅读时间
        mPresenter.readConversation(conversationId)
    }


    var mReceiver: IMMessageReceiver? = null
    private fun registerBroadcast() {
        mReceiver = IMMessageReceiver()
        val filter = IntentFilter(O2IM.IM_Message_Receiver_Action)
        registerReceiver(mReceiver, filter)
    }


    inner class IMMessageReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val body = intent?.getStringExtra(O2IM.IM_Message_Receiver_name)
            if (body != null && body.isNotEmpty()) {
                XLog.debug("接收到im消息, $body")
                try {
                    val message = O2SDKManager.instance().gson.fromJson<IMMessage>(body, IMMessage::class.java)
                    if (message.conversationId == conversationId) {
                        receiveMessage(message)
                    }
                } catch (e: Exception) {
                    XLog.error("", e)
                }

            }
        }

    }
}
