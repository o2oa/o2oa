package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.text.TextUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.callback.CreateGroupCallback
import cn.jpush.im.android.api.callback.GetAvatarBitmapCallback
import cn.jpush.im.android.api.enums.ConversationType
import cn.jpush.im.android.api.event.*
import cn.jpush.im.android.api.model.Conversation
import cn.jpush.im.android.api.model.GroupInfo
import cn.jpush.im.android.api.model.UserInfo
import cn.jpush.im.android.eventbus.EventBus
import cn.jpush.im.api.BasicCallback
import jiguang.chat.activity.ChatActivity
import jiguang.chat.application.JGApplication
import kotlinx.android.synthetic.main.fragment_main_news.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2App
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPViewPagerFragment
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.organization.ContactPickerActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.im.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.O2PersonPickerResultItem
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.O2DialogSupport
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by fancylou on 10/9/17.
 */

class NewsFragment : BaseMVPViewPagerFragment<NewsContract.View, NewsContract.Presenter>(), NewsContract.View, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {

    private val REFRESH_CONVERSATION_LIST = 0x3000
    private val DISMISS_REFRESH_HEADER = 0x3001
    private val ROAM_COMPLETED = 0x3002

    override var mPresenter: NewsContract.Presenter = NewsPresenter()

    override fun layoutResId(): Int = R.layout.fragment_main_news


    private var mConvListView: ConversationListView? = null
    private var isLogin = false
    private var mThread: HandlerThread? = null
    private var mBackgroundHandler: BackgroundHandler? = null
    private var mReceiver: NetworkReceiver? = null
    private var mListAdapter: ConversationListAdapter? = null
    private var mDatas: MutableList<Conversation> = ArrayList()
    private var topConv: ArrayList<Conversation> = ArrayList()
    private var forCurrent: ArrayList<Conversation> = ArrayList()
    private var delFeedBack: ArrayList<Conversation> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mThread = HandlerThread("MainActivity")
        mThread?.start()
        mBackgroundHandler = BackgroundHandler(mThread?.looper)


        initReceiver()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        XLog.info("NewsFragment onAttach.....................")
    }

    override fun onResume() {
        super.onResume()
        XLog.info("NewsFragment onResume.....................")
    }

    override fun onPause() {
        super.onPause()
        XLog.info("NewsFragment onPause.......................")
    }

    override fun onStop() {
        super.onStop()
        XLog.info("NewsFragment onStop.......................")
    }

    override fun initUI() {
        XLog.info("initui.........................NewsFragment")
        //订阅接收消息,子类只要重写onEvent就能收到消息
        JMessageClient.registerEventReceiver(this)
        if (view == null) {
            XLog.error("fragment views is  null...........")
        }
        mConvListView = ConversationListView(view, activity, this)
        mConvListView?.initModule()
        val manager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeInfo = manager.activeNetworkInfo
        if (null == activeInfo) {
            mConvListView?.showHeaderView()
        } else {
            mConvListView?.dismissHeaderView()
            mConvListView?.showLoadingHeader()
            mBackgroundHandler?.sendEmptyMessageDelayed(DISMISS_REFRESH_HEADER, 1000)
        }
        mConvListView?.setItemListeners(this)
        mConvListView?.setLongClickListener(this)

        mListAdapter = ConversationListAdapter(activity, mDatas, mConvListView)
        conv_list_view.adapter = mListAdapter
        XLog.info("init news fragment ui finish..........................")
        tv_conversation_log_error.setOnClickListener {
            XLog.info("click reload 。。。。。。。。。。。。。。")
            lazyLoad()
        }
        if (O2App.instance._JMIsLogin()) {
            isLogin = true
            failInitIM(false)
        } else {
            failInitIM(true)
        }
    }

    override fun lazyLoad() {
        XLog.info("NewsFragment lazyLoad............")
        if (!isLogin) {
            if (O2App.instance._JMIsLogin()) {
                isLogin = true
                failInitIM(false)
                conv_list_view.visible()
                null_conversation.gone()
                loadConversationList()
            } else { //显示的时候开没有登录成功
                failInitIM(true)
            }
        } else {
            loadConversationList()
        }
    }

    private fun loadConversationList() {

        doAsync {
            XLog.info("doAsync...........................")
            forCurrent.clear()
            topConv.clear()
            delFeedBack.clear()
            var i = 0
            val datas = JMessageClient.getConversationList()
            if (datas != null && datas.size > 0) {
                val sortConvList = SortConvList()
                Collections.sort(datas, sortConvList)
                for (con in datas) {
                    if (con.getTargetId() == "feedback_Android") {
                        delFeedBack.add(con)
                    }
                    if (!TextUtils.isEmpty(con.extra)) {
                        forCurrent.add(con)
                    }
                }
                topConv.addAll(forCurrent)
                datas.removeAll(forCurrent)
                datas.removeAll(delFeedBack)

            }
            if (topConv.size > 0) {
                val top = SortTopConvList()
                Collections.sort(topConv, top)
                for (conv in topConv) {
                    datas?.add(i, conv)
                    i++
                }
            }

            uiThread {
                mDatas.clear()
                if (datas != null && datas.isNotEmpty()) {
                    XLog.info("conv list :${datas.size}")
                    mDatas.addAll(datas)
                    mConvListView?.setNullConversation(true)
                } else {
                    XLog.info("conv list is null...")
                    mConvListView?.setNullConversation(false)
                }
                mListAdapter?.notifyDataSetChanged()
            }
        }


    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_news_tribe_create, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.menu_single_create -> {
                XLog.info("创建单聊。。。。。。。。。。。。。。。。。。。。。。。。。。。")
                if (isLogin) {
                    (activity as MainActivity).getCurrentIdentityUnit { topOrg ->
                        chooseSinglePerson(topOrg)
                    }
                } else {
                    XToast.toastShort(activity, "聊天服务器未登录成功，无法发起聊天！")
                }
                return true
            }
            R.id.menu_tribe_create -> {
                XLog.info("创建群聊。。。。。。。。。。。。。。。。。。。。。。。。。。。")
                if (isLogin) {
                    (activity as MainActivity).getCurrentIdentityUnit { topOrg ->
                        chooseMultiPerson(topOrg)
                    }
                } else {
                    XToast.toastShort(activity, "聊天服务器未登录成功，无法发起聊天！")
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun chooseSinglePerson(topOrg: String?) {
        try {
            val topList = ArrayList<String>()
            if (!TextUtils.isEmpty(topOrg)) {
                topList.add(topOrg!!)
            }
            val bundle = ContactPickerActivity.startPickerBundle(
                    arrayListOf("personPicker"),
                    topUnitList =  topList,
                    multiple = false,
                    maxNumber = 1
            )
            (activity as MainActivity).contactPicker(bundle) { result ->
                if (result != null) {
                    val users = result.users
                    if (users.isNotEmpty()) {
                        createSingleChat(users)
                    }
                }
            }
        } catch (e: Exception) {
        }
    }

    private fun chooseMultiPerson(topOrg: String?) {
        try {
            val topList = ArrayList<String>()
            if (!TextUtils.isEmpty(topOrg)) {
                topList.add(topOrg!!)
            }
            val bundle = ContactPickerActivity.startPickerBundle(
                    arrayListOf("personPicker"),
                    topUnitList =  topList,
                    multiple = true
            )
            (activity as MainActivity).contactPicker(bundle) { result ->
                if (result != null) {
                    val users = result.users
                    if (users.isNotEmpty()) {
                        createGroupChat(users)
                    }
                }
            }
        } catch (e: Exception) {
        }
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        activity?.unregisterReceiver(mReceiver)
        mBackgroundHandler?.removeCallbacksAndMessages(null)
        mThread?.looper?.quit()
        //注销消息接收
        JMessageClient.unRegisterEventReceiver(this)
        super.onDestroy()
    }


    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        //点击会话条目
        val intent = Intent(activity, ChatActivity::class.java)
        if (position > 0) {
            //这里-3是减掉添加的三个headView
            val conv = mDatas?.get(position - 2)
            if (conv != null) {
                intent.putExtra(JIMConstant.CONV_TITLE, conv.title)
                //群聊
                if (conv.type == ConversationType.group) {
                    if (mListAdapter?.includeAtMsg(conv) == true) {
                        intent.putExtra("atMsgId", mListAdapter?.getAtMsgId(conv))
                    }

                    if (mListAdapter?.includeAtAllMsg(conv) == true) {
                        intent.putExtra("atAllMsgId", mListAdapter?.getatAllMsgId(conv))
                    }
                    val groupId = (conv.targetInfo as GroupInfo).groupID
                    intent.putExtra(JIMConstant.GROUP_ID, groupId)
                    intent.putExtra(JIMConstant.DRAFT, mListAdapter?.getDraft(conv.id))

                    activity.startActivity(intent)
                    return
//                    //单聊
                } else {
                    val targetId = (conv.targetInfo as UserInfo).userName
                    intent.putExtra(JIMConstant.TARGET_ID, targetId)
                    intent.putExtra(JIMConstant.TARGET_APP_KEY, conv.targetAppKey)
                    intent.putExtra(JIMConstant.DRAFT, mListAdapter?.getDraft(conv.id))
                }
                activity.startActivity(intent)
            }
        }
    }


    override fun onItemLongClick(parent: AdapterView<*>, view: View, position: Int, id: Long): Boolean {
        val conv = mDatas?.get(position - 2)
        if (conv != null) {
            O2DialogSupport.openConfirmDialog(activity, getString(R.string.delete_conv_for_news_fragment), { _->
                if (conv.type == ConversationType.group) {
                    JMessageClient.deleteGroupConversation((conv.targetInfo as GroupInfo).groupID)
                } else {
                    JMessageClient.deleteSingleConversation((conv.targetInfo as UserInfo).userName)
                }
                mDatas.removeAt(position - 2)
                if (mDatas.size > 0) {
                    mConvListView?.setNullConversation(true)
                } else {
                    mConvListView?.setNullConversation(false)
                }
                mListAdapter?.notifyDataSetChanged()
            })
//            val listener = View.OnClickListener { v ->
//                when (v.id) {
//                //会话置顶
//                    R.id.jmui_top_conv_ll -> {
//                        //已经置顶,去取消
//                        if (!TextUtils.isEmpty(conv.extra)) {
//                            mListAdapter.setCancelConvTop(conv)
//                            //没有置顶,去置顶
//                        } else {
//                            mListAdapter.setConvTop(conv)
//                        }
//                        mDialog.dismiss()
//                    }
//                //删除会话
//                    R.id.jmui_delete_conv_ll -> {
//                        if (conv.type == ConversationType.group) {
//                            JMessageClient.deleteGroupConversation((conv.targetInfo as GroupInfo).groupID)
//                        } else {
//                            JMessageClient.deleteSingleConversation((conv.targetInfo as UserInfo).userName)
//                        }
//                        mDatas.removeAt(position - 3)
//                        if (mDatas.size > 0) {
//                            mConvListView.setNullConversation(true)
//                        } else {
//                            mConvListView.setNullConversation(false)
//                        }
//                        mListAdapter.notifyDataSetChanged()
//                        mDialog.dismiss()
//                    }
//                    else -> {
//                    }
//                }
//            }
//            mDialog = DialogCreator.createDelConversationDialog(activity, listener, TextUtils.isEmpty(conv.extra))
//            mDialog.show()
//            mDialog.getWindow()!!.setLayout((0.8 * mWidth).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
        }
        return true
    }

    fun sortConvList() {
        mListAdapter?.sortConvList()
    }

    /**
     * 收到消息
     */
    fun onEvent(event: MessageEvent) {
        //todo bottombar
//        mConvListView.setUnReadMsg(JMessageClient.getAllUnReadMsgCount())
        val msg = event.message
        if (msg.targetType == ConversationType.group) {
            val groupId = (msg.targetInfo as GroupInfo).groupID
            val conv = JMessageClient.getGroupConversation(groupId)
            if (conv != null) {
                if (msg.isAtMe) {
                    O2App.instance.isAtMe.put(groupId, true)
                    mListAdapter?.putAtConv(conv, msg.id)
                }
                if (msg.isAtAll) {
                    O2App.instance.isAtall.put(groupId, true)
                    mListAdapter?.putAtAllConv(conv, msg.id)
                }
                mBackgroundHandler?.sendMessage(mBackgroundHandler?.obtainMessage(REFRESH_CONVERSATION_LIST,
                        conv))
            }
        } else {
            val userInfo = msg.targetInfo as UserInfo
            val targetId = userInfo.userName
            val conv = JMessageClient.getSingleConversation(targetId, userInfo.appKey)
            if (conv != null) {
                activity?.runOnUiThread(Runnable {
                    if (TextUtils.isEmpty(userInfo.avatar)) {
                        userInfo.getAvatarBitmap(object : GetAvatarBitmapCallback() {
                            override fun gotResult(responseCode: Int, responseMessage: String, avatarBitmap: Bitmap) {
                                if (responseCode == 0) {
                                    mListAdapter?.notifyDataSetChanged()
                                }
                            }
                        })
                    }
                })
                mBackgroundHandler?.sendMessage(mBackgroundHandler?.obtainMessage(REFRESH_CONVERSATION_LIST, conv))
            }
        }
    }

    /**
     * 接收离线消息
     *
     * @param event 离线消息事件
     */
    fun onEvent(event: OfflineMessageEvent) {
        val conv = event.conversation
        if (conv.targetId != "feedback_Android") {
            mBackgroundHandler?.sendMessage(mBackgroundHandler?.obtainMessage(REFRESH_CONVERSATION_LIST, conv))
        }
    }

    /**
     * 消息撤回
     */
    fun onEvent(event: MessageRetractEvent) {
        val conversation = event.conversation
        mBackgroundHandler?.sendMessage(mBackgroundHandler?.obtainMessage(REFRESH_CONVERSATION_LIST, conversation))
    }

    /**
     * 消息已读事件
     */
    fun onEventMainThread(event: MessageReceiptStatusChangeEvent) {
        mListAdapter?.notifyDataSetChanged()
    }

    /**
     * 消息漫游完成事件
     *
     * @param event 漫游完成后， 刷新会话事件
     */
    fun onEvent(event: ConversationRefreshEvent) {
        val conv = event.conversation
        if (conv.targetId != "feedback_Android") {
            mBackgroundHandler?.sendMessage(mBackgroundHandler?.obtainMessage(REFRESH_CONVERSATION_LIST, conv))
            //多端在线未读数改变时刷新
            if (event.reason == ConversationRefreshEvent.Reason.UNREAD_CNT_UPDATED) {
                mBackgroundHandler?.sendMessage(mBackgroundHandler?.obtainMessage(REFRESH_CONVERSATION_LIST, conv))
            }
        }
    }

    fun onEventMainThread(event: Event) {
        when (event.type) {
            EventType.createConversation -> {
                val conv = event.conversation
                if (conv != null) {
                    mListAdapter?.addNewConversation(conv)
                }
            }
            EventType.deleteConversation -> {
                val conv = event.conversation
                if (null != conv) {
                    mListAdapter?.deleteConversation(conv)
                }
            }
        //收到保存为草稿事件
            EventType.draft -> {
                val conv = event.conversation
                val draft = event.draft
                //如果草稿内容不为空，保存，并且置顶该会话
                if (!TextUtils.isEmpty(draft)) {
                    mListAdapter?.putDraftToMap(conv, draft)
                    mListAdapter?.setToTop(conv)
                    //否则删除
                } else {
                    mListAdapter?.delDraftFromMap(conv)
                }
            }
            EventType.addFriend -> {
            }
        }
    }


    private fun failInitIM(fail: Boolean) {
        if (fail) {
            conv_list_view.gone()
            tv_conversation_log_error.visible()
        }else {
            conv_list_view.visible()
            tv_conversation_log_error.gone()
        }
    }


    private fun initReceiver() {
        mReceiver = NetworkReceiver()
        val filter = IntentFilter()
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        activity?.registerReceiver(mReceiver, filter)
    }


    //监听网络状态的广播
    private inner class NetworkReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null && intent.action == "android.net.conn.CONNECTIVITY_CHANGE") {
                val manager = activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeInfo = manager.activeNetworkInfo
                if (null == activeInfo) {
                    mConvListView?.showHeaderView()
                } else {
                    mConvListView?.dismissHeaderView()
                }
            }
        }
    }

    private inner class BackgroundHandler(looper: Looper?) : Handler(looper) {

        override fun handleMessage(msg: android.os.Message) {
            super.handleMessage(msg)
            when (msg.what) {
                REFRESH_CONVERSATION_LIST -> {
                    val conv = msg.obj as Conversation
                    mListAdapter?.setToTop(conv)
                }
                DISMISS_REFRESH_HEADER -> activity?.runOnUiThread(Runnable { mConvListView?.dismissLoadingHeader() })
                ROAM_COMPLETED -> {
                    val conv = msg.obj as Conversation
                    mListAdapter?.addAndSort(conv)
                }
            }
        }
    }

    private fun createSingleChat(chooseUserList: List<O2PersonPickerResultItem>) {
        val lastPersonIdList = chooseUserList.filter { it.id != JMessageClient.getMyInfo().userName }
        if (lastPersonIdList.isEmpty()) {
            XToast.toastShort(activity, "没有选择用户！")
            return
        }else {
            if (O2App.instance._JMIsLogin()) {
                val targetId = lastPersonIdList[0].id
                val targetName = lastPersonIdList[0].name
                var conv: Conversation? = JMessageClient.getSingleConversation(targetId)
                //如果会话为空，使用EventBus通知会话列表添加新会话
                if (conv == null) {
                    conv = Conversation.createSingleConversation(targetId)
                    EventBus.getDefault().post(jiguang.chat.entity.Event.Builder()
                            .setType(jiguang.chat.entity.EventType.createConversation)
                            .setConversation(conv)
                            .build())
                }
                val intent = Intent(activity, ChatActivity::class.java)
                //设置跳转标志
                intent.putExtra(JGApplication.CONV_TITLE, conv!!.title)
                intent.putExtra(JIMConstant.CONV_TITLE, targetName)
                intent.putExtra(JIMConstant.TARGET_ID, targetId)
                intent.putExtra(JIMConstant.TARGET_APP_KEY, O2App.instance.JM_IM_APP_KEY)
                startActivity(intent)
            }else {
                XToast.toastShort(activity, "无法聊天，没有连接到IM服务器！！")
            }
        }
    }

    //创建群聊 服务端
    private fun createGroupChat(chooseUserList: List<O2PersonPickerResultItem>) {
        val lastPersonIdList = chooseUserList.filter { it.id != JMessageClient.getMyInfo().userName }.map { it.id }
        if (lastPersonIdList.size < 2) {
            XToast.toastShort(activity, "创建群里需要3位及以上成员！")
            return
        }else {
            JMessageClient.createGroup(null, null, object : CreateGroupCallback() {
                override fun gotResult(resCode: Int, resMessage: String?, groupId: Long) {
                    if (resCode == 0) {
                        JMessageClient.addGroupMembers(groupId, lastPersonIdList, object : BasicCallback() {
                            override fun gotResult(code: Int, message: String?) {
                                if (code == 0) {
                                    //如果创建群组时添加了人,那么就在size基础上加上自己
                                    createGroup(groupId, lastPersonIdList.size+1)
                                } else {
                                    XToast.toastShort(activity, "添加群聊失败，添加的成员中有从未登录过我们O2应用的用户！")
                                }
                            }
                        })
                    } else {
                        XLog.error("code:$resCode, message:$resMessage")
                        XToast.toastShort(activity, "创建群聊失败，IM服务器返回错误！")
                    }
                }
            })
        }
    }


    //创建群聊 本地
    private fun createGroup(groupId: Long, size: Int) {
        var groupConversation: Conversation? = JMessageClient.getGroupConversation(groupId)
        if (groupConversation == null) {
            groupConversation = Conversation.createGroupConversation(groupId)
            EventBus.getDefault().post(jiguang.chat.entity.Event.Builder()
                    .setType(jiguang.chat.entity.EventType.createConversation)
                    .setConversation(groupConversation)
                    .build())
        }
        val intent = Intent(activity, ChatActivity::class.java)
        //设置跳转标志
        intent.putExtra("fromGroup", true)
        intent.putExtra(JGApplication.CONV_TITLE, groupConversation!!.title)
        intent.putExtra(JGApplication.MEMBERS_COUNT, size)
        intent.putExtra(JGApplication.GROUP_ID, groupId)
        startActivity(intent)
    }

}