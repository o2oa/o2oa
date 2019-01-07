package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.clouddrive

import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_yunpan_copperation_file.*
import net.muliba.changeskin.FancySkinManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.ViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPViewPagerFragment
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.clouddrive.viewer.PictureViewActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.PictureViewerData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.CooperationItem
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.FileExtensionHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.MiscUtilK
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.friendlyFileLength
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.go
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible


class CloudDriveCooperationFileFragment : BaseMVPViewPagerFragment<CloudDriveCooperationFileContract.View, CloudDriveCooperationFileContract.Presenter>(), CloudDriveCooperationFileContract.View {

    override var mPresenter: CloudDriveCooperationFileContract.Presenter = CloudDriveCooperationFilePresenter()

    override fun layoutResId(): Int = R.layout.fragment_yunpan_copperation_file


    companion object {
        val COOPERATION_TYPE_KEY = "COOPERATION_TYPE_KEY"
        val COOPERATION_TYPE_RECEIVE_FILE = 0
        val COOPERATION_TYPE_SHARE_FILE = 1
        val LPWW = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
    }

    private val font: Typeface  by lazy { Typeface.createFromAsset(activity.assets, "fonts/fontawesome-webfont.ttf") }
    /**
     * 分享文件夹名称，为空的时候展现 文件夹列表 点击某个文件夹 展现文件夹下面的文件列表。
     */
    private var folderName: String? = null
    private var cooperationType: Int = COOPERATION_TYPE_SHARE_FILE
    private var adapter: CommonAdapter<CooperationItem>? = null
    private var itemList: ArrayList<CooperationItem> = ArrayList()
    private val breadcrumbList: ArrayList<String> = ArrayList()
    private val viewerData: PictureViewerData = PictureViewerData()

    override fun initUI() {
        cooperationType = arguments?.getInt(COOPERATION_TYPE_KEY, COOPERATION_TYPE_SHARE_FILE) ?: COOPERATION_TYPE_SHARE_FILE
        when (cooperationType) {
            COOPERATION_TYPE_SHARE_FILE -> breadcrumbList.add(getString(R.string.tab_yunpan_share_file))
            else -> breadcrumbList.add(getString(R.string.tab_yunpan_recive_file))
        }
        swipe_refresh_cooperation_layout.setColorSchemeResources(R.color.z_color_refresh_scuba_blue,
                R.color.z_color_refresh_red, R.color.z_color_refresh_purple, R.color.z_color_refresh_orange)
        swipe_refresh_cooperation_layout.setOnRefreshListener { refreshView() }

        initAdapter()
        MiscUtilK.swipeRefreshLayoutRun(swipe_refresh_cooperation_layout, activity)
    }

    override fun lazyLoad() {
        refreshView()
    }

    override fun setFileList(files: List<CooperationItem.FileItem>) {
        swipe_refresh_cooperation_layout.isRefreshing = false
        itemList.clear()
        viewerData.clearItems()
        if (files.isEmpty()){
            tv_cooperation_file_empty.visible()
            lv_cooperation_file_list.gone()
        }else {
            itemList.addAll(files)
            files.map {
                if (FileExtensionHelper.isImageFromFileExtension(it.extension)) {
                    viewerData.addItem(it.name, it.id)
                }
            }
            tv_cooperation_file_empty.gone()
            lv_cooperation_file_list.visible()
        }
        adapter?.notifyDataSetChanged()
    }
    override fun onException(message: String) {
        swipe_refresh_cooperation_layout.isRefreshing = false
        XToast.toastShort(activity, message)
        itemList.clear()
        viewerData.clearItems()
        tv_cooperation_file_empty.visible()
        lv_cooperation_file_list.gone()
        adapter?.notifyDataSetChanged()
    }

    override fun setFolderList(folders: List<CooperationItem.FolderItem>) {
        swipe_refresh_cooperation_layout.isRefreshing = false
        itemList.clear()
        viewerData.clearItems()
        if (folders.isEmpty()) {
            tv_cooperation_file_empty.visible()
            lv_cooperation_file_list.gone()
        }else {
            itemList.addAll(folders)
            tv_cooperation_file_empty.gone()
            lv_cooperation_file_list.visible()
        }
        adapter?.notifyDataSetChanged()
    }

    /**
     * 给上层Activity使用 当用户点击返回键的时候的操作
     * @return false 的时候把事件还给Activity
     */
    fun onClickBackBtn(): Boolean {
        if (!TextUtils.isEmpty(folderName)) {
            breadcrumbList.remove(folderName)
            folderName = null
            refreshView()
            return true
        } else {
            return false
        }
    }

    fun refreshView() {
        when (cooperationType) {
            COOPERATION_TYPE_SHARE_FILE -> {
                if (!TextUtils.isEmpty(folderName)) {
                    mPresenter.loadShareFileList(folderName!!)
                } else {
                    mPresenter.loadShareFolderList()
                }
            }
            COOPERATION_TYPE_RECEIVE_FILE -> {
                if (!TextUtils.isEmpty(folderName)) {
                    mPresenter.loadReceiveFileList(folderName!!)
                } else {
                    mPresenter.loadReceiveFolderList()
                }
            }
        }
        refreshBreadcrumb()
    }

    fun refreshBreadcrumb() {
        linear_cooperation_file_reply.removeAllViews()
        breadcrumbList.mapIndexed { index, s ->
            val breadcrumbTv = TextView(activity)
            breadcrumbTv.text = s
            breadcrumbTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
            breadcrumbTv.layoutParams = LPWW
            if (index == breadcrumbList.size-1) {
                breadcrumbTv.setTextColor(FancySkinManager.instance().getColor(activity, R.color.z_color_primary))
                linear_cooperation_file_reply.addView(breadcrumbTv)
            }else {
                breadcrumbTv.setTextColor(FancySkinManager.instance().getColor(activity, R.color.z_color_text_primary_dark))
                linear_cooperation_file_reply.addView(breadcrumbTv)
                breadcrumbTv.setOnClickListener {
                    if (!TextUtils.isEmpty(folderName)) {
                        breadcrumbList.remove(folderName)
                        folderName = null
                        refreshView()
                    }
                }
                val arrowTv = TextView(activity)
                val layout = LPWW
                layout.setMargins(8, 0, 8, 0)
                arrowTv.layoutParams = layout
                arrowTv.text = getString(R.string.fa_angle_right)
                arrowTv.typeface = font
                arrowTv.setTextColor(FancySkinManager.instance().getColor(activity, R.color.z_color_text_primary_dark))
                arrowTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                linear_cooperation_file_reply.addView(arrowTv)
            }
        }
    }

    fun initAdapter() {
        adapter = object : CommonAdapter<CooperationItem>(activity, itemList, R.layout.item_file_list) {
            override fun convert(holder: ViewHolder, t: CooperationItem) {
                when(t) {
                    is CooperationItem.FolderItem -> {
                        holder.setImageViewResource(R.id.file_list_icon_id, R.mipmap.icon_folder)
                                .setText(R.id.file_list_name_id, t.name)
                                .setText(R.id.tv_file_list_time, t.count.toString() +" 项")
                        val expand = holder.getView<ImageView>(R.id.image_file_list_arrow)
                        expand.visible()
                        val size = holder.getView<TextView>(R.id.tv_file_list_size)
                        size.gone()
                    }
                    is CooperationItem.FileItem -> {
                        val resId = FileExtensionHelper.getImageResourceByFileExtension(t.extension)
                        holder.setImageViewResource(R.id.file_list_icon_id, resId)
                                .setText(R.id.file_list_name_id, t.name)
                                .setText(R.id.tv_file_list_time, t.updateTime)
                        val expand = holder.getView<ImageView>(R.id.image_file_list_arrow)
                        expand.gone()
                        val size = holder.getView<TextView>(R.id.tv_file_list_size)
                        size.visible()
                        size.text = t.length.friendlyFileLength()
                    }
                }
            }
        }

        lv_cooperation_file_list.adapter = adapter
        lv_cooperation_file_list.setOnItemClickListener { _, _, position, _ ->
            var item  = itemList[position]
            when(item){
                is CooperationItem.FolderItem -> {
                    folderName = item.value
                    breadcrumbList.add(item.value)
                    refreshView()
                }
                is CooperationItem.FileItem -> {
                    XLog.debug("点击文件：" + item.name)
                    if (FileExtensionHelper.isImageFromFileExtension(item.extension)) {//使用图片浏览器
                        val bundle = Bundle()
                        bundle.putStringArrayList(PictureViewerData.TRANSFER_FILE_ID_KEY, viewerData.fileIdList)
                        bundle.putStringArrayList(PictureViewerData.TRANSFER_TITLE_KEY, viewerData.titleList)
                        bundle.putString(PictureViewerData.TRANSFER_CURRENT_FILE_ID_KEY, item.id)
                        activity.go<PictureViewActivity>(bundle)
                    } else {
                        val activity = activity as CloudDriveActivity
                        activity.openYunPanFile(item.id, item.fileName)
                    }
                }
            }
        }
    }

}
