package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.clouddrive

import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.TypedValue
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_yunpan_myfile.*
import net.muliba.changeskin.FancySkinManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2App
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPViewPagerFragment
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.clouddrive.viewer.PictureViewActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.YunpanRecyclerViewAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.enums.AttachmentType
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.enums.FileOperateType
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.FileBreadcrumbBean
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.PictureViewerData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.YunpanItem
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.edit
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.go
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.TransparentItemDecoration
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.O2DialogSupport
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class CloudDriveMyFileFragment : BaseMVPViewPagerFragment<CloudDriveMyFileContract.View, CloudDriveMyFileContract.Presenter>(), CloudDriveMyFileContract.View {

    override var mPresenter: CloudDriveMyFileContract.Presenter = CloudDriveMyFilePresenter()

    override fun layoutResId(): Int = R.layout.fragment_yunpan_myfile

    companion object {
        val YUNPAN_PREFERENCE_FILE = "YUNPAN_PREFERENCE_FILE"
        val YUNPAN_GRID_BOOLEAN = "YUNPAN_GRID_BOOLEAN"
        val LPWW = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
    }

    val fileList = ArrayList<YunpanItem>()
    val cloudFileListAdapter: YunpanRecyclerViewAdapter by lazy { YunpanRecyclerViewAdapter() }
    val breadcrumbBeans = ArrayList<FileBreadcrumbBean>()//面包屑导航对象
    var isDescOrder = true//按时间排序 倒叙还是顺序
    var fileLevel = 0//默认进入的时候是第一层
    var isChoose = false
    var isGrid = false
    val itemDecoration: TransparentItemDecoration by lazy { TransparentItemDecoration(activity, LinearLayoutManager.VERTICAL) }
    val gridLayoutManager: GridLayoutManager by lazy { GridLayoutManager(activity, 4) }
    val linearLayoutManager: LinearLayoutManager by lazy { LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false) }
    val font: Typeface by lazy { Typeface.createFromAsset(activity.assets, "fonts/fontawesome-webfont.ttf") }
    val viewerData: PictureViewerData = PictureViewerData()


    override fun initUI() {
        val top = FileBreadcrumbBean()
        top.displayName = getString(R.string.title_activity_yunpan)
        top.folderId = ""
        top.level = 0
        breadcrumbBeans.add(top)
        isGrid = O2SDKManager.instance().prefs().getBoolean(YUNPAN_GRID_BOOLEAN, false)
        swipe_refresh_myFile_layout.setColorSchemeResources(R.color.z_color_refresh_scuba_blue,
                R.color.z_color_refresh_red, R.color.z_color_refresh_purple, R.color.z_color_refresh_orange)
        swipe_refresh_myFile_layout.setOnRefreshListener { refreshView("") }
        initRecyclerView()
        btn_my_file_rename.setOnClickListener { menuRename() }
        btn_my_file_delete.setOnClickListener { menuDelete() }
        btn_my_file_share.setOnClickListener { menuShareOrSend(CloudDriveActivity.YUNPAN_FROM_SHARE) }
        btn_my_file_send.setOnClickListener { menuShareOrSend(CloudDriveActivity.YUNPAN_FROM_SEND) }

        MiscUtilK.swipeRefreshLayoutRun(swipe_refresh_myFile_layout, activity)
    }


    override fun lazyLoad() {
        refreshView("")
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_my_file, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        menu?.clear()//先清除已经建好的menu
        if (isChoose) {
            activity.menuInflater.inflate(R.menu.menu_my_file_checkbox, menu)
        } else {
            if (isGrid) {
                activity.menuInflater.inflate(R.menu.menu_my_file_grid, menu)
            } else {
                activity.menuInflater.inflate(R.menu.menu_my_file, menu)
            }
        }
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.yunpan_menu_action_upload_file -> {
                XLog.debug(item.title.toString())
                (activity as CloudDriveActivity).clickUploadFile()
                return true
            }
            R.id.yunpan_menu_create_folder -> {
                XLog.debug(item.title.toString())
                menuCreateFolder()
                return true
            }
            R.id.yunpan_menu_order_by_createTime -> {
                XLog.debug(item.title.toString())
                menuOrderFileListByUpdateTime()
                return true
            }
            R.id.yunpan_menu_choose -> {
                XLog.debug(item.title.toString())
                menuChoose()
                isChoose = true
                return true
            }
            R.id.yunpan_menu_choose_cancel -> {
                XLog.debug(item.title.toString())
                menuChooseCancel()
                isChoose = false
                return true
            }
            R.id.yunpan_menu_grid -> {
                isGrid = true
                O2SDKManager.instance().prefs().edit {
                    putBoolean(YUNPAN_GRID_BOOLEAN, true)
                }
                gridLayout()
                return true
            }
            R.id.yunpan_menu_list -> {
                isGrid = false
                O2SDKManager.instance().prefs().edit {
                    putBoolean(YUNPAN_GRID_BOOLEAN, false)
                }
                listLayout()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun refreshView(message: String) {
        if (!TextUtils.isEmpty(message)) {
            XToast.toastShort(activity, message)
        }
        if (!breadcrumbBeans.isEmpty()) {
            val bean = breadcrumbBeans[breadcrumbBeans.size - 1]//最后一个
            loadFileList(bean.folderId, bean.level)
            loadBreadcrumb()
        }
    }

    override fun loadFileList(list: List<YunpanItem>) {
        fileList.clear()
        fileList.addAll(list)
        setData2ListView()
        swipe_refresh_myFile_layout.isRefreshing = false
    }


    override fun responseErrorMessage(message: String) {
        XToast.toastShort(activity, message)
        swipe_refresh_myFile_layout.isRefreshing = false
    }

    /**
     * 给上层Activity使用 当用户点击返回键的时候的操作
     * @return false 的时候把事件还给Activity
     */
    fun onClickBackBtn(): Boolean {
        if (isChoose) {
            menuChooseCancel()
            return true
        } else {
            if (breadcrumbBeans.size > 1) {
                breadcrumbBeans.removeAt(breadcrumbBeans.size - 1)//删除最后一个
                val bean = breadcrumbBeans[breadcrumbBeans.size - 1]//最后一个
                swipe_refresh_myFile_layout.isRefreshing = true
                loadFileList(bean.folderId, bean.level)
                loadBreadcrumb()
                return true
            }
        }
        return false
    }

    /**
     * 上传文件
     * activity menu 上传文件按钮 点击的时候执行的方法
     *
     * @param filePath
     */
    fun menuUploadFile(filePath: String) {
        XLog.debug("filePath=$filePath")
        try {
            val upFile = File(filePath)
            if (breadcrumbBeans.size > 1) {
                val bean = breadcrumbBeans[breadcrumbBeans.size - 1]//最后一个
                mPresenter.uploadFile2Folder(bean.folderId, upFile)
            } else {
                mPresenter.uploadFile2Top(upFile)
            }
        } catch (e: Exception) {
            XLog.error("", e)
        }
    }

    /**
     * 人员选择反馈结果后执行
     * 发送操作 或 分享操作
     *
     * @param sendList
     * @param type
     */
    fun menuSendResult(sendList: ArrayList<String>, type: FileOperateType) {
        XLog.debug("menuSendResult:$sendList")
        if (cloudFileListAdapter.mSelectIds.size == 1) {
            val index = selectFirstFileId()
            if (index < 0) {
                XLog.error("没有选择数据")
                return
            }
            val item = fileList[index]
            if (item is YunpanItem.FileItem) {
                if (sendList == null || sendList.isEmpty()) {
                    XLog.error("sendlist is null")
                    return
                }
                mPresenter.shareOrSendFile(item.id, sendList, type)
            } else {
                XLog.error("不能对文件夹进行本操作！")
                return
            }
        }
    }


    /**
     * 新建文件夹
     */
    private fun menuCreateFolder() {
        O2DialogSupport.openCustomViewDialog(activity, getString(R.string.yunpan_menu_create_folder), R.layout.dialog_name_modify) {
            dialog ->
            val text = dialog.findViewById<EditText>(R.id.dialog_name_editText_id)
            val content = text.text.toString()
            if (TextUtils.isEmpty(content)) {
                XToast.toastShort(activity, "文件夹名称不能为空！")
            } else {
                createFolderOnLine(content)
                dialog.dismiss()
            }
        }

    }

    private fun createFolderOnLine(folderName: String) {
        val params = HashMap<String, String>()
        params.put("name", folderName)
        if (breadcrumbBeans.size > 1) {
            val bean = breadcrumbBeans[breadcrumbBeans.size - 1]//最后一个
            params.put("superior", bean.folderId)
        } else {
            params.put("superior", "")
        }
        mPresenter.createFolder(params)

    }

    private fun menuChooseCancel() {
        XLog.debug("click menu chooseCancel")
        isChoose = false
        cloudFileListAdapter.clearSelectIds()
        cloudFileListAdapter.isChoose = isChoose
        cloudFileListAdapter.notifyDataSetChanged()
        linear_my_file_operation_bar.gone()
    }

    private fun menuChoose() {
        XLog.debug("click menu choose")
        isChoose = true
        cloudFileListAdapter.clearSelectIds()
        cloudFileListAdapter.isChoose = isChoose
        cloudFileListAdapter.notifyDataSetChanged()
        linear_my_file_operation_bar.visible()
    }

    private fun menuOrderFileListByUpdateTime() {
        // 重新排序list
        if (isDescOrder) {
            XLog.debug("顺序。。。。")
            isDescOrder = false
            val list = fileList.sortedWith(compareBy({it.updateTime}) )
            fileList.clear()
            fileList.addAll(list)
        } else {
            XLog.debug("倒序。。。。")
            isDescOrder = true
            val list = fileList.sortedWith(compareByDescending({it.updateTime}))
            fileList.clear()
            fileList.addAll(list)
        }
        setData2ListView()
    }


    private fun menuShareOrSend(from: String) {
        XLog.debug("click menu menuShareOrSend , from:$from")
        if (cloudFileListAdapter.mSelectIds.size != 1) {
            XToast.toastShort(activity, "请选择一条数据再进行操作！")
            return
        } else {
            val index = selectFirstFileId()
            if (index < 0) {
                XLog.error("没有选择数据")
                return
            }
            val fileItem = fileList[index]
            if (fileItem !is YunpanItem.FileItem) {
                XToast.toastShort(activity, "不能对文件夹进行本操作！")
                return
            }
            (activity as CloudDriveActivity).menuShareOrSend(from)
        }
    }

    private fun menuDelete() {
        XLog.debug("click menu delete")
        if (cloudFileListAdapter.mSelectIds.size > 0) {
            deleteOnLineFile()
        } else {
            XToast.toastShort(activity, "请选择至少一条数据进行删除！")
        }
    }


    private fun menuRename() {
        XLog.debug("click menu rename")
        if (cloudFileListAdapter.mSelectIds.size == 1) {
            val index = selectFirstFileId()
            if (index < 0) {
                XLog.error("没有选择数据")
                return
            }
            renameFile(index)
        } else {
            XToast.toastShort(activity, "请选择一条数据进行重命名！")
        }
    }


    private fun selectFirstFileId(): Int {
        val id = cloudFileListAdapter.mSelectIds.first()
        fileList.mapIndexed { index, it ->
            if (it.id.equals(id)) {
                return index
            }
        }
        return -1
    }

    private fun listLayout() {
        XLog.debug("click menu list layout")
        setData2ListView()
    }

    private fun gridLayout() {
        XLog.debug("click menu grid layout")
        setData2ListView()
    }

    private fun deleteOnLineFile() {
        val id = cloudFileListAdapter.mSelectIds.first()
        fileList.filter { it.id.equals(id) }.map { fileItem ->
            if (fileItem is YunpanItem.FolderItem) {
                mPresenter.deleteFile(AttachmentType.FOLDER, fileItem.id)
            } else {
                mPresenter.deleteFile(AttachmentType.FILE, fileItem.id)
            }
        }
    }

    private fun renameFile(position: Int) {
        val item = fileList[position]
        val dialog = O2DialogSupport.openCustomViewDialog(activity, getString(R.string.yunpan_rename), R.layout.dialog_name_modify) {
            dialog ->
            val text = dialog.findViewById<EditText>(R.id.dialog_name_editText_id)
            val content = text.text.toString()
            if (TextUtils.isEmpty(content)) {
                XToast.toastShort(activity, "名称不能为空！")
            } else {
                if (item is YunpanItem.FolderItem) {
                    mPresenter.reNameFolder(item.id, content)
                } else {
                    mPresenter.reNameFile(item.id, content)
                }
                dialog.dismiss()
            }
        }
        val text = dialog.findViewById<EditText>(R.id.dialog_name_editText_id)
        text.setText(item.name)
    }

    private fun setData2ListView() {
        viewerData.clearItems()
        if (!fileList.isEmpty()) {
            fileList.map {
                if (it is  YunpanItem.FileItem) {
                    if (FileExtensionHelper.isImageFromFileExtension(it.extension)) {//使用图片浏览器
                        viewerData.addItem(it.name, it.id)
                    }
                }
            }
            recycler_view_yunpan_myfile_list.visible()
            tv_yunpan_myfile_empty.gone()
        } else {
            tv_yunpan_myfile_empty.visible()
            recycler_view_yunpan_myfile_list.gone()
        }
        changeRecyclerViewLayout()
        cloudFileListAdapter.isGrid = (isGrid)
        cloudFileListAdapter.isChoose = (isChoose)
        cloudFileListAdapter.items.clear()
        cloudFileListAdapter.items.addAll(fileList)
        cloudFileListAdapter.notifyDataSetChanged()
    }

    /**
     * 加载面包屑导航
     */
    private fun loadBreadcrumb() {
        breadcrumb_id.removeAllViews()
        breadcrumbBeans.mapIndexed { index, fileBreadcrumbBean ->
            val breadcrumbTitle = TextView(activity)
            breadcrumbTitle.text = fileBreadcrumbBean.displayName
            breadcrumbTitle.tag = fileBreadcrumbBean
            breadcrumbTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
            breadcrumbTitle.layoutParams = LPWW
            if (index == breadcrumbBeans.size - 1) {
                breadcrumbTitle.setTextColor(FancySkinManager.instance().getColor(activity, R.color.z_color_primary))
                breadcrumb_id.addView(breadcrumbTitle)
            } else {
                breadcrumbTitle.setTextColor(FancySkinManager.instance().getColor(activity, R.color.z_color_text_primary_dark))
                breadcrumbTitle.setOnClickListener { v -> onClickBreadcrumb(v as TextView) }
                breadcrumb_id.addView(breadcrumbTitle)
                val arrow = TextView(activity)
                val lp = LPWW
                lp.setMargins(8, 0, 8, 0)
                arrow.layoutParams = lp
                arrow.text = getString(R.string.fa_angle_right)
                arrow.setTextColor(FancySkinManager.instance().getColor(activity, R.color.z_color_text_primary_dark))
                arrow.typeface = font
                arrow.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                breadcrumb_id.addView(arrow)
            }
        }
    }

    private fun onClickBreadcrumb(textView: TextView) {
        val bean = textView.tag as FileBreadcrumbBean
        var newLevel = 0
        breadcrumbBeans.mapIndexed { index, fileBreadcrumbBean ->
            if (bean.equals(fileBreadcrumbBean)) {
                newLevel = index
                //清空listview
                swipe_refresh_myFile_layout.isRefreshing = true
                loadFileList(fileBreadcrumbBean.folderId, fileBreadcrumbBean.level)
            }
        }
        //处理breadcrumbBeans 把多余的去掉
        if (breadcrumbBeans.size > (newLevel + 1)) {
            breadcrumbBeans.mapIndexed { index, fileBreadcrumbBean ->
                if (index > newLevel) {
                    breadcrumbBeans.remove(fileBreadcrumbBean)
                }
            }
        }
        loadBreadcrumb()
    }

    private fun loadFileList(id: String, newLevel: Int) {
        fileLevel = newLevel
        mPresenter.loadFileList(id)
    }

    private fun refreshOperationBar() {
        if (cloudFileListAdapter.mSelectIds.isEmpty()) {
            btn_my_file_rename.isEnabled = false
            btn_my_file_delete.isEnabled = false
            btn_my_file_share.isEnabled = false
            btn_my_file_send.isEnabled = false
        } else {
            btn_my_file_rename.isEnabled = true
            btn_my_file_delete.isEnabled = true
            btn_my_file_share.isEnabled = true
            btn_my_file_send.isEnabled = true
        }
    }

    private fun changeRecyclerViewLayout() {
        if (isGrid) {
            recycler_view_yunpan_myfile_list.removeItemDecoration(itemDecoration)
            recycler_view_yunpan_myfile_list.addItemDecoration(itemDecoration)
            recycler_view_yunpan_myfile_list.layoutManager = gridLayoutManager
        } else {
            recycler_view_yunpan_myfile_list.removeItemDecoration(itemDecoration)
            recycler_view_yunpan_myfile_list.layoutManager = linearLayoutManager
        }
    }


    private fun initRecyclerView() {
        cloudFileListAdapter.isGrid = isGrid
        cloudFileListAdapter.isChoose = isChoose
        cloudFileListAdapter.items.clear()
        cloudFileListAdapter.items.addAll(fileList)
        recycler_view_yunpan_myfile_list.adapter = cloudFileListAdapter
        changeRecyclerViewLayout()
        recycler_view_yunpan_myfile_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val topRowVerticalPosition = recycler_view_yunpan_myfile_list?.getChildAt(0)?.top ?: 0
                swipe_refresh_myFile_layout.isEnabled = topRowVerticalPosition >= 0
            }
        })
        cloudFileListAdapter.onItemClickListener = object : YunpanRecyclerViewAdapter.OnItemClickListener {
            override fun onItemClick(view: View, position: Int) {
                if (isChoose) {
                    val checkBox = view.findViewById<CheckBox>(R.id.file_list_choose_id)
                    val isCheck = checkBox.isChecked
                    XLog.debug("first check:" + isCheck)
                    checkBox.isChecked = !isCheck
                    cloudFileListAdapter.toggleCheckItem(position, !isCheck)
                    refreshOperationBar()
                } else {
                    val fileItem = fileList[position]
                    when (fileItem) {
                        is YunpanItem.FolderItem -> {
                            //进入下一层
                            XLog.debug("点击文件夹：" + fileItem.name)
                            val newLevel = fileLevel + 1
                            val newBean = FileBreadcrumbBean()
                            newBean.displayName = fileItem.name
                            newBean.folderId = fileItem.id
                            newBean.level = newLevel
                            breadcrumbBeans.add(newBean)
                            swipe_refresh_myFile_layout.isRefreshing = true
                            loadFileList(fileItem.id, newLevel)
                            loadBreadcrumb()
                        }
                        is YunpanItem.FileItem -> {
                            //展现文件  各种类型的文件如何展现？
                            XLog.debug("fileClick, 点击文件：" + fileItem.name)
                            if (FileExtensionHelper.isImageFromFileExtension(fileItem.extension)) {//使用图片浏览器
                                //图片。。。。。。
                                val bundle = Bundle()
                                bundle.putStringArrayList(PictureViewerData.TRANSFER_FILE_ID_KEY, viewerData.fileIdList)
                                bundle.putStringArrayList(PictureViewerData.TRANSFER_TITLE_KEY, viewerData.titleList)
                                bundle.putString(PictureViewerData.TRANSFER_CURRENT_FILE_ID_KEY, fileItem.id)
                                activity.go<PictureViewActivity>(bundle)
                            } else {
                                (activity as CloudDriveActivity).openYunPanFile(fileItem.id, fileItem.name)
                            }
                        }
                    }
                }
            }
        }
    }


}
