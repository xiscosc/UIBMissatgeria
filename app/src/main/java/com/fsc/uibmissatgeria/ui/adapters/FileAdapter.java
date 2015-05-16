package com.fsc.uibmissatgeria.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.managers.ImageManager;

import java.util.List;

/**
 * Created by Xisco on 04/03/2015.
 */

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.PeerViewHolder>
        implements View.OnClickListener {

    public static class PeerViewHolder extends RecyclerView.ViewHolder {

        private TextView fileName;
        private TextView fileSize;
        private ImageView fileImage;

        public PeerViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            fileName = (TextView) itemView.findViewById(R.id.file_name);
            fileSize = (TextView) itemView.findViewById(R.id.file_size);
            fileImage = (ImageView) itemView.findViewById(R.id.file_image);

        }

        public void bindSubject(String f, Context c) {
            ImageManager imageManager = new ImageManager(c);
            fileName.setText(imageManager.getFileName(f));
            fileSize.setText(imageManager.getSizeInMB(f)+" MB");
            fileImage.setImageBitmap(imageManager.getBitmap(f));
        }
    }

    private List<String> files;
    private Context c;
    private View.OnClickListener listener;

    public FileAdapter(List<String> files, Context c) {
        this.files = files;
        this.c = c;
    }

    @Override
    public PeerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View itemView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.listitem_file, viewGroup, false);

        itemView.setOnClickListener(this);

        return new PeerViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PeerViewHolder viewHolder, int pos) {
        String item = files.get(pos);
        viewHolder.bindSubject(item, c);
    }

    @Override
    public int getItemCount() {
        return files.size();
    }

    public void setOnClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if(listener != null)
            listener.onClick(view);
    }
}