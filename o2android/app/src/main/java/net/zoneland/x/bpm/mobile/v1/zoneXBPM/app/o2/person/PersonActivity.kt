package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.person


import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import cn.jpush.im.android.api.JMessageClient
import cn.jpush.im.android.api.callback.GetUserInfoCallback
import cn.jpush.im.android.api.model.UserInfo
import jiguang.chat.activity.ChatActivity
import kotlinx.android.synthetic.main.activity_person_info.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2App
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.enums.GenderTypeEnums
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.im.JIMConstant
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.person.PersonJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.AndroidUtils
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.ZoneUtil
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.makeCallDial
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.imageloader.O2ImageLoaderManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.imageloader.O2ImageLoaderOptions
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.CommonMenuPopupWindow
import org.jetbrains.anko.email
import org.jetbrains.anko.makeCall
import org.jetbrains.anko.sendSMS


class PersonActivity : BaseMVPActivity<PersonContract.View, PersonContract.Presenter>(), PersonContract.View, View.OnClickListener {
    override var mPresenter: PersonContract.Presenter = PersonPresenter()
    override fun layoutResId(): Int = R.layout.activity_person_info

    companion object {
        val PERSON_NAME_KEY = "name"

        fun startBundleData(person: String): Bundle {
            val bundle = Bundle()
            bundle.putString(PERSON_NAME_KEY, person)
            return bundle
        }
    }

    var loadedPersonId = ""//用户的id字段
    var personId = ""
    var genderName = ""
    var hasCollection = false
    val mobileMenuItemList: ArrayList<String> = arrayListOf("拨打电话", "发送短信", "复制")
    val mobileClickMenu: CommonMenuPopupWindow by lazy { CommonMenuPopupWindow(mobileMenuItemList, this) }
    val emailMenuItemList: ArrayList<String> = arrayListOf("发送邮件","复制")
    val emailClickMenu: CommonMenuPopupWindow by lazy { CommonMenuPopupWindow(emailMenuItemList, this) }
    private var canTalkTo = false

    override fun afterSetContentView(savedInstanceState: Bundle?) {
        personId = intent.extras?.getString(PERSON_NAME_KEY, "")?:""
        if (TextUtils.isEmpty(personId)) {
            XToast.toastShort(this, "没有传入人员帐号，无法获取人员信息！")
            finish()
            return
        }

        linear_person_mobile_button.setOnClickListener(this)
        linear_person_email_button.setOnClickListener(this)
        linear_person_collection_button.setOnClickListener(this)
        image_person_back.setOnClickListener(this)
        btn_begin_talk.setOnClickListener(this)

        showLoadingDialog()
        mPresenter.loadPersonInfo(personId)
        mPresenter.isUsuallyPerson(O2SDKManager.instance().distinguishedName, personId)
    }


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.linear_person_mobile_button -> callPhone()
            R.id.linear_person_email_button -> sendEmail()
            R.id.linear_person_collection_button -> usuallyBtnClick()
            R.id.image_person_back -> finish()
            R.id.btn_begin_talk -> {
                // 开始聊天
                if (O2App.instance._JMIsLogin()) {
                    if (canTalkTo && O2SDKManager.instance().cId != loadedPersonId) {
                        val intent = Intent(this, ChatActivity::class.java)
                        val user = O2App.instance._JMMyUserInfo()
                        val name = tv_person_name.text.toString()
                        XLog.info("current user: ${user?.userName}, ${user?.nickname}, ${user?.appKey}")
                        XLog.info("to user: $loadedPersonId, $name")
                        intent.putExtra(JIMConstant.CONV_TITLE, name)
                        intent.putExtra(JIMConstant.TARGET_ID, loadedPersonId)
                        intent.putExtra(JIMConstant.TARGET_APP_KEY, O2App.instance.JM_IM_APP_KEY)
                        startActivity(intent)
                    }else {
                        XToast.toastShort(this, "无法发起聊天，该用户没有启用聊天功能！")
                    }

                }else {
                    XToast.toastShort(this, "无法聊天，没有连接到IM服务器！！")
                }
            }
        }
    }

    private fun usuallyBtnClick() {
        if (O2SDKManager.instance().distinguishedName == personId) {
            XLog.debug("自己收藏自己。。。。" + personId)
            return
        }
        if (hasCollection) {
            mPresenter.deleteUsuallyPerson(O2SDKManager.instance().distinguishedName, personId)
            image_person_collection.setImageResource(R.mipmap.icon_collection_disable_50dp)
            tv_person_collection_text.text = getString(R.string.person_collect)
            hasCollection = false
        } else {
            val mobile = tv_person_mobile.text.toString()
            val name = tv_person_name.text.toString()
            mPresenter.collectionUsuallyPerson(O2SDKManager.instance().distinguishedName, personId, O2SDKManager.instance().cName, name, genderName, mobile)
            image_person_collection.setImageResource(R.mipmap.icon_collection_enable_50dp)
            tv_person_collection_text.text = getString(R.string.person_collect_cancel)
            hasCollection = true
        }
    }

    private fun sendEmail() {
        val emailAddress = tv_person_email.text.toString()
        if (TextUtils.isEmpty(emailAddress)) {
            return
        }
        emailClickMenu.setOnDismissListener { ZoneUtil.lightOn(this@PersonActivity) }
        emailClickMenu.onMenuItemClickListener = object : CommonMenuPopupWindow.OnMenuItemClickListener {
            override fun itemClick(position: Int) {
                when(position){
                    0 -> email(emailAddress)
                    1 ->  {
                        AndroidUtils.copyTextToClipboard(emailAddress, this@PersonActivity)
                        XToast.toastShort(this@PersonActivity, "邮箱地址复制成功！")
                    }
                }
                emailClickMenu.dismiss()
            }
        }
        emailClickMenu.showAtLocation(main_content, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 0)
        ZoneUtil.lightOff(this)
    }

    private fun callPhone() {
        val phone = tv_person_mobile.text.toString()
        XLog.debug("拨打电话：$phone")
        if (TextUtils.isEmpty(phone)) {
            return
        }
        mobileClickMenu.setOnDismissListener { ZoneUtil.lightOn(this@PersonActivity) }
        mobileClickMenu.onMenuItemClickListener = object : CommonMenuPopupWindow.OnMenuItemClickListener {
            override fun itemClick(position: Int) {
                when (position) {
                    0 -> makeCallDial(phone)
                    1 -> sendSMS(phone)
                    2 -> {
                        AndroidUtils.copyTextToClipboard(phone, this@PersonActivity)
                        XToast.toastShort(this@PersonActivity, "手机号码复制成功！")
                    }
                }
                mobileClickMenu.dismiss()
            }
        }
        mobileClickMenu.showAtLocation(main_content, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 0)
        ZoneUtil.lightOff(this)
    }

    override fun isUsuallyPerson(flag: Boolean) {
        if (flag) {
            image_person_collection.setImageResource(R.mipmap.icon_collection_enable_50dp)
            tv_person_collection_text.text = getString(R.string.person_collect_cancel)
            hasCollection = true
        } else {
            image_person_collection.setImageResource(R.mipmap.icon_collection_disable_50dp)
            tv_person_collection_text.text = getString(R.string.person_collect)
            hasCollection = false
        }
    }

    override fun loadPersonInfo(personInfo: PersonJson) {
        hideLoadingDialog()
        loadedPersonId = personInfo.id
        tv_person_mobile.text = personInfo.mobile
        tv_person_email.text = personInfo.mail
        if (GenderTypeEnums.FEMALE.key == personInfo.genderType) {
            linear_person_gender_women_button.visible()
            linear_person_gender_men_button.gone()
        } else {
            linear_person_gender_women_button.gone()
            linear_person_gender_men_button.visible()
        }
        genderName = GenderTypeEnums.getNameByKey(personInfo.genderType)
        if (!TextUtils.isEmpty(personInfo.qq)) {
            tv_person_qq.text = "QQ ".plus(personInfo.qq)
        }
        tv_person_name.text = personInfo.name
        tv_person_name_2.text = personInfo.name
        if (personInfo.woIdentityList != null && !personInfo.woIdentityList.isEmpty()) {
            var department = ""
            personInfo.woIdentityList.mapIndexed { index, woIdentityListItem ->
                if (index != personInfo.woIdentityList.size - 1) {
                    department += woIdentityListItem.unitName + ","
                } else {
                    department += woIdentityListItem.unitName
                }
            }
            tv_person_department.text = department
        }
        tv_person_employee.text = personInfo.employee
        tv_person_distinguishedName.text = personInfo.distinguishedName
        val url = APIAddressHelper.instance().getPersonAvatarUrlWithId(personInfo.id)
        O2ImageLoaderManager.instance().showImage(image_person_avatar, url, O2ImageLoaderOptions(placeHolder = R.mipmap.icon_avatar_men))

        //IM 服务端获取用户信息
        JMessageClient.getUserInfo(loadedPersonId, object : GetUserInfoCallback(){
            override fun gotResult(responseCode: Int, responseMessage: String?, info: UserInfo?) {
                XLog.info("responseCode:$responseCode, responseMessage:$responseMessage ")
                canTalkTo = responseCode == 0
            }
        })
    }

    override fun loadPersonInfoFail() {
        hideLoadingDialog()
    }
}
