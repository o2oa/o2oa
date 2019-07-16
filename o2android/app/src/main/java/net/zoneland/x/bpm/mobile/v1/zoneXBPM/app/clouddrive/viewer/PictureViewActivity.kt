package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.clouddrive.viewer


import android.os.Bundle
import android.support.v4.app.Fragment
import kotlinx.android.synthetic.main.activity_picture_viewer.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonFragmentPagerAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.PictureViewerData
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.addOnPageChangeListener


class PictureViewActivity : BaseMVPActivity<PictureViewContract.View, PictureViewContract.Presenter>(), PictureViewContract.View {
    override var mPresenter: PictureViewContract.Presenter = PictureViewPresenter()
    override fun layoutResId(): Int = R.layout.activity_picture_viewer


    val fragmentList = ArrayList<Fragment>()
    var titleList = ArrayList<String>()
    var fileList = ArrayList<String>()
    var transferCurrentId:String = ""
    var currentIndex = 0
    var currentTitle:String = ""

    override fun afterSetContentView(savedInstanceState: Bundle?) {
        titleList = intent.extras?.getStringArrayList(PictureViewerData.TRANSFER_TITLE_KEY) ?: ArrayList()
        fileList = intent.extras?.getStringArrayList(PictureViewerData.TRANSFER_FILE_ID_KEY) ?:  ArrayList()
        transferCurrentId = intent.extras?.getString(PictureViewerData.TRANSFER_CURRENT_FILE_ID_KEY) ?: ""

        fileList.mapIndexed { index, it ->
            val bundle = Bundle()
            bundle.putString(CloudDrivePictureViewerFragment.FILE_ID_KEY, it)
            bundle.putString(CloudDrivePictureViewerFragment.FILE_NAME_KEY, titleList[index])
            val fragment = CloudDrivePictureViewerFragment()
            fragment.arguments = bundle
            fragmentList.add(fragment)
            if (it == transferCurrentId){
                currentIndex = index
                currentTitle = titleList[index]
            }
        }

        val adapter = CommonFragmentPagerAdapter(supportFragmentManager, fragmentList, titleList)
        view_pager_picture_view.adapter = adapter
        view_pager_picture_view.addOnPageChangeListener {
            onPageSelected { position->
                tv_picture_view_title.text = titleList[position]
            }
        }
        view_pager_picture_view.currentItem = currentIndex
        tv_picture_view_title.text = currentTitle


        image_picture_view_back_btn.setOnClickListener { finish() }

    }
}
