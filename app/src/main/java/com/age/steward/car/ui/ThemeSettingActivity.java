package com.age.steward.car.ui;


import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

import com.age.steward.car.AppConfig;
import com.age.steward.car.AppContext;
import com.age.steward.car.AppData;
import com.age.steward.car.R;
import com.age.steward.car.listeners.NewsCenter;
import com.age.steward.car.ui.custom.LineEditText;
import com.age.steward.car.utils.Hint;
import com.age.steward.car.utils.StringUtils;
import com.age.steward.car.utils.ViewUtil;
import com.android.library.retrofit.RHttp;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 主题设置
 */
public class ThemeSettingActivity extends BasicActivity implements OnClickListener {
    private CheckBox[] checkBoxs = new CheckBox[4];
    private int themeSelectIndex = 0;
    @BindView(R.id.et_server_setting_serverPath)
    LineEditText etServerUrl;
    @BindView(R.id.btn_server_setting_save)
    Button btnServerSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setBaseContentView(R.layout.theme_setting_act);
        setPagerTitle();
        ButterKnife.bind(this);
        initView();
        initData();
    }

    private void setPagerTitle() {
        setCustomTitle();
        setTitle("设置");
        setTitleSize(20);
        setTopLeftViewBM(R.drawable.title_left_back);
        setTopLeftViewClick(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        RelativeLayout rl_theme_1 = findViewById(R.id.rl_theme_1);
        RelativeLayout rl_theme_2 = findViewById(R.id.rl_theme_2);
        RelativeLayout rl_theme_3 = findViewById(R.id.rl_theme_3);
        RelativeLayout rl_theme_4 = findViewById(R.id.rl_theme_4);
        rl_theme_1.setOnClickListener(this);
        rl_theme_2.setOnClickListener(this);
        rl_theme_3.setOnClickListener(this);
        rl_theme_4.setOnClickListener(this);

        checkBoxs[0] = findViewById(R.id.cb_theme_1);
        checkBoxs[1] = findViewById(R.id.cb_theme_2);
        checkBoxs[2] = findViewById(R.id.cb_theme_3);
        checkBoxs[3] = findViewById(R.id.cb_theme_4);
        checkBoxs[0].setOnClickListener(this);
        checkBoxs[1].setOnClickListener(this);
        checkBoxs[2].setOnClickListener(this);
        checkBoxs[3].setOnClickListener(this);
        btnServerSave.setOnClickListener(this);
    }


    private void initData() {

        String serverUrl = AppContext.getPreference("SERVER_URL");
        etServerUrl.setText(serverUrl);
        int themeIndex = 0;
        int index = AppContext.getIntPreference("APP_THEME_SELECT");
        if (index >= 0) {
            themeIndex = index;
        }
        setCheckBoxData(themeIndex);
    }

    private void setCheckBoxData(int selectIndex) {
        for (int i = 0; i < 4; i++) {
            if (selectIndex == i) {
                checkBoxs[i].setChecked(true);
                themeSelectIndex = i;
            } else {
                checkBoxs[i].setChecked(false);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_theme_1:
            case R.id.cb_theme_1:
                setCheckBoxData(0);
                break;
            case R.id.rl_theme_2:
            case R.id.cb_theme_2:
                setCheckBoxData(1);
                break;
            case R.id.rl_theme_3:
            case R.id.cb_theme_3:
                setCheckBoxData(2);
                break;
            case R.id.rl_theme_4:
            case R.id.cb_theme_4:
                setCheckBoxData(3);
                break;
            case R.id.btn_server_setting_save:
                String serverUrl = etServerUrl.getText().toString().trim();
                AppConfig.setMainHttpUrl(serverUrl);
                if (!StringUtils.isNull(serverUrl)) {
                    ViewUtil.hideSoftInput(ThemeSettingActivity.this);
                    AppContext.putPreference("SERVER_URL", serverUrl);
                    NewsCenter.getInstance().onStatus(NewsCenter.MSG_APP_SERVER_SAVE, 0, null);
                } else {
                    Hint.showShort(this, "请设置连接的服务器地址");
                }

                AppContext.putPreference("APP_THEME_SELECT", themeSelectIndex);
                NewsCenter.getInstance().onStatus(NewsCenter.MSG_APP_THEME_CHANGE, 0, null);
                int[] themeColors = AppData.THEME_COLORS;
                setMainTitleViewBgColor(getResources().getColor(
                        themeColors[themeSelectIndex]));
                finish();
                break;
            default:
                break;
        }
    }


}
