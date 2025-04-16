package com.example.gomind.api;

import android.content.Context;

import com.example.gomind.SharedPrefManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
public class RetrofitClient {
    private static final String BASE_URL =  "https://www.gwork.press:8443/";
    private static RetrofitClient mInstance;
    private Retrofit retrofit;
    public static CookieManager cookieManager;
    private RetrofitClient(Context context){
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        cookieManager = new CookieManager();
// Создание AuthInterceptor, теперь передаем оба параметра
       AuthInterceptor authInterceptor = new AuthInterceptor(context);


        //   cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(loggingInterceptor)
                .cookieJar(cookieManager)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static synchronized RetrofitClient getInstance(Context context){
        if(mInstance == null){
            mInstance = new RetrofitClient(context);
        }

        return mInstance;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public AuthAPI getAuthApi(){
        return retrofit.create(AuthAPI.class);
    }

//    public PaymentAPI getPaymentApi(){
//        return retrofit.create(PaymentAPI.class);
//    }

    public UserAPI getUserAPI(){
        return retrofit.create(UserAPI.class);
    }

    public QuestionAPI getQuestionAPI(){
        return retrofit.create(QuestionAPI.class);
    }



}
