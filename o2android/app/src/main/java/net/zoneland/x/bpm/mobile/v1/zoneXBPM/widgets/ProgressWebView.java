package net.zoneland.x.bpm.mobile.v1.zoneXBPM.widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2SDKManager;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.StringUtil;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.O2App;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.R;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.core.component.api.APIAddressHelper;
import net.zoneland.x.bpm.mobile.v1.zoneXBPM.utils.XLog;

/**
 * 带进度条的WebView
 * 加了一些当前项目业务 设置登录cookie等
 * Created by fancy on 2017/3/1.
 */

public class ProgressWebView extends WebView {

    private ProgressBar progressBar;


    public ProgressWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        progressBar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 10, 0, 0));
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.web_view_progress_bar);
        progressBar.setProgressDrawable(drawable);
        addView(progressBar);
        //滚动条样式
        setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        initSettings();
        setWebChromeClient(new ProgressWebChromeClient());

    }


    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        LayoutParams lp = (LayoutParams) progressBar.getLayoutParams();
        lp.x = l;
        lp.y = t;
        progressBar.setLayoutParams(lp);
        super.onScrollChanged(l, t, oldl, oldt);
    }


    public ProgressBar getProgressBar() {
        return progressBar;
    }

    /**
     * 设置当前登录用户的cookie信息
     * @param context
     * @return
     */
    public void webViewSetCookie(Context context, String url) {
        //设置cookie
        String domain = StringUtil.getTopDomain(url);
        XLog.debug("domain:"+domain);
        String cookie = "x-token="+ O2SDKManager.Companion.instance().getZToken();
        XLog.debug("cookie:"+cookie);
        String host = APIAddressHelper.Companion.instance().getWebViewHost();
        XLog.debug("host:"+host);
        String cookieStr;
        if (StringUtil.isIp(host)){
            cookieStr = cookie;
        }else {
            cookieStr = cookie +"; path=/; domain=."+domain;
        }
        XLog.debug("Set-Cookie:"+cookieStr);

        CookieSyncManager.createInstance(context);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setCookie(url, cookieStr);
        CookieSyncManager.getInstance().sync();

        String newCookie = cookieManager.getCookie(url);
        if(newCookie != null){
            XLog.debug("Nat: webView.syncCookie.newCookie "+newCookie);
        }
        XLog.debug("mCookieManager is finish");
    }


    /**
     * webview 定制一些属性
     */
    private void initSettings() {
        WebSettings webSettings = getSettings();
//        webSettings.setJavaScriptEnabled(true);
//        webSettings.setUseWideViewPort(true);
//        webSettings.setAllowFileAccess(true);
//        webSettings.setAppCacheEnabled(true);
//        webSettings.setBuiltInZoomControls(false);
//        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//        webSettings.setDomStorageEnabled(true);
//        webSettings.setDatabaseEnabled(true);

        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setBuiltInZoomControls(false);
    }


    public class ProgressWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                progressBar.setVisibility(GONE);
            } else {
                if (progressBar.getVisibility() == GONE)
                    progressBar.setVisibility(VISIBLE);
                progressBar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }


    }
}
