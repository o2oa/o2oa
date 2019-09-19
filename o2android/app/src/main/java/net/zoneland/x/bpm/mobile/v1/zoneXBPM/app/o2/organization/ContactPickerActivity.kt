package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.organization

import android.app.Activity
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.snippet_appbarlayout_tablayout_toolbar.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.main.person.PersonJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.replaceFragmentSafely
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible

class ContactPickerActivity : BaseMVPActivity<ContactPickerActivityContract.View, ContactPickerActivityContract.Presenter>(), ContactPickerActivityContract.View {

    override var mPresenter: ContactPickerActivityContract.Presenter = ContactPickerActivityPresenter()


    override fun layoutResId(): Int = R.layout.activity_contact_picker

    companion object {
        const val CONTACT_PICKED_RESULT = "CONTACT_PICKED_RESULT"
        val picker_mode_list = arrayOf("departmentPicker", "identityPicker", "groupPicker", "personPicker")
        const val PICKER_MODE_KEY = "PICKER_MODE_KEY"
        const val TOP_LIST_KEY = "TOP_LIST_KEY"
        const val ORG_TYPE_KEY = "ORG_TYPE_KEY"
        const val MULIPLE_KEY = "multiple_KEY"
        const val MAX_NUMBER_KEY = "MAX_NUMBER_KEY"
        const val DUTY_KEY = "DUTY_KEY"
        const val PICKED_DEPT_ARRAY_KEY = "PICKED_DEPT_ARRAY_KEY"
        const val PICKED_ID_ARRAY_KEY = "PICKED_ID_ARRAY_KEY"
        const val PICKED_GROUP_ARRAY_KEY = "PICKED_GROUP_ARRAY_KEY"
        const val PICKED_USER_ARRAY_KEY = "PICKED_USER_ARRAY_KEY"

        fun startPickerBundle(
                pickerModes: ArrayList<String>,
                topUnitList: ArrayList<String> = arrayListOf(),
                unitType: String = "",
                maxNumber: Int = 0,
                multiple: Boolean = true,
                dutyList: ArrayList<String> = arrayListOf(),
                initDeptList: ArrayList<String> = arrayListOf(),
                initIdList: ArrayList<String> = arrayListOf(),
                initGroupList: ArrayList<String> = arrayListOf(),
                initUserList: ArrayList<String> = arrayListOf()
        ): Bundle {
            val bundle = Bundle()
            bundle.putStringArrayList(PICKER_MODE_KEY, pickerModes)
            bundle.putStringArrayList(TOP_LIST_KEY, topUnitList)
            bundle.putString(ORG_TYPE_KEY, unitType)
            bundle.putInt(MAX_NUMBER_KEY, maxNumber)
            bundle.putBoolean(MULIPLE_KEY, multiple)
            bundle.putStringArrayList(DUTY_KEY, dutyList)
            bundle.putStringArrayList(PICKED_DEPT_ARRAY_KEY, initDeptList)
            bundle.putStringArrayList(PICKED_ID_ARRAY_KEY, initIdList)
            bundle.putStringArrayList(PICKED_GROUP_ARRAY_KEY, initGroupList)
            bundle.putStringArrayList(PICKED_USER_ARRAY_KEY, initUserList)
            return bundle
        }
    }
    private var pickerModes: ArrayList<String> = arrayListOf()
    private var multiple = true//是否多选
    private var maxNumber = 0//当multiple为true的时候，最多可选择的数量
    private var topList: ArrayList<String> = ArrayList()//可选的顶级组织列表
    private var orgType = ""//可选择的组织类别
    private var duty: ArrayList<String> = ArrayList()//人员职责


    private val mSelectDepartments: ArrayList<O2UnitPickerResultItem> = arrayListOf()
    private val mSelectIdentities: ArrayList<O2IdentityPickerResultItem> = arrayListOf()
    private val mSelectGroups: ArrayList<O2GroupPickerResultItem> = arrayListOf()
    private val mSelectUsers: ArrayList<O2PersonPickerResultItem> = arrayListOf()

    private val fragments = ArrayList<Fragment>()
    private var currentSelect = 0
    private var pickerTitle = "选择器"


    override fun afterSetContentView(savedInstanceState: Bundle?) {
        pickerModes = intent.extras?.getStringArrayList(PICKER_MODE_KEY) ?: arrayListOf()
        if (pickerModes.isEmpty()) {
            pickerModes.addAll( picker_mode_list.toList() )
        }
        //初始化传入参数
        //是否多选
        multiple = intent.extras?.getBoolean(MULIPLE_KEY) ?: true
        //组织类型
        orgType = intent.extras?.getString(ORG_TYPE_KEY) ?: ""
        //最多可选
        maxNumber = intent.extras?.getInt(MAX_NUMBER_KEY) ?: 0
        if (!multiple) {
            maxNumber = 1
        }
        //人员职责
        duty = intent.extras?.getStringArrayList(DUTY_KEY) ?: ArrayList()
        //顶层组织
        topList = intent.extras?.getStringArrayList(TOP_LIST_KEY) ?: ArrayList()
        val initDeptList: ArrayList<String> = intent.extras?.getStringArrayList(PICKED_DEPT_ARRAY_KEY) ?: ArrayList()
        initDeptList.forEach {
            val name = if (it.contains("@")) {
                it.split("@")[0]
            }else {
                it
            }
            val unit = O2UnitPickerResultItem(name = name, distinguishedName = it)
            mSelectDepartments.add(unit)
        }
        val initIdList: ArrayList<String> = intent.extras?.getStringArrayList(PICKED_ID_ARRAY_KEY) ?: ArrayList()
        initIdList.forEach {
            val name = if (it.contains("@")) {
                it.split("@")[0]
            }else {
                it
            }
            val identity = O2IdentityPickerResultItem(name = name, distinguishedName = it)
            mSelectIdentities.add(identity)
        }
        val initGroupList: ArrayList<String> = intent.extras?.getStringArrayList(PICKED_GROUP_ARRAY_KEY) ?: ArrayList()
        initGroupList.forEach {
            val name = if (it.contains("@")) {
                it.split("@")[0]
            }else {
                it
            }
            val group = O2GroupPickerResultItem(name = name, distinguishedName = it)
            mSelectGroups.add(group)
        }
        val initUserList: ArrayList<String> = intent.extras?.getStringArrayList(PICKED_USER_ARRAY_KEY) ?: ArrayList()
        initUserList.forEach {
            val name = if (it.contains("@")) {
                it.split("@")[0]
            }else {
                it
            }
            val user = O2PersonPickerResultItem(name = name, distinguishedName = it)
            mSelectUsers.add(user)
        }

        initView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_organization_check, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val count = mSelectDepartments.size + mSelectIdentities.size + mSelectGroups.size + mSelectUsers.size
        if (maxNumber > 0) {
            menu?.findItem(R.id.org_menu_choose)?.title = getString(R.string.menu_choose)+ "($count / $maxNumber)"
        }else {
            menu?.findItem(R.id.org_menu_choose)?.title = getString(R.string.menu_choose)+ "($count)"
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.org_menu_choose -> {
                val count = mSelectDepartments.size + mSelectIdentities.size + mSelectGroups.size + mSelectUsers.size
                XLog.debug("选择了$count")
                if (count < 1) {
                    XToast.toastShort(this, "请至少选择一条数据！")
                    return true
                }
                val result = ContactPickerResult(mSelectDepartments, mSelectIdentities, mSelectGroups, mSelectUsers)
                intent.putExtra(CONTACT_PICKED_RESULT, result)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            var isOperate = true
            if (currentSelect == 0) {
                isOperate = (fragments[0] as ContactUnitAndIdentityPicker).clickBackBtn()
            }
            return if (!isOperate) {
                finish()
                true
            }else {
                true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun setPersonInfo(info: PersonJson, type: String) {
        if (type == "0") { //identity
            mSelectIdentities.filter {
                it.person == info.distinguishedName
            }.forEach {
                it.person = info.id
                it.personDn = info.distinguishedName
                it.personName = info.name
                it.personUnique = info.unique
            }
        } else {
            mSelectUsers.filter {
                it.distinguishedName == info.distinguishedName
            }.forEach {
                it.id = info.id
                it.name = info.name
                it.unique = info.unique
                it.distinguishedName = info.distinguishedName
                it.employee = info.employee
                it.genderType = info.genderType
                it.mail = info.mail
                it.mobile = info.mobile
                it.officePhone = ""
                it.qq = info.qq
                it.weixin = info.weixin
            }
        }
    }



    // 检查值是否已经包含在选中的列表中
    fun isSelectedValue(value: NewContactListVO) : Boolean {
        return when (value) {
            is NewContactListVO.Department -> mSelectDepartments.any {
                it.distinguishedName == value.distinguishedName
            }
            is NewContactListVO.Identity -> mSelectIdentities.any {
                it.distinguishedName == value.distinguishedName
            }
            is NewContactListVO.Group -> mSelectGroups.any {
                it.distinguishedName == value.distinguishedName
            }
            is NewContactListVO.Person -> mSelectUsers.any {
                it.distinguishedName == value.distinguishedName
            }
            else -> false
        }
    }
    // 删除一个选中的值
    fun removeSelectedValue(value: NewContactListVO) {
        when(value) {
            is NewContactListVO.Department -> {
                val item = mSelectDepartments.firstOrNull { it.distinguishedName == value.distinguishedName }
                if (item != null) {
                    mSelectDepartments.remove(item)
                }
            }
            is NewContactListVO.Identity -> {
                val item = mSelectIdentities.firstOrNull { it.distinguishedName == value.distinguishedName }
                if (item != null) {
                    mSelectIdentities.remove(item)
                }
            }
            is NewContactListVO.Group -> {
                val item = mSelectGroups.firstOrNull { it.distinguishedName == value.distinguishedName }
                if (item != null) {
                    mSelectGroups.remove(item)
                }
            }
            is NewContactListVO.Person -> {
                val item = mSelectUsers.firstOrNull { it.distinguishedName == value.distinguishedName }
                if (item != null) {
                    mSelectUsers.remove(item)
                }
            }
        }
        refreshMenu()
    }
    // 添加一个选中的值
    fun addSelectedValue(value: NewContactListVO) {
        val count = mSelectDepartments.size + mSelectIdentities.size + mSelectGroups.size + mSelectUsers.size
        if (maxNumber in 1..count) {
           XToast.toastShort(this, "不能添加更多了！")
            return
        }
        when(value) {
            is NewContactListVO.Department -> {
                val o2Unit = O2UnitPickerResultItem(value.id, value.name, value.unique,
                        value.distinguishedName, value.typeList, value.shortName,
                        value.level, value.levelName)
                mSelectDepartments.add(o2Unit)
            }
            is NewContactListVO.Identity -> {
                val o2Identity = O2IdentityPickerResultItem(value.id, value.name, value.unique,
                        value.distinguishedName, value.person, value.unit, value.unitName, "", "", "",
                        value.unitLevel, value.unitLevelName)
                mSelectIdentities.add(o2Identity)
                //todo 查询person信息填充进去
                mPresenter.getPerson(value.person, "0")
            }
            is NewContactListVO.Group -> {
                val o2group = O2GroupPickerResultItem(value.id, value.name, value.unique, value.distinguishedName)
                mSelectGroups.add(o2group)
            }
            is NewContactListVO.Person -> {
                val o2person = O2PersonPickerResultItem(name = value.name, distinguishedName = value.distinguishedName)
                mSelectUsers.add(o2person)
                //todo 查询person信息填充进去
                mPresenter.getPerson(value.distinguishedName, "1")
            }
        }
        refreshMenu()
    }


    private fun refreshMenu() {
        invalidateOptionsMenu()
    }


    private fun addFragment(fragment: Fragment){
        replaceFragmentSafely(fragment, fragment.javaClass.simpleName, R.id.frame_contact_picker_main, allowState = true)
    }

    private fun createFragment(mode: String, index: Int, isShowTab:Boolean) {
        when(mode) {
            "departmentPicker" -> {
                if (index == 0) {pickerTitle = "组织选择"}
                val f = ContactUnitAndIdentityPicker.startPicker(
                        ContactUnitAndIdentityPicker.ORG_PICK_MODE,
                        topList = topList,
                        orgType = orgType,
                        maxNumber = maxNumber,
                        multiple = multiple,
                        duty = duty
                )
                fragments.add(f)
                if (isShowTab) {toolbar_snippet_tab_layout.addTab(toolbar_snippet_tab_layout.newTab().setText("组织选择"))}
            }
            "identityPicker" -> {
                if (index == 0) {pickerTitle = "身份选择"}
                val f = ContactUnitAndIdentityPicker.startPicker(
                        ContactUnitAndIdentityPicker.IDENTITY_PICK_MODE,
                        topList = topList,
                        orgType = orgType,
                        maxNumber = maxNumber,
                        multiple = multiple,
                        duty = duty
                )
                fragments.add(f)
                if (isShowTab) {toolbar_snippet_tab_layout.addTab(toolbar_snippet_tab_layout.newTab().setText("身份选择"))}
            }
            "groupPicker" -> {
                if (index == 0) {pickerTitle = "群组选择"}
                val f = ContactPersonGroupPicker.startPicker(
                        ContactPersonGroupPicker.GROUP_PICK_MODE,
                        multiple,
                        maxNumber
                )
                fragments.add(f)
                if (isShowTab) {toolbar_snippet_tab_layout.addTab(toolbar_snippet_tab_layout.newTab().setText("群组选择"))}
            }
            "personPicker" -> {
                if (index == 0) {pickerTitle = "人员选择"}
                val f =  ContactUnitAndIdentityPicker.startPicker(
                        ContactUnitAndIdentityPicker.PERSON_PICK_MODE,
                        topList = topList,
                        maxNumber = maxNumber,
                        multiple = multiple
                )
                fragments.add(f)
                if (isShowTab) {toolbar_snippet_tab_layout.addTab(toolbar_snippet_tab_layout.newTab().setText("人员选择"))}
            }
        }
    }

    private fun initView() {
        if (pickerModes.size == 1) {
            createFragment(pickerModes[0], 0, false)
            toolbar_snippet_tab_layout.gone()
        } else {
            pickerModes.forEachIndexed { index, mode ->
                createFragment(mode, index, true)
            }
            toolbar_snippet_tab_layout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(tab: TabLayout.Tab?) {
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabSelected(tab: TabLayout.Tab?) {
                    XLog.debug("selected..................")
                    val name = tab?.text.toString()
                    val p = tab?.position ?: 0
                    currentSelect = p
                    addFragment(fragments[p])
                    updateToolbarTitle(name)
                }
            })
            toolbar_snippet_tab_layout.tabMode = TabLayout.MODE_FIXED
            toolbar_snippet_tab_layout.visible()
        }
        setupToolBar(pickerTitle, true, isCloseBackIcon = true)
        if (fragments.isEmpty()) {
            XToast.toastShort(this, "传入的选择器类型不正确！")
            finish()
        }else {
            addFragment(fragments[0])
        }
    }


}
