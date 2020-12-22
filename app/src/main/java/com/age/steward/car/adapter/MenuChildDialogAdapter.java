package com.age.steward.car.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.age.steward.car.R;
import com.age.steward.car.bean.MenuBean;

import java.util.List;


/**
 * 子菜单Adapter
 */
public class MenuChildDialogAdapter extends BaseAdapter {
    private Context mContext;
    private List<MenuBean> list;
    public MenuChildDialogAdapter(Context mContext, List<MenuBean> list){
        this.mContext=mContext;
        this.list=list;
    }
    @Override
    public int getCount() {

        return list==null?0:list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder ;
        if(convertView==null){
            holder=new ViewHolder();
            convertView= LayoutInflater.from(mContext).inflate(R.layout.menu_child_dialog_item,null);

            holder.tvName=convertView.findViewById(R.id.tv_menu_child_dialog_item_name);
            convertView.setTag(holder);
        }else {
            holder= (ViewHolder) convertView.getTag();
        }
        final MenuBean bean=list.get(position);
        holder.tvName.setText(bean.getName());
        return convertView;
    }

    private static class ViewHolder {

        private TextView tvName;

    }
}
