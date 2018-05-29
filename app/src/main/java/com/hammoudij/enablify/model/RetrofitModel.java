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
