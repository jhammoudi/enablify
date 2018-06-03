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

package com.hammoudij.enablify.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * The AudioConfig model class containing all elements of an AudioConfig for the Google Cloud API
 * This model is used for the Google Text-to-speech API, where it determines the audio configuration of the speech
 */

public class AudioConfig {

    @SerializedName("audioEncoding")
    @Expose
    private String audioEncoding;

    @SerializedName("pitch")
    @Expose
    private Integer pitch;

    @SerializedName("speakingRate")
    @Expose
    private Integer speakingRate;

    public String getAudioEncoding() {
        return audioEncoding;
    }

    public void setAudioEncoding(String audioEncoding) {
        this.audioEncoding = audioEncoding;
    }

    public Integer getPitch() {
        return pitch;
    }

    public void setPitch(Integer pitch) {
        this.pitch = pitch;
    }

    public Integer getSpeakingRate() {
        return speakingRate;
    }

    public void setSpeakingRate(Integer speakingRate) {
        this.speakingRate = speakingRate;
    }

}
