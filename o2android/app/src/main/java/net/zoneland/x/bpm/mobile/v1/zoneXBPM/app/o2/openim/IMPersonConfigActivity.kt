package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.openim

import android.os.Bundle
import android.text.TextUtils
import kotlinx.android.synthetic.main.activity_im_person_config.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.organization.ContactPickerActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.person.PersonActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.person.PersonJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.goThenKill
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.imageloader.O2ImageLoaderManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.imageloader.O2ImageLoaderOptions
import java.util.*

class IMPersonConfigActivity : BaseMVPActivity<IMPersonConfigContract.View, IMPersonConfigContract.Presenter>(), IMPersonConfigContract.View {
    override var mPresenter: IMPersonConfigContract.Presenter = IMPersonConfigActivityPresenter()

    override fun layoutResId(): Int  = R.layout.activity_im_person_config

    companion object {
        val PERSON_ID_KEY = "PERSON_ID_KEY"
        fun start(personId: String): Bundle {
            val bundle = Bundle()
            bundle.putString(PERSON_ID_KEY, personId)
            return bundle
        }
    }

    var personId: String = ""
    var distinguishedName: String = ""
    override fun afterSetContentView(savedInstanceState: Bundle?) {
        personId = intent.extras?.getString(PERSON_ID_KEY, "") ?: ""
        if (TextUtils.isEmpty(personId)){
            XToast.toastShort(this, "传入参数为空！")
            finish()
        }
        setupToolBar(getString(R.string.activity_im_person_config_label), true)

        val avatarUrl = APIAddressHelper.instance().getPersonAvatarUrlWithId(personId)
        O2ImageLoaderManager.instance().showImage(img_person_icon, avatarUrl, O2ImageLoaderOptions(placeHolder = R.mipmap.icon_avatar_men))


        rl_im_person_arrow_btn.setOnClickListener {
            goThenKill<PersonActivity>(PersonActivity.startBundleData(distinguishedName))
        }
        rl_im_person_tribe_create_btn.setOnClickListener {
            val personList = arrayListOf(O2SDKManager.instance().distinguishedName, personId)
            val bundle = ContactPickerActivity.startPickerBundle(
                    arrayListOf("personPicker"),
                    multiple = true,
                    initUserList = personList
            )
            contactPicker(bundle) { result ->
                if (result != null) {
                    val list = ArrayList<String>()
                    val users = result.users
                    users.map { list.add(it.distinguishedName) }
                    goThenKill<IMTribeCreateActivity>(IMTribeCreateActivity.startCreate(list))
                }
            }

        }
        showLoadingDialog()
        mPresenter.loadPersonInfo(personId)
    }

    override fun loadPersonInfo(personInfo: PersonJson) {
        tv_person_name.text = personInfo.name
        distinguishedName = personInfo.distinguishedName
        hideLoadingDialog()
    }

    override fun loadPersonInfoFail() {
        hideLoadingDialog()
        XToast.toastShort(this, "查询用户信息失败")
        finish()
    }
}
