package com.example.gomind.api;

//import com.example.gomind.model.PaymentMappingRequest;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

//// Определяем интерфейс для платежного сервиса
//public interface PaymentAPI {
//    // URL формируется как базовый URL + "payment/mapping"
//    @POST("payment/mapping")
//   // Call<ResponseBody> postMapping(@Body PaymentMappingRequest request);
//}