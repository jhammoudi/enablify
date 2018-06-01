package com.hammoudij.enablify.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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
