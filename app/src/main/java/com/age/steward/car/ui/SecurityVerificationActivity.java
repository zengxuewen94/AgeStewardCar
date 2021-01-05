package com.age.steward.car.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.android.library.retrofit.callback.HttpCallback;
import com.android.library.retrofit.http.OnHttpCallBack;

import com.age.steward.car.AppConfig;
import com.age.steward.car.AppContext;
import com.age.steward.car.AppData;
import com.age.steward.car.R;
import com.age.steward.car.dialog.DialogBuilderUtil;
import com.age.steward.car.expand.ConstDataConfig;
import com.age.steward.car.ui.custom.LineEditText;
import com.age.steward.car.utils.Hint;
import com.age.steward.car.utils.StringUtils;
import com.age.steward.car.utils.UIHelper;
import com.age.steward.car.utils.ViewUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 安全验证
 */
public class SecurityVerificationActivity extends BasicActivity implements OnHttpCallBack, OnClickListener {
    @BindView(R.id.et_security_verification_content)
    LineEditText etText;
    @BindView(R.id.btn_security_verification_save)
    View btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBaseContentView(R.layout.security_verification_act);
        setPagerTitle();
        ButterKnife.bind(this);
        setButtonBg();

    }

    private void setPagerTitle() {
        setCustomTitle();
        setTitle("安全验证");
        setTopLeftViewBM(R.drawable.title_left_back);
        setTopLeftViewClick(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    //设置标题的背景色
    private void setButtonBg() {
        int themeIndex = 0;
        int index = AppContext.getIntPreference("APP_THEME_SELECT");
        if (index >= 0) {
            themeIndex = index;
        }
        int[] themeColors = AppData.THEME_BUTTON_COLORS;
        btnSave.setBackgroundResource(themeColors[themeIndex]);

    }

    @OnClick(R.id.btn_security_verification_save)
    @Override
    public void onClick(View arg0) {
        String text = etText.getText().toString().trim();
        if (arg0.getId() == R.id.btn_security_verification_save) {
            if (!StringUtils.isNull(text) && ConstDataConfig.PASSWORD.equals(text)) {
                ViewUtil.hideSoftInput(SecurityVerificationActivity.this);
                Class<?> cls = ThemeSettingActivity.class;
                Bundle bundle = getIntent().getBundleExtra("bundle");
                UIHelper.showActivity(SecurityVerificationActivity.this,
                        cls, bundle);
                finish();
            } else {
                Hint.showShort(this, "请输入正确的验证码");
            }
        }
    }


    @Override
    public void onSuccess(HttpCallback httpCallback, Object o) {

    }

    @Override
    public void onError(HttpCallback httpCallback, int i, String s) {
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onLogin() {

    }
}
