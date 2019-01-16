package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.LoadingDialog

/**
 * Created by fancy on 2017/6/9.
 */


abstract class BaseMVPViewPagerFragment<in V : BaseView, T : BasePresenter<V>> : Fragment(), BaseView {

    protected abstract var mPresenter: T
    abstract fun lazyLoad()
    abstract fun layoutResId(): Int
    abstract fun initUI()


    val loadingDialog: LoadingDialog by lazy { LoadingDialog(activity) }

    /**
     * fragment 是否已经建好UI
     */
    protected var isViewInit = false
    /** Fragment当前状态是否可见 */
    protected var isViewVisible = false

    override fun getContext(): Context = activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //一定要调用，否则无法将菜单加入ActionItem
        setHasOptionsMenu(true)

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val mRootView =   inflater?.inflate(layoutResId(), container, false)
        mPresenter.attachView(this as V)
        XLog.info("onCreateView.............res:"+layoutResId())
        if (mRootView == null) {
            XLog.info("mRootView is null.........................")
        }
        return mRootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        isViewInit = true
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        XLog.info("onViewCreated.............")
        if (view == null) {
            XLog.info("view is null.........................")
        }
        initUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isViewInit = false
        mPresenter.detachView()
    }

    override fun onResume() {
        super.onResume()
        // 判断当前fragment是否显示
        if (isViewVisible) {
            fetchData()
        }
    }


    //对用户是否可见，在onCreateView之前调用
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        isViewVisible = isVisibleToUser
        fetchData()
    }


    override fun onDestroy() {
        super.onDestroy()

    }

    fun fetchData() {
        if (isViewInit && isViewVisible) {
            lazyLoad()
        }
    }

    fun showLoadingDialog() {
        loadingDialog.show()
    }

    fun hideLoadingDialog() {
        loadingDialog.dismiss()
    }
}