package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.main

import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_main_contact.*
import net.muliba.changeskin.FancySkinManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.CircleImageView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPViewPagerFragment
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.organization.NewOrganizationActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.person.PersonActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.NewContactFragmentAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.NewContactFragmentVO
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.MiscUtilK
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.go
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.inVisible
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.imageloader.O2ImageLoaderManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.imageloader.O2ImageLoaderOptions


class NewContactFragment : BaseMVPViewPagerFragment<NewContactContract.View, NewContactContract.Presenter>(), NewContactContract.View {

    override var mPresenter: NewContactContract.Presenter = NewContactPresenter()

    override fun layoutResId(): Int = R.layout.fragment_main_contact

    val items = ArrayList<NewContactFragmentVO>()
    val adapter: NewContactFragmentAdapter by lazy {
        object : NewContactFragmentAdapter(items) {
            override fun bindMyDepartment(department: NewContactFragmentVO.MyDepartment, holder: CommonRecyclerViewHolder?) {
                holder?.setText(R.id.tv_item_contact_fragment_body_name, department.unitName)
                        ?.setCircleTextView(R.id.image_item_contact_fragment_body_icon, if (TextUtils.isEmpty(department.unitName)){""} else {department.unitName.substring(0, 1)},
                                FancySkinManager.instance().getColor(activity, R.color.z_color_primary))
                if (department.hasChildren) {
                    holder?.getView<ImageView>(R.id.image_item_contact_fragment_body_arrow)?.visible()
                }else {
                    holder?.getView<ImageView>(R.id.image_item_contact_fragment_body_arrow)?.gone()
                }
            }

            override fun clickMyDepartment(department: NewContactFragmentVO.MyDepartment) {
                XLog.debug("点击部门 $department")
                if (department.hasChildren) {
                    activity.go<NewOrganizationActivity>(NewOrganizationActivity.startBundleData(department.unit, department.unitName))
                }else {
                    XLog.error("没有子元素。。。。。。。。。。。。。。")
                }
            }

            override fun bindMyCollect(collect: NewContactFragmentVO.MyCollect, holder: CommonRecyclerViewHolder?) {
                holder?.setText(R.id.tv_item_contact_fragment_body_collect_name, collect.personName)
                val mobileTv = holder?.getView<TextView>(R.id.tv_item_contact_fragment_body_collect_mobile)
                if (TextUtils.isEmpty(collect.mobile)) {
                    mobileTv?.inVisible()
                } else {
                    mobileTv?.text = collect.mobile
                    mobileTv?.visible()
                }
                val icon = holder?.getView<CircleImageView>(R.id.image_item_contact_fragment_body_collect_icon)
                val url = APIAddressHelper.instance().getPersonAvatarUrlWithId(collect.personId)
                if (icon!=null) {
                    if (collect.gender == "男") {
                        O2ImageLoaderManager.instance()
                                .showImage(icon, url, O2ImageLoaderOptions(placeHolder = R.mipmap.icon_avatar_men))

                    } else {
                        O2ImageLoaderManager.instance()
                                .showImage(icon, url, O2ImageLoaderOptions(placeHolder = R.mipmap.icon_avatar_women))
                    }
                }
            }

            override fun clickMyCollect(collect: NewContactFragmentVO.MyCollect) {
                XLog.debug("点击常用联系人，$collect")
                activity.go<PersonActivity>(PersonActivity.startBundleData(collect.personId))
            }
        }
    }

    override fun initUI() {
        main_contact_refresh_layout_id.setColorSchemeResources(R.color.z_color_refresh_scuba_blue,
                R.color.z_color_refresh_red, R.color.z_color_refresh_purple, R.color.z_color_refresh_orange)
        main_contact_refresh_layout_id.setOnRefreshListener { lazyLoad() }
        linear_contact_fragment_search.setOnClickListener { activity.go<NewOrganizationActivity>(NewOrganizationActivity.startBundleData(status = NewOrganizationActivity.SEARCH_STATUS)) }
        MiscUtilK.swipeRefreshLayoutRun(main_contact_refresh_layout_id, activity)

        recycler_contact_fragment_list.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        recycler_contact_fragment_list.adapter = adapter
    }

    override fun lazyLoad() {
        mPresenter.loadNewContact()
    }

    override fun loadContactFail() {
        finishRefresh()
        items.clear()
        adapter.notifyDataSetChanged()
    }

    override fun loadContact(list: List<NewContactFragmentVO>) {
        finishRefresh()
        items.clear()
        items.addAll(list)
        adapter.notifyDataSetChanged()
    }

    private fun finishRefresh() {
        main_contact_refresh_layout_id.isRefreshing = false
    }

}
