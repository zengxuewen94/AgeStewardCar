package com.age.steward.car.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.os.EnvironmentCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.age.steward.car.expand.ConstDataConfig;
import com.age.steward.car.utils.PhotoUtils;
import com.age.steward.car.utils.TimeUtil;
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

import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
    private MainMenuAdapter mainMenuAdapter;
    private List<MenuBean> list = new ArrayList<>();
    private FNCObject fncObject;
    private boolean mDoubleClickExit = false;
    private boolean mFirst = true;
    private boolean mAppQrCode = false;//
    private long firstExitTime = 0;
    private int themeIndex = 0;
    private int[] themeColors = AppData.THEME_COLORS;
    private static final String TAG = MapActivity.class.getSimpleName();
    private int clickCount = 0;//标题点击次数
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mUploadCallbackAboveL;
    private final static int RESULT_CODE_CAMERA = 100;
    private final static int RESULT_CODE_PHOTO = 110;
    private final static int RESULT_CODE_FILE = 102;
    //用于保存拍照图片的uri
    private Uri mCameraUri;
    // 用于保存图片的文件路径，Android 10以下使用图片路径访问图片
    private String mCameraImagePath;
    // 是否是Android 10以上手机
    private boolean isAndroidQ = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    //This is Test Git Push
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
        setTopTitleBg();
        initData();
        NewsCenter.getInstance().attachListener(listener);
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
        //在js中调用本地java方
        mWebView.addJavascriptInterface(new AppInterface(this), "androidObj");
        mWebView.loadUrl(AppConfig.newsUrl());
        mWebView.setOnHtml5WebViewListener(this);
        mWebView.requestFocus(View.FOCUS_DOWN);


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
     * 调起相机拍照
     */
    private void openCamera() {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 判断是否有相机
        if (captureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            Uri photoUri = null;

            if (isAndroidQ) {
                // 适配android 10
                photoUri = createImageUri();
            } else {
                try {
                    photoFile = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (photoFile != null) {
                    mCameraImagePath = photoFile.getAbsolutePath();
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
                startActivityForResult(captureIntent, RESULT_CODE_CAMERA);
            }
        }
    }

    public void openFileManager() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");//设置类型，我这里是任意类型，可以过滤文件类型
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, RESULT_CODE_FILE);
    }

    /**
     * 创建图片地址uri,用于保存拍照后的照片 Android 10以后使用这种方法
     */
    private Uri createImageUri() {
        String status = Environment.getExternalStorageState();
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        } else {
            return getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, new ContentValues());
        }
    }

    /**
     * 创建保存图片的文件
     */
    private File createImageFile() throws IOException {
        String imageName = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) {
            storageDir.mkdir();
        }
        File tempFile = new File(storageDir, imageName);
        if (!Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(tempFile))) {
            return null;
        }
        return tempFile;
    }


    /**
     * 选择图
     */
    private void selectPhoto() {
        Intent albumIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        albumIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(albumIntent, RESULT_CODE_PHOTO);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (fncObject != null) fncObject.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (fncObject != null) fncObject.onPause();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (fncObject != null) fncObject.resolveIntent(intent);
    }

    private void initData() {
        String serverUrl = AppContext.getPreference("SERVER_URL");
        if (StringUtils.isNull(serverUrl)) {
            Hint.showShort(this, "请设置连接的服务器地址");
            return;
        }
        AppConfig.setMainHttpUrl(serverUrl);
        getMenuList(mFirst);
        mWebView.loadUrl(AppConfig.newsUrl());
        mFirst = false;
    }


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

    private void mWebViewReload() {
        if (!NetworkUtil.isNetworkConnected(this)) {
            Hint.showShort(this, R.string.network_error);
            return;
        }
        mWebView.reload();
    }

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

    private void mWebViewLoad() {
        if (!NetworkUtil.isNetworkConnected(this)) {
            Hint.showShort(this, R.string.network_error);
            return;
        }
        mWebView.loadUrl(tvLeft.getTag().toString());
    }

    /**
     * 跳转二维码扫描
     */
    private void startQrCode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !XXPermissions.hasPermission(this, Permission.CAMERA)) {
            XXPermissions.with(this)
                    // 申请单个权限
                    .permission(Permission.CAMERA)
                    .request(new OnPermission() {

                        @Override
                        public void hasPermission(List<String> granted, boolean all) {
                            if (all) {
                                Intent intent = new Intent(MainActivity.this, CaptureActivity.class);
                                startActivityForResult(intent, CaptureActivity.REQ_QR_CODE);
                            }
                        }

                        @Override
                        public void noPermission(List<String> denied, boolean never) {
                            if (never) {
                                Hint.showShort(MainActivity.this, "被永久拒绝授权，请手动授予录相机权限");
                                // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                XXPermissions.startPermissionActivity(MainActivity.this, denied);
                            } else {
                                Hint.showShort(MainActivity.this, "获取相机权限失败权限失败");
                            }
                        }
                    });
//            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                // 申请权限
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CaptureActivity.REQ_PERM_CAMERA);
//                return;
//            }
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
                && (!XXPermissions.hasPermission(this, Permission.CAMERA)
                || !XXPermissions.hasPermission(this, Permission.Group.STORAGE))) {
            XXPermissions.with(this)
                    // 申请单个权限
                    .permission(Permission.CAMERA)
                    .permission(Permission.Group.STORAGE)
                    .request(new OnPermission() {

                        @Override
                        public void hasPermission(List<String> granted, boolean all) {
                            if (all) {
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("meMenuList", (Serializable) ConstDataConfig.initFile());
                                DialogBuilderUtil.showMenuChildDialog(MainActivity.this, 1, mWebView, bundle);
                            }
                        }

                        @Override
                        public void noPermission(List<String> denied, boolean never) {
                            if (never) {
                                Hint.showShort(MainActivity.this, "被永久拒绝授权，请手动授予录相机权限");
                                // 如果是被永久拒绝就跳转到应用权限系统设置页面
                                XXPermissions.startPermissionActivity(MainActivity.this, denied);
                            } else {
                                Hint.showShort(MainActivity.this, "获取部分权限失败");
                                selectFileError();
                            }
                        }
                    });
//            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                // 申请权限
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CaptureActivity.REQ_PERM_CAMERA);
//                return;
        } else {
            Bundle bundle = new Bundle();
            bundle.putSerializable("meMenuList", (Serializable) ConstDataConfig.initFile());
            DialogBuilderUtil.showMenuChildDialog(MainActivity.this, 1, mWebView, bundle);
        }
    }

    private void initNfc() {
        fncObject = new FNCObject(getIntent(), MainActivity.this);
        fncObject.setOnFNCintf(this);
        fncObject.onResume();
    }

    private Bundle setBundleData(MenuBean menuBean, List<MenuBean> menuList) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("menuBean", menuBean);
        bundle.putSerializable("meMenuList", (Serializable) menuList);
        return bundle;
    }


    // 发送消息,通知数据的改变
    private NewsCenter.NewsListener listener = new NewsCenter.NewsListener() {

        @Override
        public void onStatus(int msgType, int what, Bundle value) {
            Message msg = mHandler.obtainMessage();
            msg.what = msgType;
            msg.setData(value);
            mHandler.sendMessage(msg);
        }
    };


    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            switch (msg.what) {
                case 200:
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
                    break;
                case NewsCenter.MSG_APP_THEME_CHANGE:// 改变主题
                    setTopTitleBg();
                case NewsCenter.MSG_APP_SERVER_SAVE://服务器保存
                    getMenuList(false);
                    String oldUrl = mWebView.getUrl();
                    String newUrl = AppConfig.newsUrl();
                    if (newUrl.substring(newUrl.length() - 7, newUrl.length() - 1)
                            .equals(oldUrl.substring(oldUrl.length() - 7, oldUrl.length() - 1))) {
                        if (newUrl.equals(oldUrl)) {
                            mWebView.reload();
                        } else {
                            mWebView.setNeedClear(true);
                            mWebView.loadUrl(newUrl);
                        }
                    } else {
                        mWebView.reload();
                    }
                    break;
                case NewsCenter.MSG_APP_QR_CODE://二维码扫描
                    mAppQrCode = true;
                    startQrCode();
                    break;
                case NewsCenter.MSG_APP_NFC://nfc
                    initNfc();
                    break;
                case NewsCenter.MSG_APP_FILE:
                    ConstDataConfig.isSelectFile = true;
                    String fileType = bundle.getString("fileType");
                    if ("拍照".equals(fileType)) {
                        openCamera();
                    } else if ("相册".equals(fileType)) {
                        selectPhoto();
                    } else if ("文件".equals(fileType)) {
                        openFileManager();
                    }
                    break;
                case NewsCenter.MSG_APP_DIALOG_DISMISS:
                    selectFileError();
                    break;
                default:
                    break;
            }

            return false;
        }
    });

    private void selectFileError() {
        if (!ConstDataConfig.isSelectFile) {
            mUploadCallbackAboveL.onReceiveValue(null);
            mUploadCallbackAboveL = null;
        }
    }


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


    @OnClick({R.id.tv_main_title_left, R.id.iv_main_title_refresh
            , R.id.iv_main_title_left, R.id.rl_main_tab_me
            , R.id.loaderror_btn_data_reload, R.id.tv_main_title})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_main_title_left:
                if (mWebView.canGoBack()) {
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
                mWebViewLoad();
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
                    mWebViewReload();
                }
                break;
            case R.id.rl_main_tab_me:
                showView(1);
                mWebView.loadUrl(AppConfig.autoRemoteUrl());
                break;
            case R.id.loaderror_btn_data_reload:
                getMenuList(true);
                mWebViewReload();
            default:
                break;

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                    return true;
                } else {
                    return false;
                }
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
            Log.e("scanResult", scanResult.toString().trim());
            String result = scanResult.toString().trim();
            mWebView.loadUrl("javascript:scanResult('" + result + "')");
            if (mAppQrCode) {
                mWebView.evaluateJavascript("javascript:scanResult('" + result + "')", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {

                    }
                });
            }
        } else if (requestCode == RESULT_CODE_CAMERA || requestCode == RESULT_CODE_PHOTO || requestCode == RESULT_CODE_FILE) {
            if (null == mUploadMessage && null == mUploadCallbackAboveL) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (mUploadCallbackAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent data) {
        if ((requestCode != RESULT_CODE_CAMERA && requestCode != RESULT_CODE_PHOTO && requestCode != RESULT_CODE_FILE) || mUploadCallbackAboveL == null) {
            return;
        }
        Uri[] results = null;
        if (requestCode == RESULT_CODE_CAMERA) {
            if (isAndroidQ) {
                results = new Uri[]{mCameraUri};
            } else {
                results = new Uri[]{Uri.parse(mCameraImagePath)};
            }
        } else if (requestCode == RESULT_CODE_PHOTO || requestCode == RESULT_CODE_FILE) {
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
        //mWebView.setNeedClear(true);
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


    /**
     * Android与JS通信的接口
     */
    class AppInterface {

        private Context mContext;

        AppInterface(Context mContext) {
            this.mContext = mContext;
        }

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
            Message msg = mHandler.obtainMessage();
            msg.what = 200;
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }

        public void loadUrl(String url) {
            mWebView.loadUrl(url);
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
        super.onDestroy();
    }

}
