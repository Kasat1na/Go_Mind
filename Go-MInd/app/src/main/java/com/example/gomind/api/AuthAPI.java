package com.example.gomind.api;

import com.example.gomind.model.RefreshToken;
import com.example.gomind.model.Token;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface AuthAPI {
    @POST("authentication/register")
    Call<ResponseBody> register(@Body RequestBody requestBody);

    @Headers("Content-Type: application/json")
    @POST("authentication/login")
    Call<ResponseBody> login(@Body String body);

   @Headers("Content-Type: application/json")
    @POST("authentication/refresh-token")
    Call<Token> refreshToken(@Body RefreshToken request);
}

