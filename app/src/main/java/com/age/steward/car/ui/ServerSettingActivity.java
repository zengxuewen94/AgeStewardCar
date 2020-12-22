package com.age.steward.car.ui;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import com.android.library.retrofit.callback.HttpCallback;
import com.android.library.retrofit.http.OnHttpCallBack;
import com.age.steward.car.AppConfig;
import com.age.steward.car.AppContext;
import com.age.steward.car.AppData;
import com.age.steward.car.R;
import com.age.steward.car.listeners.NewsCenter;
import com.age.steward.car.ui.custom.LineEditText;
import com.age.steward.car.utils.Hint;
import com.age.steward.car.utils.StringUtils;
import com.age.steward.car.utils.ViewUtil;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 服务器设置
 */
public class ServerSettingActivity  {
////	@BindView(R.id.et_server_setting_serverPath)
////	LineEditText etServerUrl;
////	@BindView(R.id.btn_server_setting_save)
//	View btnServerSave ;
//	private String mTag="ServerSettingActivity";
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setBaseContentView(R.layout.server_setting_act);
//		setPagerTitle();
//		ButterKnife.bind(this);
//		initView();
//		setButtonBg();
//		initData();
//		requestPermissions(new String[]{Manifest.permission.CAMERA});
//	}
//
//	@Override
//	public void requestPermissionsResult() {
//		Hint.showLong(this,"6666666666");
//	}
//
//	private void setPagerTitle() {
//		setCustomTitle();
//		setTitle("服务器设置");
//		setTopLeftViewBM(R.drawable.title_left_back);
//		setTopLeftViewClick(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				finish();
//			}
//		});
//	}
//
//	private void initView(){
//		btnServerSave.setOnClickListener(this);
//	}
//	//设置标题的背景色
//	private void setButtonBg(){
//		int themeIndex = 0;
//		int index = AppContext.getIntPreference("APP_THEME_SELECT");
//		if (index >= 0) {
//			themeIndex = index;
//		}
//		int[] themeColors= AppData.THEME_BUTTON_COLORS;
//		btnServerSave.setBackgroundResource(themeColors[themeIndex]);
//
//	}
//	private void initData(){
//		String serverUrl = AppContext.getPreference("SERVER_URL");
//		etServerUrl.setText(serverUrl);
//	}
//
//	@Override
//	public void onClick(View arg0) {
//		String serverUrl = etServerUrl.getText().toString().trim();
//		if (arg0.getId()==R.id.btn_server_setting_save){
//			AppConfig.setMainHttpUrl(serverUrl);
//			if (!StringUtils.isNull(serverUrl)) {
//				ViewUtil.hideSoftInput(ServerSettingActivity.this);
//				AppContext.putPreference("SERVER_URL", serverUrl);
//				NewsCenter.getInstance().onStatus(NewsCenter.MSG_APP_SERVER_SAVE,0,null);
//				finish();
//			}else{
//				Hint.showShort(this, "请设置连接的服务器地址");
//			}
//		}
//	}
//
//
//
//
//	@Override
//	public void onSuccess(HttpCallback httpCallback, Object o) {
//
//	}
//
//	@Override
//	public void onError(HttpCallback httpCallback, int i, String s) {
//
//
//	}
//
//	@Override
//	public void onCancel() {
//
//	}
//
//	@Override
//	public void onLogin() {
//
//	}
}
