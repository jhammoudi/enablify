package com.hammoudij.enablify.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hammoudij.enablify.R;
import com.hammoudij.enablify.db.AppDatabase;
import com.hammoudij.enablify.model.Audio;

import java.util.List;

class AudioAdapter extends RecyclerView.Adapter<AudioAdapter.ViewHolder> {

    private List<Audio> mAudioList;
    private AppDatabase db;


    public AudioAdapter(List<Audio> audioList, AppDatabase db) {
        this.mAudioList = audioList;
        this.db = db;
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
        Audio audio = mAudioList.get(position);
        mAudioList.remove(position);
        db.audioDao().delete(audio);
        notifyItemRemoved(position);
    }

    private void shareItem(View view, int position) {

        Audio audio = mAudioList.get(position);
        String filePath = audio.getFilePath();
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("audio/mp3");
        share.putExtra(Intent.EXTRA_STREAM, filePath);
        view.getContext().startActivity(Intent.createChooser(share, "Share Audio File"));
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener, View.OnCreateContextMenuListener {

        public TextView name;
        public TextView length;
        public TextView time;
        public int position = 0;


        public ViewHolder(View itemView, List<Audio> mAudioList) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
            name = itemView.findViewById(R.id.audio_name);
            length = itemView.findViewById(R.id.audio_length);
            time = itemView.findViewById(R.id.audio_date_time);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Audio audio = mAudioList.get(position);
            String filePath = audio.getFilePath();

            Intent intent = new Intent(Intent.ACTION_VIEW);

            Uri audioUri = Uri.parse(filePath);
            intent.setDataAndType(audioUri,
                    "audio/mp3");

            v.getContext().startActivity(intent);
        }

        @Override
        public boolean onLongClick(View v) {
            setMenuPosition(getAdapterPosition());
            itemView.showContextMenu();
            return true;
        }

        public void setMenuPosition(int position) {
            this.position = position;
        }

        public int getMenuPosition() {
            return position;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            MenuItem delete = menu.add(Menu.NONE, 1, 1, "Delete");
            MenuItem share = menu.add(Menu.NONE, 2, 2, "Share");
            delete.setOnMenuItemClickListener(onEditMenu);
            share.setOnMenuItemClickListener(onEditMenu);
        }

        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case 1:
                        removeItem(getMenuPosition());
                        break;

                    case 2:
                        shareItem(itemView, getMenuPosition());
                        break;
                }
                return true;
            }
        };
    }

}
