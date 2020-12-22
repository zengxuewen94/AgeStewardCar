package com.age.steward.car.dialog;


import android.content.Context;
import android.os.Bundle;

import com.age.steward.car.bean.MenuBean;
import com.age.steward.car.expand.ConstDataConfig;
import com.age.steward.car.listeners.NewsCenter;
import com.age.steward.car.ui.custom.Html5WebView;

import java.util.List;

import com.age.steward.car.ui.SecurityVerificationActivity;
import com.age.steward.car.utils.UIHelper;

import static com.age.steward.car.expand.ConstDataConfig.SERVER_SETTING;
import static com.age.steward.car.expand.ConstDataConfig.THEME_SETTING;
import static com.age.steward.car.expand.ConstDataConfig.QR_CODE;
import static com.age.steward.car.expand.ConstDataConfig.NFC;

/**
 * 各种对话框显示类
 */
public class DialogBuilderUtil {

    public static void showMenuChildDialog(final Context mContext, final int t, final Html5WebView mWebView, final Bundle bundle) {
        MenuBean menuBean = (MenuBean) bundle.getSerializable("menuBean");
        List<MenuBean> childList = (List<MenuBean>) bundle.getSerializable("meMenuList");
        ConstDataConfig.isSelectFile=false;
        MenuChildDialog menuChildDialog = new MenuChildDialog(mContext, t, t == 0 ? menuBean.getName() : "", childList);
        menuChildDialog.setOnConfirmLister((o, type) -> {
            MenuBean childMenuBean = (MenuBean) o;
            if (type == 0) {
                if (THEME_SETTING.equals(childMenuBean.getUrl()) || SERVER_SETTING.equals(childMenuBean.getUrl())) {
                    bundle.putString("class", childMenuBean.getUrl());
                    UIHelper.showActivity(mContext, SecurityVerificationActivity.class, bundle);
                } else if (QR_CODE.equals(childMenuBean.getName())) {
                    bundle.putSerializable("qrCode", childMenuBean);
                    NewsCenter.getInstance().onStatus(NewsCenter.MSG_APP_QR_CODE, 0, bundle);
                } else if (NFC.equals(childMenuBean.getName())) {
                    bundle.putSerializable("nfc", childMenuBean);
                    NewsCenter.getInstance().onStatus(NewsCenter.MSG_APP_NFC, 0, bundle);
                } else {
                    mWebView.loadUrl(childMenuBean.getUrl());
                }
            } else {
                bundle.putSerializable("fileType", childMenuBean.getName());
                NewsCenter.getInstance().onStatus(NewsCenter.MSG_APP_FILE, 0, bundle);
            }
        });
        menuChildDialog.show();
    }

}