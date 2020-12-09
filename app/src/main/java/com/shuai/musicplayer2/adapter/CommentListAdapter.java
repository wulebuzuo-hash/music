package com.shuai.musicplayer2.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.shuai.musicplayer2.R;
import com.shuai.musicplayer2.domain.CommentList;
import com.shuai.musicplayer2.utils.GetCommentList;

import java.util.ArrayList;
import java.util.List;

public class CommentListAdapter extends RecyclerView.Adapter<CommentListAdapter.InnerHolder> {

    private View mItemView;
    private Context mContext;
    private List<CommentList.HotCommentsBean> mClList = new ArrayList<CommentList.HotCommentsBean>();
    private static final String TAG = "CommentListAdapter";

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext().getApplicationContext();
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_commentlist, null);
        return new InnerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        CommentList.HotCommentsBean commentsBean = mClList.get(position);
        CommentList.UserBean userBean = commentsBean.getUser();
        ImageView cl_pic=  mItemView.findViewById(R.id.cl_pic);
        Glide.with(mContext)
                .load(userBean.getAvatarUrl())
                .thumbnail(0.1f)
                .into(cl_pic);
        ((TextView)mItemView
                .findViewById(R.id.cl_name))
                .setText(userBean.getNickname());
        ((TextView)mItemView
                .findViewById(R.id.cl_description))
                .setText(commentsBean.getContent());
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return mClList.size();
    }

    /**
     * 设置数据
     */
    public void setData() {
        mClList.clear();
        mClList = GetCommentList.sHotComments;
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            mItemView = itemView;
        }
    }
}
