package com.shuai.musicplayer2.control;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.shuai.musicplayer2.R;
import com.shuai.musicplayer2.adapter.CommentListAdapter;
import com.shuai.musicplayer2.utils.GetCommentList;

public class Comment extends AppCompatActivity {

    public static Handler mHandler;
    private CommentListAdapter mAdapter;
    private RecyclerView mCl;
    private String mId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        initView();
        //加载评论的数据
        new GetCommentList(mId);
    }

    private void initView() {
        Intent intent = getIntent();
        mId = intent.getStringExtra("id");
        ((TextView)findViewById(R.id.comment_info)).setText("歌曲评论："+intent.getStringExtra("name"));
        ProgressBar pb = findViewById(R.id.comment_pb);
        mCl = findViewById(R.id.comment_list);
        mAdapter = new CommentListAdapter();
        pb.setVisibility(View.VISIBLE);
        mHandler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                if (msg.what==500){
                    pb.setVisibility(View.GONE);
                    Toast.makeText(Comment.this, "评论加载成功", Toast.LENGTH_SHORT).show();
                    updateComment();
                }else if(msg.what==600){
                    pb.setVisibility(View.GONE);
                    Toast.makeText(Comment.this, "评论加载失败", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private void updateComment() {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,RecyclerView.VERTICAL);
        mAdapter.setData();
        mCl.setAdapter(mAdapter);
        mCl.setLayoutManager(layoutManager);
    }

}
