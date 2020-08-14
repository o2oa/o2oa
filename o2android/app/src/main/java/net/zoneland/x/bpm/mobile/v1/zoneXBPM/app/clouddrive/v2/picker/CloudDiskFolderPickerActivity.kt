package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.clouddrive.v2.picker

import android.app.Activity
import android.graphics.Typeface
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.util.TypedValue
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.wugang.activityresult.library.ActivityResult
import kotlinx.android.synthetic.main.activity_cloud_disk_folder_picker.*
import net.muliba.changeskin.FancySkinManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecycleViewAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.FileBreadcrumbBean
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.CloudDiskItem
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast


class CloudDiskFolderPickerActivity : BaseMVPActivity<CloudDiskFolderPickerContract.View, CloudDiskFolderPickerContract.Presenter>(), CloudDiskFolderPickerContract.View {
    override var mPresenter: CloudDiskFolderPickerContract.Presenter = CloudDiskFolderPickerPresenter()

    override fun layoutResId(): Int = R.layout.activity_cloud_disk_folder_picker


    private var items = ArrayList<CloudDiskItem>()
    companion object {
        val RESULT_BACK_KEY = "RESULT_BACK_KEY"
        val ARG_FOLDER_ID_KEY = "ARG_FOLDER_ID_KEY"
        val LPWW = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        fun pickFolder(activity: Activity, result: (String) -> Unit) {
            ActivityResult.of(activity)
                    .className(CloudDiskFolderPickerActivity::class.java)
                    .greenChannel()
                    .forResult { resultCode, data ->
                        if (resultCode == Activity.RESULT_OK) {
                            val r = data.getStringExtra(RESULT_BACK_KEY)
                            if (r!=null) {
                                result(r)
                            }
                        }
                    }
        }

    }
    private val font: Typeface by lazy { Typeface.createFromAsset(assets, "fonts/fontawesome-webfont.ttf") }
    private val breadcrumbBeans = ArrayList<FileBreadcrumbBean>()//面包屑导航对象
    private var fileLevel = 0//默认进入的时候是第一层


    override fun afterSetContentView(savedInstanceState: Bundle?) {
        setupToolBar("目录选择", setupBackButton = true, isCloseBackIcon = true)

        if (breadcrumbBeans.isEmpty()) {
            val top = FileBreadcrumbBean()
            top.displayName = getString(R.string.yunpan_all_file)
            top.folderId = ""
            top.level = 0
            breadcrumbBeans.add(top)
        }

        rv_folder_picker.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_folder_picker.adapter = adapter
        adapter.setOnItemClickListener { _, position ->
            //进入下一层
            val folder = items[position]
            XLog.debug("点击文件夹：" + folder.name)
            val newLevel = fileLevel + 1
            val newBean = FileBreadcrumbBean()
            newBean.displayName = folder.name
            newBean.folderId = folder.id
            newBean.level = newLevel
            breadcrumbBeans.add(newBean)
            loadFileList(folder.id, newLevel)
            loadBreadcrumb()
        }
        refreshView()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_cloud_disk_picker, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.menu_cloud_disk_picker_top) {
            backResult("")
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (breadcrumbBeans.size > 1) {
                breadcrumbBeans.removeAt(breadcrumbBeans.size - 1)//删除最后一个
                refreshView()
            }else {
                finish()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }


    override fun itemList(list: List<CloudDiskItem>) {
        hideLoadingDialog()
        items.clear()
        items.addAll(list)
        adapter.notifyDataSetChanged()
    }

    override fun error(error: String) {
        XToast.toastShort(this, error)
        hideLoadingDialog()
    }

    private fun refreshView() {
        val current = breadcrumbBeans.last()
        loadFileList(current.folderId, current.level)
        loadBreadcrumb()
    }

    private fun loadFileList(id: String, newLevel: Int) {
        fileLevel = newLevel
        showLoadingDialog()
        mPresenter.getItemList(id)
    }

    /**
     * 加载面包屑导航
     */
    private fun loadBreadcrumb() {
        ll_folder_picker_breadcrumb.removeAllViews()
        breadcrumbBeans.mapIndexed { index, fileBreadcrumbBean ->
            val breadcrumbTitle = TextView(this)
            breadcrumbTitle.text = fileBreadcrumbBean.displayName
            breadcrumbTitle.tag = fileBreadcrumbBean
            breadcrumbTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
            breadcrumbTitle.layoutParams = LPWW
            if (index == breadcrumbBeans.size - 1) {
                breadcrumbTitle.setTextColor(FancySkinManager.instance().getColor(this, R.color.z_color_primary))
                ll_folder_picker_breadcrumb.addView(breadcrumbTitle)
            } else {
                breadcrumbTitle.setTextColor(FancySkinManager.instance().getColor(this, R.color.z_color_text_primary_dark))
                breadcrumbTitle.setOnClickListener { v -> onClickBreadcrumb(v as TextView) }
                ll_folder_picker_breadcrumb.addView(breadcrumbTitle)
                val arrow = TextView(this)
                val lp = LPWW
                lp.setMargins(8, 0, 8, 0)
                arrow.layoutParams = lp
                arrow.text = getString(R.string.fa_angle_right)
                arrow.setTextColor(FancySkinManager.instance().getColor(this, R.color.z_color_text_primary_dark))
                arrow.typeface = font
                arrow.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                ll_folder_picker_breadcrumb.addView(arrow)
            }
        }
    }

    /**
     * 点击面包屑导航
     */
    private fun onClickBreadcrumb(textView: TextView) {
        val bean = textView.tag as FileBreadcrumbBean
        var newLevel = 0
        breadcrumbBeans.mapIndexed { index, fileBreadcrumbBean ->
            if (bean == fileBreadcrumbBean) {
                newLevel = index
                //清空listview
                loadFileList(fileBreadcrumbBean.folderId, fileBreadcrumbBean.level)
            }
        }
        //处理breadcrumbBeans 把多余的去掉
        if (breadcrumbBeans.size > (newLevel + 1)) {
            val s = breadcrumbBeans.size
            for (i in (s-1) downTo (newLevel+1)) {
                println(i)
                breadcrumbBeans.removeAt(i)
            }
        }

        loadBreadcrumb()
    }

    private val adapter: CommonRecycleViewAdapter<CloudDiskItem> by lazy {
        object : CommonRecycleViewAdapter<CloudDiskItem>(this, items, R.layout.item_folder_list_picker) {
            override fun convert(holder: CommonRecyclerViewHolder?, t: CloudDiskItem?) {
                if (holder != null && t != null && t is CloudDiskItem.FolderItem) {
                    holder.setImageViewResource(R.id.file_list_icon_id, R.mipmap.icon_folder)
                            .setText(R.id.file_list_name_id, t.name)
                            .setText(R.id.tv_file_list_time, t.updateTime)

                    val chooseBtn = holder.getView<Button>(R.id.file_list_choose_id)
                    chooseBtn.visibility = View.VISIBLE
                    chooseBtn.setOnClickListener {
                        backResult(t.id)
                    }
                }
            }

        }
    }

    private fun backResult(id: String) {
        intent.putExtra(RESULT_BACK_KEY, id)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
