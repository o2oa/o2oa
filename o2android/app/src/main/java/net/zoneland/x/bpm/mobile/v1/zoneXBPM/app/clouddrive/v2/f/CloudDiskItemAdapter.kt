package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.clouddrive.v2.f

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.CloudDiskItem
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.FileExtensionHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.friendlyFileLength
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible


class CloudDiskItemAdapter : RecyclerView.Adapter<CommonRecyclerViewHolder>() {

    var items = ArrayList<CloudDiskItem>()
    val mSelectIds: HashSet<String> = HashSet()
    var onItemClickListener: OnItemClickListener? = null
    var onCheckChangeListener: OnCheckChangeListener?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonRecyclerViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent?.context)
        return CommonRecyclerViewHolder(inflater.inflate(R.layout.item_file_list_v2, parent, false))
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: CommonRecyclerViewHolder, position: Int) {
        val item = items[position]
        when (item) {
            is CloudDiskItem.FolderItem -> {
                holder.setImageViewResource(R.id.file_list_icon_id, R.mipmap.icon_folder)
                        .setText(R.id.file_list_name_id, item.name)
                        .setText(R.id.tv_file_list_time, item.updateTime)
                val size = holder.getView<TextView>(R.id.tv_file_list_size)
                size.visibility = View.GONE
            }
            is CloudDiskItem.FileItem -> {
                val resId = FileExtensionHelper.getImageResourceByFileExtension(item.extension)
                holder.setImageViewResource(R.id.file_list_icon_id, resId)
                        .setText(R.id.file_list_name_id, item.name)
                        .setText(R.id.tv_file_list_time, item.updateTime)
                val size = holder.getView<TextView>(R.id.tv_file_list_size)
                size.visibility = View.VISIBLE
                size.text = item.length.friendlyFileLength()
            }
        }
        val checkBox = holder.getView<CheckBox>(R.id.file_list_choose_id)
        checkBox.isChecked = false
        checkBox.visibility = View.VISIBLE
        checkBox.setOnCheckedChangeListener { _, isChecked -> toggleCheckItem(position, isChecked) }
        if (mSelectIds.contains(item.id)) {
            checkBox.isChecked = true
        }
        if (position == items.size - 1) {
            holder.getView<View>(R.id.view_file_list_split).gone()
        }else {
            holder.getView<View>(R.id.view_file_list_split).visible()
        }

        holder.itemView.setOnClickListener {
            when(item) {
                is CloudDiskItem.FileItem -> onItemClickListener?.onFileClick(item)
                is CloudDiskItem.FolderItem -> onItemClickListener?.onFolderClick(item)
            }
        }
    }

    fun clearSelectIds() {
        mSelectIds.clear()
    }

    private fun toggleCheckItem(position: Int, checked: Boolean) {
        XLog.debug("toggleCheckItem, position:$position, checked:$checked")
        if (checked) {
            mSelectIds.add(items[position].id)
        }else{
            mSelectIds.remove(items[position].id)
        }
        onCheckChangeListener?.onChange()
    }

    interface OnItemClickListener {
        fun onFolderClick(folder: CloudDiskItem.FolderItem)
        fun onFileClick(file: CloudDiskItem.FileItem)
    }
    interface OnCheckChangeListener {
        fun onChange()
    }
}