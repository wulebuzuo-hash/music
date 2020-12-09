package com.shuai.musicplayer2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shuai.musicplayer2.R;
import com.shuai.musicplayer2.domain.TopList;
import com.shuai.musicplayer2.utils.GetTopList;

import java.util.ArrayList;
import java.util.List;

public class TopListAdapter extends RecyclerView.Adapter<TopListAdapter.InnerHolder> {

    private View mItemView;
    private Context mContext;
    private List<TopList.ListBean>  mTopList = new ArrayList<TopList.ListBean>();
    private OnTopListClickListener mTopListClickListener;
    private static final String TAG = "TopListAdapter";

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext().getApplicationContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_toplist, null);
        return new InnerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        TopList.ListBean listBean = mTopList.get(position);
        ImageView toplist_pic=  mItemView.findViewById(R.id.cl_pic);
        Glide.with(mContext)
                .load(listBean.getCoverImgUrl())
                .thumbnail(0.1f)
                .into(toplist_pic);
        ((TextView)mItemView
                .findViewById(R.id.toplist_updateFrequency))
                .setText(listBean.getUpdateFrequency());
        ((TextView)mItemView
                .findViewById(R.id.cl_name))
                .setText(listBean.getName());
        ((TextView)mItemView
                .findViewById(R.id.cl_description))
                .setText(listBean.getDescription());
        holder.setInfo(listBean.getId(),listBean.getName());
    }

    @Override
    public int getItemCount() {
        return mTopList.size();
    }

    public void setOnTopListClick(OnTopListClickListener listener){
        mTopListClickListener = listener;
    }

    public interface OnTopListClickListener{
        void onTopListClick(String id,String name);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    /**
     * 设置数据
     */
    public void setData() {
        mTopList.clear();
        if (GetTopList.sTopList!=null){
            mTopList.addAll(GetTopList.sTopList);
        }
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        private String mId;
        private String mName;
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            mItemView = itemView;
            mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mTopListClickListener != null){
                        mTopListClickListener.onTopListClick(mId,mName);
                    }
                }
            });
        }
        public void setInfo(String id,String name){
            mId = id;
            mName = name;
        }
    }
}
