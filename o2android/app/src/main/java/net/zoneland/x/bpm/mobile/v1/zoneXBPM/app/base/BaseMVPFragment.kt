package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.LoadingDialog

/**
 * Created by fancy on 2017/6/8.
 */


abstract class BaseMVPFragment<in V: BaseView, T: BasePresenter<V>>: Fragment(), BaseView {

    abstract protected var mPresenter : T
    abstract fun layoutResId(): Int
    abstract fun initUI()

    open fun initData() {

    }

    val loadingDialog: LoadingDialog by lazy { LoadingDialog(activity) }

    override fun getContext(): Context  = activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPresenter.attachView(this as V)
        initData()
    }
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return  inflater?.inflate(layoutResId(), container, false)
    }
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }
    override fun onDestroy() {
        super.onDestroy()
        mPresenter.detachView()
    }

    fun showLoadingDialog() {
        loadingDialog.show()
    }
    fun hideLoadingDialog() {
        loadingDialog.dismiss()
    }
}