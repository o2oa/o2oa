package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.organization


import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.TypedValue
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_organization_new.*
import net.muliba.changeskin.FancySkinManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.openim.IMTribeCreateActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.person.PersonActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.NewContactListAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.ContactBreadcrumbBean
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.NewContactListVO
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.ZoneUtil
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.imageloader.O2ImageLoaderManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.imageloader.O2ImageLoaderOptions
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.CircleImageView


class NewOrganizationActivity : BaseMVPActivity<NewOrganizationContract.View, NewOrganizationContract.Presenter>(), NewOrganizationContract.View {
    override var mPresenter: NewOrganizationContract.Presenter = NewOrganizationPresenter()


    override fun layoutResId(): Int = R.layout.activity_organization_new


    companion object {
        val DEFAULT_MODE = 0
        val SINGLE_PERSON_CHOOSE_MODE = 1 //人员单选模式
        val MULTI_PERSON_CHOOSE_MODE = 2 //人员多选模式
        val SINGLE_IDENTITY_CHOOSE_MODE = 3 //身份单选模式
        val MULTI_IDENTITY_CHOOSE_MODE = 4 //身份多选模式

        val SINGLE_PERSON_CHOOSE_RESULT = "SINGLE_PERSON_CHOOSE_RESULT"
        val MULTI_PERSON_CHOOSE_RESULT = "MULTI_PERSON_CHOOSE_RESULT"
        val SINGLE_IDENTITY_CHOOSE_RESULT = "SINGLE_IDENTITY_CHOOSE_RESULT"
        val MULTI_IDENTITY_CHOOSE_RESULT = "MULTI_IDENTITY_CHOOSE_RESULT"


        val DEFAULT_STATUS = 0
        val SEARCH_STATUS = 1

        val NOT_IM_CHOOSE = -1
        val IM_CHOOSE_NEED_START_ACTIVITY = 0
        val IM_CHOOSE_FROM_REQUEST = 1

        val MODE_KEY = "MODE_KEY"
        val STATUS_KEY = "STATUS_KEY"
        val UNIT_PARENT_KEY = "UNIT_PARENT_KEY"
        val UNIT_PARENT_NAME_KEY = "UNIT_PARENT_NAME_KEY"
        val FROM_IM_CHOOSE_KEY = "FROM_IM_CHOOSE_KEY"
        val ALREADY_CHOOSE_PERSON_LIST_KEY = "ALREADY_CHOOSE_PERSON_LIST_KEY"

        val UNIT_TOP_PARENT_ID = "-1"

        fun startBundleData(parent: String = "", parentName: String = "", mode: Int = DEFAULT_MODE, status: Int = DEFAULT_STATUS): Bundle {
            val bundle = Bundle()
            bundle.putInt(FROM_IM_CHOOSE_KEY, NOT_IM_CHOOSE)
            bundle.putString(UNIT_PARENT_KEY, parent)
            bundle.putString(UNIT_PARENT_NAME_KEY, parentName)
            bundle.putInt(MODE_KEY, mode)
            bundle.putInt(STATUS_KEY, status)
            return bundle
        }

//        fun startBundleDataForIMChoose(choosePersonList: ArrayList<String>, chooseFromRequest: Int = IM_CHOOSE_NEED_START_ACTIVITY): Bundle {
//            val bundle = Bundle()
//            bundle.putInt(FROM_IM_CHOOSE_KEY, chooseFromRequest)
//            bundle.putString(UNIT_PARENT_KEY, "")
//            bundle.putString(UNIT_PARENT_NAME_KEY, "")
//            bundle.putInt(MODE_KEY, MULTI_PERSON_CHOOSE_MODE)
//            bundle.putInt(STATUS_KEY, DEFAULT_STATUS)
//            bundle.putStringArrayList(ALREADY_CHOOSE_PERSON_LIST_KEY, choosePersonList)
//            return bundle
//        }
    }


    val mSelectPersonSet = HashSet<String>() //选中的人员 MULTI_PERSON_CHOOSE_MODE 的时候使用
    val mSelectIdentitySet = HashSet<String>() //选中的身份 MULTI_IDENTITY_CHOOSE_MODE 的时候使用
    var mode = DEFAULT_MODE
    var status = DEFAULT_STATUS
    var unitParentId = ""
    var unitParentName = ""
    var fromImChoose = NOT_IM_CHOOSE
    //面包屑导航
    val breadcrumbBeans = ArrayList<ContactBreadcrumbBean>()
    var orgLevel = 0//默认进入的时候是第一层组织
    val mainItemList = ArrayList<NewContactListVO>()
    val mainListAdapter: NewContactListAdapter by lazy {
        object : NewContactListAdapter(mainItemList) {
            override fun bindDepartment(hold: CommonRecyclerViewHolder?, department: NewContactListVO.Department, position: Int) {
                val totalChild = department.identityCount + department.departmentCount
                hold?.setText(R.id.tv_item_contact_body_org_name, department.name)
                        ?.setText(R.id.tv_item_contact_body_org_size, "($totalChild)")
                        ?.setCircleTextView(R.id.image_item_contact_body_org_icon,
                                department.name.substring(0, 1), FancySkinManager.instance().getColor(getContext(), R.color.z_color_primary))
                val arrow = hold?.getView<ImageView>(R.id.image_item_contact_body_org_arrow)
                if (totalChild < 1) {
                    arrow?.inVisible()
                } else {
                    arrow?.visible()
                }
            }

            override fun clickDepartment(department: NewContactListVO.Department) {
                XLog.debug("click department ${department.name}")
                val totalChild = department.identityCount + department.departmentCount
                if (totalChild > 0) {
                    val newLevel = orgLevel + 1
                    val bean = ContactBreadcrumbBean(department.id, department.name, newLevel)
                    breadcrumbBeans.add(bean)
                    refreshOrganizationMain()
                }
            }

            override fun bindIdentity(hold: CommonRecyclerViewHolder?, identity: NewContactListVO.Identity, position: Int) {
                hold?.setText(R.id.tv_item_contact_person_body_name, identity.name)
                        ?.setText(R.id.tv_item_contact_person_body_mobile, "")
                val icon = hold?.getView<CircleImageView>(R.id.image_item_contact_person_body_icon)
                if (icon != null) {
                    val url = APIAddressHelper.instance().getPersonAvatarUrlWithId(identity.person)
                    O2ImageLoaderManager.instance().showImage(icon, url, O2ImageLoaderOptions(placeHolder = R.mipmap.icon_avatar_men))
                }
                val checkBox = hold?.getView<CheckBox>(R.id.check_item_contact_person_body_select)
                val arrow = hold?.getView<ImageView>(R.id.image_item_contact_person_body_arrow)
                arrow?.visible()
                checkBox?.gone()
                checkBox?.isChecked = false
                when (mode) {
                    MULTI_PERSON_CHOOSE_MODE -> {
                        arrow?.gone()
                        checkBox?.visible()
                        checkBox?.setOnClickListener {
                            var check = checkBox.isChecked
                            toggleCheckPerson(identity, check)
                        }
                        mSelectPersonSet.filter {
                            it == identity.person
                        }.map {
                            checkBox?.isChecked = true
                        }
                    }
                    MULTI_IDENTITY_CHOOSE_MODE -> {
                        arrow?.gone()
                        checkBox?.visible()
                        checkBox?.setOnClickListener {
                            var check = checkBox.isChecked
                            toggleCheckIdentity(identity, check)
                        }
                        mSelectIdentitySet.filter {
                            it == identity.id
                        }.map {
                            checkBox?.isChecked = true
                        }
                    }
                }

                //是否显示顶部间隔
                var isShowGap = false
                if (position >= 1) {
                    val preItem = items[(position - 1)]
                    if ((preItem !is NewContactListVO.Identity)) {
                        isShowGap = true
                    }
                } else {
                    isShowGap = true
                }
                val gap = hold?.getView<RelativeLayout>(R.id.relative_item_contact_person_body_top_gap)
                val topLine = hold?.getView<View>(R.id.view_item_contact_person_body_top_divider)
                if (isShowGap) {
                    gap?.visibility = View.VISIBLE
                    topLine?.visibility = View.GONE
                } else {
                    gap?.visibility = View.GONE
                    topLine?.visibility = View.VISIBLE
                }
            }

            override fun clickIdentity(view: View, identity: NewContactListVO.Identity) {
                when (mode) {
                    DEFAULT_MODE -> go<PersonActivity>(PersonActivity.startBundleData(identity.person))
                    SINGLE_PERSON_CHOOSE_MODE -> {
                        intent.putExtra(SINGLE_PERSON_CHOOSE_RESULT, identity.person)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                    MULTI_PERSON_CHOOSE_MODE -> {
                        val checkBox = view.findViewById<CheckBox>(R.id.check_item_contact_person_body_select)
                        val isCheck = checkBox.isChecked
                        checkBox.isChecked = !isCheck
                        toggleCheckPerson(identity, !isCheck)
                    }
                    SINGLE_IDENTITY_CHOOSE_MODE -> {
                        intent.putExtra(SINGLE_IDENTITY_CHOOSE_RESULT, identity.id)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                    MULTI_IDENTITY_CHOOSE_MODE -> {
                        val checkBox = view.findViewById<CheckBox>(R.id.check_item_contact_person_body_select)
                        val isCheck = checkBox.isChecked
                        checkBox.isChecked = !isCheck
                        toggleCheckIdentity(identity, !isCheck)
                    }
                }
            }
        }
    }


    override fun afterSetContentView(savedInstanceState: Bundle?) {
        unitParentId = intent.extras?.getString(UNIT_PARENT_KEY) ?: ""
        status = intent.extras?.getInt(STATUS_KEY) ?: DEFAULT_STATUS
        mode = intent.extras?.getInt(MODE_KEY) ?: DEFAULT_MODE
        if (TextUtils.isEmpty(unitParentId)) {
            unitParentId = UNIT_TOP_PARENT_ID
        }
        unitParentName = intent.extras?.getString(UNIT_PARENT_NAME_KEY) ?: ""
        if (TextUtils.isEmpty(unitParentName)) {
            unitParentName = getString(R.string.tab_contact)
        }
        fromImChoose = intent.extras?.getInt(FROM_IM_CHOOSE_KEY, NOT_IM_CHOOSE) ?: NOT_IM_CHOOSE
        val alreadyChoose = intent.extras?.getStringArrayList(ALREADY_CHOOSE_PERSON_LIST_KEY)?: ArrayList<String>()
        if (alreadyChoose.size>0) {
            if (fromImChoose != NOT_IM_CHOOSE){
                mSelectPersonSet.addAll(alreadyChoose)
            }else {
                when(mode) {
                    MULTI_IDENTITY_CHOOSE_MODE -> mSelectIdentitySet.addAll(alreadyChoose)
                    MULTI_PERSON_CHOOSE_MODE -> mSelectPersonSet.addAll(alreadyChoose)
                }
            }
        }

        breadcrumbBeans.add(ContactBreadcrumbBean(unitParentId, unitParentName, orgLevel))
        toolbar_organization.title = ""
        setSupportActionBar((toolbar_organization as Toolbar))
        toolbar_organization.setNavigationIcon(R.mipmap.ic_back_mtrl_white_alpha)
        toolbar_organization.setNavigationOnClickListener {
            when (status) {
                DEFAULT_STATUS -> finish()
                SEARCH_STATUS -> changeStatusToDefault()
            }
        }

        if (mode == SINGLE_IDENTITY_CHOOSE_MODE || mode == MULTI_IDENTITY_CHOOSE_MODE) {
            linear_organization_search_button.gone()
        } else {
            linear_organization_search_button.visible()
            linear_organization_search_button.setOnClickListener { changeStatusToSearch() }
        }

        swipe_refresh_organization_main.setColorSchemeResources(R.color.z_color_refresh_scuba_blue,
                R.color.z_color_refresh_red, R.color.z_color_refresh_purple, R.color.z_color_refresh_orange)
        swipe_refresh_organization_main.setOnRefreshListener {
            if (status == DEFAULT_STATUS) {
                refreshOrganizationMain()
            }
        }
        edit_toolbar_organization_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                var result = s?.toString() ?: ""
                if (TextUtils.isEmpty(result)) {
                    clearSearchList()
                } else {
                    mPresenter.searchPersonWithKey(result)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        list_organization_main.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        list_organization_main.adapter = mainListAdapter

        when (status) {
            DEFAULT_STATUS -> initViewDefaultStatus()
            SEARCH_STATUS -> initViewSearchStatus()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        when (mode) {
            MULTI_IDENTITY_CHOOSE_MODE, MULTI_PERSON_CHOOSE_MODE -> {
                menuInflater.inflate(R.menu.menu_organization_check, menu)
            }
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
            when (mode) {
                MULTI_IDENTITY_CHOOSE_MODE -> {
                    if (mSelectIdentitySet.size > 0) {
                        menu?.findItem(R.id.org_menu_choose)?.title = getString(R.string.menu_choose)+ "(" + mSelectIdentitySet.size.toString() + ")"
                    } else {
                        menu?.findItem(R.id.org_menu_choose)?.title = getString(R.string.menu_choose)
                    }
                }
                MULTI_PERSON_CHOOSE_MODE -> {
                    if (mSelectPersonSet.size > 0) {
                        menu?.findItem(R.id.org_menu_choose)?.title = getString(R.string.menu_choose) + "(" + mSelectPersonSet.size.toString() + ")"
                    } else {
                        menu?.findItem(R.id.org_menu_choose)?.title = getString(R.string.menu_choose)
                    }
                }

        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.org_menu_choose -> {
                when (mode) {
                    MULTI_IDENTITY_CHOOSE_MODE -> {
                        if (mSelectIdentitySet.isEmpty()) {
                            XToast.toastShort(this, "请至少选择一个身份！")
                            return true
                        }
                        val list = ArrayList<String>()
                        mSelectIdentitySet.map { list.add(it) }
                        intent.putStringArrayListExtra(MULTI_IDENTITY_CHOOSE_RESULT, list)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                    MULTI_PERSON_CHOOSE_MODE -> {
                        when (fromImChoose) {
                            IM_CHOOSE_NEED_START_ACTIVITY -> {
                                if (mSelectPersonSet.size < 3) {
                                    XToast.toastShort(this, "创建群组至少需要选择3个人！")
                                    return true
                                } else {
                                    val list = ArrayList<String>()
                                    mSelectPersonSet.map { list.add(it) }
                                    goThenKill<IMTribeCreateActivity>(IMTribeCreateActivity.startCreate(list))
                                }
                            }
                            IM_CHOOSE_FROM_REQUEST -> {
//                                if (mSelectPersonSet.size < 3) {
//                                    XToast.toastShort(this, "创建群组至少需要选择3个人！")
//                                    return true
//                                } else {
                                    val list = ArrayList<String>()
                                    mSelectPersonSet.map { list.add(it) }
                                    intent.putStringArrayListExtra(MULTI_PERSON_CHOOSE_RESULT, list)
                                    setResult(Activity.RESULT_OK, intent)
                                    finish()
//                                }
                            }
                            else -> {
                                if (mSelectPersonSet.isEmpty()) {
                                    XToast.toastShort(this, "请至少选择一个人员！")
                                    return true
                                }
                                val list = ArrayList<String>()
                                mSelectPersonSet.map { list.add(it) }
                                intent.putStringArrayListExtra(MULTI_PERSON_CHOOSE_RESULT, list)
                                setResult(Activity.RESULT_OK, intent)
                                finish()
                            }
                        }
                    }
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (status == SEARCH_STATUS) {
                changeStatusToDefault()
                return true
            } else {
                if (breadcrumbBeans.size > 1) {
                    breadcrumbBeans.removeAt(breadcrumbBeans.size - 1)
                    refreshOrganizationMain()
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun callbackResult(list: List<NewContactListVO>) {
        mainItemList.clear()
        mainItemList.addAll(list)
        mainListAdapter.notifyDataSetChanged()
        shimmer_organization_layout.gone()
        swipe_refresh_organization_main.visible()
        swipe_refresh_organization_main.isRefreshing = false
    }

    override fun backError(error: String) {
        if (!TextUtils.isEmpty(error)) {
            XToast.toastShort(this, error)
        }
        mainItemList.clear()
        mainListAdapter.notifyDataSetChanged()
        shimmer_organization_layout.gone()
        swipe_refresh_organization_main.visible()
        swipe_refresh_organization_main.isRefreshing = false
    }

    private fun initViewDefaultStatus() {
        tv_toolbar_title_organization.visible()
        edit_toolbar_organization_search.gone()
        linear_organization_search_button.visible()
        linear_organization_breadcrumb_bar.visible()
        shimmer_organization_layout.visible()
        swipe_refresh_organization_main.gone()
        refreshOrganizationMain()
    }

    private fun initViewSearchStatus() {
        tv_toolbar_title_organization.gone()
        edit_toolbar_organization_search.visible()
        linear_organization_search_button.gone()
        linear_organization_breadcrumb_bar.gone()
        shimmer_organization_layout.gone()
        swipe_refresh_organization_main.visible()
        //获得焦点
        edit_toolbar_organization_search.isFocusable = true
        edit_toolbar_organization_search.isFocusableInTouchMode = true
        edit_toolbar_organization_search.requestFocus()
        edit_toolbar_organization_search.requestFocusFromTouch()
        ZoneUtil.toggleSoftInput(edit_toolbar_organization_search, true)
    }


    /**
     * 从search状态切换成普通状态
     */
    private fun changeStatusToDefault() {
        status = DEFAULT_STATUS
        tv_toolbar_title_organization.visible()
        edit_toolbar_organization_search.gone()
        linear_organization_search_button.visible()
        linear_organization_breadcrumb_bar.visible()
        shimmer_organization_layout.gone()
        swipe_refresh_organization_main.visible()

        //隐藏软键盘
        ZoneUtil.toggleSoftInput(edit_toolbar_organization_search, false)
        refreshOrganizationMain()
    }

    /**
     * 从普通状态切换成查询状态
     */
    private fun changeStatusToSearch() {
        status = SEARCH_STATUS
        tv_toolbar_title_organization.gone()
        linear_organization_search_button.gone()
        linear_organization_breadcrumb_bar.gone()
        shimmer_organization_layout.gone()
        swipe_refresh_organization_main.visible()
        edit_toolbar_organization_search.visible()
        edit_toolbar_organization_search.setText("")
        //获得焦点
        edit_toolbar_organization_search.isFocusable = true
        edit_toolbar_organization_search.isFocusableInTouchMode = true
        edit_toolbar_organization_search.requestFocus()
        edit_toolbar_organization_search.requestFocusFromTouch()
        ZoneUtil.toggleSoftInput(edit_toolbar_organization_search, true)
    }

    /**
     * 刷新组织人员列表
     */
    private fun refreshOrganizationMain() {
        val bean = breadcrumbBeans[breadcrumbBeans.size - 1]//最后一个
        loadOrganization(bean.key, bean.name, bean.level)
        refreshBreadcrumb()
    }

    private fun loadOrganization(id: String, name: String, level: Int) {
        tv_toolbar_title_organization.text = name
        orgLevel = level
        swipe_refresh_organization_main.isRefreshing = true
        mPresenter.loadChildrenWithParent(id)
    }

    val mHandler = Handler()
    private val mScrollToBottom = Runnable {
        val off = linear_organization_breadcrumb_layout.measuredWidth - scroll_organization_breadcrumb_scroll.width
        if (off > 0) {
            scroll_organization_breadcrumb_scroll.scrollTo(off, 0)
        }
    }

    /**
     * 刷新导航条
     */
    private fun refreshBreadcrumb() {
        linear_organization_breadcrumb_layout.removeAllViews()
        breadcrumbBeans.mapIndexed { index, contactBreadcrumbBean ->
            val breadcrumbTitle = TextView(this)
            breadcrumbTitle.text = contactBreadcrumbBean.name
            breadcrumbTitle.tag = contactBreadcrumbBean.key
            breadcrumbTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
            breadcrumbTitle.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            if (index == breadcrumbBeans.size - 1) {
                breadcrumbTitle.setTextColor(FancySkinManager.instance().getColor(this, R.color.z_color_primary))
                linear_organization_breadcrumb_layout.addView(breadcrumbTitle)
            } else {
                breadcrumbTitle.setTextColor(FancySkinManager.instance().getColor(this, R.color.z_color_text_primary_dark))
                breadcrumbTitle.setOnClickListener { view -> onClickBreadcrumb((view as TextView)) }
                linear_organization_breadcrumb_layout.addView(breadcrumbTitle)
                val arrow = ImageView(this)
                arrow.setImageResource(R.mipmap.icon_arrow_22dp)
                arrow.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                linear_organization_breadcrumb_layout.addView(arrow)
            }
        }
        mHandler.post(mScrollToBottom)//启动线程 把滚动条滚动到最后
    }

    /**
     * 点击导航条上的某一个层级名
     */
    private fun onClickBreadcrumb(textView: TextView) {
        val tag = textView.tag as String
        var newLevel = 0
        for ((index, bean) in breadcrumbBeans.withIndex()) {
            if (tag == bean.key) {
                newLevel = index
                loadOrganization(bean.key, bean.name, newLevel)
                break
            }
        }
        //处理breadcrumbBeans 把多余的去掉
        if (breadcrumbBeans.size > newLevel + 1) {
            (breadcrumbBeans.size - 1 downTo 0)
                    .filter { it > newLevel }
                    .forEach { breadcrumbBeans.removeAt(it) }
        }
        refreshBreadcrumb()
    }

    /**
     * 清空查询结果列表
     */
    private fun clearSearchList() {
        mainItemList.clear()
        mainListAdapter.notifyDataSetChanged()
    }


    private fun toggleCheckPerson(identity: NewContactListVO.Identity, checked: Boolean) {
        if (checked) {
            mSelectPersonSet.add(identity.person)
        } else {
            mSelectPersonSet.remove(identity.person)
        }
        refreshMenu()
    }

    private fun toggleCheckIdentity(identity: NewContactListVO.Identity, checked: Boolean) {
        if (checked) {
            mSelectIdentitySet.add(identity.id)
        } else {
            mSelectIdentitySet.remove(identity.id)
        }
        refreshMenu()
    }

    private fun refreshMenu() {
        // getWindow().invalidatePanelMenu(Window.FEATURE_OPTIONS_PANEL);
        invalidateOptionsMenu()
    }

}
