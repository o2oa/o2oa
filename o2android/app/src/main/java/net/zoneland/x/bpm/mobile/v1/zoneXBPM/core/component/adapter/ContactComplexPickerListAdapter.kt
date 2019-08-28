package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.NewContactListVO
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.inflate

/**
 * Created by fancy on 2017/4/25.
 */

abstract class ContactComplexPickerListAdapter(var items: ArrayList<NewContactListVO>) : RecyclerView.Adapter<CommonRecyclerViewHolder>() {

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: CommonRecyclerViewHolder?, position: Int) {
        when(items[position]) {
            is NewContactListVO.Department -> {
                val department = items[position] as NewContactListVO.Department
                bindDepartment(holder, department, position)
                holder?.convertView?.setOnClickListener { view -> clickDepartment(view, department) }
            }
            else -> {
                val identity = items[position] as NewContactListVO.Identity
                bindIdentity(holder, identity, position)
                holder?.convertView?.setOnClickListener { view -> clickIdentity(view, identity) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CommonRecyclerViewHolder {
        return when(viewType) {
            0 -> CommonRecyclerViewHolder(parent?.inflate(R.layout.item_contact_complex_picker_org))
            else -> CommonRecyclerViewHolder(parent?.inflate(R.layout.item_contact_complex_picker_identity))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return  when(items[position]){
            is NewContactListVO.Department -> 0
            else -> 1
        }
    }


    abstract fun bindDepartment(hold: CommonRecyclerViewHolder?, department: NewContactListVO.Department, position: Int)
    abstract fun clickDepartment(view:View, department: NewContactListVO.Department)
    abstract fun bindIdentity(hold: CommonRecyclerViewHolder?, identity: NewContactListVO.Identity, position: Int)
    abstract fun clickIdentity(view:View, identity: NewContactListVO.Identity)
}