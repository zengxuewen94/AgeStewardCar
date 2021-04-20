package com.age.steward.car.listeners;

import java.util.LinkedList;
import java.util.List;

import android.os.Bundle;

import com.age.steward.car.utils.CLog;

/**
 * @author zxw
 * @Description: 消息改变监听中心
 * @created 2015-4-15
 */

public class NewsCenter {
    // 改变主题
    public static final int MSG_APP_THEME_CHANGE = 1;
    // 服务器设置
    public static final int MSG_APP_SERVER_SAVE = 2;
    // 二维码扫描
    public static final int MSG_APP_QR_CODE = 3;
    // NFC
    public static final int MSG_APP_NFC = 4;
    // 选择文件
    public static final int MSG_APP_FILE = 5;
    // dialog消失
    public static final int MSG_APP_DIALOG_DISMISS = 6;
    // app标题左上角自定义图标
    public static final int MSG_APP_TOP_LEFT = 7;
    private final String LOG_TAG = "NewsCenter";
    private static NewsCenter newsCenter;
    private List<NewsListener> listenerList = new LinkedList<>();

    public static NewsCenter getInstance() {
        if (newsCenter == null) {
            newsCenter = new NewsCenter();
        }
        return newsCenter;
    }

    public void attachListener(NewsListener listener) {
        synchronized (listenerList) {
            if (listenerList == null) {
                return;
            }
            boolean listenerExist = false;
            for (NewsListener item : listenerList) {
                if (item != null && item == listener) {
                    listenerExist = true;
                    break;
                }
            }
            if (listenerExist == false) {
                listenerList.add(listener);
            }
        }
    }

    public void onStatus(int msgType, int what, Bundle value) {
        synchronized (listenerList) {
            for (NewsListener listener : listenerList) {
                listener.onStatus(msgType, what, value);
            }
        }
    }

    public void detachListener(NewsListener listener) {
        synchronized (listenerList) {
            if (listenerList == null) {
                CLog.e(LOG_TAG, "IN DataCenter,  Error, listDBListener is null");
                return;
            }
            boolean ret = listenerList.remove(listener);
            if (ret == false) {
                System.out
                        .println("IN DataCenter,  detachListener()===, IDBListener detach fail=====");
            } else {
                System.out
                        .println("IN DataCenter,  detachListener()===, IDBListener detach OK!!=====");
            }
            System.out
                    .println("IN DataCenter,  After detachListener(), listenerSIZE=="
                            + listenerList.size());
        }
    }

    public interface NewsListener {
        void onStatus(int msgType, int what, Bundle value);// 通知有新的消息
    }


}
