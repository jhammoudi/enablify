package com.hammoudij.enablify.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hammoudij.enablify.R;
import com.hammoudij.enablify.model.Audio;

import java.io.File;
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
        holder.name.setText(mAudioList.get(position).getName());
        holder.length.setText(mAudioList.get(position).getLength());
        holder.time.setText(mAudioList.get(position).getTime());
    }

    @Override
    public int getItemCount() {
        return mAudioList.size();
    }

    public void removeItem(int position) {
        mAudioList.remove(position);
        notifyItemRemoved(position);
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView length;
        public TextView time;

//        File musicFile2Play = new File(Environment.getExternalStorageDirectory().getPath()+"some_file.mp3");
//        Intent i2 = new Intent();
//                    i2.setAction(android.content.Intent.ACTION_VIEW);
//                    i2.setDataAndType(Uri.fromFile(musicFile2Play), "audio/mp3");
//        startActivity(i2);

        public ViewHolder(View itemView, List<Audio> mAudioList) {
            super(itemView);
            name = itemView.findViewById(R.id.audio_name);
            length = itemView.findViewById(R.id.audio_length);
            time = itemView.findViewById(R.id.audio_date_time);
        }
    }
}
