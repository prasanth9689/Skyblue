package com.skyblue.skybluea.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient(){

        List<Protocol> protocols = new ArrayList<Protocol>()
        {{
            add(Protocol.HTTP_1_1); // <-- The only protocol used
            add(Protocol.HTTP_2);
        }};

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .callTimeout(0, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .protocols(protocols)
                .build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();


        if(retrofit ==null){
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://skyblue.co.in")
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
        return  retrofit;
    }
}
