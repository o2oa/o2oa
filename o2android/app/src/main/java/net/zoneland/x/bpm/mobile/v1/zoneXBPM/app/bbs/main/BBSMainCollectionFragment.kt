package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.bbs.main

import android.support.v7.widget.GridLayoutManager
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import kotlinx.android.synthetic.main.fragment_bbs_main_collection.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPViewPagerFragment
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecycleViewAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.BBSCollectionSectionVO
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.setImageBase64
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible


class BBSMainCollectionFragment : BaseMVPViewPagerFragment<BBSMainCollectionContract.View, BBSMainCollectionContract.Presenter>(), BBSMainCollectionContract.View {

    override var mPresenter: BBSMainCollectionContract.Presenter = BBSMainCollectionPresenter()

    override fun layoutResId(): Int = R.layout.fragment_bbs_main_collection

    var canCheck: Boolean = false
    val mSelectIds: HashSet<String> = HashSet()
    val items: ArrayList<BBSCollectionSectionVO> = ArrayList()
    val adapter: CommonRecycleViewAdapter<BBSCollectionSectionVO> by lazy { object : CommonRecycleViewAdapter<BBSCollectionSectionVO>(context, items, R.layout.item_bbs_main_content_section_item){
        override fun convert(holder: CommonRecyclerViewHolder, child: BBSCollectionSectionVO) {
            holder.setText(R.id.tv_bbs_main_content_section_item_body, child.sectionName)
            val sectionIcon = holder.getView<ImageView>(R.id.tv_bbs_main_content_section_item_icon)
            sectionIcon.setImageResource(R.mipmap.icon_forum_default)
            sectionIcon.tag = child.id
            sectionIcon.setImageBase64(child.sectionIcon, child.id)
            val collectionIcon = holder.getView<ImageView>(R.id.tv_bbs_main_collect_icon)
            collectionIcon.setImageResource(R.mipmap.icon_collect_por)

            holder.convertView.setOnClickListener { clickCollectionItem(holder.convertView, child) }
            holder.convertView.setOnLongClickListener { showCheckTool() }
        }
    } }


    override fun initUI() {
        val glm = GridLayoutManager(activity, 2)
        recycler_bbs_main_collection_content.layoutManager = glm
        recycler_bbs_main_collection_content.adapter = adapter
    }

    override fun lazyLoad() {
        mPresenter.queryAllMyBBSCollections()
    }

    override fun queryAllMyCollectionsResponse(list: List<BBSCollectionSectionVO>) {
        items.clear()
        if (!list.isEmpty()){
            items.addAll(list)
            recycler_bbs_main_collection_content.visible()
            tv_bbs_main_collection_empty.gone()
        }else{
            recycler_bbs_main_collection_content.gone()
            tv_bbs_main_collection_empty.visible()
        }
        adapter.notifyDataSetChanged()
    }

    override fun queryAllMyCollectionsResponseError() {
        XToast.toastShort(activity, "查询收藏版块失败！")
    }

    override fun mustSelectMoreThanOne() {
        XToast.toastShort(activity, "请选择一个以上的选项！")
    }

    override fun cancelCollectionResponse(flag: Boolean) {
        if (flag) {
            mPresenter.queryAllMyBBSCollections()
        }else {
            XToast.toastShort(activity, "取消收藏失败！")
        }
        hideCheckTool()
    }
    /**
     * 隐藏选择工具
     */
    fun hideCheckTool() {
        mSelectIds.clear()
        canCheck = false
        (activity as BBSMainActivity).hideCancelButton()
        adapter.notifyDataSetChanged()
    }

    /**
     * 显示选择工具
     */
    private fun showCheckTool() : Boolean {
        mSelectIds.clear()
        canCheck = true
        (activity as BBSMainActivity).showCancelButton()
        adapter.notifyDataSetChanged()
        return true
    }

    fun cancelCollection() {
        mPresenter.cancelSomeCollections(mSelectIds)
    }
    /**
     * 点击item执行的函数
     */
    private fun clickCollectionItem(view: View, child: BBSCollectionSectionVO) {
        if (canCheck){
            val checkBox = view.findViewById<CheckBox>(R.id.check_bbs_main_collection_section_item_choose)
            var isNowCheck : Boolean= checkBox.isChecked
            if (isNowCheck) {
                mSelectIds.remove(child.id)
            }else {
                mSelectIds.add(child.id)
            }
            checkBox.isChecked = !isNowCheck
        }else {
            (activity as BBSMainActivity).enterBBSSection(child.id, child.sectionName)
        }
    }
}
