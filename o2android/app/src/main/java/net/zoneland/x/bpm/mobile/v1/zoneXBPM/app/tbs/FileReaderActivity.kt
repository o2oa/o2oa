package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.tbs

import android.app.Activity
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.FrameLayout
import com.tencent.smtt.sdk.TbsReaderView
import kotlinx.android.synthetic.main.activity_file_reader.*
import net.muliba.fancyfilepickerlibrary.FilePicker
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseO2BindActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.databinding.ActivityFileReaderBinding
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.FileExtensionHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog

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
        val filePath = intent.extras.getString(file_reader_file_path_key) ?: ""
        if (!TextUtils.isEmpty(filePath)) {
            openFileWithTBS(filePath)
        }
    }

//    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
//        menu?.clear()
//        menuInflater?.inflate(R.menu.menu_file_reader, menu)
//        return super.onPrepareOptionsMenu(menu)
//    }

//    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
//        when(item?.itemId) {
//            R.id.menu_file_choose -> {
//                FilePicker().withActivity(this)
//                        .chooseType(FilePicker.CHOOSE_TYPE_SINGLE)
//                        .requestCode(1024)
//                        .start()
//                return true
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (resultCode == Activity.RESULT_OK) {
//            when(requestCode) {
//                1024->{
//                    val file = data?.getStringExtra(FilePicker.FANCY_FILE_PICKER_SINGLE_RESULT_KEY)
//                    if (!TextUtils.isEmpty(file)) {
//                        openFileWithTBS(file!!)
//                    }
//                }
//            }
//        }
//    }

    override fun onDestroy() {
        mTbsReaderView?.onStop()
        super.onDestroy()
    }

    private fun openFileWithTBS(file: String) {
        XLog.info("打开文件：$file")

        val bund = Bundle()
        bund.putString("filePath", file)
        bund.putString("tempPath", FileExtensionHelper.getXBPMTempFolder())
        val type = getFileType(file)
        val b = mTbsReaderView?.preOpen(type, false)
        if (b == true) {
            mTbsReaderView?.openFile(bund)
        }else {
            XLog.error("type is error , $type")
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
