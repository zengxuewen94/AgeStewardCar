package com.age.steward.car;
/**
 * 应用配置类
 */
public class AppConfig {

	private int deviceWidth = 0;
	private int deviceHeight = 0;
	//应用主地址
	public static String mainHttpUrl ="http://3q1617t303.wicp.vip:80";
	//应用主地址
//	public static String mainHttpUrl ="http://3q1617t303.wicp.vip:23115/appapi";
	//	//应用地址
	private static String mainUrl(){ return mainHttpUrl;}
	public static String mainListRemoteUrl(){return  mainUrl()+"/Menu/List.asp";}
	public static String indexRemoteUrl(){return  mainUrl()+"/Index.asp";}
	public static String welcomeUrl() {return mainUrl()+"/Images/Welcome/splash.png";}
	public static String autoRemoteUrl(){return mainUrl()+"/Auto.asp";}
	public static String errorUrl(){return  "file:///android_asset/Error.html";}
	public static String indexUrl(){return  "file:///android_asset/Index.html";}
    public static String newsUrl(){return mainUrl()+"/News/List.asp";}
	private static AppConfig appConfig;
	public static AppConfig getAppConfig() {
		if (appConfig == null) {
			appConfig = new AppConfig();
		}
		return appConfig;
	}

	public int getDeviceWidth() {
		return deviceWidth;
	}

	public void setDeviceWidth(int deviceWidth) {
		this.deviceWidth = deviceWidth;
	}

	public int getDeviceHeight() {
		return deviceHeight;
	}

	public void setDeviceHeight(int deviceHeight) {
		this.deviceHeight = deviceHeight;
	}

	public static void setMainHttpUrl(String mainHttpUrl) {
		AppConfig.mainHttpUrl = mainHttpUrl+"";
	}

}
