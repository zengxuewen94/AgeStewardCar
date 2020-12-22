package com.age.steward.car.bean;
import com.age.steward.car.http.AppJsonParser;
import java.io.Serializable;



/**
 * 更新实体类
 */
public class Update implements Serializable{
	private int versionCode;
	private String versionName;
	private String downloadUrl;
	private String updateLog;
	public int getVersionCode() {
		return versionCode;
	}
	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}
	public String getVersionName() {
		return versionName;
	}
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	public String getDownloadUrl() {
		return downloadUrl;
	}
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
	public String getUpdateLog() {
		return updateLog;
	}
	public void setUpdateLog(String updateLog) {
		this.updateLog = updateLog;
	}
	
	public static Update parse(String inputText) {
		Update update = null;
		try{
			//if (!StringUtils.isNull(inputText)){
				update = AppJsonParser.getAppJsonParser().getUpdateEntity(inputText);
			//}
			
		}catch(Exception e){
			update = null;
		}
		return update;
	}
}
