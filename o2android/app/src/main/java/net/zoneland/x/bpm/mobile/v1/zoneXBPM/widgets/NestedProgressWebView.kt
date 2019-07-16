package net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.support.v4.content.ContextCompat
import android.support.v4.view.MotionEventCompat
import android.support.v4.view.NestedScrollingChild
import android.support.v4.view.NestedScrollingChildHelper
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.ActionMode
import android.view.MotionEvent
import android.view.View
import android.webkit.*
import android.widget.ProgressBar
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.StringUtil
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog


/**
 * 仿NestedScrollView webview内容滚动也能使Coordinatorlayout响应对应的事件
 * Created by fancy on 2017/5/31.
 */

class NestedProgressWebView : WebView, NestedScrollingChild {

    private var mLastY: Int = 0
    private val mScrollOffset = IntArray(2)
    private val mScrollConsumed = IntArray(2)
    private var mNestedOffsetY: Int = 0
    private val mChildHelper: NestedScrollingChildHelper = NestedScrollingChildHelper(this)
    private lateinit var progressBar: ProgressBar
    private val mActionList = ArrayList<String>()
    private var mActionMode: ActionMode? = null
    private var mLinkJsInterfaceName:String = "fancyActionJsInterface"

    var mSelectActionListener: ActionSelectClickListener? = null

    constructor(context: Context): super(context) {
        isNestedScrollingEnabled = true
        initProgress()
    }
    constructor(context: Context, attributeSet: AttributeSet): super(context, attributeSet) {
        isNestedScrollingEnabled = true
        initProgress()
    }
    constructor(context: Context, attributeSet: AttributeSet, def: Int): super(context, attributeSet, def) {
        isNestedScrollingEnabled = true
        initProgress()
    }

    private fun initProgress() {
        progressBar = ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal)
        progressBar.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, 10, 0, 0)
        val drawable = ContextCompat.getDrawable(context, R.drawable.web_view_progress_bar)
        progressBar.progressDrawable = drawable
        addView(progressBar)
        //滚动条样式
        scrollBarStyle = View.SCROLLBARS_OUTSIDE_OVERLAY
        initSettings()
        webChromeClient = ProgressWebChromeClient()

    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            settings.safeBrowsingEnabled = false
        }

        settings.javaScriptEnabled = true
        settings.allowFileAccess = true
        settings.setAppCacheEnabled(true)
        settings.builtInZoomControls = false
        settings.setSupportMultipleWindows(true)
        settings.javaScriptCanOpenWindowsAutomatically = true
        //5.0以上开启混合模式加载
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
    }

    fun addActionList(list: List<String>) {
        mActionList.addAll(list)
        addJavascriptInterface(ActionSelectInterface(), mLinkJsInterfaceName)
    }
    fun clearAllAction() {
        mActionList.clear()
    }

    /**
     * 设置当前登录用户的cookie信息
     * @param context
     * *
     * @return
     */
    fun webViewSetCookie(context: Context, url: String) {
        //设置cookie
        val domain = StringUtil.getTopDomain(url)
        XLog.info("domain:$domain")
        val cookie = "x-token=" + O2SDKManager.instance().zToken
        XLog.info("cookie:$cookie")
        val host = APIAddressHelper.instance().getWebViewHost()
        XLog.info("host:$host")
        val cookieStr: String
        cookieStr = if (StringUtil.isIp(host)) {
            "$cookie; path=/; domain=$host"
        } else {
            "$cookie; path=/; domain=.$domain"
        }
        XLog.info("Set-Cookie:$cookieStr")

//        CookieSyncManager.createInstance(context)
        val cookieManager = CookieManager.getInstance()
        cookieManager.setAcceptCookie(true)
        cookieManager.setCookie(url, cookieStr)
//        CookieSyncManager.getInstance().sync()

        val newCookie = cookieManager.getCookie(url)
        if (newCookie != null) {
            XLog.info("Nat: webView.syncCookie.newCookie $newCookie")
        }
        XLog.info("mCookieManager is finish")
    }

    override fun startActionMode(callback: ActionMode.Callback?): ActionMode? {
        val actionMode =  super.startActionMode(callback)
        if (mActionList.isEmpty()) {
            return actionMode
        }else {
            return resolveActionMode(actionMode)
        }
    }


    override fun startActionMode(callback: ActionMode.Callback?, type: Int): ActionMode? {
        val actionMode = super.startActionMode(callback, type)
        if (mActionList.isEmpty()) {
            return actionMode
        }else {
            return resolveActionMode(actionMode)
        }
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        val lp = progressBar.layoutParams as LayoutParams
        lp.x = l
        lp.y = t
        progressBar.layoutParams = lp
        super.onScrollChanged(l, t, oldl, oldt)
    }


    override fun onTouchEvent(ev: MotionEvent): Boolean {
        var returnValue = false

        val event = MotionEvent.obtain(ev)
        val action = MotionEventCompat.getActionMasked(event)
        if (action == MotionEvent.ACTION_DOWN) {
            mNestedOffsetY = 0
        }
        val eventY = event.y.toInt()
        event.offsetLocation(0f, mNestedOffsetY.toFloat())
        when (action) {
            MotionEvent.ACTION_MOVE -> {
                var deltaY = mLastY - eventY
                // NestedPreScroll
                if (dispatchNestedPreScroll(0, deltaY, mScrollConsumed, mScrollOffset)) {
                    deltaY -= mScrollConsumed[1]
                    mLastY = eventY - mScrollOffset[1]
                    event.offsetLocation(0f, -mScrollOffset[1].toFloat())
                    mNestedOffsetY += mScrollOffset[1]
                }
                returnValue = super.onTouchEvent(event)

                // NestedScroll
                if (dispatchNestedScroll(0, mScrollOffset[1], 0, deltaY, mScrollOffset)) {
                    event.offsetLocation(0f, mScrollOffset[1].toFloat())
                    mNestedOffsetY += mScrollOffset[1]
                    mLastY -= mScrollOffset[1]
                }
            }
            MotionEvent.ACTION_DOWN -> {
                returnValue = super.onTouchEvent(event)
                mLastY = eventY
                // start NestedScroll
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                returnValue = super.onTouchEvent(event)
                // end NestedScroll
                stopNestedScroll()
            }
        }
        return returnValue
    }

    // Nested Scroll implements
    override fun setNestedScrollingEnabled(enabled: Boolean) {
        mChildHelper.isNestedScrollingEnabled = enabled
    }

    override fun isNestedScrollingEnabled(): Boolean {
        return mChildHelper.isNestedScrollingEnabled
    }

    override fun startNestedScroll(axes: Int): Boolean {
        return mChildHelper.startNestedScroll(axes)
    }

    override fun stopNestedScroll() {
        mChildHelper.stopNestedScroll()
    }

    override fun hasNestedScrollingParent(): Boolean {
        return mChildHelper.hasNestedScrollingParent()
    }

    override fun dispatchNestedScroll(dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int,
                                      offsetInWindow: IntArray?): Boolean {
        return mChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow)
    }

    override fun dispatchNestedPreScroll(dx: Int, dy: Int, consumed: IntArray?, offsetInWindow: IntArray?): Boolean {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow)
    }

    override fun dispatchNestedFling(velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed)
    }

    override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean {
        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY)
    }

    //webview 长按菜单处理
    private fun resolveActionMode(actionMode: ActionMode?): ActionMode? {
        if (actionMode!=null) {
            mActionMode = actionMode
            val menu = actionMode.menu
            menu?.let { m->
                m.clear()
                mActionList.map { menu.add(it) }
                mActionList.mapIndexed { index, s ->
                    val item = m.getItem(index)
                    item.setOnMenuItemClickListener {
                        getSelectedData(it.title as String)
                        releaseAction()
                        true
                    }
                }
            }

        }
        mActionMode = actionMode
        return actionMode
    }

    private fun releaseAction() {
        if (mActionMode!=null) {
            mActionMode?.finish()
            mActionMode = null
        }
    }

    private fun getSelectedData(title: String) {
        val js = "(function getSelectedText() {" +
                "var txt;" +
                "var title = \"$title\";" +
                "if (window.getSelection) {" +
                "txt = window.getSelection().toString();" +
                "} else if (window.document.getSelection) {" +
                "txt = window.document.getSelection().toString();" +
                "} else if (window.document.selection) {" +
                "txt = window.document.selection.createRange().text;" +
                "}" +
                "$mLinkJsInterfaceName.actionClickCallback(txt,title);" +
                "})()"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            evaluateJavascript("javascript:$js", null)
        } else {
            loadUrl("javascript:$js")
        }

    }


    interface ActionSelectClickListener {
        fun onClickActionItem(txt: String, title: String)
    }


    inner class ActionSelectInterface {
        @JavascriptInterface
        fun actionClickCallback(txt:String, title:String) {
            mSelectActionListener?.onClickActionItem(txt, title)
        }
    }

    inner class ProgressWebChromeClient : WebChromeClient() {
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            if (newProgress == 100) {
                progressBar.visibility = View.GONE
            } else {
                if (progressBar.visibility == View.GONE)
                    progressBar.visibility = View.VISIBLE
                progressBar.progress = newProgress
            }
            super.onProgressChanged(view, newProgress)
        }


    }
}