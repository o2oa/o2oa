package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.openim

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.callback.CreateGroupCallback
import cn.jpush.im.android.api.model.Conversation
import cn.jpush.im.android.eventbus.EventBus
import cn.jpush.im.api.BasicCallback
import jiguang.chat.activity.ChatActivity
import jiguang.chat.application.JGApplication
import jiguang.chat.entity.Event
import jiguang.chat.entity.EventType
import kotlinx.android.synthetic.main.activity_im_tribe_create.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.organization.ContactPickerActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecycleViewAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.imageloader.O2ImageLoaderManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.CircleImageView

class IMTribeCreateActivity : BaseMVPActivity<IMTribeCreateContract.View, IMTribeCreateContract.Presenter>(), IMTribeCreateContract.View {

    override var mPresenter: IMTribeCreateContract.Presenter = IMTribeCreateActivityPresenter()
    override fun layoutResId(): Int = R.layout.activity_im_tribe_create

    companion object {
        val PERSON_LIST_KEY = "PERSON_LIST_KEY"

        fun startCreate(personList: ArrayList<String>): Bundle {
            val bundle = Bundle()
            bundle.putStringArrayList(PERSON_LIST_KEY, personList)
            return bundle
        }

    }

    private val invitePersonAdd = "添加"

    private var personList = ArrayList<String>()

    override fun afterSetContentView(savedInstanceState: Bundle?) {
        personList = intent.extras?.getStringArrayList(PERSON_LIST_KEY) ?: ArrayList()
        setupToolBar(getString(R.string.activity_im_tribe_create_label), true)
        btn_im_tribe_submit.text = getString(R.string.activity_im_tribe_save)

        personList.add(0, invitePersonAdd)

        personAdapter.setOnItemClickListener { _, position ->
            when (position) {
                0 -> {
                    val nowList = ArrayList<String>()
                    personList.filter { it != invitePersonAdd }.map {
                        nowList.add(it)
                    }
                    val bundle = ContactPickerActivity.startPickerBundle(
                            arrayListOf("personPicker"),
                            multiple = true,
                            initUserList = nowList
                    )
                    contactPicker(bundle) { result ->
                        if (result != null) {
                            val users = result.users
                            personList.clear()
                            personList.add(invitePersonAdd)
                            users.map { personList.add(it.distinguishedName)  }
                            personAdapter.notifyDataSetChanged()
                        }
                    }
                }
                else -> {
                    if (personList[position] != O2SDKManager.instance().cId) {
                        personList.removeAt(position)
                        personAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
        recycler_im_tribe_members.layoutManager = GridLayoutManager(this, 5)
        recycler_im_tribe_members.adapter = personAdapter

        btn_im_tribe_submit.setOnClickListener {
            //提交
            val lastPersonList = ArrayList<String>()
            personList.filter { it != invitePersonAdd }.map {
                lastPersonList.add(it)
            }
            var tribeName = edit_im_tribe_name.text.toString()
            val tribeNotice = edit_im_tribe_notice.text.toString()
            if (lastPersonList.size < 3) {
                XToast.toastShort(this@IMTribeCreateActivity, "群成员必须有3个人及以上")
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(tribeName)) {
                var num = 0
                (0 until personList.size).map outerMap@ {
                    val itemview = (recycler_im_tribe_members.layoutManager as GridLayoutManager).findViewByPosition(it)
                    val nameTv = itemview.findViewById<TextView>(R.id.tv_name)
                    val name = nameTv.text.toString()
                    if (name != invitePersonAdd) {
                        tribeName += nameTv.text.toString() + "、"
                        if (num > 3) {
                            return@outerMap
                        }
                        num++
                    }
                }
                tribeName += "..."
            }

            JMessageClient.createGroup(tribeName, tribeNotice, object : CreateGroupCallback() {
                override fun gotResult(resCode: Int, resMessage: String?, groupId: Long) {
                    if (resCode == 0) {
                        JMessageClient.addGroupMembers(groupId, lastPersonList.filter { it != JMessageClient.getMyInfo().userName }, object : BasicCallback(){
                            override fun gotResult(code: Int, message: String?) {
                                if (code == 0) {
                                    //如果创建群组时添加了人,那么就在size基础上加上自己
                                    createGroup(groupId, lastPersonList.size)
//                                } else if (code == 810007) {
//                                    XLog.error("不能添加自己。。。。。。")
//                                    XToast.toastShort(this@IMTribeCreateActivity, "不能添加自己")
                                } else {
                                    XToast.toastShort(this@IMTribeCreateActivity, "添加群聊失败，添加的成员中有从未登录过我们O2应用的用户！")
                                }
                            }
                        })
                    }else {
                        XLog.error("code:$resCode, message:$resMessage")
                        XToast.toastShort(this@IMTribeCreateActivity, "创建群聊失败，IM服务器返回错误！")
                    }
                }
            })

        }

    }

    private fun createGroup(groupId: Long, size: Int) {
        var groupConversation: Conversation? = JMessageClient.getGroupConversation(groupId)
        if (groupConversation == null) {
            groupConversation = Conversation.createGroupConversation(groupId)
            EventBus.getDefault().post(Event.Builder()
                    .setType(EventType.createConversation)
                    .setConversation(groupConversation)
                    .build())
        }
        val intent = Intent(this, ChatActivity::class.java)
        //设置跳转标志
        intent.putExtra("fromGroup", true)
        intent.putExtra(JGApplication.CONV_TITLE, groupConversation!!.title)
        intent.putExtra(JGApplication.MEMBERS_COUNT, size)
        intent.putExtra(JGApplication.GROUP_ID, groupId)
        startActivity(intent)
        finish()
    }


    private val personAdapter: CommonRecycleViewAdapter<String> by lazy {
        object : CommonRecycleViewAdapter<String>(this, personList, R.layout.item_person_avatar_name) {
            override fun convert(holder: CommonRecyclerViewHolder?, t: String?) {
                if (TextUtils.isEmpty(t)) {
                    XLog.error("person id is null!!!!!!")
                    return
                }
                val avatar = holder?.getView<CircleImageView>(R.id.circle_image_avatar)
                val delete = holder?.getView<ImageView>(R.id.delete_people_iv)
                delete?.visibility = View.VISIBLE
                avatar?.setImageResource(R.mipmap.contact_icon_avatar)
                if (avatar != null) {
                    if (invitePersonAdd == t) {
                        avatar.setImageResource(R.mipmap.icon_add_people)
                        delete?.visibility = View.GONE
                    } else {
                        if (t == O2SDKManager.instance().cId) {
                            delete?.visibility = View.GONE
                        }
                        val url = APIAddressHelper.instance().getPersonAvatarUrlWithId(t!!)
                        O2ImageLoaderManager.instance().showImage(avatar, url)
                    }
                }
                val nameTv = holder?.getView<TextView>(R.id.tv_name)
                if (nameTv != null) {
                    if (invitePersonAdd == t) {
                        nameTv.text = t
                    } else {
                        nameTv.tag = t
                        mPresenter.asyncLoadPersonName(nameTv, t!!)
                    }
                }
            }
        }
    }
}
