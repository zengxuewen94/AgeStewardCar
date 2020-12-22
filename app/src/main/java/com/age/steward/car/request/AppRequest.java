package com.age.steward.car.request;

import android.content.Context;

import com.age.steward.car.R;
import com.age.steward.car.bean.MenuBean;
import com.age.steward.car.http.AppJsonParser;
import com.android.library.retrofit.RHttp;
import com.android.library.retrofit.callback.HttpCallback;
import com.android.library.retrofit.http.OnHttpCallBack;
import com.age.steward.car.AppConfig;
import com.age.steward.car.utils.Hint;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.util.List;
import java.util.Map;

/**
 * 网络请求封装类
 */
public class AppRequest implements OnHttpCallBack {
    private Context mContext;
    private String mTag;
    private LifecycleProvider mLifecycleProvider;
    public static final int REQ_MENU_LIST = 1;//菜单查询

    public static AppRequest getInstance(Context mContext, String mTag, LifecycleProvider mLifecycleProvider) {
        return new AppRequest(mContext, mTag, mLifecycleProvider);
    }

    private AppRequest(Context mContext, String mTag, LifecycleProvider mLifecycleProvider) {
        this.mContext = mContext;
        this.mTag = mTag;
        this.mLifecycleProvider = mLifecycleProvider;
    }


    public void getMenuList(Map<String, Object> data,boolean showDialog) {
        RHttp http = new RHttp.Builder(mContext)
                .get()
                .lifecycle(mLifecycleProvider)
                .tag(mTag + REQ_MENU_LIST)
                .setDialogContent("数据加载中...")
                .setShowDialog(showDialog)
                .projectUrl("car") //需要使用的token的项目名
                .apiUrl(AppConfig.mainListRemoteUrl())
                .addParameter(data)
                .setWhat(REQ_MENU_LIST)
                .build();
        http.request(this);
    }


    @Override
    public void onSuccess(HttpCallback httpCallback, Object data) {
        int what = httpCallback.getWhat();
        if (what == REQ_MENU_LIST) {
            List<MenuBean> list = null;
            try {
                list = AppJsonParser.getMenuList(data.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
            RHttp.cancel(mTag + REQ_MENU_LIST);
            if (onSignRequest != null) {
                onSignRequest.signRequest(list,REQ_MENU_LIST,0);
            }
        }
    }

    @Override
    public void onError(HttpCallback httpCallback, int i, String s) {
        RHttp.cancel(mTag + REQ_MENU_LIST);
        int what = httpCallback.getWhat();
        if (what == REQ_MENU_LIST) {
            Hint.showShort(mContext,mContext.getText(R.string.server_error));
            RHttp.cancel(mTag + REQ_MENU_LIST);
            if (onSignRequest != null) {
                onSignRequest.signRequest(null,REQ_MENU_LIST,1);
            }
        }
    }

    @Override
    public void onCancel() {
        //
    }

    @Override
    public void onLogin() {
    }

    public interface OnSignRequest {
        void signRequest(Object object, int requestType, int resultType);
    }

    private OnSignRequest onSignRequest;

    public void setOnSignRequest(OnSignRequest onSignRequest) {
        this.onSignRequest = onSignRequest;
    }
}
