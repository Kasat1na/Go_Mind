package com.example.gomind.api;

import com.example.gomind.model.Auction;
import com.example.gomind.model.Leader;
import com.example.gomind.model.Question;
import com.example.gomind.model.TimeResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface QuestionAPI {
    @GET("quiz/random-question")
    Call<Question> getRandomQuestion(
            @Query("category") String category,
            @Query("type") String type
    );
    @GET("quiz/quiz-time-left")
    Call<TimeResponse> getRemainingTime();

    @GET("/quiz/current-user/points")
    Call<String> getPoints(@Header("Cookie") String cookie);

    @POST("quiz/submit-answer")
    Call<Void> submitAnswer(
            @Header("Cookie") String cookie,
            @Query("category") String category,
            @Query("questionId") long questionId,
            @Query("userAnswer") int userAnswer
    );

    @GET("quiz/users-with-points")
    Call<List<Leader>> getUsersWithPoints();

    @GET("advertisements/advertisement-max-cost-file")
    Call<Integer> getAdvertisementsByCost(  @Header("Cookie") String cookie);


    @Multipart
    @POST("advertisements/add-advertisements")
    Call<String> addAdvertisementWithFile(
            @Header("Cookie") String cookie,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part("cost") RequestBody cost,
            @Part MultipartBody.Part file
    );

    @GET("quiz/advertisement-max-cost-file")
    Call<Integer> getAdvertisementMaxCostFile();

    @GET("advertisements/advertisements-by-cost")
    Call<List<Auction>> getAuctions();
    // **Добавил метод для обновления вопросов**

}
