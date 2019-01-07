package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.main

import android.text.TextUtils
import android.view.Gravity
import android.view.View
import kotlinx.android.synthetic.main.fragment_main_my.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2App
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPViewPagerFragment
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.enums.GenderTypeEnums
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.person.PersonJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.ZoneUtil
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.imageloader.O2ImageLoaderManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.imageloader.O2ImageLoaderOptions
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.CommonMenuPopupWindow
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File

/**
 * Created by fancylou on 10/9/17.
 */

class MyFragment: BaseMVPViewPagerFragment<MyContract.View, MyContract.Presenter>(), MyContract.View, View.OnClickListener {


    override var mPresenter: MyContract.Presenter = MyPresenter()
    override fun layoutResId(): Int = R.layout.fragment_main_my

    val avatarMenuList: ArrayList<String> = arrayListOf("拍照", "从手机相册选择")
    val avatarMenu: CommonMenuPopupWindow by lazy { CommonMenuPopupWindow(avatarMenuList, activity) }

    var person: PersonJson? = null
    var isEdit = false



    override fun lazyLoad() {
        showLoadingDialog()
        mPresenter.loadMyInfo()
        showAvatar()
    }

    override fun initUI() {
        image_myInfo_avatar.setOnClickListener(this)
        tv_myInfo_edit_save_btn.setOnClickListener(this)
        linear_myInfo_gender_men_button.setOnClickListener(this)
        linear_myInfo_gender_women_button.setOnClickListener(this)
        avatarMenu.setOnDismissListener { ZoneUtil.lightOn(activity) }
        avatarMenu.onMenuItemClickListener = object : CommonMenuPopupWindow.OnMenuItemClickListener {
            override fun itemClick(position: Int) {
                when (position) {
                    0 -> (activity as MainActivity).takeFromCamera()
                    1 -> (activity as MainActivity).takeFromPictures()
                }
                avatarMenu.dismiss()
            }
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.image_myInfo_avatar -> modifyAvatarMenuShow()
            R.id.tv_myInfo_edit_save_btn -> switchTheStatusAndUpdateTheData()
            R.id.linear_myInfo_gender_men_button -> {
                if (isEdit) {
                    image_myInfo_gender_men_edit.visibility = View.VISIBLE
                    image_myInfo_gender_women_edit.visibility = View.GONE
                }
            }
            R.id.linear_myInfo_gender_women_button -> {
                if (isEdit) {
                    image_myInfo_gender_men_edit.visibility = View.GONE
                    image_myInfo_gender_women_edit.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun loadMyInfoSuccess(personal: PersonJson) {
        hideLoadingDialog()
        person = personal
        switchStatus(false)
        tv_myInfo_name.text = personal.name
        tv_myInfo_name_edit.text = personal.name
        edit_myInfo_name.setText(personal.name)
        tv_myInfo_sign_edit.text = personal.signature
        tv_myInfo_sign.text = personal.signature
        edit_myInfo_sign.setText(personal.signature)
        myInfo_mobile_value_id.text = personal.mobile
        edit_myInfo_mobile.setText(personal.mobile)
        myInfo_email_value_id.text = personal.mail
        edit_myInfo_email.setText(personal.mail)
        myInfo_qq_value_id.text = personal.qq
        edit_myInfo_qq.setText(personal.qq)
        if (GenderTypeEnums.MALE.key == personal.genderType) {
            image_myInfo_gender_men.setImageResource(R.mipmap.icon_gender_men_enable_50dp)
            image_myInfo_gender_women.setImageResource(R.mipmap.icon_gender_women_disable_50dp)
        } else {
            image_myInfo_gender_men.setImageResource(R.mipmap.icon_gender_men_disable_50dp)
            image_myInfo_gender_women.setImageResource(R.mipmap.icon_gender_women_enable_50dp)
        }
        scroll_myInfo.scrollTo(0, 0)
    }

    override fun loadMyInfoFail() {
        hideLoadingDialog()
        XToast.toastShort(activity, "获取个人信息失败！")
    }

    override fun updateMyInfoFail() {
        hideLoadingDialog()
        XToast.toastShort(activity, "更新个人信息失败！")
        switchStatus(false)
    }

    override fun updateMyIcon(f: Boolean) {
        hideLoadingDialog()
        if (f) {
            doAsync {
                //休眠1秒
                XLog.debug("sleep 1 second")
                Thread.sleep(1000L)
                uiThread {
                    showAvatar()
                }
            }
        }
    }



    ///activity中使用
    fun clickBackPress():Boolean {
        if (isEdit) {
            switchStatus(false)
            return true
        }else{
            return false
        }
    }
    //activity中使用
    fun modifyAvatar2Remote(filePath: String?) {
        try {
            XLog.debug("in modifyAvatar2Remote...... $filePath")
            showLoadingDialog()
            mPresenter.updateMyIcon(File(filePath))
        } catch (e: Exception) {
            XLog.error("更新头像失败", e)
            hideLoadingDialog()
        }
    }



    private fun switchStatus(flag: Boolean) {
        if (flag) {
            tv_myInfo_sign_edit.visibility = View.GONE
            edit_myInfo_sign.visibility = View.VISIBLE
            tv_myInfo_name_edit.visibility = View.GONE
            edit_myInfo_name.visibility = View.VISIBLE
            myInfo_mobile_value_id.visibility = View.GONE
            edit_myInfo_mobile.visibility = View.VISIBLE
            myInfo_email_value_id.visibility = View.GONE
            edit_myInfo_email.visibility = View.VISIBLE
            myInfo_qq_value_id.visibility = View.GONE
            edit_myInfo_qq.visibility = View.VISIBLE
            if (GenderTypeEnums.MALE.key.equals(person?.genderType)) {
                image_myInfo_gender_men_edit.visibility = View.VISIBLE
                image_myInfo_gender_women_edit.visibility = View.GONE
            } else {
                image_myInfo_gender_men_edit.visibility = View.GONE
                image_myInfo_gender_women_edit.visibility = View.VISIBLE
            }
            image_myInfo_gender_men.setImageResource(R.mipmap.icon_gender_men_enable_50dp)
            image_myInfo_gender_women.setImageResource(R.mipmap.icon_gender_women_enable_50dp)
            image_myInfo_edit_avatar.visibility = View.VISIBLE
        } else {
            tv_myInfo_sign_edit.visibility = View.VISIBLE
            edit_myInfo_sign.visibility = View.GONE
            tv_myInfo_name_edit.visibility = View.VISIBLE
            edit_myInfo_name.visibility = View.GONE
            myInfo_mobile_value_id.visibility = View.VISIBLE
            edit_myInfo_mobile.visibility = View.GONE
            myInfo_email_value_id.visibility = View.VISIBLE
            edit_myInfo_email.visibility = View.GONE
            myInfo_qq_value_id.visibility = View.VISIBLE
            edit_myInfo_qq.visibility = View.GONE
            image_myInfo_gender_men_edit.visibility = View.GONE
            image_myInfo_gender_women_edit.visibility = View.GONE
            image_myInfo_edit_avatar.visibility = View.GONE
        }
        isEdit = flag
        refreshMenu()
    }
    private fun refreshMenu() {
        if (isEdit) {
            tv_myInfo_edit_save_btn.text = getString(R.string.menu_save)
        } else {
            tv_myInfo_edit_save_btn.text = getString(R.string.menu_edit)
        }
    }
    private fun modifyAvatarMenuShow() {
        avatarMenu.showAtLocation(image_myInfo_avatar, Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL, 0, 0)
        ZoneUtil.lightOff(activity)
    }

    private fun switchTheStatusAndUpdateTheData() {
        if (isEdit) {
            val name = edit_myInfo_name.text.toString()
            if (TextUtils.isEmpty(name)) {
                XToast.toastShort(activity, "姓名不能为空")
                return
            }
            if (person != null) {
                person?.name = name
                person?.signature = edit_myInfo_sign.text.toString()
                person?.mobile = edit_myInfo_mobile.text.toString()
                person?.mail = edit_myInfo_email.text.toString()
                person?.qq = edit_myInfo_qq.text.toString()
                if (View.VISIBLE == image_myInfo_gender_men_edit.visibility) {
                    person?.genderType = GenderTypeEnums.MALE.key
                } else {
                    person?.genderType = GenderTypeEnums.FEMALE.key
                }
                showLoadingDialog()
                mPresenter.updateMyInfo(person!!)
            } else {
                XLog.error("my info  is null.......")
                XToast.toastShort(activity, "我的信息对象为空无法修改！")
            }

        } else {
            switchStatus(true)
        }

    }

    private fun showAvatar() {
        //头像
        var url = APIAddressHelper.instance().getPersonAvatarUrlWithId(O2SDKManager.instance().distinguishedName)
        O2ImageLoaderManager.instance().showImage(image_myInfo_avatar, url, O2ImageLoaderOptions(isSkipCache = true))
    }
}