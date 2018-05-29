package com.hammoudij.enablify.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Voice {

    @SerializedName("languageCodes")
    @Expose
    private List<String> languageCodes = null;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("ssmlGender")
    @Expose
    private String ssmlGender;
    @SerializedName("naturalSampleRateHertz")
    @Expose
    private Integer naturalSampleRateHertz;

    public List<String> getLanguageCodes() {
        return languageCodes;
    }

    public void setLanguageCodes(List<String> languageCodes) {
        this.languageCodes = languageCodes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSsmlGender() {
        return ssmlGender;
    }

    public void setSsmlGender(String ssmlGender) {
        this.ssmlGender = ssmlGender;
    }

    public Integer getNaturalSampleRateHertz() {
        return naturalSampleRateHertz;
    }

    public void setNaturalSampleRateHertz(Integer naturalSampleRateHertz) {
        this.naturalSampleRateHertz = naturalSampleRateHertz;
    }
}
