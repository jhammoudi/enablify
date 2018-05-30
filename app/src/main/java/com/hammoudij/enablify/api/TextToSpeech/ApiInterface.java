package com.hammoudij.enablify.api.TextToSpeech;

import com.hammoudij.enablify.model.AudioConfig;
import com.hammoudij.enablify.model.Input;
import com.hammoudij.enablify.model.RetrofitModel;
import com.hammoudij.enablify.model.Voice;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("voices")
    Call<RetrofitModel> getVoices(@Query("key") String apiKey);

    @GET("voices")
    Call<RetrofitModel> getVoiceType(
            @Query("languageCode") String languageCode,
            @Query("key") String apiKey);

//    @Headers("Content-Type: application/json")
//    @POST("./text:synthesize")
//    Call<RetrofitModel> synthesizeText(
//            @Query("input") Input input,
//            @Query("voice") Voice voice,
//            @Query("audioConfig") AudioConfig audioConfig,
//            @Query("key") String apiKey);

    @Headers("Content-Type: application/json")
    @POST("./text:synthesize")
    Call<RetrofitModel> synthesizeText(
            @Body RetrofitModel retrofitModel,
            @Query("key") String apiKey);
}
