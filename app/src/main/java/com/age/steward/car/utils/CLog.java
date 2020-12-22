package com.age.steward.car.utils;

import android.util.Log;

/**
 * 
 * @Description: 调试注释显示类
 * @author dxh
 * @created 2015-3-24
 */
public class CLog {
	private static final String TAG = "AgeStewardCar_";
	private static final Boolean SWITCH = false;// log开关 开true 关false

	/** if CURRENT_LEVEL > DEFAULT_LEVEL log is show */
	private static final int DEFAULT_LEVEL = 1;
	public static final int CURRENT_LEVEL = 2;

	private static final int CLOG_I = 0;
	private static final int CLOG_V = 1;
	private static final int CLOG_D = 2;
	private static final int CLOG_E = 3;
	private static String classTag = "class_tag";

	public static void i(Object obj, String str) {
		if (SWITCH) {
			if (!classTag.equals(obj.getClass().getSimpleName()) && obj.getClass().getSimpleName().length() > 0) {
				CLog.outPut(CURRENT_LEVEL, CLOG_I, "log of <" + classTag + "> is --END--");
				classTag = obj.getClass().getSimpleName();
				CLog.outPut(CURRENT_LEVEL, CLOG_I, "=========================>> " + classTag + " <<=========================");
			}
			CLog.outPut(CURRENT_LEVEL, CLOG_I, "<" + obj.getClass().getSimpleName() + "> # " + str);
		}

	}

	public static void v(int level, Object obj, String str) {
		if (SWITCH)
			CLog.outPut(level, CLOG_V, "<" + obj.getClass().getSimpleName() + "> # " + str);
	}

	public static void d(Object obj, String str) {
		if (SWITCH)
			CLog.outPut(CURRENT_LEVEL, CLOG_D, "<" + obj.getClass().getSimpleName() + "> # " + str);
	}

	public static void e(Object obj, String str) {
		if (SWITCH)
			CLog.outPut(CURRENT_LEVEL, CLOG_E, "<" + obj.getClass().getSimpleName() + "> # " + str);
	}

	protected static void outPut(int level, int type, String str) {
		if (SWITCH) {
			if (level > DEFAULT_LEVEL) {
				switch (type) {
				case CLOG_I:
					Log.i(TAG + level, str);
					break;

				case CLOG_V:
					Log.v(TAG + level, str);
					break;

				case CLOG_D:
					Log.d(TAG + level, str);
					break;

				case CLOG_E:
					Log.e(TAG + level, str);
					break;

				default:
					break;
				}
			}
			return;
		}
	}
}