package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.clouddrive.v2

import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintSet
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import android.view.KeyEvent
import android.view.View
import kotlinx.android.synthetic.main.activity_cloud_disk.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.VideoPlayerActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.clouddrive.v2.f.FileFolderListFragment
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.clouddrive.v2.type.CloudDiskFileTypeActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.clouddrive.v2.type.FileTypeEnum
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.tbs.FileReaderActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.CloudDiskItem
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.addFragmentSafely
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.go


class CloudDiskActivity : BaseMVPActivity<CloudDiskContract.View, CloudDiskContract.Presenter>(), CloudDiskContract.View {
    override var mPresenter: CloudDiskContract.Presenter = CloudDiskPresenter()



    override fun layoutResId(): Int = R.layout.activity_cloud_disk


    private val constraintSet: ConstraintSet by lazy { ConstraintSet() }

    private val ffFragment: FileFolderListFragment by lazy { FileFolderListFragment() }
    private val downloader: CloudDiskFileDownloadHelper by lazy { CloudDiskFileDownloadHelper(this) }


    override fun afterSetContentView(savedInstanceState: Bundle?) {
        setupToolBar(getString(R.string.title_activity_yunpan), setupBackButton = true, isCloseBackIcon = true)

        addFragmentSafely(ffFragment, "fileFolder", allowState = true,
                containerViewId = R.id.frame_disk_content)

        constraintSet.clone(cl_cloud_disk)

        tv_disk_document.setOnClickListener { CloudDiskFileTypeActivity.start(this, FileTypeEnum.office.key) }
        tv_disk_music.setOnClickListener { CloudDiskFileTypeActivity.start(this, FileTypeEnum.music.key) }
        tv_disk_video.setOnClickListener { CloudDiskFileTypeActivity.start(this, FileTypeEnum.movie.key) }
        tv_disk_other.setOnClickListener { CloudDiskFileTypeActivity.start(this, FileTypeEnum.other.key) }
        tv_disk_image.setOnClickListener { CloudDiskFileTypeActivity.start(this, FileTypeEnum.image.key) }
        tv_disk_share.setOnClickListener {  }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_scale_in, R.anim.activity_scale_out)
    }

    override fun onStop() {
        downloader.closeDownload()
        super.onStop()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!ffFragment.onClickBackBtn()) {
                finish()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    fun hideCategoryArea() {
        constraintSet.setVisibility(R.id.ll_disk_line1, View.GONE)
        constraintSet.setVisibility(R.id.ll_disk_line2, View.GONE)
        val transition = AutoTransition()
        transition.duration = 300
        TransitionManager.beginDelayedTransition(cl_cloud_disk, transition)
        constraintSet.applyTo(cl_cloud_disk)
    }

    fun showCategoryArea() {
        constraintSet.setVisibility(R.id.ll_disk_line1, View.VISIBLE)
        constraintSet.setVisibility(R.id.ll_disk_line2, View.VISIBLE)
        val transition = AutoTransition()
        transition.duration = 150
        TransitionManager.beginDelayedTransition(cl_cloud_disk, transition)
        constraintSet.applyTo(cl_cloud_disk)
    }

    fun openFile(item: CloudDiskItem.FileItem) {
        downloader.showLoading = { showLoadingDialog() }
        downloader.hideLoading = { hideLoadingDialog() }
        downloader.startDownload(item.id, item.extension) { file->
            if (file!=null) {
                if (item.type == FileTypeEnum.movie.key) {
                    VideoPlayerActivity.startPlay(this, file.absolutePath, item.name)
                }else {
                    go<FileReaderActivity>(FileReaderActivity.startBundle(file.absolutePath))
                }
            }else {
                XToast.toastShort(this, "打开文件异常！")
            }
        }
    }
}
