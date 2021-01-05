package com.age.steward.car;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.multidex.MultiDexApplication;

import com.android.library.retrofit.RHttp;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import com.age.steward.car.utils.CLog;
import com.age.steward.car.utils.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;

/**
 * @Description: 应用程序扩展类
 * @author zxw
 * @created 2015-3-24
 */
public class AppContext extends MultiDexApplication {
	private static AppContext mInstance=null;
	private static Context _context = null;
	private int versionCode;
	private String versionName;
	private long firstInstallTime;
	public static final String FIRST_INSTALL_TIME = "first_install_time";
	private static SharedPreferences sharedPreferences = null;

	public int getVersionCode() {
		return versionCode;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		//Thread.setDefaultUncaughtExceptionHandler(restartHandler);
		if (_context == null) {
			_context = this;
		}
		if (mInstance==null){
			mInstance=this;
		}
		if (getCurProcessName(this) != null && !"com.age.steward.car".equals(getCurProcessName(this))) {
			return;
		}
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		RHttp.Configure.get()
				.baseUrl(AppConfig.mainHttpUrl)      //基础URL
				.init(this);
		getAppVersionCode();

		//DataCenter.getInstance().init();
		//* 获取数据库表为全局数据，延迟500ms执行，等DataCenter的thread启动之后再执行。
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				AppData.getInstance().initInstance();
			}
		}, 500);
		if (AppContext.getBPreference("isCreateIcon")) {
			addShortcut();
			AppContext.putPreference("isCreateIcon", false);

		}
		//在使用SDK各组件之前初始化context信息，传入ApplicationContext
		SDKInitializer.initialize(this);
		//自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
		//包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
		SDKInitializer.setCoordType(CoordType.BD09LL);
	}

	public static AppContext getInstance(){
		if (mInstance == null) {
			mInstance = new AppContext();
		}
		if (sharedPreferences==null){
			sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mInstance);
		}
		return mInstance;
	}

	public static AppContext getAppContext() {
		return (AppContext)_context;
	}

	public static void putPreference(String key, boolean value) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public static boolean getBPreference(String key) {
		return sharedPreferences.getBoolean(key, false);
	}

	public static void putPreference(String key, String value) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public static String getStringPreference(String key, String defValue) {
		return sharedPreferences.getString(key, defValue);
	}

	public static String getPreference(String key) {
		return sharedPreferences.getString(key, null);
	}

	public static void putLongPreference(String key, long time) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putLong(key, time);
		editor.commit();
	}

	public static long getLongPreference(String key) {
		return sharedPreferences.getLong(key, -1);
	}

	public static void putPreference(String key, int value) {
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public static int getIntPreference(String key) {
		return sharedPreferences.getInt(key, -1);
	}

	public void getAppVersionCode() {
		try {
			PackageManager pm = getPackageManager();
			PackageInfo pinfo = pm.getPackageInfo(getPackageName(), PackageManager.GET_CONFIGURATIONS);
			versionName = pinfo.versionName;
			versionCode = pinfo.versionCode;
			// 当前Android系统版本是否在（ Gingerbread） Android 2.3x或 Android 2.3x 以上
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {// 如果大于
				firstInstallTime = pinfo.firstInstallTime;// 应用第一次安装的时间
			} else {
				firstInstallTime = getLongPreference(FIRST_INSTALL_TIME);
				if (firstInstallTime == -1) {
					Calendar calendar = Calendar.getInstance();
					firstInstallTime = calendar.getTimeInMillis();
					putLongPreference(FIRST_INSTALL_TIME, firstInstallTime);
				} else {
					firstInstallTime = getLongPreference(FIRST_INSTALL_TIME);
				}
			}

			ApplicationInfo applicationInfo = pm.getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
			//int SOURCE_ID = applicationInfo.metaData.getInt("SOURCE_ID");
			WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			String MAC = wifi.getConnectionInfo().getMacAddress();

		} catch (NameNotFoundException e) {
			Log.e("Exception","Exception:"+e.getMessage());
		}
	}

	public String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			CLog.e("WifiPreference IpAddress", ex.toString());
		}
		return "";
	}

	/**
	 * 为程序创建桌面快捷方式
	 */
	private void addShortcut() {
		Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");

		// 快捷方式的名称
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
		shortcut.putExtra("duplicate", false); // 不允许重复创建

		// 指定当前的Activity为快捷方式启动的对象: 如 com.everest.video.VideoPlayer
		// 注意: ComponentName的第二个参数必须是完整的类名（包名+类名），否则无法删除快捷方式
		ComponentName comp = new ComponentName(this.getPackageName(), this.getPackageName() + ".AppStart");

		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		intent.setComponent(comp);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
		// 快捷方式的图标
		ShortcutIconResource iconRes = ShortcutIconResource.fromContext(this, R.drawable.ic_launcher);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
		sendBroadcast(shortcut);
	}
	
	/**
	 * 判断缓存数据是否可读
	 */
	private boolean isReadDataCache(String cachefile){
		return readObject(cachefile) != null;
	}
	
	/**
	 * 判断缓存是否存在
	 */
	private boolean isExistDataCache(String cachefile)
	{
		boolean exist = false;
		File data = getFileStreamPath(cachefile);
		if(data.exists())
			exist = true;
		return exist;
	}
	
	/**
	 * 保存磁盘缓存
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public void setDiskCache(String key, String value) throws IOException {
		try(FileOutputStream fos = openFileOutput("cache_"+key+".data", Context.MODE_PRIVATE)){
			fos.write(value.getBytes());
			fos.flush();
		}
	}
	
	/**
	 * 获取磁盘缓存数据
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public String getDiskCache(String key) throws IOException {
		try(FileInputStream fis = openFileInput("cache_"+key+".data")){
			byte[] datas = new byte[fis.available()];
			int count = fis.read(datas);
			return new String(datas);
		}
	}
	
	/**
	 * 保存对象
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public boolean saveObject(Serializable ser, String file) {
		try(FileOutputStream fos = openFileOutput(file, MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos)){
			oos.writeObject(ser);
			oos.flush();
			return true;
		}catch(Exception e){
			Log.e("Exception","Exception:"+e.getMessage());
			return false;
		}
	}
	
	/**
	 * 读取对象
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public Serializable readObject(String file){
		if(!isExistDataCache(file))
			return null;
		try(FileInputStream fis = openFileInput(file);
            ObjectInputStream ois = new ObjectInputStream(fis)){
			return (Serializable)ois.readObject();
		}catch(InvalidClassException e){
			Log.e("Exception","Exception:"+e.getMessage());
			//反序列化失败 - 删除缓存文件
			File data = getFileStreamPath(file);
			if (data.exists()) {
				data.deleteOnExit();
			}
		} catch (FileNotFoundException e) {
			Log.e("Exception","Exception:"+e.getMessage());
		} catch (IOException e) {
			Log.e("Exception","Exception:"+e.getMessage());
		} catch (ClassNotFoundException e) {
			Log.e("Exception","Exception:"+e.getMessage());
		}
		return null;
	}
	
	private String getCurProcessName(Context context) {
		int pid = android.os.Process.myPid();
		ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
			if (appProcess.pid == pid) {
				return appProcess.processName;
			}
		}
		return null;
	}
	
	public static boolean isServiceWork(Context mContext, String serviceName) {
	    boolean isWork = false;  
	    ActivityManager myAM = (ActivityManager) mContext
	            .getSystemService(Context.ACTIVITY_SERVICE);
	    List<RunningServiceInfo> myList = myAM.getRunningServices(40);
	    if (myList.size() <= 0) {  
	        return false;  
	    }  
	    for (int i = 0; i < myList.size(); i++) {  
	        String mName = myList.get(i).service.getClassName().toString();
	        if (mName.equals(serviceName)) {  
	            isWork = true;  
	            break;  
	        }  
	    }  
	    return isWork;  
	}

	//异常崩溃Crash重启
	private Thread.UncaughtExceptionHandler restartHandler = new Thread.UncaughtExceptionHandler() {
		public void uncaughtException(Thread thread, Throwable ex) {
			restartApp();
		}
	};

	//重新APP
	public void restartApp() {
		Intent intent = new Intent(getApplicationContext(), AppStart.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK );
		startActivity(intent);
		android.os.Process.killProcess(android.os.Process.myPid());
	}

}
