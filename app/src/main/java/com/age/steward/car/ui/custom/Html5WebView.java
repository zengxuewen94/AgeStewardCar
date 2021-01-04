package com.age.steward.car.ui.custom;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Message;

import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.DownloadListener;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;

import com.age.steward.car.R;
import com.age.steward.car.ui.ChildWindowActivity;
import com.age.steward.car.utils.Hint;
import com.age.steward.car.utils.NetworkUtil;

public class Html5WebView extends WebView {
    private ProgressView progressView;//进度条
    private Context context;
    private static final String TAG = "Html5WebView";
    private boolean needClear = false;

    public Html5WebView(Context context) {
        this(context, null);
    }

    public Html5WebView(Context context, AttributeSet attrs) {
        this(context, attrs, Resources.getSystem().getIdentifier("webViewStyle", "attr", "android"));
    }

    public Html5WebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        //初始化进度条
        progressView = new ProgressView(context);
        progressView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp2px(context, 2)));
        progressView.setColor(getResources().getColor(R.color.gray_text_color));
        progressView.setProgress(10);
        //把进度条加到WebView中
        addView(progressView);
        //初始化设置
        initWebSettings();
        setWebChromeClient(new MyWebChromeClient());
        setWebViewClient(new MyWebViewClient());
        setDownloadListener(new WebViewDownloadListener());
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebSettings() {
        // 存储(storage)
        // 启用HTML5 DOM storage API，默认值 false
        getSettings().setDomStorageEnabled(true);
        // 启用Web SQL Database API，这个设置会影响同一进程内的所有WebView，默认值 false
        // 此API已不推荐使用
        getSettings().setDatabaseEnabled(true);
        // 启用Application Caches API，必需设置有效的缓存路径才能生效，默认值 false
        getSettings().setAppCacheEnabled(true);
        getSettings().setAppCachePath(context.getCacheDir().getAbsolutePath());
        // 定位(location)
        getSettings().setGeolocationEnabled(true);
        // 是否保存表单数据
        getSettings().setSaveFormData(true);
        // 是否当webview调用requestFocus时为页面的某个元素设置焦点，默认值 true
        getSettings().setNeedInitialFocus(true);
        // 是否支持viewport属性，默认值 false
        // 页面通过`<meta name="viewport" ... />`自适应手机屏幕
        getSettings().setUseWideViewPort(true);
        // 是否使用overview mode加载页面，默认值 false
        // 当页面宽度大于WebView宽度时，缩小使页面宽度等于WebView宽度
        getSettings().setLoadWithOverviewMode(true);
        // 布局算法
        getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        // 是否支持Javascript，默认值false
        getSettings().setJavaScriptEnabled(true);
        // 是否支持多窗口，默认值false
        getSettings().setSupportMultipleWindows(true);
        // 是否可用Javascript(window.open)打开窗口，默认值 false
        getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        // 资源访问
        getSettings().setAllowContentAccess(true); // 是否可访问Content Provider的资源，默认值 true
        getSettings().setAllowFileAccess(true);    // 是否可访问本地文件，默认值 true
        // 是否允许通过file url加载的Javascript读取本地文件，默认值 false
        getSettings().setAllowFileAccessFromFileURLs(true);
        // 是否允许通过file url加载的Javascript读取全部资源(包括文件,http,https)，默认值 false
        getSettings().setAllowUniversalAccessFromFileURLs(true);
        // 资源加载
        getSettings().setLoadsImagesAutomatically(true); // 是否自动加载图片
        getSettings().setBlockNetworkImage(false);       // 禁止加载网络图片
        getSettings().setBlockNetworkLoads(false);       // 禁止加载所有网络资源
        // 缩放(zoom)
        getSettings().setSupportZoom(true);          // 是否支持缩放
        getSettings().setBuiltInZoomControls(false); // 是否使用内置缩放机制
        getSettings().setDisplayZoomControls(true);  // 是否显示内置缩放控件
        // 默认文本编码，默认值 "UTF-8"
        getSettings().setDefaultTextEncodingName("UTF-8");
        getSettings().setDefaultFontSize(16);        // 默认文字尺寸，默认值16，取值范围1-72
        getSettings().setDefaultFixedFontSize(16);   // 默认等宽字体尺寸，默认值16
        getSettings().setMinimumFontSize(8);         // 最小文字尺寸，默认值 8
        getSettings().setMinimumLogicalFontSize(8);  // 最小文字逻辑尺寸，默认值 8
        getSettings().setTextZoom(100);              // 文字缩放百分比，默认值 100

    }


    private class MyWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                //加载完毕进度条消失
                progressView.setVisibility(View.GONE);
            } else {
                progressView.setVisibility(View.VISIBLE);
                //更新进度
                progressView.setProgress(newProgress);

            }
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            Log.e("onReceivedTitle", title);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                if (title.contains("404") || title.contains("500") || title.contains("Error")) {
                    if (webViewListener != null) {
                        webViewListener.onWebViewLoadError(view.getUrl());
                    }
                }
            }
            super.onReceivedTitle(view, title);
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            return super.onJsAlert(view, url, message, result);
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            return super.onConsoleMessage(consoleMessage);
        }

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            WebView web2 = new WebView(context);//新创建一个webview
            web2.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                    loadUrl(url);//将拦截到url交由第一个WebView打开
                    Intent intent=new Intent(context, ChildWindowActivity.class);
                    intent.putExtra("url",url);
                    context.startActivity(intent);
                    return true;
                }
            });
            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            //以下的操作应该就是让新的webview去加载对应的url等操作。
            transport.setWebView(web2);
            resultMsg.sendToTarget();

            return true;
        }

        @Override
        public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
            return super.onJsBeforeUnload(view, url, message, result);
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            return super.onJsConfirm(view, url, message, result);
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }

        @Override
        public void onCloseWindow(WebView window) {
            super.onCloseWindow(window);
        }

        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
//            return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
            webViewListener.image(filePathCallback);
            return true;
        }


    }

    private class MyWebViewClient extends WebViewClient {


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (!NetworkUtil.isNetworkConnected(context)) {
                Hint.showShort(context, R.string.network_error);
                stopLoading();
                return false;
            } else if (urlCanLoad(url.toLowerCase())) {  // 加载正常网页
                loadUrl(url);
            } else {  // 打开第三方应用或者下载apk等
                startThirdPartyApp(url);
            }
            return true;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            return super.shouldOverrideUrlLoading(view, request);
        }


        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            //回调发生在子线程中,不能直接进行UI操作
            return super.shouldInterceptRequest(view, url);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            Log.e("TTT", "shouldInterceptRequest 1 request url is " + request.getUrl().toString());
            return super.shouldInterceptRequest(view, request);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Log.e("TTT", "onPageFinished" + url);
            super.onPageFinished(view, url);
        }


        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
        }


        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Log.e("TTT", "onReceivedError:" + errorCode);
            if (webViewListener != null) {
                webViewListener.onWebViewLoadError(view.getUrl());
            }
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            Log.e("TTT", "onReceivedError");
            if (webViewListener != null) {
                webViewListener.onWebViewLoadError(view.getUrl());
            }
            super.onReceivedError(view, request, error);
        }

        @Override
        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
            super.doUpdateVisitedHistory(view, url, isReload);
            if (needClear) {
                view.clearHistory();
                needClear = false;
            }
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            Log.e("TTT", "onReceivedSslError");
            if (webViewListener != null) {
                webViewListener.onWebViewLoadError(view.getUrl());
            }
            super.onReceivedSslError(view, handler, error);
        }

        @Override
        public void onLoadResource(WebView view, String url) {
            Log.e("TTT", "onLoadResource");
            super.onLoadResource(view, url);
        }


    }

    /**
     * dp转换成px
     *
     * @param context Context
     * @param dp      dp
     * @return px值
     */
    private int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }


    /**
     * 列举正常情况下能正常加载的网页url
     *
     * @param url
     * @return
     */
    private boolean urlCanLoad(String url) {
        return url.startsWith("http://") || url.startsWith("https://") ||
                url.startsWith("ftp://") || url.startsWith("file://");
    }

    /**
     * 打开第三方app。如果没安装则跳转到应用市场
     *
     * @param url
     */
    private void startThirdPartyApp(String url) {
        try {
            Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME); // 注释1
            if (context.getPackageManager().resolveActivity(intent, 0) == null) {  // 如果手机还没安装app，则跳转到应用市场
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse
                        ("market://details?id=" + intent.getPackage())); // 注释2
            }
            context.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * 下载文件监听器
     */
    private class WebViewDownloadListener implements DownloadListener {
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            Log.e(TAG, "--- onDownloadStart ---" + " url = " + url + ", userAgent = " + userAgent + ", " +
                    "contentDisposition = " + contentDisposition + ", mimetype = " + mimetype + ", contentLength = " + contentLength);
            downloadByBrowser(url);
        }
    }

    /**
     * 通过浏览器下载
     *
     * @param url
     */
    private void downloadByBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }

    public interface OnHtml5WebViewListener {
        void onWebViewLoadError(String url);

        void onStartLoad();

        void image(ValueCallback<Uri[]> filePathCallback);
    }

    private OnHtml5WebViewListener webViewListener;

    public void setOnHtml5WebViewListener(OnHtml5WebViewListener webViewListener) {
        this.webViewListener = webViewListener;
    }


    public void setNeedClear(boolean needClear) {
        this.needClear = needClear;
    }
}
