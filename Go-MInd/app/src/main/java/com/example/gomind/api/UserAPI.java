package com.example.gomind.api;

import com.example.gomind.model.ApiResponse;
import com.example.gomind.model.UserData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface UserAPI {

    @GET("user/profile")
    Call<UserData> getProfile(@Header("Cookie") String cookie);
    @POST("/user/request-email-change")
    Call<ApiResponse> requestEmailChange(@Query("newEmail") String newEmail);

    @PATCH("/user/update-nickname")
    Call<ApiResponse> updateNickname(@Query("newNickname") String newNickname);

}

