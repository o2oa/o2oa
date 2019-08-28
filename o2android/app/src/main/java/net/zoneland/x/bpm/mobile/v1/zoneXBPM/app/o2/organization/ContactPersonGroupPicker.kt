package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.organization

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.widget.CheckBox
import kotlinx.android.synthetic.main.fragment_person_group_picker.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPFragment
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.SwipeRefreshCommonRecyclerViewAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.NewContactListVO
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.imageloader.O2ImageLoaderManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.imageloader.O2ImageLoaderOptions
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.CircleImageView
import org.jetbrains.anko.dip

/**
 * Created by fancyLou on 2019-08-21.
 * Copyright © 2019 O2. All rights reserved.
 */

class ContactPersonGroupPicker : BaseMVPFragment<ContactPersonGroupActivityContract.View, ContactPersonGroupActivityContract.Presenter>(), ContactPersonGroupActivityContract.View {
    override var mPresenter: ContactPersonGroupActivityContract.Presenter = ContactPersonGroupActivityPresenter()

    override fun layoutResId(): Int = R.layout.fragment_person_group_picker

    companion object {
        const val GROUP_PICK_MODE = "0" //群组选择
        const val PERSON_PICK_MODE = "1" //人员选择

        const val PICK_MODE_KEY = "PICK_MODE_KEY"//选择模式的key
        const val multiple_KEY = "multiple_KEY"
        const val MAX_NUMBER_KEY = "MAX_NUMBER_KEY"

        /**
         * @param pickMode 选择模式 GROUP_PICK_MODE群组选择、PERSON_PICK_MODE人员选择
         */
        fun startPicker(pickMode: String,
                              multiple:Boolean = false,
                              maxNumber: Int = 0
        ): ContactPersonGroupPicker {
            val picker = ContactPersonGroupPicker()
            val bundle = Bundle()
            bundle.putString(PICK_MODE_KEY, pickMode)
            bundle.putBoolean(multiple_KEY, multiple)
            bundle.putInt(MAX_NUMBER_KEY, maxNumber)
            picker.arguments = bundle
            return picker
        }
    }

    private var pickMode = GROUP_PICK_MODE
    private var multiple = true
    private var maxNumber = 0
    private val itemList: ArrayList<NewContactListVO> = ArrayList()

    private val adapter: SwipeRefreshCommonRecyclerViewAdapter<NewContactListVO> by lazy {
        object : SwipeRefreshCommonRecyclerViewAdapter<NewContactListVO>(activity, itemList, R.layout.item_contact_complex_picker_identity) {
            override fun convert(holder: CommonRecyclerViewHolder?, data: NewContactListVO?) {
                if (data!=null) {
                    if (data is NewContactListVO.Group) {
                        holder?.setText(R.id.tv_item_contact_complex_picker_identity_name, data.name)
                                ?.setImageViewResource(R.id.image_item_contact_complex_picker_identity_icon, R.mipmap.icon_avatar_tribe_40)
                        val checkBox = holder?.getView<CheckBox>(R.id.check_item_contact_complex_picker_identity_select)
                        checkBox?.isChecked = false
                        checkBox?.setOnClickListener {
                            val check = checkBox.isChecked
                            toggleCheck(data, check)
                        }
                        checkBox?.isChecked = (activity as ContactPickerActivity).isSelectedValue(data)
                    }else if (data is NewContactListVO.Person) {
                        holder?.setText(R.id.tv_item_contact_complex_picker_identity_name, data.name)
                        val icon = holder?.getView<CircleImageView>(R.id.image_item_contact_complex_picker_identity_icon)
                        if (icon != null) {
                            val url = APIAddressHelper.instance().getPersonAvatarUrlWithId(data.distinguishedName)
                            O2ImageLoaderManager.instance().showImage(icon, url, O2ImageLoaderOptions(placeHolder = R.mipmap.icon_avatar_men))
                        }
                        val checkBox = holder?.getView<CheckBox>(R.id.check_item_contact_complex_picker_identity_select)
                        checkBox?.isChecked = false
                        checkBox?.setOnClickListener {
                            val check = checkBox.isChecked
                            toggleCheck(data, check)
                        }
                        checkBox?.isChecked = (activity as ContactPickerActivity).isSelectedValue(data)
                    }
                }
            }
        }
    }

    private var isRefresh = false
    private var isLoading = false
    private var lastId = ""

    override fun initUI() {
        ///初始化传入参数
        pickMode = arguments?.getString(PICK_MODE_KEY) ?: GROUP_PICK_MODE
        multiple = arguments?.getBoolean(multiple_KEY) ?: true
        maxNumber = arguments?.getInt(MAX_NUMBER_KEY) ?: 0

        swipe_refresh_contact_person_group_picker_main.touchSlop = activity.dip(70f)
        swipe_refresh_contact_person_group_picker_main.setColorSchemeResources(R.color.z_color_refresh_scuba_blue,
                R.color.z_color_refresh_red, R.color.z_color_refresh_purple, R.color.z_color_refresh_orange)
        swipe_refresh_contact_person_group_picker_main.recyclerViewPageNumber = O2.DEFAULT_PAGE_NUMBER
        swipe_refresh_contact_person_group_picker_main.setOnRefreshListener{
            if (!isLoading && !isRefresh) {
                getDatas(true)
                isRefresh = true
            }
        }
        swipe_refresh_contact_person_group_picker_main.setOnLoadMoreListener {
            if (!isLoading && !isRefresh) {
                if (TextUtils.isEmpty(lastId)) {
                    getDatas(true)
                } else {
                    getDatas(false)
                }
                isLoading = true
            }
        }

        rv_contact_person_group_picker_main.adapter = adapter
        rv_contact_person_group_picker_main.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        adapter.setOnItemClickListener { view, position ->
            val checkBox = view.findViewById<CheckBox>(R.id.check_item_contact_complex_picker_identity_select)
            val isCheck = checkBox.isChecked
            checkBox.isChecked = !isCheck
            val item = itemList[position]
            if (item is NewContactListVO.Group) {
                toggleCheck(item, !isCheck)
            }else if (item is NewContactListVO.Person) {
                toggleCheck(item, !isCheck)
            }
        }
        //初始化加载数据
        isRefresh = true
        getDatas(true)
    }

    override fun callbackResult(list: List<NewContactListVO>) {
        if (isRefresh) {
            itemList.clear()
        }
        itemList.addAll(list)
        if (list.isNotEmpty()) {
            val item = list[list.size-1]
            if (item is NewContactListVO.Person) {
                lastId = item.id
            }else if (item is NewContactListVO.Group) {
                lastId = item.id
            }
        }
        adapter.notifyDataSetChanged()
        finishAnimation()
    }

    override fun backError(error: String) {
        XToast.toastShort(activity, "获取任务列表失败")
        itemList.clear()
        adapter.notifyDataSetChanged()
        finishAnimation()
    }



    private fun toggleCheck(v: NewContactListVO, check: Boolean) {
        XLog.debug("click toggleCheckIdentity, $check")
        if (check) {
            (activity as ContactPickerActivity).addSelectedValue(v)
        } else {
            (activity as ContactPickerActivity).removeSelectedValue(v)
        }
        adapter.notifyDataSetChanged()
    }

    private fun finishAnimation() {
        if (isRefresh) {
            swipe_refresh_contact_person_group_picker_main.isRefreshing = false
            isRefresh = false
        }
        if (isLoading) {
            swipe_refresh_contact_person_group_picker_main.setLoading(false)
            isLoading = false
        }
    }

    //加载数据
    private fun getDatas(flag: Boolean) {
        if (flag) {
            mPresenter.findListByPage(pickMode, O2.FIRST_PAGE_TAG)
        }else {
            mPresenter.findListByPage(pickMode, lastId)
        }
    }
}