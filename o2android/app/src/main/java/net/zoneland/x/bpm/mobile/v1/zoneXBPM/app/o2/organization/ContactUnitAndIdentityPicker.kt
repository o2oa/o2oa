package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.organization

import android.os.Bundle
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.fragment_unit_identity_picker.*
import net.muliba.changeskin.FancySkinManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPFragment
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.ContactComplexPickerListAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.ContactBreadcrumbBean
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.NewContactListVO
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.imageloader.O2ImageLoaderManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.imageloader.O2ImageLoaderOptions
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.CircleImageView

/**
 * Created by fancyLou on 2019-08-20.
 * Copyright © 2019 O2. All rights reserved.
 */

class ContactUnitAndIdentityPicker: BaseMVPFragment<ContactUnitAndIdentityPickerContract.View,
        ContactUnitAndIdentityPickerContract.Presenter>(), ContactUnitAndIdentityPickerContract.View {

    companion object {
        const val ORG_PICK_MODE = "0" //组织选择
        const val IDENTITY_PICK_MODE = "1" //身份选择
        const val PERSON_PICK_MODE = "2" //人员选择

        const val PICK_MODE_KEY = "PICK_MODE_KEY"//选择模式的key
        const val TOP_LIST_KEY = "TOP_LIST_KEY"
        const val ORG_TYPE_KEY = "ORG_TYPE_KEY"
        const val MULIPLE_KEY = "multiple_KEY"
        const val MAX_NUMBER_KEY = "MAX_NUMBER_KEY"
        const val DUTY_KEY = "DUTY_KEY"

        fun startPicker(pickMode: String,
                              topList: ArrayList<String> = ArrayList(),
                              orgType: String = "",
                              multiple:Boolean = false,
                              maxNumber: Int = 0,
                              duty: ArrayList<String> = ArrayList()
        ): ContactUnitAndIdentityPicker {
            val picker =  ContactUnitAndIdentityPicker()
            val bundle = Bundle()
            bundle.putString(PICK_MODE_KEY, pickMode)
            bundle.putStringArrayList(TOP_LIST_KEY, topList)
            bundle.putString(ORG_TYPE_KEY, orgType)
            bundle.putStringArrayList(DUTY_KEY, duty)
            bundle.putBoolean(MULIPLE_KEY, multiple)
            bundle.putInt(MAX_NUMBER_KEY, maxNumber)
            picker.arguments = bundle
            return picker
        }
    }

    override var mPresenter: ContactUnitAndIdentityPickerContract.Presenter = ContactUnitAndIdentityPickerPresenter()

    override fun layoutResId(): Int = R.layout.fragment_unit_identity_picker

    private var pickMode = ORG_PICK_MODE//选择模式 0组织选择、1身份选择
    private var topList: List<String> = ArrayList()//可选的顶级组织列表
    private var orgType = ""//可选择的组织类别
    private var multiple = true//是否多选
    private var maxNumber = 0//当multiple为true的时候，最多可选择的数量
    private var duty: List<String> = ArrayList()//人员职责


    //面包屑导航
    private val breadcrumbBeans = ArrayList<ContactBreadcrumbBean>()
    private var orgLevel = 0//默认进入的时候是第一层组织
    private var unitParentId = ""
    private var unitParentName = ""
    private val itemList: ArrayList<NewContactListVO> = ArrayList()
    private val adapter: ContactComplexPickerListAdapter by lazy {
        object: ContactComplexPickerListAdapter(itemList) {
            override fun bindDepartment(hold: CommonRecyclerViewHolder?, department: NewContactListVO.Department, position: Int) {
                val checkBox = hold?.getView<CheckBox>(R.id.check_item_contact_complex_picker_org_body)
                if (pickMode == ORG_PICK_MODE) {
                    checkBox?.visible()
                }else {
                    checkBox?.gone()
                }
                checkBox?.isChecked = false
                checkBox?.setOnClickListener {
                    val check = checkBox.isChecked
                    toggleCheckOrg(department, check)
                }
                checkBox?.isChecked = (activity as ContactPickerActivity).isSelectedValue(department)
                val count = if (pickMode == ORG_PICK_MODE) {
                    department.departmentCount
                }else {
                    department.departmentCount + department.identityCount
                }
                hold?.setCircleTextView(R.id.image_item_contact_complex_picker_org_body_icon,
                        department.name.substring(0, 1), FancySkinManager.instance().getColor(activity, R.color.z_color_primary))
                        ?.setText(R.id.tv_item_contact_complex_picker_org_body_name, department.name)//+"($count)" 不显示数量 不准确
                val nextLevelBtn = hold?.getView<Button>(R.id.btn_item_contact_complex_picker_org_body_next)
                if (count>0) {
                    nextLevelBtn?.visible()
                    nextLevelBtn?.setOnClickListener {
                        val newLevel = orgLevel + 1
                        val bean = ContactBreadcrumbBean(department.distinguishedName, department.name, newLevel)
                        breadcrumbBeans.add(bean)
                        refreshRV()
                    }
                }else {
                    nextLevelBtn?.gone()
                }
            }

            override fun clickDepartment(view: View, department: NewContactListVO.Department) {
                XLog.debug("click Department")
                if (pickMode == ORG_PICK_MODE) {
                    val checkBox = view.findViewById<CheckBox>(R.id.check_item_contact_complex_picker_org_body)
                    val isCheck = checkBox.isChecked
                    checkBox.isChecked = !isCheck
                    toggleCheckOrg(department, !isCheck)
                }
            }

            override fun bindIdentity(hold: CommonRecyclerViewHolder?, identity: NewContactListVO.Identity, position: Int) {
                val checkBox = hold?.getView<CheckBox>(R.id.check_item_contact_complex_picker_identity_select)
                if (pickMode != ORG_PICK_MODE) {
                    checkBox?.visible()
                }else {
                    checkBox?.gone()
                }
                checkBox?.isChecked = false
                checkBox?.setOnClickListener {
                    val check = checkBox.isChecked
                    toggleCheckIdentity(identity, check)
                }
                if (pickMode == PERSON_PICK_MODE) {
                    checkBox?.isChecked = (activity as ContactPickerActivity).isSelectedValue(NewContactListVO.Person(name = identity.name, distinguishedName = identity.person))
                }else {
                    checkBox?.isChecked = (activity as ContactPickerActivity).isSelectedValue(identity)
                }
                val icon = hold?.getView<CircleImageView>(R.id.image_item_contact_complex_picker_identity_icon)
                if (icon != null) {
                    val url = APIAddressHelper.instance().getPersonAvatarUrlWithId(identity.person)
                    O2ImageLoaderManager.instance().showImage(icon, url, O2ImageLoaderOptions(placeHolder = R.mipmap.icon_avatar_men))
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
                val gap = hold?.getView<RelativeLayout>(R.id.rl_item_contact_complex_picker_identity_top_gap)
                if (isShowGap) {
                    gap?.visible()
                }else {
                    gap?.gone()
                }
                hold?.setText(R.id.tv_item_contact_complex_picker_identity_name, identity.name)
            }

            override fun clickIdentity(view: View, identity: NewContactListVO.Identity) {
                XLog.debug("click Identity")
                if (pickMode != ORG_PICK_MODE) {
                    val checkBox = view.findViewById<CheckBox>(R.id.check_item_contact_complex_picker_identity_select)
                    val isCheck = checkBox.isChecked
                    checkBox.isChecked = !isCheck
                    toggleCheckIdentity(identity, !isCheck)
                }
            }

        }
    }

    override fun initUI() {
        //选择模式
        pickMode = arguments?.getString(PICK_MODE_KEY) ?: ORG_PICK_MODE
        if (pickMode != ORG_PICK_MODE && pickMode != IDENTITY_PICK_MODE && pickMode != PERSON_PICK_MODE) {
            pickMode = ORG_PICK_MODE
        }
        //是否多选
        multiple = arguments?.getBoolean(MULIPLE_KEY) ?: true
        //组织类型
        orgType = arguments?.getString(ORG_TYPE_KEY) ?: ""
        //最多可选
        maxNumber = arguments?.getInt(MAX_NUMBER_KEY) ?: 0
        if (!multiple) {
            maxNumber = 1
        }
        //人员职责
        duty = arguments?.getStringArrayList(DUTY_KEY) ?: ArrayList()
        //顶层组织
        topList = arguments?.getStringArrayList(TOP_LIST_KEY) ?: ArrayList()

        //初始化view
        swipe_refresh_contact_complex_picker_main.setColorSchemeResources(R.color.z_color_refresh_scuba_blue,
                R.color.z_color_refresh_red, R.color.z_color_refresh_purple, R.color.z_color_refresh_orange)
        swipe_refresh_contact_complex_picker_main.setOnRefreshListener {
            refreshRV()
        }
        rv_contact_complex_picker_main.layoutManager =  LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        rv_contact_complex_picker_main.adapter = adapter

        //初始化数据
        orgLevel = 0
        unitParentId = "-1" //顶层
        unitParentName = getString(R.string.tab_contact)
        breadcrumbBeans.clear()
        breadcrumbBeans.add(ContactBreadcrumbBean(unitParentId, unitParentName, orgLevel))

        refreshRV()
    }

    override fun callbackResult(list: List<NewContactListVO>) {
        itemList.clear()
        itemList.addAll(list)
        adapter.notifyDataSetChanged()
        swipe_refresh_contact_complex_picker_main.isRefreshing = false
    }

    override fun backError(error: String) {
        if (!TextUtils.isEmpty(error)) {
            XToast.toastShort(activity, error)
        }
        itemList.clear()
        adapter.notifyDataSetChanged()
        swipe_refresh_contact_complex_picker_main.isRefreshing = false
    }

    //点击返回按钮
    fun clickBackBtn(): Boolean {
        if (breadcrumbBeans.size > 1) {
            breadcrumbBeans.removeAt(breadcrumbBeans.size - 1)
            refreshRV()
            return true
        }
        return false
    }

    //刷新列表
    private fun refreshRV() {
        val bean = breadcrumbBeans[breadcrumbBeans.size - 1]//最后一个
        loadOrgAndIdentityData(bean.key, bean.level)
        refreshBreadcrumb()
    }

    //加载数据
    private fun loadOrgAndIdentityData(id: String, level: Int) {
        orgLevel = level
        swipe_refresh_contact_complex_picker_main.isRefreshing = true
        mPresenter.loadUnitWithParent(id, pickMode!=ORG_PICK_MODE, topList, orgType, duty)
    }


    //面包屑导航滚动条
    val mHandler = Handler()
    private val mScrollToBottom = Runnable {
        val off = ll_contact_complex_picker_breadcrumb_layout.measuredWidth - hs_contact_complex_picker_breadcrumb_scroll.width
        if (off > 0) {
            hs_contact_complex_picker_breadcrumb_scroll.scrollTo(off, 0)
        }
    }

    /**
     * 刷新导航条
     */
    private fun refreshBreadcrumb() {
        ll_contact_complex_picker_breadcrumb_layout.removeAllViews()
        breadcrumbBeans.mapIndexed { index, contactBreadcrumbBean ->
            val breadcrumbTitle = TextView(activity)
            breadcrumbTitle.text = contactBreadcrumbBean.name
            breadcrumbTitle.tag = contactBreadcrumbBean.key
            breadcrumbTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
            breadcrumbTitle.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            if (index == breadcrumbBeans.size - 1) {
                breadcrumbTitle.setTextColor(FancySkinManager.instance().getColor(activity, R.color.z_color_primary))
                ll_contact_complex_picker_breadcrumb_layout.addView(breadcrumbTitle)
            } else {
                breadcrumbTitle.setTextColor(FancySkinManager.instance().getColor(activity, R.color.z_color_text_primary_dark))
                breadcrumbTitle.setOnClickListener { view -> onClickBreadcrumb((view as TextView)) }
                ll_contact_complex_picker_breadcrumb_layout.addView(breadcrumbTitle)
                val arrow = ImageView(activity)
                arrow.setImageResource(R.mipmap.icon_arrow_22dp)
                arrow.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                ll_contact_complex_picker_breadcrumb_layout.addView(arrow)
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
                loadOrgAndIdentityData(bean.key, newLevel)
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

    private fun toggleCheckOrg(department: NewContactListVO.Department, check: Boolean) {
        XLog.debug("click toggleCheckOrg, $check")
        if (check) {
            (activity as ContactPickerActivity).addSelectedValue(department)
        } else {
            (activity as ContactPickerActivity).removeSelectedValue(department)
        }
        adapter.notifyDataSetChanged()
    }

    private fun toggleCheckIdentity(identity: NewContactListVO.Identity, check: Boolean) {
        XLog.debug("click toggleCheckIdentity, $check")
        if (pickMode == PERSON_PICK_MODE) {
            if (check) {
                (activity as ContactPickerActivity).addSelectedValue(NewContactListVO.Person(name = identity.name, distinguishedName = identity.person))
            } else {
                (activity as ContactPickerActivity).removeSelectedValue(NewContactListVO.Person(name = identity.name, distinguishedName = identity.person))
            }
        }else {
            if (check) {
                (activity as ContactPickerActivity).addSelectedValue(identity)
            } else {
                (activity as ContactPickerActivity).removeSelectedValue(identity)
            }
        }
        adapter.notifyDataSetChanged()
    }

}