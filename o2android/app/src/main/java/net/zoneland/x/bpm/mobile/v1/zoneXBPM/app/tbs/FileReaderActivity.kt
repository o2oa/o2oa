package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.tbs

import androidx.lifecycle.ViewModelProviders
import androidx.databinding.DataBindingUtil
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.widget.Button
import android.widget.FrameLayout
import com.tencent.smtt.sdk.TbsReaderView
import kotlinx.android.synthetic.main.activity_file_reader.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseO2BindActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.databinding.ActivityFileReaderBinding
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.AndroidUtils
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.FileExtensionHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import java.io.File

class FileReaderActivity : BaseO2BindActivity() {


    private val viewModel: FileReaderViewModel by lazy { ViewModelProviders.of(this).get(FileReaderViewModel::class.java) }
    private var mTbsReaderView: TbsReaderView?=null

    companion object {
        const val file_reader_file_path_key = "file_reader_file_path_key"
        fun startBundle(filePath: String): Bundle {
            val bundle = Bundle()
            bundle.putString(file_reader_file_path_key, filePath)
            return bundle
        }
    }

    override fun bindView(savedInstanceState: Bundle?) {
        val bind = DataBindingUtil.setContentView<ActivityFileReaderBinding>(this, R.layout.activity_file_reader)
        bind.viewmodel = viewModel
        bind.setLifecycleOwner(this)
    }

    override fun afterSetContentView(savedInstanceState: Bundle?) {
        setupToolBar("文件预览", true)
        mTbsReaderView = TbsReaderView(this) { arg, arg1, arg2 ->
            XLog.info("arg:$arg, 1:$arg1, 2:$arg2")
        }
        fl_file_reader_container.addView(mTbsReaderView, FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))
        val filePath = intent.extras?.getString(file_reader_file_path_key) ?: ""
        if (!TextUtils.isEmpty(filePath)) {
            openFileWithTBS(filePath)
        }
    }

    override fun onDestroy() {
        mTbsReaderView?.onStop()
        super.onDestroy()
    }

    private fun openFileWithTBS(file: String) {
        XLog.info("打开文件：$file")


        val type = getFileType(file)
        val b = mTbsReaderView?.preOpen(type, false)
        if (b == true) {
            val bund = Bundle()
            bund.putString("filePath", file)
            bund.putString("tempPath", FileExtensionHelper.getXBPMTempFolder())
            mTbsReaderView?.openFile(bund)
        }else {
            XLog.error("type is error , $type")
            XToast.toastShort(this, "该文件类型无法预览！")
            fl_file_reader_container.removeAllViews()
            val btn = Button(this)
            btn.text = "用其它应用打开文件"
            val param = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
            param.gravity = Gravity.CENTER
            fl_file_reader_container.addView(btn, param)
            btn.setOnClickListener {
                val f = File(file)
                AndroidUtils.openFileWithDefaultApp(this, f)
                finish()
            }
        }

    }

    private fun getFileType(path: String): String {
        var str = ""

        if (TextUtils.isEmpty(path)) {
            return str
        }
        val i = path.lastIndexOf('.')
        if (i <= -1) {
            return str
        }
        str = path.substring(i + 1)
        return str
    }

}
