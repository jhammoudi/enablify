/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

    @Headers("Content-Type: application/json")
    @POST("./text:synthesize")
    Call<RetrofitModel> synthesizeText(
            @Body RetrofitModel retrofitModel,
            @Query("key") String apiKey);
}
