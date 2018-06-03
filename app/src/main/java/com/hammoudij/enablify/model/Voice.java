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

/**
 * The Voice model class containing all elements of an Voice for the Google Cloud API
 * This model is used for the Google Text-to-speech API, where it determines the voice language and type, to create speech.
 */

public class Voice {

    @SerializedName("languageCodes")
    @Expose
    private List<String> languageCodes = null;

    @SerializedName("languageCode")
    @Expose
    private String languageCode;

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

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
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
