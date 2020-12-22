package com.age.steward.car.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.age.steward.car.R;
import com.age.steward.car.adapter.MenuChildDialogAdapter;
import com.age.steward.car.bean.MenuBean;

import java.util.List;

import com.age.steward.car.AppConfig;
import com.age.steward.car.listeners.NewsCenter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MenuChildDialog extends Dialog implements AdapterView.OnItemClickListener {
    private Context mContext;
    @BindView(R.id.tv_menu_child_dialog_title)
    TextView tvTitle;
    @BindView(R.id.lv_menu_child_dialog_list)
    ListView listView;
    private List<MenuBean> menuList;
    private MenuChildDialogAdapter adapter;
    private String name;
    private int type;//0 菜单 1相册

    public MenuChildDialog(@NonNull Context context, int type, String name, List<MenuBean> menuList) {
        super(context, R.style.dialog);
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);
        this.mContext = context;
        this.menuList = menuList;
        this.name = name;
        this.type = type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAttrs();
        setContentView(R.layout.menu_child_dialog);
        ButterKnife.bind(this);
        initData();

    }

    private void initData() {
        setCanceledOnTouchOutside(true);//外部点击消失
        adapter = new MenuChildDialogAdapter(mContext, menuList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        tvTitle.setText(name);
    }

    //设置属性
    private void setAttrs() {
        Window window = getWindow();
        //设置dialog显示动画
        window.setWindowAnimations(R.style.AnimBottom);
        //下面代码是设置dialog宽度占满全屏
        // 把 DecorView 的默认 padding 取消，同时 DecorView 的默认大小也会取消
        window.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        // 设置宽度
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        //当数据来源大小大于4设置dialog高度为屏幕高度2/5
        if (menuList != null && menuList.size() > 4) {
            layoutParams.height = (int) (AppConfig.getAppConfig().getDeviceHeight() * 0.4);
        }
        window.setAttributes(layoutParams);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        MenuBean orgBean = (MenuBean) parent.getAdapter().getItem(position);
        dialogLister.setConfirm(orgBean, type);
        this.dismiss();
    }

    @Override
    public void dismiss() {
        if (type == 1) {
            NewsCenter.getInstance().onStatus(NewsCenter.MSG_APP_DIALOG_DISMISS, 0, null);
        }
        super.dismiss();
    }

    private MenuChildDialogLister dialogLister;

    public interface MenuChildDialogLister {
        void setConfirm(Object o, int type);
    }

    public void setOnConfirmLister(MenuChildDialogLister dialogLister) {
        this.dialogLister = dialogLister;
    }

}
