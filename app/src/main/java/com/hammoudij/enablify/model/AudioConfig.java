package com.hammoudij.enablify.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AudioConfig {
    @SerializedName("audioEncoding")
    @Expose
    private String audioEncoding;

    public String getAudioEncoding() {
        return audioEncoding;
    }

    public void setAudioEncoding(String audioEncoding) {
        this.audioEncoding = audioEncoding;
    }
}
