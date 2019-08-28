package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.openim

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.text.TextUtils
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_im_tribe_info.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.organization.ContactPickerActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecycleViewAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.imageloader.O2ImageLoaderManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.CircleImageView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.O2DialogSupport

class IMTribeInfoActivity : BaseMVPActivity<IMTribeInfoContract.View, IMTribeInfoContract.Presenter>(), IMTribeInfoContract.View, View.OnClickListener {

    override var mPresenter: IMTribeInfoContract.Presenter = IMTribeInfoActivityPresenter()
    override fun layoutResId(): Int = R.layout.activity_im_tribe_info

    companion object {
        val DEFAULT_ID: Long = -1024
        val TRIBE_ID_KEY = "TRIBE_ID_KEY"

        fun start(tribeId: Long): Bundle {
            val bundle = Bundle()
            bundle.putLong(TRIBE_ID_KEY, tribeId)
            return bundle
        }


    }

    private val invitePersonAdd = "添加"
    private val personList = ArrayList<String>()
    private var tribeId: Long = DEFAULT_ID
    override fun afterSetContentView(savedInstanceState: Bundle?) {
        tribeId = intent.extras?.getLong(TRIBE_ID_KEY) ?: DEFAULT_ID
        if (tribeId == DEFAULT_ID) {
            XToast.toastShort(this, "参数异常无法查询群信息")
            finish()
        }
        setupToolBar(getString(R.string.activity_im_tribe_update_label), true)
        personList.add(0, invitePersonAdd)

        recycler_im_tribe_members.layoutManager = GridLayoutManager(this, 5)
        recycler_im_tribe_members.adapter = personAdapter
        personAdapter.setOnItemClickListener { _, position ->
            if (position == 0) {
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
                        val addList = ArrayList<String>()
                        for (rId in users) {
                            var isOld = false
                            personList.map {
                                if (rId.distinguishedName == it) {
                                    isOld = true
                                    return@map
                                }
                            }
                            if (!isOld){
                                addList.add(rId.distinguishedName)
                            }
                        }
                        //add
                        addNewMembers(addList)
                    }
                }
            }
        }
        loadTribeInfo()

        rl_im_tribe_name_btn.setOnClickListener(this)
        rl_im_tribe_notice_btn.setOnClickListener(this)
        btn_im_tribe_out.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.rl_im_tribe_name_btn -> {
                updateTribeName()
            }
            R.id.rl_im_tribe_notice_btn -> {
                updateTribeNotice()
            }
            R.id.btn_im_tribe_out -> {
                exitFromTribe()
            }
        }
    }


    private fun updateTribeName() {
        val dialog = O2DialogSupport.openCustomViewDialog(this, getString(R.string.activity_im_tribe_name),
                R.layout.dialog_name_modify) { dialog->
            val text = dialog.findViewById<EditText>(R.id.dialog_name_editText_id)
            val content = text.text.toString()
            if (TextUtils.isEmpty(content)) {
                XToast.toastShort(this@IMTribeInfoActivity, "群名称不能为空！")
            }else{
//                O2App.instance.mIMKit?.tribeService?.modifyTribeInfo(object : IWxCallback{
//                    override fun onSuccess(vararg p0: Any?) {
//                    }
//
//                    override fun onProgress(p0: Int) {
//                    }
//
//                    override fun onError(p0: Int, p1: String?) {
//                        XToast.toastShort(this@IMTribeInfoActivity, "群名称修改异常， code:$p0, 错误： $p1")
//                    }
//                }, tribeId, content, "")
                tv_im_tribe_name.text = content
            }
            dialog.dismiss()
        }
        val nameTV = dialog.findViewById<EditText>(R.id.dialog_name_editText_id)
        nameTV.setText(tv_im_tribe_name.text.toString())
    }
    private  fun updateTribeNotice() {
        val dialog = O2DialogSupport.openCustomViewDialog(this, getString(R.string.activity_im_tribe_notice_label),
                R.layout.dialog_name_modify) { dialog->
            val text = dialog.findViewById<EditText>(R.id.dialog_name_editText_id)
            val content = text.text.toString()
//            O2App.instance.mIMKit?.tribeService?.modifyTribeInfo(object : IWxCallback{
//                override fun onSuccess(vararg p0: Any?) {
//                }
//
//                override fun onProgress(p0: Int) {
//                }
//
//                override fun onError(p0: Int, p1: String?) {
//                    XToast.toastShort(this@IMTribeInfoActivity, "群公告修改异常， code:$p0, 错误： $p1")
//                }
//            }, tribeId, "", content)
            tv_im_tribe_notice.text = content
            dialog.dismiss()
        }
        val nameTV = dialog.findViewById<EditText>(R.id.dialog_name_editText_id)
        nameTV.setText(tv_im_tribe_notice.text.toString())
    }




    private fun addNewMembers(addList: ArrayList<String>) {
        if (addList.isEmpty()) {
            return
        }
//        val users = ArrayList<IYWContact>()
//        addList.forEach {
//            val user = YWContactFactory.createAPPContact(it, O2App.instance.OPENIM_APP_KEY)
//            users.add(user)
//        }
//        O2App.instance.mIMKit?.tribeService?.inviteMembers(tribeId, users, object : IWxCallback {
//            override fun onSuccess(vararg p0: Any?) {
//                try {
//                    this@IMTribeInfoActivity.runOnUiThread{
//                        personList.addAll(addList)
//                        personAdapter.notifyDataSetChanged()
//                    }
//                } catch (e: Exception) {
//                }
//            }
//
//            override fun onProgress(p0: Int) {
//            }
//
//            override fun onError(p0: Int, p1: String?) {
//                XToast.toastShort(this@IMTribeInfoActivity, "添加成员失败，code:$p0, 错误：$p1")
//            }
//        })
    }

    private fun exitFromTribe() {
        O2DialogSupport.openConfirmDialog(this,"您确定要退出当前群聊？", {
//            O2App.instance.mIMKit?.tribeService?.exitFromTribe(object : IWxCallback {
//                override fun onSuccess(vararg p0: Any?) {
//                    finish()
//                }
//
//                override fun onProgress(p0: Int) {
//                }
//
//                override fun onError(p0: Int, p1: String?) {
//                    XToast.toastShort(this@IMTribeInfoActivity, "提交申请异常，code:$p0, 错误：$p1")
//                }
//            }, tribeId)
        })

    }

    private fun loadTribeInfo() {
//        O2App.instance.mIMKit?.tribeService?.getTribeFromServer(object : IWxCallback {
//            override fun onSuccess(vararg p0: Any?) {
//                try {
//                    this@IMTribeInfoActivity.runOnUiThread {
//                        val tribe = p0[0] as YWTribe
//                        if (tribe != null) {
//                            tv_im_tribe_name.text = tribe.tribeName?:""
//                            tv_im_tribe_notice.text = tribe.tribeNotice?:""
//                        }
//                    }
//                } catch (e: Exception) {
//                    XLog.error("", e)
//                }
//            }
//
//            override fun onProgress(p0: Int) {
//            }
//
//            override fun onError(p0: Int, p1: String?) {
//                XLog.error("获取群信息异常，code: $p0, desc: $p1")
//            }
//        }, tribeId)
//        O2App.instance.mIMKit?.tribeService?.getMembersFromServer(object : IWxCallback {
//            override fun onSuccess(vararg p0: Any?) {
//                try {
//                    this@IMTribeInfoActivity.runOnUiThread {
//                        val members = p0[0] as ArrayList<YWTribeMember>
//                        members.map {
//                            if (!TextUtils.isEmpty(it.userId)){
//                                personList.add(it.userId)
//                            }
//                        }
//                        personAdapter.notifyDataSetChanged()
//                    }
//
//                } catch (e: Exception) {
//                    XLog.error("", e)
//                }
//            }
//
//            override fun onProgress(p0: Int) {
//            }
//
//            override fun onError(p0: Int, p1: String?) {
//                XLog.error("获取群成员异常，code: $p0, desc: $p1")
//            }
//        }, tribeId)
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
                delete?.gone()
                avatar?.setImageResource(R.mipmap.contact_icon_avatar)
                if (avatar != null) {
                    if (invitePersonAdd == t) {
                        avatar.setImageResource(R.mipmap.icon_add_people)
                    } else {
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
