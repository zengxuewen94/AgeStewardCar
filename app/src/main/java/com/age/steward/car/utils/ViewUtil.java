package com.age.steward.car.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.age.steward.car.R;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ViewUtil {
	private static long lastClickTime;

	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if ( 0 < timeD && timeD < 800) {
			return true;
		}
		lastClickTime = time;
		return false;
	}


	/**
	 * 隐藏软键盘
	 */
	public static void hideSoftInputView(Activity activity) {
		InputMethodManager manager = ((InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE));
		if (activity.getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (activity.getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
	
	/**
	 * 隐藏键盘
	 */
	public static void hideSoftInput(Activity activity){
		View view = activity.getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputManger = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputManger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
	}

	/**
	 * 显示键盘
	 */
	public static void showSoftInput(Activity activity, View view){
		if (view != null) {
			InputMethodManager inputmanger = (InputMethodManager) activity. getSystemService(Context.INPUT_METHOD_SERVICE);
			inputmanger.showSoftInput(view, 0);
		}
	}
	
	/**
	 * 设置布局中子控件点击状态
	 */
	public static void setViewEnable(boolean isenable, View view){
		getAllChildViews(isenable,view);
	}
	
	private static List<View> getAllChildViews(boolean isenable, View view) {
        List<View> allchildren = new ArrayList<View>();
        if (view instanceof ViewGroup) {
            ViewGroup vp = (ViewGroup) view;
            for (int i = 0; i < vp.getChildCount(); i++) {
                View viewchild = vp.getChildAt(i);
                viewchild.setEnabled(isenable);
           	 	//viewchild.setBackgroundResource(R.drawable.edt_name_notput);
                allchildren.add(viewchild);
                allchildren.addAll(getAllChildViews(isenable,viewchild));
            }
        }
        return allchildren;
    }
	
	/**
	 * 计算GridView的高度
	 */
	public static void setGridViewHeightBasedOnChildren(GridView gridView) {
	    // 获取gridView的adapter  
	       ListAdapter listAdapter = gridView.getAdapter();
	       if (listAdapter == null) {  
	           return;  
	       }  
	       // 固定列宽，有多少列  
	       int col = 1;// gridView.getNumColumns();  
	       int totalHeight = 0;  
	       // i每次加4，相当于listAdapter.getCount()小于等于4时 循环一次，计算一次item的高度，  
	       // listAdapter.getCount()小于等于8时计算两次高度相加  
	       for (int i = 0; i < listAdapter.getCount(); i += col) {  
	        // 获取gridView的每一个item  
	           View listItem = listAdapter.getView(i, null, gridView);
	           listItem.measure(0, 0);  
	           // 获取item的高度和  
	           totalHeight += listItem.getMeasuredHeight();  
	       }  
	  
	       // 获取gridView的布局参数  
	       ViewGroup.LayoutParams params = gridView.getLayoutParams();
	       // 设置高度  
	       params.height = totalHeight;  
	       // 设置margin  
	       //((MarginLayoutParams) params).setMargins(10, 0, 10, 0);  
	       // 设置参数  
	       gridView.setLayoutParams(params);  
	   } 
	
	/**
	 * 计算ListView的高度
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {
	    // 获取gridView的adapter  
	   ListAdapter listAdapter = listView.getAdapter();
	   if (listAdapter == null) {
		   return;
	   }
	   // 固定列宽，有多少列
	   int col = 1;// gridView.getNumColumns();
	   int totalHeight = 0;
	   // i每次加4，相当于listAdapter.getCount()小于等于4时 循环一次，计算一次item的高度，
	   // listAdapter.getCount()小于等于8时计算两次高度相加
	   for (int i = 0; i < listAdapter.getCount(); i += col) {
		// 获取gridView的每一个item
		   View listItem = listAdapter.getView(i, null, listView);
		   listItem.measure(0, 0);
		   // 获取item的高度和
		   totalHeight += listItem.getMeasuredHeight();
	   }

	   // 获取gridView的布局参数
	   ViewGroup.LayoutParams params = listView.getLayoutParams();
	   // 设置高度
	   params.height = totalHeight+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
	   // 设置margin
	   //((MarginLayoutParams) params).setMargins(10, 0, 10, 0);
	   // 设置参数
	   listView.setLayoutParams(params);
   }

	/**
	 * 根据资源名称获取资源在项目中的ID
	 */
   	public static int getResourceIdByName(String name){
		Class drawable = R.drawable.class;
		try {
			Field field = drawable.getField(name);
			return  field.getInt(field.getName());
		} catch (NoSuchFieldException e) {
			Log.e("Exception","Exception:"+e.getMessage());
		} catch (IllegalAccessException e) {
			Log.e("IllegalAccessException","Exception:"+e.getMessage());
		}
		return 0;
	}

}
