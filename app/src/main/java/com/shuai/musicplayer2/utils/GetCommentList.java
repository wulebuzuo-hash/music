package com.shuai.musicplayer2.utils;

import android.os.Message;
import android.util.Log;

import com.shuai.musicplayer2.api.Api;
import com.shuai.musicplayer2.control.Comment;
import com.shuai.musicplayer2.domain.CommentList;

import org.w3c.dom.ls.LSException;

import java.net.HttpURLConnection;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class GetCommentList {
    private final Retrofit mRetrofit;
    private final Api mApi;
    private static final String TAG = "GetCommentList";
    public static List<CommentList.HotCommentsBean> sHotComments;

    public GetCommentList(String id){
        mRetrofit = RetrofitManager.getRetrofit();
        mApi = mRetrofit.create(Api.class);
        Call<CommentList> commentTask = mApi.getComment(id);
        commentTask.enqueue(new Callback<CommentList>() {
            @Override
            public void onResponse(Call<CommentList> call, Response<CommentList> response) {
                if (response.code()== HttpURLConnection.HTTP_OK){
                    sHotComments = response.body().getHotComments();
                    Message message = Message.obtain();
                    message.what = 500;
                    Comment.mHandler.sendMessage(message);
                }
            }

            @Override
            public void onFailure(Call<CommentList> call, Throwable t) {
                Log.i(TAG,"获取失败");
                Log.i(TAG,t.toString());
                Message message = Message.obtain();
                message.what = 600;
                Comment.mHandler.sendMessage(message);
            }
        });
    }
}
