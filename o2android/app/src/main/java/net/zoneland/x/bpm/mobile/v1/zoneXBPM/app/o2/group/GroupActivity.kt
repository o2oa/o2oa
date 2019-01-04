package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.group


import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_group.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.person.PersonActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.ViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.go
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.imageloader.O2ImageLoaderManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.CircleImageView


class GroupActivity : BaseMVPActivity<GroupContract.View, GroupContract.Presenter>(), GroupContract.View {

    companion object {
        val GROUP_NAME_KEY = "groupName"
    }

    val memberList = ArrayList<String>()


    override fun layoutResId(): Int = R.layout.activity_group

    override var mPresenter: GroupContract.Presenter = GroupPresenter()

    override fun afterSetContentView(savedInstanceState: Bundle?) {
        val name = intent.extras?.getString(GROUP_NAME_KEY, "") ?: ""
        if (TextUtils.isEmpty(name)) {
            XToast.toastShort(this, "没有群组名称，无法获取群组信息！")
            finish()
            return
        }

        setupToolBar(getString(R.string.title_activity_group), true)

        group_name_id.text = name
        mPresenter.loadGroupMembers(name)

        group_list_id.adapter = adapter
        group_list_id.setOnItemClickListener { parent, view, position, id ->
            val bundle = Bundle()
            bundle.putString(PersonActivity.PERSON_NAME_KEY, memberList[position])
            go<PersonActivity>(bundle)
        }
    }

    override fun loadGroupMembers(members: List<String>) {
        memberList.clear()
        memberList.addAll(members)
        adapter.notifyDataSetChanged()
    }


    val adapter: CommonAdapter<String>  by lazy {
        object : CommonAdapter<String>(this, memberList, R.layout.item_contact_person_body) {
            override fun convert(holder: ViewHolder?, t: String?) {
                holder?.setText(R.id.tv_item_contact_person_body_name, t)
                val icon = holder?.getView<CircleImageView>(R.id.image_item_contact_person_body_icon)
                if (icon != null) {
                    val url = APIAddressHelper.instance().getPersonAvatarUrlWithId(t!!)
                    O2ImageLoaderManager.instance().showImage(icon, url)
                }

                //异步处理 部门和手机号码
                val mobileTv = holder?.getView<TextView>(R.id.tv_item_contact_person_body_mobile)
                mobileTv?.tag = t
                val deptTv = holder?.getView<TextView>(R.id.tv_item_contact_person_body_dept)
                deptTv?.tag = t
                mPresenter.asyncLoadPersonMobileAndDepartment(t, mobileTv, deptTv)
                //是否显示顶部间隔
                var isShowGap = false
                if (memberList[0].equals(t)) {
                    isShowGap = true
                }
                val gap = holder?.getView<RelativeLayout>(R.id.relative_item_contact_person_body_top_gap)
                val topLine = holder?.getView<View>(R.id.view_item_contact_person_body_top_divider)
                if (isShowGap) {
                    gap?.visibility = View.VISIBLE
                    topLine?.visibility = View.GONE
                } else {
                    gap?.visibility = View.GONE
                    topLine?.visibility = View.VISIBLE
                }
            }
        }
    }
}
