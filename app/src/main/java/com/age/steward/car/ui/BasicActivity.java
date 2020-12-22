package com.age.steward.car.ui;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.age.steward.car.AppConfig;
import com.age.steward.car.AppContext;
import com.age.steward.car.AppData;

import com.age.steward.car.utils.Hint;
import com.age.steward.car.utils.StatusBarUtil;


import com.age.steward.car.R;
import com.android.library.zxing.activity.CaptureActivity;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 应用程序Activity的基类
 */
public class BasicActivity extends RxAppCompatActivity {
	
	private View topLeftView;
	private View topRightView;
	private View middleView;
	private RelativeLayout rlMainTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setPreCustomTitle();
		setContentView(R.layout.basic_title_layout);
		//这里注意下 因为在评论区发现有网友调用setRootViewFitsSystemWindows 里面 winContent.getChildCount()=0 导致代码无法继续
		//是因为你需要在setContentView之后才可以调用 setRootViewFitsSystemWindows
	}

	private void setPreCustomTitle() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	//要显示的布局方法
	public void setBaseContentView(int layoutID){
		LinearLayout llContent = findViewById(R.id.ll_basic_content);
		//获得inflater
		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//把继承该BaseActivity的layoutID放进来 显示
		View view = inflater.inflate(layoutID, null);
		//addview
		llContent.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
	}

	/** 一定要放到setContentView(）方法之后，切记！！！ */
	protected void setCustomTitle() {
		topLeftView = findViewById(R.id.btn_basic_top_left);
		topRightView = findViewById(R.id.btn_basic_top_right);
		middleView = findViewById(R.id.btn_basic_main_title);
		rlMainTitle = findViewById(R.id.ll_basic_main_title);
		AppConfig appConfig = AppConfig.getAppConfig();
		initTitleBg();
		// 获取屏幕尺寸
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		appConfig.setDeviceWidth(dm.widthPixels);
		appConfig.setDeviceHeight(dm.heightPixels);
	}

	private void initTitleBg(){
		int themeIndex = 0;
		int index = AppContext.getIntPreference("APP_THEME_SELECT");
		if (index >= 0) {
			themeIndex = index;
		}
		int[] themeColors = AppData.THEME_COLORS;
		rlMainTitle.setBackgroundColor(getResources().getColor(
				themeColors[themeIndex]));

		//当FitsSystemWindows设置 true 时，会在屏幕最上方预留出状态栏高度的 padding
		StatusBarUtil.setRootViewFitsSystemWindows(this,true);
		//设置状态栏透明
		StatusBarUtil.setTranslucentStatus(this);
		StatusBarUtil.setStatusBarColor(this,getResources().getColor(
				themeColors[themeIndex]));
		//一般的手机的状态栏文字和图标都是白色的, 可如果你的应用也是纯白色的, 或导致状态栏文字看不清
		//所以如果你是这种情况,请使用以下代码, 设置状态使用深色文字图标风格, 否则你可以选择性注释掉这个if内容
//		if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
//			//如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
//			//这样半透明+白=灰, 状态栏的文字能看得清
//			StatusBarUtil.setStatusBarColor(this,0x55000000);
//		}
	}

	//设置Activity中的控件是否可修改
	protected void setViewEnable(boolean isChange){
		 if (!isChange) {
			 View view = this.getWindow().getDecorView();
				getAllChildViews(view);
				topLeftView.setEnabled(true);
		}
		
	}
	
	private List<View> getAllChildViews(View view) {
        List<View> allchildren = new ArrayList<View>();
        if (view instanceof ViewGroup) {
            ViewGroup vp = (ViewGroup) view;
            for (int i = 0; i < vp.getChildCount(); i++) {
                View viewchild = vp.getChildAt(i);
                if (viewchild != topLeftView) {
                	if (viewchild instanceof ListView) {
					}else if (viewchild instanceof GridView) {
						viewchild.setEnabled(false);
						((GridView) viewchild).setOnItemClickListener(null);
					}else{
						viewchild.setEnabled(false);
						viewchild.setOnClickListener(null);
					}
				}
                allchildren.add(viewchild);
                allchildren.addAll(getAllChildViews(viewchild));
            }
        }
        return allchildren;
    }

	// 隐藏整个标题栏
	protected void setMainTitleView(boolean bool) {
		if (bool) {
			rlMainTitle.setVisibility(View.INVISIBLE);
		} else {
			rlMainTitle.setVisibility(View.VISIBLE);
		}
	}
	
	protected void setMainTitleViewBg(int titleBg) {
		rlMainTitle.setBackgroundResource(titleBg);
	}
	
	protected void setMainTitleViewBgColor(int titleBgColor) {
		rlMainTitle.setBackgroundColor(titleBgColor);
	}

	// 设置左上角view 的点击事件监听器
	protected final void setTopLeftViewClick(View.OnClickListener listener) {
		topLeftView.setOnClickListener(listener);
	}

	// 设置右上角view 的点击事件监听器
	protected final void setTopRightViewClick(View.OnClickListener listener) {
		topRightView.setOnClickListener(listener);
	}
	

	@Override
	public void setTitle(CharSequence title) {
		if (middleView != null && title != null) {
			((TextView) this.middleView).setText(title);
		} else {
			super.setTitle(title);
		}
	}

	@Override
	public void setTitle(int titleId) {
		if (middleView != null) {
			((TextView) this.middleView).setText(this.getString(titleId));
		} else {
			super.setTitle(titleId);
		}
	}

	public void setTitleSize(int titleSize) {
		if (middleView != null) {
			((TextView) this.middleView).setTextSize(titleSize);
		} else {
			super.setTitle(titleSize);
		}
	}
	
	public void setTitleColor(int color){
		if (middleView != null) {
			((TextView) this.middleView).setTextColor(color);
		}
	}

	// 是否隐藏标题
	protected void setTitleView(boolean bool) {
		if (bool) {
			middleView.setVisibility(View.INVISIBLE);
		} else {
			middleView.setVisibility(View.VISIBLE);
		}
	}
	
	// 设置左上角view图片
	protected void setTopLeftViewBM(int bm) {
		((ImageView) topLeftView).setImageResource(bm);
		topLeftView.setVisibility(View.VISIBLE);
	}

	// 设置右上角view图片
	protected void setTopRightViewBM(int bm) {
		((ImageView) topRightView).setImageResource(bm);
		topRightView.setVisibility(View.VISIBLE);
	}
	//设置右上角View文本按钮
	protected void setTopRightViewCaption(String name){
		topRightView.setVisibility(View.INVISIBLE);
	}

	// 是否隐藏左上角view
	protected void setHideTopLeftView(boolean bool) {
		if (bool) {
			topLeftView.setVisibility(View.INVISIBLE);
		} else {
			topLeftView.setVisibility(View.VISIBLE);
		}
	}
	
	// 是否隐藏右上角view
	protected void setHideTopRightView(boolean bool) {
		if (bool) {
			topRightView.setVisibility(View.INVISIBLE);
		} else {
			topRightView.setVisibility(View.VISIBLE);
		}
	}

	// 设置右上角view是否可用
	protected void setTopRightViewEnable(boolean bool) {
		topRightView.setEnabled(bool);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}


	protected void requestPermissions(String[] permissions){
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
				// 申请权限
				ActivityCompat.requestPermissions(this, permissions, CaptureActivity.REQ_PERM_CAMERA);
				return;
			}
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode == CaptureActivity.REQ_PERM_CAMERA) {
			// 摄像头权限申请
			if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
				// 获得授权
				requestPermissionsResult();
			} else {
				// 被禁止授权
				Hint.showShort(this, R.string.camera);
			}
		}
	}


	public void requestPermissionsResult(){}


}