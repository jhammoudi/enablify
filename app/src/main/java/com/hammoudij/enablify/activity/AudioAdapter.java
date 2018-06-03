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

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hammoudij.enablify.MainMVP;
import com.hammoudij.enablify.R;
import com.hammoudij.enablify.db.AppDatabase;
import com.hammoudij.enablify.model.Audio;
import com.hammoudij.enablify.presenter.AudioPresenter;

import java.util.List;

/**
 * The Audio Adapter class that extends the Recycler View
 */

public class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.ViewHolder> {

    private List<Audio> mAudioList;
    private MainMVP.AudioPresenter mPresenter;
    private AppDatabase mDb;

    /**
     * Audio Adapter Constructor
     * @param audioList List of Audio Objects
     * @param db AppDatabase
     */
    public AudioAdapter(List<Audio> audioList, AppDatabase db) {
        this.mAudioList = audioList;
        this.mDb = db;
        setupMVP();
    }

    /**
     * Sets up the MVP Architecture
     */
    private void setupMVP() {
        //Initialising the Presenter
        mPresenter = new AudioPresenter();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AudioAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.audio_item, parent, false);

        return new ViewHolder(itemView);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onBindViewHolder(AudioAdapter.ViewHolder holder, int position) {
        holder.mName.setText(mAudioList.get(position).getName());
        holder.mLength.setText(mAudioList.get(position).getLength());
        holder.mTime.setText(mAudioList.get(position).getTime());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getItemCount() {
        return mAudioList.size();
    }

    /**
     * Removes the Audio at position from list and database
     */
    private void removeItem(int position) {
        mPresenter.removeItem(position, mAudioList, mDb, this);
    }

    /**
     * Shares the Audio at position by starting a Share Intent
     */
    private void shareItem(View v, int position) {
        mPresenter.shareItem(v, position, mAudioList);
    }

    /**
     * ViewHolder class extending ViewHolder
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, View.OnCreateContextMenuListener {

        private static final int FIRST_MENU_ITEM = 1;
        private static final int SECOND_MENU_ITEM = 2;
        private TextView mName;
        private TextView mLength;
        private TextView mTime;
        private int mPosition = 0;

        /**
         * ViewHolder Constructor
         */
        private ViewHolder(View itemView) {
            super(itemView);

            //Set listeners to the View, needed to handle clicks and long presses
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            itemView.setOnCreateContextMenuListener(this);

            mName = itemView.findViewById(R.id.audio_name);
            mLength = itemView.findViewById(R.id.audio_length);
            mTime = itemView.findViewById(R.id.audio_date_time);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            //Starts an Intent to play the Audio as the adapter position
            mPresenter.clickItem(v, position, mAudioList);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean onLongClick(View v) {
            setMenuPosition(getAdapterPosition());
            //when ViewHolder is long pressed, open the context menu
            itemView.showContextMenu();
            return true;
        }

        /**
         * Sets the adapter position to the global position variable
         */
        private void setMenuPosition(int position) {
            this.mPosition = position;
        }

        /**
         * Gets the global position variable
         */
        private int getMenuPosition() {
            return mPosition;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            //Creates a menu item to allow users to delete an Audio
            MenuItem delete = menu.add(Menu.NONE, FIRST_MENU_ITEM, FIRST_MENU_ITEM, v.getResources().getString(R.string.delete_string));

            //Creates a menu item to allow users to share an Audio
            MenuItem share = menu.add(Menu.NONE, SECOND_MENU_ITEM, SECOND_MENU_ITEM, v.getResources().getString(R.string.share_string));

            delete.setOnMenuItemClickListener(onEditMenu);
            share.setOnMenuItemClickListener(onEditMenu);
        }

        //Creates onMenuItemClickListener to listen and handles clicks on the Context menu
        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case FIRST_MENU_ITEM:
                        //if the first menu item is clicked (Delete method is called), then remove item
                        removeItem(getMenuPosition());
                        break;

                    case SECOND_MENU_ITEM:
                        //if the second menu item is clicked (Share method is called), then share item
                        shareItem(itemView, getMenuPosition());
                        break;
                }
                return true;
            }
        };
    }
}
