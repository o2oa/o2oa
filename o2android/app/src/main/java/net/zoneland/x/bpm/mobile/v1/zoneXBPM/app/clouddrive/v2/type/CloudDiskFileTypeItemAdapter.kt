package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.clouddrive.v2.type

import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.yunpan.FileJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.FileExtensionHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.friendlyFileLength
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.imageloader.O2ImageLoaderManager
import org.jetbrains.anko.dip


class CloudDiskFileTypeItemAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private val ITEM_VIEW_TYPE_LINEAR = 0
    private val ITEM_VIEW_TYPE_GRID = 1
    private val FOOTER_VIEW_TYPE = 2


    val datas =  ArrayList<FileJson>()
    var isGrid = false
    var onItemClickListener: OnItemClickListener? = null

    private var footer: View? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater: LayoutInflater = LayoutInflater.from(parent?.context)
        return when(viewType) {
            ITEM_VIEW_TYPE_GRID -> CommonRecyclerViewHolder(inflater.inflate(R.layout.item_file_grid_list_v2, parent, false))
            FOOTER_VIEW_TYPE -> FooterViewHolder(footer!!)
            else -> CommonRecyclerViewHolder(inflater.inflate(R.layout.item_file_list_v2, parent, false))
        }
    }

    override fun getItemCount(): Int = datas.size + getFooterCount()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CommonRecyclerViewHolder) {
            val item = datas[position]
            if (isGrid) {
                val resId = FileExtensionHelper.getImageResourceByFileExtension(item.extension)
                holder.setImageViewResource(R.id.file_list_icon_id, resId)
                        .setText(R.id.file_list_name_id, item.name)
                val imageView = holder.getView<ImageView>(R.id.file_list_icon_id)
                val size = holder.convertView.dip(40)
                val url = APIAddressHelper.instance().getCloudDiskImageUrl(item.id, size, size)
                O2ImageLoaderManager.instance().showImage(imageView, url)
                ViewCompat.setTransitionName(imageView, url)
            }else {
                val resId = FileExtensionHelper.getImageResourceByFileExtension(item.extension)
                holder.setImageViewResource(R.id.file_list_icon_id, resId)
                        .setText(R.id.file_list_name_id, item.name)
                        .setText(R.id.tv_file_list_time, item.updateTime)
                val size = holder.getView<TextView>(R.id.tv_file_list_size)
                size.visibility = View.VISIBLE
                size.text = item.length.friendlyFileLength()
                val checkBox = holder.getView<CheckBox>(R.id.file_list_choose_id)
                checkBox.gone()
                if (position == datas.size - 1) {
                    holder.getView<View>(R.id.view_file_list_split).gone()
                }else {
                    holder.getView<View>(R.id.view_file_list_split).visible()
                }
            }

            if (onItemClickListener != null) {
                holder.itemView.setOnClickListener { v -> onItemClickListener?.onItemClick(v, item) }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getFooterCount() > 0) {
            if (position + 1 == itemCount) {
                FOOTER_VIEW_TYPE
            } else {
                if (isGrid) {
                    ITEM_VIEW_TYPE_GRID
                }else {
                    ITEM_VIEW_TYPE_LINEAR
                }
            }
        } else {
            if (isGrid) {
                ITEM_VIEW_TYPE_GRID
            }else {
                ITEM_VIEW_TYPE_LINEAR
            }
        }
    }

    /**
     * 添加底部
     * @param view
     */
    fun addFooter(view: View) {
        footer = view
        this.notifyDataSetChanged()
    }

    /**
     * 删除头部
     * @param view
     */
    fun removeFooter(view: View) {
        footer = null
        this.notifyDataSetChanged()
    }


    private fun getFooterCount(): Int {
        return if (footer != null) {
            1
        } else 0
    }


    internal inner class FooterViewHolder(view: View) : RecyclerView.ViewHolder(view)

    interface OnItemClickListener {
        fun onItemClick(view: View, item: FileJson)
    }
}