package com.age.steward.car.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.age.steward.car.ui.MainActivity;

/**
 * @Description: 打开界面接口类
 * @author dxh
 * @created 2015-4-11
 */
public class UIHelper {
	
	/**
	 * 跳转到拨号盘
	 */
	public static void showCallView(Context context, String number){
		if (number!=null&&!number.trim().equals("")) {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_DIAL);
			intent.setData(Uri.parse("tel:"+number));
			context.startActivity(intent);
		}
	}

	public static void showActivity(Context context, Class<?> cls,Bundle bundle){
		Intent intent = new Intent(context, cls);
		intent.putExtra("bundle",bundle);
		((Activity)context).startActivityForResult(intent,1);
	}

}
