package com.hammoudij.enablify.api.TextToSpeech;

import com.hammoudij.enablify.model.AudioConfig;
import com.hammoudij.enablify.model.Input;
import com.hammoudij.enablify.model.RetrofitModel;
import com.hammoudij.enablify.model.Voice;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("voices")
    Call<RetrofitModel> getVoices(@Query("key") String apiKey);

    @GET("voices")
    Call<RetrofitModel> getVoiceType(
            @Query("languageCode") String languageCode,
            @Query("key") String apiKey);

    @POST("text:synthesize")
    Call<RetrofitModel> synthesizeText(@Field("input") Input input,
                                       @Field("voice") Voice voice,
                                       @Field("audioConfig") AudioConfig audioConfig,
                                       @Field("key") String apiKey);
}
