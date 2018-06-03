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

import com.hammoudij.enablify.model.RetrofitModel;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Api Interface for the Google Cloud API - Text-to-speech
 */

public interface ApiInterface {

    /**
     * Retrieves all Voices from the API GET call
     */
    @GET("voices")
    Call<RetrofitModel> getVoices(@Query("key") String apiKey);

    /**
     * Retrieves all Voice types by passing in language Code in API GET call
     * Passing in language code acts as a filter, and returns voice types that are compatible with the Language
     */
    @GET("voices")
    Call<RetrofitModel> getVoiceType(
            @Query("languageCode") String languageCode,
            @Query("key") String apiKey);

    /**
     * Posting the RetrofitModel model to the API, and retrieving the base64 encoded audio string
     */
    @Headers("Content-Type: application/json")
    @POST("./text:synthesize")
    Call<RetrofitModel> synthesizeText(
            @Body RetrofitModel retrofitModel,
            @Query("key") String apiKey);
}
