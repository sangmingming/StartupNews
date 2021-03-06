package com.halzhang.android.apps.startupnews.ui.widgets;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Window;
import com.google.analytics.tracking.android.EasyTracker;
import com.halzhang.android.apps.startupnews.R;
import com.halzhang.android.apps.startupnews.utils.UIUtils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import java.lang.ref.WeakReference;

/**
 * WebView and Browse Bar Controller
 * @author Hal
 */
public class WebViewController implements OnClickListener {
    
    private WeakReference<Activity> mActivityRef;
    
    private WebView mWebView;
    
    private ImageButton mBackButton;
    private ImageButton mForwardButton;
    private ImageButton mReadabilityButton;
    private ImageButton mRefreshButton;
    private ImageButton mWebSiteButton;
    
    private String mCurrentUrl;
    
    public WebViewController(Activity activity){
        mActivityRef = new WeakReference<Activity>(activity);
    }
    
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressLint("SetJavaScriptEnabled")
    public void initControllerView(WebView webView,View view){
        if(webView == null || view == null){
            return;
        }
        mWebView = webView;
        mWebView.setWebChromeClient(new MyWebChromeClient());
        mWebView.setWebViewClient(new MyWebViewClient());

        WebSettings settings = mWebView.getSettings();
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        if (UIUtils.hasHoneycomb()) {
            settings.setDisplayZoomControls(false);
        }
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        mBackButton = (ImageButton) view.findViewById(R.id.browse_back);
        mForwardButton = (ImageButton) view.findViewById(R.id.browse_forward);
        mReadabilityButton = (ImageButton) view.findViewById(R.id.browse_readability);
        mRefreshButton = (ImageButton) view.findViewById(R.id.browse_refresh);
        mWebSiteButton = (ImageButton) view.findViewById(R.id.browse_website);
        
        mBackButton.setOnClickListener(this);
        mForwardButton.setOnClickListener(this);
        mReadabilityButton.setOnClickListener(this);
        mRefreshButton.setOnClickListener(this);
        mWebSiteButton.setOnClickListener(this);
    }
    
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            setCurrentUrl(url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            setCurrentUrl(url);
        }
    }
    
    private void setCurrentUrl(String url) {
        mCurrentUrl = url;
    }
    
    private String getCurrentUrl() {
        return mCurrentUrl; //TextUtils.isEmpty(mCurrentUrl) ? mOriginalUrl : mCurrentUrl;
    }

    private class MyWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            int progress = (Window.PROGRESS_END - Window.PROGRESS_START) / 100 * newProgress;
            Activity activity = mActivityRef.get();
            if(activity != null && activity instanceof SherlockFragmentActivity){
                ((SherlockFragmentActivity)activity).setSupportProgress(progress);
            }
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            mActivityRef.get().setTitle(title);
        }

    }
    
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.browse_back:
                back();
                break;
            case R.id.browse_forward:
                forward();
                break;
            case R.id.browse_readability:
                readability();
                break;
            case R.id.browse_refresh:
                refresh();
                break;
            case R.id.browse_website:
                webSite();
                break;
            default:
                break;
        }

    }

    private void back() {
        EasyTracker.getTracker().sendEvent("ui_action", "options_item_selected",
                "browseactivity_menu_back", 0L);
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        }
    }

    private void forward() {
        EasyTracker.getTracker().sendEvent("ui_action", "options_item_selected",
                "browseactivity_menu_forward", 0L);
        if (mWebView.canGoForward()) {
            mWebView.goForward();
        }
    }

    private void readability() {
        EasyTracker.getTracker().sendEvent("ui_action", "options_item_selected",
                "browseactivity_menu_readability", 0L);
        if(TextUtils.isEmpty(mCurrentUrl)){
            return;
        }
        mWebView.loadUrl("http://www.readability.com/m?url=" + getCurrentUrl());
    }

    private void refresh() {
        EasyTracker.getTracker().sendEvent("ui_action", "options_item_selected",
                "browseactivity_menu_refresh", 0L);
        mWebView.reload();
    }

    private void webSite() {
        // 打开原链接，还是转码的链接呢？
        EasyTracker.getTracker().sendEvent("ui_action", "options_item_selected",
                "browseactivity_menu_website", 0L);
        if(TextUtils.isEmpty(mCurrentUrl)){
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getCurrentUrl()));
        mActivityRef.get().startActivity(intent);
    }
    
    public WebView getWebView(){
        return mWebView;
    }
    
    public void loadUrl(String url){
        if(TextUtils.isEmpty(url) || url.equals(mCurrentUrl)){
            return;
        }
        mWebView.clearHistory();
        mWebView.loadUrl(url);
    }

}
