package com.shuai.musicplayer2.control;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.shuai.musicplayer2.R;
import com.shuai.musicplayer2.adapter.TopListAdapter;
import com.shuai.musicplayer2.utils.DlCRUD;
import com.shuai.musicplayer2.utils.GetMenuList;
import com.shuai.musicplayer2.utils.LikeCRUD;
import com.shuai.musicplayer2.utils.MenuList;
import com.shuai.musicplayer2.utils.GetTopList;

import java.io.File;


public class Main extends AppCompatActivity {

    private EditText mEditText;
    private static final String TAG = "Main";
    private TopListAdapter mAdapter;
    public static Handler mHandler;
    private String mKeyWord;
    private RecyclerView mTopList;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        new GetTopList();
    }

    private void initView() {
        mEditText = findViewById(R.id.keywords);
        mTopList = findViewById(R.id.rv_toplist);
        mAdapter = new TopListAdapter();
        mProgressBar = findViewById(R.id.pv_main);
        mProgressBar.setVisibility(View.VISIBLE);
        mHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if (msg.what == 200) {
                    initTopList();
                    mProgressBar.setVisibility(View.GONE);
                }
                return false;
            }
        });
    }

    public void like(View view){
        if(new LikeCRUD().likeSelete(getApplicationContext(),10)){
            Intent intent = new Intent(Main.this,Result.class);
            intent.putExtra("Tag","我喜欢的音乐");
            intent.putExtra("keyword","");
            startActivity(intent);
        }else {
            Toast.makeText(this, "喜欢的音乐为空", Toast.LENGTH_SHORT).show();
        }
    }

    public void dlList(View view){
        startActivity(new Intent(Main.this,Download.class));
    }

    public void search(View view) {
        mKeyWord = mEditText.getText().toString();
        if(mKeyWord ==null|| mKeyWord.equals("")){
            Toast.makeText(this, "请输入搜索关键词", Toast.LENGTH_SHORT).show();
        }
        // TODO: 2020/11/11 实现加载更多后更改这里 
        new GetMenuList(30).getMusicList(mKeyWord);
        Intent intent = new Intent(Main.this,Result.class);
        intent.putExtra("Tag","搜索：");
        intent.putExtra("keyword",mKeyWord);
        startActivity(intent);
    }

    //实现点击的接口
    public void initListener(){
        mAdapter.setOnTopListClick(new TopListAdapter.OnTopListClickListener() {
            @Override
            public void onTopListClick(String id,String name) {
                // TODO: 2020/11/11 实现加载更多或更改这里 
                new GetMenuList(30).getTopList(id);
                Intent intent = new Intent(Main.this,Result.class);
                intent.putExtra("Tag","模块：");
                intent.putExtra("keyword",name);
                startActivity(intent);
            }
        });
    }

    //更新榜单信息
    private void initTopList() {
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,RecyclerView.VERTICAL);
        mAdapter.setData();
        mTopList.setAdapter(mAdapter);
        mTopList.setLayoutManager(layoutManager);
        initListener();
    }

}
