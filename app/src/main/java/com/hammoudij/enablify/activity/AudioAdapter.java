package com.hammoudij.enablify.activity;

import android.content.Context;
import android.speech.tts.Voice;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hammoudij.enablify.R;
import com.hammoudij.enablify.model.Audio;

import java.util.ArrayList;
import java.util.List;

class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.ViewHolder> {

    private List<Audio> mAudioList;


    public AudioAdapter(List<Audio> audioList) {
        this.mAudioList = audioList;
    }

    @Override
    public AudioAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.audio_item, parent, false);

        return new ViewHolder(itemView, mAudioList);
    }

    @Override
    public void onBindViewHolder(AudioAdapter.ViewHolder holder, int position) {
        holder.name.setText(mAudioList.get(position).getAudioName());
        holder.category.setText(mAudioList.get(position).getCategory());
    }

    @Override
    public int getItemCount() {
        return mAudioList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView category;

        public ViewHolder(View itemView, List<Audio> mAudioList) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            category = itemView.findViewById(R.id.category);
        }
    }
}
