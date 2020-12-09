package com.shuai.musicplayer2.utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitManager {
    private static Retrofit mRetrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://autumnfish.cn/")
            .build();
    public static Retrofit getRetrofit(){
        return mRetrofit;
    }
}
