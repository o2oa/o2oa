package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.cms.index


import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.text.TextUtils
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_cms_main.*
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base.BaseMVPActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.cms.application.CMSApplicationActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecycleViewAdapter
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.adapter.CommonRecyclerViewHolder
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.bo.api.cms.CMSApplicationInfoJson
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.Base64ImageUtil
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XToast
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.go
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.gone
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.extension.visible
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


class CMSIndexActivity : BaseMVPActivity<CMSIndexContract.View, CMSIndexContract.Presenter>(), CMSIndexContract.View {
    override var mPresenter: CMSIndexContract.Presenter = CMSIndexPresenter()


    override fun layoutResId(): Int = R.layout.activity_cms_main

    val applicationList = ArrayList<CMSApplicationInfoJson>()
    val adapter: CommonRecycleViewAdapter<CMSApplicationInfoJson> by lazy {
        object : CommonRecycleViewAdapter<CMSApplicationInfoJson>(this, applicationList, R.layout.item_cms_main_application) {
            override fun convert(holder: CommonRecyclerViewHolder?, t: CMSApplicationInfoJson?) {
                val icon = holder?.getView<ImageView>(R.id.image_cms_main_application_icon)
                icon?.setImageResource(R.mipmap.icon_cms_application_default)
                icon?.tag = t?.id
                refreshApplicationIcon(icon, t?.id, t?.appIcon)
                holder?.setText(R.id.tv_cms_main_application_name, t?.appName?:"")
            }
        }
    }


    override fun afterSetContentView(savedInstanceState: Bundle?) {
        setupToolBar(getString(R.string.cms), true, true)

        recycler_cms_main_content.layoutManager = GridLayoutManager(this, 4)
        recycler_cms_main_content.adapter = adapter
        adapter.setOnItemClickListener { _, position ->
            val info = applicationList[position]
            if (info.wrapOutCategoryList.isNotEmpty()) {
                gotoCmsApplication(info)
            }
        }


        mPresenter.findAllApplication()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_scale_in, R.anim.activity_scale_out)
    }

    override fun loadApplicationFail() {
        XToast.toastShort(this, "获取数据失败！")
        recycler_cms_main_content.gone()
        tv_cms_main_empty.visible()
    }

    override fun loadApplicationSuccess(list: List<CMSApplicationInfoJson>) {
        if (list!=null && !list.isEmpty()) {
            recycler_cms_main_content.visible()
            tv_cms_main_empty.gone()
            applicationList.clear()
            applicationList.addAll(list)
            adapter.notifyDataSetChanged()
        }else{
            recycler_cms_main_content.gone()
            tv_cms_main_empty.visible()
        }
    }

    private fun gotoCmsApplication(info: CMSApplicationInfoJson) {
        XLog.debug("click application ${info.appName} ")
        go<CMSApplicationActivity>(CMSApplicationActivity.startBundleData(info))
    }


    private fun refreshApplicationIcon(icon: ImageView?, id: String?, appIcon: String?) {
        if (icon==null || TextUtils.isEmpty(id) || TextUtils.isEmpty(appIcon)) {
            return
        }
        Observable.create(object : Observable.OnSubscribe<Bitmap>{
            override fun call(t: Subscriber<in Bitmap>?) {
                try {
                    val input = Base64ImageUtil.generateBase642Inputstream(appIcon)
                    val bit = BitmapFactory.decodeStream(input)
                    t?.onNext(bit)
                } catch(e: Exception) {
                    t?.onError(e)
                }
                t?.onCompleted()
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({bitmap->
                    if (bitmap != null && id!!.equals(icon.tag)) {
                        icon.setImageBitmap(bitmap)
                    }
                }, {e-> XLog.error("", e)})
    }
}
