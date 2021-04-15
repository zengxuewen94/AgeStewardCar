package com.age.steward.car.ui;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.ValueCallback;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.age.steward.car.AppConfig;
import com.age.steward.car.AppContext;
import com.age.steward.car.AppData;
import com.age.steward.car.R;
import com.age.steward.car.ui.custom.Html5WebView;
import com.age.steward.car.utils.StatusBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 子窗口界面
 */
public class ChildWindowActivity extends AppCompatActivity implements Html5WebView.OnHtml5WebViewListener {
    @BindView(R.id.rl_child_window_title)
    RelativeLayout rlTitle;
    @BindView(R.id.iv_child_window_title_left)
    ImageView ivLeftBack;
    @BindView(R.id.iv_child_window_title_refresh)
    ImageView ivRightRefresh;
    @BindView(R.id.wv_child_window)
    Html5WebView mWebView;
    @BindView(R.id.loaderror_btn_data_reload)
    TextView tvReload;
    private int themeIndex = 0;
    private int[] themeColors = AppData.THEME_COLORS;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_window);
        ButterKnife.bind(this);
        initView();
        setTopTitleBg();
    }


    private void initView() {
        initWebView();
        ivLeftBack.setOnClickListener(v -> finish());

    }

    //设置标题的背景色
    private void setTopTitleBg() {
        themeIndex = 0;
        int index = AppContext.getIntPreference("APP_THEME_SELECT");
        if (index >= 0) {
            themeIndex = index;
        }
        rlTitle.setBackgroundColor(getResources().getColor(
                themeColors[themeIndex]));
        tvReload.setBackgroundColor(getResources().getColor(
                themeColors[themeIndex]));
        StatusBarUtil.setRootViewFitsSystemWindows(this, true);
        //设置状态栏颜色
        StatusBarUtil.setStatusBarColor(this, getResources().getColor(
                themeColors[themeIndex]));
    }

    private void initWebView() {
        //在js中调用本地java方
        mWebView.setOnHtml5WebViewListener(this);
        mWebView.requestFocus(View.FOCUS_DOWN);
    }


    @Override
    public void onWebViewLoadError(String url) {

    }

    @Override
    public void onStartLoad() {

    }

    @Override
    public void image(ValueCallback<Uri[]> filePathCallback) {

    }

    @Override
    public void onLoadNewWebView(boolean isLoad, Html5WebView html5WebView) {

    }
}
