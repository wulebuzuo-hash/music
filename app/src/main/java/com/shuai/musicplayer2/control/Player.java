package com.shuai.musicplayer2.control;

import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.shuai.musicplayer2.R;
import com.shuai.musicplayer2.interfaces.IPlayerController;
import com.shuai.musicplayer2.interfaces.IPlayerViewController;
import com.shuai.musicplayer2.service.PlayService;
import com.shuai.musicplayer2.utils.DlCRUD;
import com.shuai.musicplayer2.utils.MenuList;
import com.shuai.musicplayer2.utils.LikeCRUD;


import java.io.File;

import static com.shuai.musicplayer2.interfaces.IPlayerController.PLAY_STATE_PAUSE;
import static com.shuai.musicplayer2.interfaces.IPlayerController.PLAY_STATE_START;
import static com.shuai.musicplayer2.interfaces.IPlayerController.PLAY_STATE_STOP;

public class Player extends AppCompatActivity {

    private static final int LAST_MUSIC = -1;
    private static final int NEXT_MUSIC = 1;
    private static final String TAG = "Player";
    private ImageView mPic;
    private ObjectAnimator mRotation;
    private IPlayerController mController;
    private PlayerConnection mPlayerConnection;
    private boolean isTouch = false;
    private SeekBar mSeek;
    private Button mSp;
    private static int mPosition;
    private static String mMusicId;;
    private com.shuai.musicplayer2.domain.MusicListInfo mMusicInfo;
    private boolean isLike = false;
    private Button mLike;
    private Button mDownload;
    private Button mComment;
    public static Handler sHandler;
    private Button mLast;
    private Button mNext;
    private Button mMv;
    private int mMenuSize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_player);
        //初始化数据
        initData();
        //初始化界面
        initView();
        //初始化控件的点击事件
        initEvent();
        initService();
        initBindService();
    }

    private void initData() {
        /**
         * 判断与上一首歌是否相同
         */
        Intent intent = getIntent();
        int newPosition = intent.getIntExtra("position",-1);
        String newMusicId = MenuList.sMusicListInfo.get(newPosition).getId();
        mMenuSize = MenuList.sMusicListInfo.size();
        if (mMusicId!=null&&mMusicId.equals(newMusicId)){
            isLike = true;
        }
        mPosition = newPosition;
    }

    /**
     * 根据数据设置初始界面
     */
    private void initView() {
        mMusicId = MenuList.sMusicListInfo.get(mPosition).getId();;
        mMusicInfo = MenuList.sMusicListInfo.get(mPosition);
        ((TextView)findViewById(R.id.player_title)).setText(mMusicInfo.getName());
        ((TextView)findViewById(R.id.player_alis)).setText(mMusicInfo.getAlia());
        ((TextView)findViewById(R.id.player_artists)).setText(mMusicInfo.getArtistsName());
        mLike = findViewById(R.id.player_like);
        mDownload = findViewById(R.id.player_download);
        mComment = findViewById(R.id.player_comment);
        mLast = findViewById(R.id.player_last);
        mNext = findViewById(R.id.player_next);
        mMv = findViewById(R.id.player_mv);
        if (mMusicInfo.getMvid()==0){
            mMv.setVisibility(View.GONE);
        }
        mPic = findViewById(R.id.player_pic);
        //设置背景图片
        Glide.with(mPic.getContext()).load(mMusicInfo.getPicUrl()).into(mPic);
        //设置下载的接收器，当接收到数据后会通知前台
        sHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                String showInfo = "";
                if(msg.what == 301){
                    showInfo = "下载成功";
                }else if(msg.what == 302){
                    showInfo="下载失败";
                }else if(msg.what == 303){
                    showInfo = "下载列表中已经存在";
                }
                Toast.makeText(Player.this, showInfo, Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        if (mRotation != null) {
            mRotation.start();
            return;
        }
        //设置旋转动画
        mRotation = ObjectAnimator
                .ofFloat(mPic, "rotation", 0,360)
                .setDuration(15000);
        mRotation.setRepeatCount(Animation.INFINITE);
        mRotation.setInterpolator(new LinearInterpolator());
        mRotation.start();
    }


    /**
     * 初始化音乐播放事件，包括进度条更新
     */

    private void initEvent() {
        mSeek = findViewById(R.id.player_seek);
        mSp = findViewById(R.id.player_sp);
        mSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //当进度条进度发生改变
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //当开始触摸进度条
                isTouch = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //停止触摸进度条
                if (mController != null) {
                    mController.seekTo(seekBar.getProgress());
                    isTouch=false;
                }
            }
        });

        //开始或暂停按钮播放按钮的点击事件
        mSp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mController != null) {
                    mController.pauseOrResume();
                }
            }
        });

        //添加喜欢的音乐的点击事件
        mLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new LikeCRUD().likeAdd(getApplicationContext(),mPosition);
            }
        });

        //添加下载按钮的点击事件
        mDownload.setOnClickListener(new View.OnClickListener() {
            // TODO: 2020/11/10 外部存储器的读取 
            @Override
            public void onClick(View v) {
                File path = getFilesDir();
                File file = new File(path,"/music/"+mMusicInfo.getName()+".mp3");
                Toast.makeText(Player.this, "开始下载", Toast.LENGTH_SHORT).show();
                new DlCRUD().downLoadMusic(mPosition,file);
            }
        });

        //添加评论按钮的点击事件
        mComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Player.this,Comment.class);
                intent.putExtra("name",mMusicInfo.getName());
                intent.putExtra("id",mMusicInfo.getId());
                startActivity(intent);
            }
        });

        //播放音乐mv
        mMv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Player.this, "MV播放功能暂未实现，程序员小哥哥正在抓紧实现中", Toast.LENGTH_SHORT).show();
            }
        });

        //上一首音乐按钮实现
        mLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeMusic(LAST_MUSIC);
            }
        });

        //下一首音乐实现
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeMusic(NEXT_MUSIC);
            }
        });
    }

    /**
     * 切换音乐
     * @param mode 切换类型（上一首（mode=-1）/下一首（model=1））
     */
    public void changeMusic(int mode){
        isLike = false;
        int newPosition;
        int i = mode;
        do {
            if (i!=mode){
                Toast.makeText(this, "已经为您自动跳过付费歌曲", Toast.LENGTH_SHORT).show();
            }
            newPosition = (mPosition+mode+mMenuSize)%mMenuSize;
            i = i+mode;
        }while (MenuList.sMusicListInfo.get(newPosition).getUrl()==null);
        mPosition = newPosition;
        initView();
        startPlay();
    }

    /**
     * 初始化音乐服务
     */
    private void initService() {
        Log.i(TAG,"->initService");
        startService(new Intent(this, PlayService.class));
    }

    /**
     * 绑定音乐服务
     */
    private void initBindService() {
        Log.i(TAG,"->initBindService");
        Intent intent = new Intent(this, PlayService.class);
        if (mPlayerConnection == null) {
            Log.i(TAG,"->mPlayerConnection");
            mPlayerConnection = new PlayerConnection();
        }
        bindService(intent,mPlayerConnection,BIND_AUTO_CREATE);
    }

    private class PlayerConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG,"->onServiceConnected");
            mController = (IPlayerController)service;
            //服务完成绑定后将UI控制器传到逻辑层
            mController.registerIPlayViewController(mPlayerViewController);
            startPlay();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG,"->onServiceDisconnected");
            mController = null;
        }
    }

    private IPlayerViewController mPlayerViewController = new IPlayerViewController() {
        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        @Override
        public void onPlayStateChange(int state) {
            switch (state){
                case PLAY_STATE_START:
                    mRotation.resume();
                    mSp.setBackgroundResource(R.drawable.pause);
                    break;
                case PLAY_STATE_PAUSE:
                    mRotation.pause();
                    mSp.setBackgroundResource(R.drawable.start);
                    //设置暂停
                case PLAY_STATE_STOP:
                    break;
            }
        }

        @Override
        public void onSeekChange(int seek) {

            /**
             * 设置进度条的进度
             */
            if (mSeek != null&&isTouch==false) {
                mSeek.setProgress(seek);
                if(seek == 100){
                    mController.seekTo(0);
                }
            }
        }
    };

    /**
     * 开始播放
     */
    private void startPlay() {
        //进入新的音乐
        if (mController != null) {
            mController.start(mMusicInfo.getUrl(), isLike);
        }
    }

    /**
     * 销毁时取消绑定服务
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayerConnection != null) {
            unbindService(mPlayerConnection);
            mPlayerViewController = null;
        }
    }
}
