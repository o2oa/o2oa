package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.clouddrive.v2.type

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.View
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_cloud_disk_file_type.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.VideoPlayerActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.clouddrive.v2.CloudDiskFileDownloadHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.clouddrive.v2.viewer.BigImageViewActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.tbs.FileReaderActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.yunpan.FileJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.MiscUtilK
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.go
import org.jetbrains.anko.dip

class CloudDiskFileTypeActivity : BaseMVPActivity<CloudDiskFileTypeContract.View, CloudDiskFileTypeContract.Presenter>(), CloudDiskFileTypeContract.View {
    override var mPresenter: CloudDiskFileTypeContract.Presenter = CloudDiskFileTypePresenter()



    override fun layoutResId(): Int  = R.layout.activity_cloud_disk_file_type

    companion object {
        const val FILE_TYPE_ARG_KEY = "FILE_TYPE_ARG_KEY"
        fun start(activity: Activity, type: String) {
            val bundle = Bundle()
            bundle.putString(FILE_TYPE_ARG_KEY, type)
            activity?.go<CloudDiskFileTypeActivity>(bundle)
        }
    }

    private val downloader: CloudDiskFileDownloadHelper by lazy { CloudDiskFileDownloadHelper(this) }

    private var type = FileTypeEnum.image.key
    private val adapter: CloudDiskFileTypeItemAdapter by lazy { CloudDiskFileTypeItemAdapter() }
    var isRefresh = false //下拉刷新
    var isLoading = false //上拉加载
    var page = 1

    override fun afterSetContentView(savedInstanceState: Bundle?) {
        type = intent.getStringExtra(FILE_TYPE_ARG_KEY) ?: FileTypeEnum.image.key
        val title = when(type) {
            FileTypeEnum.image.key -> FileTypeEnum.image.cn
            FileTypeEnum.office.key -> FileTypeEnum.office.cn
            FileTypeEnum.movie.key -> FileTypeEnum.movie.cn
            FileTypeEnum.music.key -> FileTypeEnum.music.cn
            FileTypeEnum.other.key -> FileTypeEnum.other.cn
            else -> "未知"
        }
        setupToolBar(title, setupBackButton = true)

        swipe_refresh_file_type_layout.touchSlop = dip(70)
        swipe_refresh_file_type_layout.setColorSchemeResources(R.color.z_color_refresh_scuba_blue,
                R.color.z_color_refresh_red, R.color.z_color_refresh_purple, R.color.z_color_refresh_orange)
        swipe_refresh_file_type_layout.recyclerViewPageNumber = O2.DEFAULT_PAGE_NUMBER
        swipe_refresh_file_type_layout.setOnRefreshListener {
            if (!isRefresh && !isLoading) {
                isRefresh = true
                getData(true)
            }
        }
        swipe_refresh_file_type_layout.setOnLoadMoreListener {
            if(!isLoading && !isRefresh) {
                isLoading = true
                getData(false)
            }
        }
        initRecyclerView()

        MiscUtilK.swipeRefreshLayoutRun(swipe_refresh_file_type_layout, this)

    }

    override fun onResume() {
        super.onResume()
        isRefresh = true
        getData(true)
    }

    override fun onStop() {
        downloader.closeDownload()
        super.onStop()
    }

    override fun pageItems(items: List<FileJson>) {
        if (isRefresh) {
            adapter.datas.clear()
            adapter.datas.addAll(items)
            adapter.notifyDataSetChanged()
        }else if(isLoading){
            adapter.datas.addAll(items)
            adapter.notifyDataSetChanged()
        }
        finishAnimation()
    }

    override fun error(error: String) {
        XToast.toastShort(this, error)
        finishAnimation()
    }

    private fun finishAnimation() {
        if (isRefresh) {
            swipe_refresh_file_type_layout.isRefreshing = false
            isRefresh = false
        }
        if (isLoading) {
            swipe_refresh_file_type_layout.setLoading(false)
            isLoading = false
        }
    }


    private fun initRecyclerView() {
        if (type == FileTypeEnum.image.key) {
            rv_file_type_list.layoutManager = GridLayoutManager(this, 4)
            adapter.isGrid = true
        }else {
            rv_file_type_list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            adapter.isGrid = false
        }
        rv_file_type_list.adapter = adapter
        adapter.onItemClickListener = object : CloudDiskFileTypeItemAdapter.OnItemClickListener {
            override fun onItemClick(view: View, item: FileJson) {
                if (item.type == FileTypeEnum.image.key) {
                    BigImageViewActivity.start(this@CloudDiskFileTypeActivity, item.id, item.extension)
//                    val size = dip(40)
//                    //小图url
//                    val url = APIAddressHelper.instance().getCloudDiskImageUrl(item.id, size, size)
//                    val imageView = view.findViewById<ImageView>(R.id.file_list_icon_id)
//                    val activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this@CloudDiskFileTypeActivity,
//                            android.support.v4.util.Pair<View, String>(imageView, url))
//                    val intent = Intent(this@CloudDiskFileTypeActivity, BigImageViewActivity::class.java)
//                    intent.putExtra(BigImageViewActivity.MINI_IMAGE_URL_KEY, url)
//                    intent.putExtra(BigImageViewActivity.IMAGE_ID_KEY, item.id)
//                    intent.putExtra(BigImageViewActivity.IMAGE_EXTENSION_KEY, item.extension)
//                    ActivityCompat.startActivity(this@CloudDiskFileTypeActivity, intent, activityOptions.toBundle())
                }else {
                    openFile(item)
                }
            }
        }
    }

    private fun getData(refresh: Boolean) {
        if (refresh) {
            page = 1
        }else {
            page += 1
        }
        mPresenter.getPageItems(page, type)
    }

    private fun openFile(item: FileJson) {
        downloader.showLoading = { showLoadingDialog() }
        downloader.hideLoading = { hideLoadingDialog() }
        downloader.startDownload(item.id, item.extension) { file->
            if (file!=null) {
                if (item.type == FileTypeEnum.movie.key) {
                    VideoPlayerActivity.startPlay(this@CloudDiskFileTypeActivity, file.absolutePath, item.name)
                }else {
                    go<FileReaderActivity>(FileReaderActivity.startBundle(file.absolutePath))
                }
            }else {
                XToast.toastShort(this, "打开文件异常！")
            }
        }
    }
}
