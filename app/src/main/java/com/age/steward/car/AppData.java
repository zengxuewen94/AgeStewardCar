package com.age.steward.car;


import android.content.Context;


/**
 * 应用程序数据类
 */
public class AppData {

	private Context mContext;
	private static AppData mInstance = null;

	// 主题背景色
	public static int[] THEME_COLORS = { R.color.red_color,
			R.color.green_color, R.color.blue_color, R.color.yellow_color };
	// 按钮背景色
	public static int[] THEME_BUTTON_COLORS = { R.drawable.button_red_bg_selector,
			R.drawable.button_green_bg_selector, R.drawable.button_blue_bg_selector, R.drawable.button_yellow_bg_selector };

	public static AppData getInstance() {
		if (mInstance == null) {
			mInstance = new AppData();
		}
		return mInstance;
	}

	public void initInstance() {

	}




}
