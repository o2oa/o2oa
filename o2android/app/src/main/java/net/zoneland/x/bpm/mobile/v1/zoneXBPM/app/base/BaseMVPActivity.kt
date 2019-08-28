package net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.base

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.TextView
import com.wugang.activityresult.library.ActivityResult
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.app.o2.organization.ContactPickerActivity
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.model.vo.ContactPickerResult
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.ImmersedStatusBarUtils
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets.dialog.LoadingDialog


/**
 * Created by fancy on 2017/6/5.
 */

abstract class BaseMVPActivity<in V: BaseView, T: BasePresenter<V>>: AppCompatActivity(), BaseView {

    //need override
    abstract protected var mPresenter : T
    abstract fun afterSetContentView(savedInstanceState: Bundle?)
    abstract fun layoutResId(): Int
    //
    open fun beforeSetContentView(){}
    override fun getContext(): Context  = this

    //Toolbar 标题栏
    protected var toolbar: Toolbar? = null
    /**
     * ActionBar居中的标题
     */
    protected var toolbarTitle: TextView? = null

    var loadingDialog: LoadingDialog? = null


    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        beforeSetContentView()
        setContentView(layoutResId())
        // 沉浸式状态栏
        ImmersedStatusBarUtils.setImmersedStatusBar(this)

        mPresenter.attachView(this as V)
        afterSetContentView(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.detachView()
    }

    fun setupToolBar(title:String = "", setupBackButton:Boolean = false, isCloseBackIcon: Boolean = false) {
        toolbar = findViewById(R.id.toolbar_snippet_top_bar)
        toolbar?.title = ""
        setSupportActionBar(toolbar)
        toolbarTitle = findViewById(R.id.tv_snippet_top_title)
        toolbarTitle?.text = title
        if (setupBackButton) {
            if (isCloseBackIcon){
                setToolbarBackBtnWithCloseIcon()
            }else {
                setToolbarBackBtn()
            }
        }
    }

    fun updateToolbarTitle(title: String) {
        toolbarTitle?.text = title
    }

    fun setToolbarBackBtn() {
        toolbar?.setNavigationIcon(R.mipmap.ic_back_mtrl_white_alpha)
        toolbar?.setNavigationOnClickListener { finish() }
    }
    fun setToolbarBackBtnWithCloseIcon() {
        toolbar?.setNavigationIcon(R.mipmap.icon_menu_window_close)
        toolbar?.setNavigationOnClickListener { finish() }
    }

    fun showLoadingDialog() {
        if (loadingDialog==null) {
            loadingDialog = LoadingDialog(this)
        }
        loadingDialog?.show()
    }
    fun hideLoadingDialog() {
        loadingDialog?.dismiss()
    }


    fun contactPicker(bundle: Bundle, callback: (ContactPickerResult?)-> Unit) {
        ActivityResult.of(this)
                .className(ContactPickerActivity::class.java)
                .params(bundle)
                .greenChannel().forResult { _, data ->
                    val result = data?.getParcelableExtra<ContactPickerResult>(ContactPickerActivity.CONTACT_PICKED_RESULT)
                    if (result != null) {
                        callback(result)
                    }else {
                        callback(null)
                    }
                }
    }

}