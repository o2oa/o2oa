package net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.YunpanItem
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.FileExtensionHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog

/**
 * Created by fancy on 2017/3/29.
 */

class YunpanRecyclerViewAdapter : RecyclerView.Adapter<CommonRecyclerViewHolder>() {


    companion object {
        val ITEM_VIEW_TYPE_GRID = 0
        val ITEM_VIEW_TYPE_LIST = 1
    }

    var items = ArrayList<YunpanItem>()
    var isGrid = false// 网格还是列表
    var isChoose = false
    var onItemClickListener: OnItemClickListener? = null
    val mSelectIds: HashSet<String> = HashSet()



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonRecyclerViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ITEM_VIEW_TYPE_GRID -> CommonRecyclerViewHolder(inflater.inflate(R.layout.item_file_grid_list, parent, false))
            else -> CommonRecyclerViewHolder(inflater.inflate(R.layout.item_file_list, parent, false))
        }
    }

    override fun onBindViewHolder(holder: CommonRecyclerViewHolder, position: Int) {
        val item: YunpanItem = items[position]
        when(item){
            is YunpanItem.FolderItem -> {
                holder.setImageViewResource(R.id.file_list_icon_id, R.mipmap.icon_folder)
                        .setText(R.id.file_list_name_id, item.name)
                        .setText(R.id.tv_file_list_time, item.updateTime)
                val expand = holder.getView<ImageView>(R.id.image_file_list_arrow)
                expand.visibility = View.VISIBLE
                if (isGrid) {
                    expand.visibility = View.GONE
                } else {
                    expand.visibility = View.VISIBLE
                }
                val size = holder.getView<TextView>(R.id.tv_file_list_size)
                size.visibility = View.GONE
            }
            is YunpanItem.FileItem -> {
                val resId = FileExtensionHelper.getImageResourceByFileExtension(item.extension)
                holder.setImageViewResource(R.id.file_list_icon_id, resId)
                        .setText(R.id.file_list_name_id, item.name)
                        .setText(R.id.tv_file_list_time, item.updateTime)
                val expand = holder.getView<ImageView>(R.id.image_file_list_arrow)
                expand.visibility = View.GONE
                val size = holder.getView<TextView>(R.id.tv_file_list_size)
                if (isGrid){
                    size.visibility = View.GONE
                }else {
                    size.visibility = View.VISIBLE
                    size.text = item.length
                }

            }
        }
        val checkBox = holder.getView<CheckBox>(R.id.file_list_choose_id)
        if (isChoose) {
            XLog.debug("checkBox, setChecked:false ........" + position)
            checkBox.isChecked = false
            checkBox.visibility = View.VISIBLE
            checkBox.setOnCheckedChangeListener { _, isChecked -> toggleCheckItem(position, isChecked) }
        } else {
            checkBox.visibility = View.GONE
        }
        if (mSelectIds.contains(item.id)) {
            checkBox.isChecked = true
        }
        //bind item click
        holder.itemView.setOnClickListener { onItemClickListener?.onItemClick(holder.itemView, position) }
    }


    override fun getItemCount(): Int = items.size

    override fun getItemViewType(position: Int): Int {
        return when (isGrid) {
            true -> ITEM_VIEW_TYPE_GRID
            false -> ITEM_VIEW_TYPE_LIST
        }
    }

    fun clearSelectIds() {
        mSelectIds.clear()
    }

    fun toggleCheckItem(position: Int, checked: Boolean) {
        XLog.debug("toggleCheckItem, position:$position, checked:$checked")
        if (checked) {
            mSelectIds.add(items[position].id)
        }else{
            mSelectIds.remove(items[position].id)
        }
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }
}