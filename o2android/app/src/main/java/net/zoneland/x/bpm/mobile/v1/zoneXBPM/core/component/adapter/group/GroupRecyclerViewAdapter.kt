package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.group

import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.ViewGroup
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.inflate

/**
 * Created by fancy on 2017/3/30.
 */

abstract class GroupRecyclerViewAdapter<T, R>(val groups:List<Group<T, R>>, val headerResourceId:Int, val childResourceId:Int) :
        RecyclerView.Adapter<CommonRecyclerViewHolder>() {


    companion object {
        val ITEM_TYPE_HEAD = 0
        val ITEM_TYPE_CHILD = 1
    }
    //每个recyclerview item对应的类型
    private var items: SparseArray<Int> = SparseArray()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonRecyclerViewHolder {
        return when(viewType) {
            ITEM_TYPE_HEAD -> CommonRecyclerViewHolder(parent.inflate(headerResourceId))
            else -> CommonRecyclerViewHolder(parent.inflate(childResourceId))
        }
    }
    override fun onBindViewHolder(holder: CommonRecyclerViewHolder, position: Int) {
        var currentPosition = 0
        groups.forEach {
            val childrenTotal = it.children.size
            when(position) {
                //表示position>=currentPosition且position<=currentPosition+childrenTotal 则向下执行
                in currentPosition..(currentPosition+childrenTotal) -> {
                    if (currentPosition == position) {
                        onBindHeaderViewHolder(holder, it.header, position)
                        return
                    }else {
                        val childIndex = position - currentPosition - 1
                        onBindChildViewHolder(holder, it.children[childIndex], position)
                        return
                    }
                }
            }
            currentPosition += childrenTotal + 1
        }

    }

    override fun getItemViewType(position: Int): Int {
        return items[position]
    }
    override fun getItemCount(): Int {
        var count:Int = 0
        groups.map {
            items.put(count, ITEM_TYPE_HEAD)
            count++
            it.children.map {
                items.put(count, ITEM_TYPE_CHILD)
                count++
            }
        }
        return count
    }

    abstract fun onBindHeaderViewHolder(holder: CommonRecyclerViewHolder, header: T, position: Int)

    abstract fun onBindChildViewHolder(holder: CommonRecyclerViewHolder, child: R, position: Int)


}