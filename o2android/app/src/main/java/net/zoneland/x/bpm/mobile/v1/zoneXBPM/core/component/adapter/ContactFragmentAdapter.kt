package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.ContactFragmentVO
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.inflate

/**
 * Created by fancy on 2017/4/24.
 */

abstract class ContactFragmentAdapter(var items: List<ContactFragmentVO>): RecyclerView.Adapter<CommonRecyclerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CommonRecyclerViewHolder {
        return when(viewType) {
            0 -> CommonRecyclerViewHolder(parent?.inflate(R.layout.item_contact_fragment_header))
            1,2 -> CommonRecyclerViewHolder(parent?.inflate(R.layout.item_contact_fragment_body))
            3 -> CommonRecyclerViewHolder(parent?.inflate(R.layout.item_contact_fragment_body_collect))
            else -> CommonRecyclerViewHolder(parent?.inflate(R.layout.item_contact_fragment_body_group))
        }
    }

    override fun onBindViewHolder(holder: CommonRecyclerViewHolder?, position: Int) {
        when(items[position]){
            is ContactFragmentVO.GroupHeader -> {
                val header = items[position] as ContactFragmentVO.GroupHeader
                holder?.setText(R.id.tv_item_contact_fragment_header_title, header.name)
                        ?.setImageViewResource(R.id.image_item_contact_fragment_header_icon, header.resId)
            }
            is ContactFragmentVO.MyDepartment -> {
                val body = items[position] as ContactFragmentVO.MyDepartment
                bindMyDepartment(body, holder)
                holder?.convertView?.setOnClickListener { clickMyDepartment(body) }
            }
            is ContactFragmentVO.MyCompany -> {
                val body = items[position] as ContactFragmentVO.MyCompany
                bindMyCompany(body, holder)
                holder?.convertView?.setOnClickListener { clickMyCompany(body) }
            }
            is ContactFragmentVO.MyCollect -> {
                val body = items[position] as ContactFragmentVO.MyCollect
                bindMyCollect(body, holder)
                holder?.convertView?.setOnClickListener { clickMyCollect(body) }
            }
            is ContactFragmentVO.MyGroup -> {
                val body = items[position] as ContactFragmentVO.MyGroup
                bindMyGroup(body, holder)
                holder?.convertView?.setOnClickListener { clickMyGroup(body) }
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return when(items[position]) {
            is ContactFragmentVO.GroupHeader -> 0
            is ContactFragmentVO.MyDepartment -> 1
            is ContactFragmentVO.MyCompany -> 2
            is ContactFragmentVO.MyCollect -> 3
            else -> 4
        }
    }

    abstract fun bindMyDepartment(department: ContactFragmentVO.MyDepartment, holder: CommonRecyclerViewHolder?)
    abstract fun clickMyDepartment(department: ContactFragmentVO.MyDepartment)
    abstract fun bindMyCompany(company: ContactFragmentVO.MyCompany, holder: CommonRecyclerViewHolder?)
    abstract fun clickMyCompany(company: ContactFragmentVO.MyCompany)
    abstract fun bindMyCollect(collect: ContactFragmentVO.MyCollect, holder: CommonRecyclerViewHolder?)
    abstract fun clickMyCollect(collect: ContactFragmentVO.MyCollect)
    abstract fun bindMyGroup(group: ContactFragmentVO.MyGroup, holder: CommonRecyclerViewHolder?)
    abstract fun clickMyGroup(group: ContactFragmentVO.MyGroup)

}