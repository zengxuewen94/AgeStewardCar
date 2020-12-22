package com.age.steward.car.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.age.steward.car.R;
import java.util.List;
import com.age.steward.car.AppConfig;
import com.age.steward.car.bean.MenuBean;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 */
public class MainMenuAdapter extends RecyclerView.Adapter<MainMenuAdapter.ViewHolder> {
    private Context mContext;
    private List<MenuBean> list;
    public MainMenuAdapter(Context mContext, List<MenuBean> list) {
        this.mContext = mContext;
        this.list = list;
        color=mContext.getResources().getColor(R.color.red_color);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.main_menu_list_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final MenuBean menuBean=list.get(i);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) viewHolder.rlItem .getLayoutParams();
        params.width = AppConfig.getAppConfig().getDeviceWidth() / 18*5;
        viewHolder.rlItem.setLayoutParams(params);
        viewHolder.tvMenuName.setText(menuBean.getName());
        viewHolder.itemView.setTag(i);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(menuBean);
            }
        });
        viewHolder.rlItem.setBackgroundColor(getColor());
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rl_main_menu_list_item)
        RelativeLayout rlItem;
        @BindView(R.id.tv_main_menu_list_item_menuName)
        TextView tvMenuName;
        @BindView(R.id.vw_main_menu_list_item_line)
        View viewLine;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
    public interface OnItemClickListener {
        void onItemClick(MenuBean menuBean);
        void onItemLongClick(MenuBean menuBean);
    }
    private OnItemClickListener onItemClickListener;

    public void setOnLongClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener=onItemClickListener;
    }

    private int color;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
