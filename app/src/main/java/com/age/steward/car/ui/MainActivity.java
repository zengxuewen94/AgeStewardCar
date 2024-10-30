package com.age.steward.car.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.age.steward.car.expand.ConstDataConfig;
import com.age.steward.car.utils.PhotoUtils;
import com.android.library.zxing.activity.CaptureActivity;
import com.age.steward.car.AppConfig;
import com.age.steward.car.AppContext;
import com.age.steward.car.AppData;
import com.age.steward.car.R;
import com.age.steward.car.adapter.MainMenuAdapter;
import com.age.steward.car.bean.MenuBean;
import com.age.steward.car.dialog.DialogBuilderUtil;
import com.age.steward.car.listeners.NewsCenter;
import com.age.steward.car.nfc_reader.FNCObject;
import com.age.steward.car.request.AppRequest;
import com.age.steward.car.ui.custom.Html5WebView;
import com.age.steward.car.utils.Hint;
import com.age.steward.car.utils.NetworkUtil;
import com.age.steward.car.utils.StatusBarUtil;
import com.age.steward.car.utils.StringUtils;
import com.age.steward.car.utils.UIHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 主界面
 */
public class MainActivity extends RxAppCompatActivity implements View.OnClickListener, AppRequest.OnSignRequest
        , FNCObject.OnFNCInterface, Html5WebView.OnHtml5WebViewListener {
    @BindView(R.id.rl_main_title)
    RelativeLayout rlTitle;
    @BindView(R.id.tv_main_title)
    TextView tvTitle;
    @BindView(R.id.tv_main_title_left)
    TextView tvLeft;
    @BindView(R.id.iv_main_title_left)
    ImageView ivLeft;
    @BindView(R.id.iv_main_title_refresh)
    ImageView ivRefresh;
    @BindView(R.id.rv_main_menu_list)
    RecyclerView recyclerView;
    @BindView(R.id.rl_main_tab_me)
    RelativeLayout rlTabMe;
    @BindView(R.id.layout_loaderror)
    RelativeLayout rlLoadError;
    @BindView(R.id.wv_main_activity_content)
    Html5WebView mWebView;
    @BindView(R.id.loaderror_btn_data_reload)
    TextView tvReload;
    @BindView(R.id.ll_main_activity_tabs)
    LinearLayout llTab;
    @BindView(R.id.fl_main_activity_content)
    FrameLayout flContent;
    private Html5WebView html5WebView;
    private MainMenuAdapter mainMenuAdapter;
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mUploadCallbackAboveL;
    private FNCObject fncObject;
    private List<MenuBean> list = new ArrayList<>();
    private boolean mDoubleClickExit = false;
    private boolean mFirst = true;
    private boolean mAppQrCode = false;
    private long firstExitTime = 0;
    private static final String TAG = MapActivity.class.getSimpleName();
    //主题下标
    private int themeIndex = 0;
    //标题点击次数
    private int clickCount = 0;
    //主题颜色数组
    private int[] themeColors = AppData.THEME_COLORS;
    //用于保存拍照图片的uri
    private Uri mCameraUri;
    // 是否是Android 10以上手机
    private boolean isAndroidQ = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    private MyHandler myHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        setTopTitleBg();
        initData();

    }

    private void initView() {
        initWebView();
        mainMenuAdapter = new MainMenuAdapter(this, list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mainMenuAdapter);
        mainMenuAdapter.setOnLongClickListener(new MainMenuAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(MenuBean menuBean) {
                List<MenuBean> childList = menuBean.getChildMenuList();
                if (childList != null && !childList.isEmpty()) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("menuBean", menuBean);
                    bundle.putSerializable("meMenuList", (Serializable) childList);
                    DialogBuilderUtil.showMenuChildDialog(MainActivity.this, 0, mWebView, bundle);
                } else {
                    mWebView.loadUrl(menuBean.getUrl());
                }
            }

            @Override
            public void onItemLongClick(MenuBean menuBean) {

            }
        });
    }


    private void initWebView() {
        //在js中调用本地java方法
        mWebView.addJavascriptInterface(new AppInterface(), "androidObj");
        mWebView.setOnHtml5WebViewListener(this);
        mWebView.requestFocus(View.FOCUS_DOWN);
    }

    private void initData() {
        myHandler = new MyHandler(MainActivity.this);
        NewsCenter.getInstance().attachListener(listener);
        String serverUrl = AppContext.getPreference("SERVER_URL");
        if (StringUtils.isNull(serverUrl)) {
            Hint.showShort(this, "请设置连接的服务器地址");
            showView(2);
            return;
        }
        AppConfig.setMainHttpUrl(serverUrl);
        mWebView.loadUrl(AppConfig.newsUrl());
        getMenuList(mFirst);
        mFirst = false;
    }











    /**
     * 获取菜单
     *
     * @param showDialog 是否显示弹窗
     */
    private void getMenuList(boolean showDialog) {
        if (!NetworkUtil.isNetworkConnected(this)) {
            Hint.showShort(this, R.string.network_error);
            showView(2);
            return;
        }
        String serverUrl = AppContext.getPreference("SERVER_URL");
        if (StringUtils.isNull(serverUrl)) {
            Hint.showShort(this, "请设置连接的服务器地址");
            return;
        }
        AppRequest signRequest = AppRequest.getInstance(this, "menuList", this);
        Map<String, Object> map = new HashMap<>();
        signRequest.setOnSignRequest(this);
        signRequest.getMenuList(map, showDialog);
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
        llTab.setBackgroundColor(getResources().getColor(
                themeColors[themeIndex]));
        rlTabMe.setBackgroundColor(getResources().getColor(
                themeColors[themeIndex]));
        mainMenuAdapter.setColor(getResources().getColor(
                themeColors[themeIndex]));
        tvReload.setBackgroundColor(getResources().getColor(
                themeColors[themeIndex]));
        mainMenuAdapter.notifyDataSetChanged();
        StatusBarUtil.setRootViewFitsSystemWindows(this, true);
        //设置状态栏颜色
        StatusBarUtil.setStatusBarColor(this, getResources().getColor(
                themeColors[themeIndex]));
    }

    /**
     * 初始化NFC
     */
    private void initNfc() {
        fncObject = new FNCObject(getIntent(), MainActivity.this);
        fncObject.setOnFNCintf(this);
        fncObject.onResume();
    }

    /**
     * 调起相机拍照
     */
    private void openCamera() {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 判断是否有相机
        if (captureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            Uri photoUri = null;
            if (isAndroidQ) {// 适配android 10
                photoUri = PhotoUtils.createImageUri(this);
            } else {
                try {
                    photoFile = PhotoUtils.createImageFile(this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (photoFile != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        //适配Android 7.0文件权限，通过FileProvider创建一个content类型的Uri
                        photoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
                    } else {
                        photoUri = Uri.fromFile(photoFile);
                    }
                }
            }

            mCameraUri = photoUri;
            if (photoUri != null) {
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(captureIntent, ConstDataConfig.RESULT_CODE_CAMERA);
            }
        }
    }

    /**
     * 打开文件管理器
     */
    private void openFileManager() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//设置类型，我这里是任意类型，可以过滤文件类型
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, ConstDataConfig.RESULT_CODE_FILE);
    }


    /**
     * 选择图片
     */
    private void selectPhoto() {
        Intent albumIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        albumIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(albumIntent, ConstDataConfig.RESULT_CODE_PHOTO);
    }

    /**
     * 左上角文字按钮自定义
     *
     * @param bundle
     */
    private void onSetTopLeft(Bundle bundle) {
        String name = bundle.getString("name");
        String url = bundle.getString("url");
        tvLeft.setText(name);
        tvLeft.setTag(url);
        if (!StringUtils.isNull(name) && !StringUtils.isNull(url)) {
            tvLeft.setVisibility(View.VISIBLE);
            ivLeft.setVisibility(View.GONE);
        } else {
            tvLeft.setVisibility(View.GONE);
            ivLeft.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 文件选择
     *
     * @param bundle
     */
    private void onAppFile(Bundle bundle) {
        ConstDataConfig.isSelectFile = true;
        String fileType = bundle.getString("fileType");
        if ("拍照".equals(fileType)) {
            openCamera();
        } else if ("相册".equals(fileType)) {
            selectPhoto();
        } else if ("文件".equals(fileType)) {
            openFileManager();
        }
    }

    /**
     * WebView刷新
     */
    private void mWebViewReload(boolean isReload) {
        if (!NetworkUtil.isNetworkConnected(this)) {
            Hint.showShort(this, R.string.network_error);
            return;
        }
        if (isReload) {
            mWebView.reload();
        } else {
            mWebView.loadUrl(tvLeft.getTag().toString());
        }
    }

    /**
     * 视图显示
     *
     * @param state 显示状态
     */
    private void showView(int state) {
        if (state == 1) {
            mWebView.setVisibility(View.VISIBLE);
            rlLoadError.setVisibility(View.GONE);
        } else if (state == 2) {
            mWebView.setVisibility(View.GONE);
            rlLoadError.setVisibility(View.VISIBLE);
        } else if (state == 3) {
            mWebView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 跳转二维码扫描
     */
    private void startQrCode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !XXPermissions.isGranted(this, Permission.CAMERA)) {
            XXPermissions.with(this)
                    // 申请单个权限
                    .permission(Permission.CAMERA)
                    .request(new OnPermissionCallback() {
                        @Override
                        public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                            if (allGranted) {
                                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                                startActivityForResult(intent, CaptureActivity.REQ_QR_CODE);
                            }
                        }

//                        @Override
//                        public void hasPermission(List<String> granted, boolean all) {
//                            if (all) {
//                                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
//                                startActivityForResult(intent, CaptureActivity.REQ_QR_CODE);
//                            }
//                        }
//
//                        @Override
//                        public void noPermission(List<String> denied, boolean never) {
//                            if (never) {
//                                Hint.showShort(MainActivity.this, "被永久拒绝授权，请手动授予相机权限");
//                                // 如果是被永久拒绝就跳转到应用权限系统设置页面
//                                XXPermissions.startPermissionActivity(MainActivity.this, denied);
//                            } else {
//                                Hint.showShort(MainActivity.this, "获取相机权限失败");
//                            }
//                        }






                    });
        } else {
            Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
            startActivityForResult(intent, CaptureActivity.REQ_QR_CODE);
        }

    }

    /**
     * 选择文件
     */
    private void startSelectFile() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && (!XXPermissions.isGranted(this, Permission.CAMERA)
                || !XXPermissions.isGranted(this, Permission.Group.STORAGE))) {
            XXPermissions.with(this)
                    // 申请单个权限
                    .permission(Permission.CAMERA)
                    .permission(Permission.Group.STORAGE)
                    .request(new OnPermissionCallback() {
                        @Override
                        public void onGranted(@NonNull List<String> permissions, boolean allGranted) {
                            if (allGranted) {
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("meMenuList", (Serializable) ConstDataConfig.initFile());
                                DialogBuilderUtil.showMenuChildDialog(MainActivity.this, 1, mWebView, bundle);
                            }
                        }
                    });
        } else {
            Bundle bundle = new Bundle();
            bundle.putSerializable("meMenuList", (Serializable) ConstDataConfig.initFile());
            DialogBuilderUtil.showMenuChildDialog(MainActivity.this, 1, mWebView, bundle);
        }
    }


    /**
     * 选择文件失败
     */
    private void selectFileError() {
        if (!ConstDataConfig.isSelectFile) {
            mUploadCallbackAboveL.onReceiveValue(null);
            mUploadCallbackAboveL = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (fncObject != null) {
            fncObject.onResume();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (fncObject != null) {
            fncObject.onPause();
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (fncObject != null) {
            fncObject.resolveIntent(intent);
        }
    }


    // 发送消息,通知数据的改变
    private NewsCenter.NewsListener listener = new NewsCenter.NewsListener() {
        @Override
        public void onStatus(int msgType, int what, Bundle value) {
            myHandler.obtainMessage(msgType, value).sendToTarget();
        }
    };


    @Override
    public void signRequest(Object object, int requestType, int resultType) {
        if (requestType == AppRequest.REQ_MENU_LIST) {
            if (resultType == 1) {
                showView(2);
            } else {
                List<MenuBean> mList = (List<MenuBean>) object;
                list.clear();
                if (mList != null && !mList.isEmpty()) {
                    for (MenuBean menuBean : mList) {
                        if (menuBean.getFatherId().equals("0")) {
                            list.add(menuBean);
                        }
                    }
                    for (MenuBean menuBean : list) {
                        List<MenuBean> childList = new ArrayList<>();
                        for (MenuBean childBean : mList) {
                            if (childBean.getFatherId().equals(menuBean.getId())) {
                                childList.add(childBean);
                            }
                        }
                        menuBean.setChildMenuList(childList);
                    }
                }

                mainMenuAdapter.setColor(getResources().getColor(
                        themeColors[themeIndex]));
                mainMenuAdapter.notifyDataSetChanged();
                showView(1);
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @OnClick({R.id.tv_main_title_left, R.id.iv_main_title_refresh
            , R.id.iv_main_title_left, R.id.rl_main_tab_me
            , R.id.loaderror_btn_data_reload, R.id.tv_main_title})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_main_title_left://返回
                if (html5WebView != null && flContent != null && flContent.getVisibility() == View.VISIBLE) {
                    html5WebView.getWebChromeClient().onCloseWindow(html5WebView);
                } else if (mWebView.canGoBack()) {
                    mWebView.goBack();
                }
                break;
            case R.id.tv_main_title:
                long doubleExitTime = System.currentTimeMillis();
                if (mDoubleClickExit && (doubleExitTime - firstExitTime) < 3500) {
                    Bundle bundle = new Bundle();
                    bundle.putString("class", "server");
                    UIHelper.showActivity(MainActivity.this, SecurityVerificationActivity.class, bundle);
                    mDoubleClickExit = false;
                    clickCount = 0;
                } else {
                    firstExitTime = doubleExitTime;
                    clickCount += 1;
                    if (clickCount == 15) {
                        mDoubleClickExit = true;
                    }
                }
                break;
            case R.id.tv_main_title_left:
                showView(1);
                mWebViewReload(false);
                break;
            case R.id.iv_main_title_refresh:
                String serverUrl = AppContext.getPreference("SERVER_URL");
                if (StringUtils.isNull(serverUrl)) {
                    Hint.showShort(this, "请设置连接的服务器地址");
                    return;
                }
                if (list.isEmpty()) {
                    getMenuList(mFirst);
                    mWebView.loadUrl(AppConfig.newsUrl());
                } else {
                    mWebViewReload(true);
                }
                break;
            case R.id.rl_main_tab_me:
                showView(1);
                mWebView.loadUrl(AppConfig.autoRemoteUrl());
                break;
            case R.id.loaderror_btn_data_reload:
                getMenuList(true);
                mWebViewReload(true);
            default:
                break;

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (flContent.getVisibility() != View.VISIBLE && mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            } else {
                return false;
            }

        }
        return super.onKeyDown(keyCode, event);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CaptureActivity.REQ_QR_CODE && resultCode == Activity.RESULT_OK) {
            assert data != null;
            Bundle bundle = data.getExtras();
            String scanResult = bundle.getString(CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN);
            assert scanResult != null;
            String result = scanResult.trim();
            mWebView.loadUrl("javascript:scanResult('" + result + "')");
            if (mAppQrCode) {
                mWebView.evaluateJavascript("javascript:scanResult('" + result + "')", value -> {

                });
            }
        } else if (requestCode == ConstDataConfig.RESULT_CODE_CAMERA || requestCode == ConstDataConfig.RESULT_CODE_PHOTO || requestCode == ConstDataConfig.RESULT_CODE_FILE) {
            if (null == mUploadMessage && null == mUploadCallbackAboveL) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (mUploadCallbackAboveL != null) {
                onActivityResultAboveL(requestCode, data);
            } else if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        }
    }

    /**
     * 文件选择回调
     *
     * @param requestCode
     * @param data
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, Intent data) {
        if ((requestCode != ConstDataConfig.RESULT_CODE_CAMERA && requestCode != ConstDataConfig.RESULT_CODE_PHOTO && requestCode != ConstDataConfig.RESULT_CODE_FILE) || mUploadCallbackAboveL == null) {
            return;
        }
        Uri[] results;
        if (requestCode == ConstDataConfig.RESULT_CODE_CAMERA) {
            results = new Uri[]{mCameraUri};
        } else {
            if (data == null) {
                results = null;
            } else {
                results = new Uri[]{Uri.parse(data.getDataString())};
            }
        }
        mUploadCallbackAboveL.onReceiveValue(results);
        mUploadCallbackAboveL = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CaptureActivity.REQ_PERM_CAMERA) {
            // 摄像头权限申请
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 获得授权
                startQrCode();
            } else {
                // 被禁止授权
                Hint.showShort(this, R.string.camera);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void getFNcid(int type, final String value) {
        if (!StringUtils.isNull(value)) {
            Log.i(TAG, "识别成功,结果为：" + value);
            mWebView.loadUrl("javascript:nfcResult('" + value + "')");
        } else {
            Hint.showShort(this, "识别失败");
        }
    }

    @Override
    public void onWebViewLoadError(String url) {
        showView(2);
    }

    @Override
    public void onStartLoad() {
    }

    @Override
    public void image(ValueCallback<Uri[]> filePathCallback) {
        Log.d(TAG, "onShowFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture)");
        mUploadCallbackAboveL = filePathCallback;
        startSelectFile();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onLoadNewWebView(boolean isLoad, Html5WebView html5WebView) {
        this.html5WebView = html5WebView;
        html5WebView.getWebChromeClient().onProgressChanged(html5WebView, 100);
        html5WebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onCloseWindow(WebView window) {
                flContent.removeAllViews();
                flContent.setVisibility(View.GONE);
                ivRefresh.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(tvLeft.getText().toString().trim())) {
                    ivLeft.setVisibility(View.GONE);
                    tvLeft.setVisibility(View.VISIBLE);
                }
                super.onCloseWindow(window);
            }
        });
        flContent.removeAllViews();
        flContent.addView(html5WebView);
        flContent.setVisibility(View.VISIBLE);
        ivRefresh.setVisibility(View.GONE);
        tvLeft.setVisibility(View.GONE);
        ivLeft.setVisibility(View.GONE);
    }


    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.stopLoading();
            mWebView.clearHistory();
            mWebView.clearCache(true);
            mWebView.loadUrl("about:blank");
            mWebView.pauseTimers();
            mWebView = null;
        }
        NewsCenter.getInstance().detachListener(listener);
        if (myHandler != null) {
            myHandler.removeCallbacksAndMessages(null);
            myHandler = null;
        }
        super.onDestroy();
    }

    /**
     * Android与JS通信的接口
     */
    class AppInterface {

        /**
         * 左上角按钮文字和定向url
         *
         * @param name
         * @param url
         */
        @JavascriptInterface
        public void setActionButton(String name, String url) {
            Bundle bundle = new Bundle();
            bundle.putString("name", name);
            bundle.putString("url", url);
            myHandler.obtainMessage(NewsCenter.MSG_APP_TOP_LEFT, bundle).sendToTarget();
        }

        /**
         * 二维码/条形码扫描
         *
         * @param object
         */
        @JavascriptInterface
        public void scan(Object object) {
            mAppQrCode = false;
            startQrCode();
        }

        /**
         * fnc
         */
        @JavascriptInterface
        public void callNFC() {
            initNfc();
        }

    }


    private static class MyHandler extends Handler {
        private WeakReference<MainActivity> weakReference;

        MyHandler(MainActivity activity) {
            weakReference = new WeakReference<>(activity);

        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            MainActivity activity = weakReference.get();
            if (activity == null) {
                return;
            }
            Bundle bundle = (Bundle) msg.obj;
            switch (msg.what) {
                case NewsCenter.MSG_APP_TOP_LEFT://左上角图标自定义
                    activity.onSetTopLeft(bundle);
                    break;
                case NewsCenter.MSG_APP_THEME_CHANGE:// 改变主题
                    activity.setTopTitleBg();
                case NewsCenter.MSG_APP_SERVER_SAVE://服务器保存
                    activity.getMenuList(false);
                    activity.mWebView.loadUrl(AppConfig.newsUrl());
                    break;
                case NewsCenter.MSG_APP_QR_CODE://二维码扫描
                    activity.mAppQrCode = true;
                    activity.startQrCode();
                    break;
                case NewsCenter.MSG_APP_NFC://NFC
                    activity.initNfc();
                    break;
                case NewsCenter.MSG_APP_FILE://文件选择
                    activity.onAppFile(bundle);
                    break;
                case NewsCenter.MSG_APP_DIALOG_DISMISS:
                    activity.selectFileError();
                    break;
                default:
                    break;
            }
        }
    }

}
