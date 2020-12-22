package com.age.steward.car.http;

import com.age.steward.car.bean.MenuBean;
import com.age.steward.car.bean.Update;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;

import cn.hutool.json.JSONNull;

/**
 * JSON解析类
 */
public class AppJsonParser {

    public static AppJsonParser getAppJsonParser() {
        return new AppJsonParser();
    }

    // 如果是4.0以下版本调用这个方法BOM
    private static String JSONTokener(String in) {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion < 14) {// 14为4.0
            // consume an optional byte order mark (BOM) if it exists
            if (in != null && in.startsWith("\ufeff")) {
                in = in.substring(1);
            }
            return in;
        }
        return in;
    }

	//解析String类型的Json数据
	private static String optString(JSONObject jsonObject, String str) {
		return jsonObject.isNull(str) ? "" : jsonObject.optString(str);
	}

	/**
	 * 解析更新实体信息
	 */
	public static Update getUpdateEntity(String str) throws Exception {
		Update update = null;
		str = JSONTokener(str);
		JSONObject jsonObject = new JSONObject(str);
		int resCode = jsonObject.optInt("result");
		if (resCode == 0) {
			return update;
		}
		JSONObject updateObject = jsonObject.optJSONObject("update");
		if (updateObject != null) {
			update = new Update();
			update.setVersionCode(updateObject.optInt("VersionCode"));
			update.setVersionName(updateObject.optString("VersionName"));
			update.setUpdateLog(updateObject.optString("UpdateLog"));
			update.setDownloadUrl(updateObject.optString("DownloadUrl"));
		}
		return update;
	}


    /**
     * 将json对象中包含的null和JSONNull属性修改成""
     * @param jsonObj
     */
    static JSONObject filterNull(JSONObject jsonObj) {
        Iterator<String> it = jsonObj.keys();
        Object obj = null;
        String key = null;
        while (it.hasNext()) {
            try{
                key = it.next();
                obj = jsonObj.get(key);
                if (obj instanceof JSONObject) {
                    filterNull((JSONObject) obj);
                }
                if (obj instanceof JSONArray) {
                    JSONArray objArr = (JSONArray) obj;
                    for (int i = 0; i < objArr.length(); i++) {
                        filterNull(objArr.getJSONObject(i));
                    }
                }
                if (obj == null || obj instanceof JSONNull) {
                    jsonObj.put(key, "");
                }
                if (obj.equals(null)) {
                    jsonObj.put(key, "");
                }
            }catch (Exception e){

            }
        }
        return jsonObj;
    }

    public static List<MenuBean> getMenuList(String str) throws JSONException {
        str = JSONTokener(str);
        JSONArray jsonArray=new JSONArray(str);
        List<MenuBean> list=new ArrayList<>();
        HashMap<String, MenuBean> map = new HashMap<>();
        for (int i = 0; i <jsonArray.length() ; i++) {
            JSONObject object = jsonArray.optJSONObject(i);
            if (object!=null){
                MenuBean menuBean = new MenuBean();
                menuBean.setId(object.optString("id"));
                menuBean.setFatherId(object.optString("fatherId"));
                menuBean.setName(object.optString("name"));
                menuBean.setUrl(object.optString("url"));
                menuBean.setOrderId(object.optString("orderId"));
                menuBean.setSelected(object.optString("selected"));
                list.add(menuBean);
            }
        }
        return list;
    }

}
