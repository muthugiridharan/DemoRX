package com.projects.sainkinnovation.demorx.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkClient {
    private static Retrofit retrofit;
    private static final String BASE_URL="http://api.themoviedb.org/3/";

    public void NetworkClient(){

    }

    public static Retrofit getRetrofit(){

        if(retrofit==null){
            OkHttpClient.Builder builder = new OkHttpClient.Builder().connectTimeout(30,TimeUnit.SECONDS).readTimeout(30,TimeUnit.SECONDS).writeTimeout(30,TimeUnit.SECONDS);
            OkHttpClient okHttpClient = builder.build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(okHttpClient)
                    .build();

        }

        return retrofit;
    }

}
