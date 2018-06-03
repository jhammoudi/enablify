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

import java.util.List;

public class RetrofitModel {

    @SerializedName("input")
    @Expose
    private Input input;

    @SerializedName("voices")
    @Expose
    private List<Voice> voices = null;

    @SerializedName("voice")
    @Expose
    private Voice voice;

    @SerializedName("audioConfig")
    @Expose
    private AudioConfig audioConfig;

    @SerializedName("audioContent")
    @Expose
    private String audioContent;

    public List<Voice> getVoices() {
        return voices;
    }

    public void setVoices(List<Voice> voices) {
        this.voices = voices;
    }

    public Voice getVoice() {
        return voice;
    }

    public void setVoice(Voice voice) {
        this.voice = voice;
    }

    public Input getInput() {
        return input;
    }

    public void setInput(Input input) {
        this.input = input;
    }

    public AudioConfig getAudioConfig() {
        return audioConfig;
    }

    public void setAudioConfig(AudioConfig audioConfig) {
        this.audioConfig = audioConfig;
    }

    public String getAudioContent() {
        return audioContent;
    }

    public void setAudioContent(String audioContent) {
        this.audioContent = audioContent;
    }
}
