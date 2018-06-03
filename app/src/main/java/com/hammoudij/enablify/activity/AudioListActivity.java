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

package com.hammoudij.enablify.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.hammoudij.enablify.MainMVP;
import com.hammoudij.enablify.R;
import com.hammoudij.enablify.db.AppDatabase;
import com.hammoudij.enablify.model.Audio;
import com.hammoudij.enablify.presenter.AudioPresenter;
import com.hammoudij.enablify.presenter.CameraPresenter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AudioListActivity extends AppCompatActivity {

    @BindView(R.id.audio_recycler_view)
    public RecyclerView mRecyclerView;

    private MainMVP.AudioPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording_list);
        setupMVP();
        ButterKnife.bind(this);
        setupActionBar();
        mPresenter.setUpActivity(mRecyclerView,this);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupMVP() {
        mPresenter = new AudioPresenter();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
