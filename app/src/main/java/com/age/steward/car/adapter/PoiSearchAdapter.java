package com.age.steward.car.adapter;
import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.age.steward.car.R;
import com.baidu.mapapi.search.core.PoiInfo;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;


public class PoiSearchAdapter extends RecyclerView.Adapter<PoiSearchAdapter.SearchHistoryViewHolder> {

    private Context mContext;
    private List<PoiInfo> mList;

    public PoiSearchAdapter(Context context, List<PoiInfo> list) {
        this.mContext = context;
        this.mList = list;
    }

    @NonNull
    @Override
    public SearchHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.poi_search_item, viewGroup, false);
        return new SearchHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchHistoryViewHolder viewHolder, int i) {
        PoiInfo poiInfo=mList.get(i);
        viewHolder.mTvContent.setText(poiInfo.name);
        viewHolder.mTvXianLu.setText(poiInfo.address);
    }

    @Override
    public int getItemCount() {
        return mList==null?0:mList.size();
    }

    class SearchHistoryViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_poi_search_item_content)
        TextView mTvContent;
        @BindView(R.id.tv_poi_search_item_xianlu)
        TextView mTvXianLu;
        public SearchHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
