package com.age.steward.car.expand;


import com.age.steward.car.bean.MenuBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 常量数据类
 */
public class ConstDataConfig {
    public static final String THEME_SETTING = "theme";
    public static final String SERVER_SETTING = "server";
    public static final String QR_CODE = "qrCode";
    public static final String NFC = "nfc";
    public static boolean isSelectFile = false;
    public static List<MenuBean> initFile() {
        List<MenuBean> meMenuList = new ArrayList<>();
        meMenuList.add(new MenuBean("1", "拍照", "1", THEME_SETTING));
        meMenuList.add(new MenuBean("2", "相册", "2", SERVER_SETTING));
        meMenuList.add(new MenuBean("3", "文件", "3", SERVER_SETTING));
        return meMenuList;
    }

    //存储权限
    public static final int REQUEST_CODE_EXTERNAL_STORAGE = 0x02;
    //位置权限
    public static final int REQUEST_CODE_FINE_LOCATION = 0x03;
    //密码
    public static final String PASSWORD = "Abc.1234";

    public final static int RESULT_CODE_CAMERA = 100;
    public final static int RESULT_CODE_PHOTO = 101;
    public final static int RESULT_CODE_FILE = 102;
}
