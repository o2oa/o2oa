package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.bbs.main

import android.support.v7.widget.GridLayoutManager
import android.widget.ImageView
import kotlinx.android.synthetic.main.fragment_bbs_main_section.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPViewPagerFragment
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.group.Group
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.group.GroupRecyclerViewAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.bbs.SectionInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.BBSCollectionSectionVO
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.setImageBase64
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible


class BBSMainSectionFragment : BaseMVPViewPagerFragment<BBSMainSectionContract.View, BBSMainSectionContract.Presenter>(), BBSMainSectionContract.View {

    override var mPresenter: BBSMainSectionContract.Presenter = BBSMainSectionPresenter()

    override fun layoutResId(): Int = R.layout.fragment_bbs_main_section

    val items: ArrayList<Group<String, SectionInfoJson>> by lazy { ArrayList<Group<String, SectionInfoJson>>() }

    val adapter : GroupRecyclerViewAdapter<String, SectionInfoJson> by lazy  {
        object: GroupRecyclerViewAdapter<String, SectionInfoJson>(items, R.layout.item_bbs_main_content_header, R.layout.item_bbs_main_content_section_item) {
            override fun onBindHeaderViewHolder(holder: CommonRecyclerViewHolder, header: String, position: Int) {
                holder.setText(R.id.tv_bbs_main_content_header_name, header)
            }

            override fun onBindChildViewHolder(holder: CommonRecyclerViewHolder, child: SectionInfoJson, position: Int) {
                holder.setText(R.id.tv_bbs_main_content_section_item_body, child.sectionName)
                if (child.updateTime.length>=10) {
                    holder.setText(R.id.tv_bbs_main_content_section_date, child.updateTime.substring(5,10))
                }
                val num = child.subjectTotal
                holder.setText(R.id.tv_bbs_main_content_section_number, ""+num+"个")
                val collectionIcon = holder.getView<ImageView>(R.id.tv_bbs_main_collect_icon)
                if (child.isCollection) {
                    collectionIcon.setImageResource(R.mipmap.icon_collect_por)
                } else {
                    collectionIcon.setImageResource(R.mipmap.icon_collect_nor)
                }
                val sectionIcon = holder.getView<ImageView>(R.id.tv_bbs_main_content_section_item_icon)
                sectionIcon.setImageResource(R.mipmap.icon_forum_default)
                sectionIcon.tag = child.id
                sectionIcon.setImageBase64(child.icon, child.id)
                holder.convertView.setOnClickListener { (activity as BBSMainActivity).enterBBSSection(child.id, child.sectionName) }
            }
        }
    }



    override fun initUI() {
        val glm = GridLayoutManager(activity, 2)
        glm.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (adapter.getItemViewType(position) == GroupRecyclerViewAdapter.ITEM_TYPE_HEAD) {
                    return 2
                } else {
                    return 1
                }
            }
        }
        recycler_bbs_main_content.layoutManager = glm
        recycler_bbs_main_content.adapter = adapter
    }

    override fun lazyLoad() {
        mPresenter.loadForumList()
    }

    override fun loadFail() {
        XToast.toastShort(activity, "查询论坛板块失败！")
        recycler_bbs_main_content.gone()
        tv_bbs_main_empty.visible()
    }

    override fun loadSuccess(items: List<Group<String, SectionInfoJson>>) {
        if (items != null && !items.isEmpty() ) {
            this.items.clear()
            this.items.addAll(items)
            mPresenter.queryAllMyBBSCollections()
            recycler_bbs_main_content.visible()
            tv_bbs_main_empty.gone()
        }else {
            recycler_bbs_main_content.gone()
            tv_bbs_main_empty.visible()
            this.items.clear()
        }
        adapter.notifyDataSetChanged()
    }

    override fun queryAllMyCollectionsResponse(list: List<BBSCollectionSectionVO>) {
        if (!list.isEmpty()){
            list.map { it.sectionName }
                    .forEach { name ->
                        items.flatMap { it.children }
                                .filter { name == it.sectionName }
                                .forEach { it.isCollection = true }
                    }

        }
        recycler_bbs_main_content.visible()
        tv_bbs_main_empty.gone()
        adapter.notifyDataSetChanged()
    }

    override fun queryAllMyCollectionsResponseError() {
        XToast.toastShort(activity, "查询收藏版块失败！")
        recycler_bbs_main_content.visible()
        tv_bbs_main_empty.gone()
        adapter.notifyDataSetChanged()
    }
}
