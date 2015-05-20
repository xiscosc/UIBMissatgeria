package com.fsc.uibmissatgeria.ui.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fsc.uibmissatgeria.Constants;
import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.managers.FileManager;
import com.fsc.uibmissatgeria.managers.ImageManager;
import com.fsc.uibmissatgeria.models.FileMessage;

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
        private ImageView fileRemove;
        private CardView  fileCard;

        public PeerViewHolder(View itemView) {
            super(itemView);
            itemView.setClickable(true);
            fileName = (TextView) itemView.findViewById(R.id.file_name);
            fileSize = (TextView) itemView.findViewById(R.id.file_size);
            fileImage = (ImageView) itemView.findViewById(R.id.file_image);
            fileRemove = (ImageView)itemView.findViewById(R.id.file_delete);
            fileCard = (CardView) itemView.findViewById(R.id.file_card);

        }

        public void bindSubject(FileMessage f, Context c, boolean editable) {
            if (f.haveFile() ) {
                if (f.isImage()){
                    fileImage.setImageBitmap(f.getBitmap(c));
                } else {
                    fileImage.setImageResource(R.drawable.file_icon);
                }
                fileName.setText(f.getName());
                fileSize.setText(f.getSizeMB() + " MB");
            } else {
                fileName.setText(c.getString(R.string.file_to_download));
                fileImage.setImageResource(R.drawable.file_icon);
                fileSize.setText("");
            }
            if (editable) {
                fileRemove.setVisibility(View.VISIBLE);
            } else {
                fileRemove.setVisibility(View.GONE);
            }

        }
    }

    private List<FileMessage> files;
    private Context c;
    private View.OnClickListener listener;
    private boolean editable;

    public FileAdapter(List<FileMessage> files, Context c, boolean editable) {
        this.files = files;
        this.c = c;
        this.editable = editable;
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
        final FileAdapter self = this;
        FileMessage item = files.get(pos);
        viewHolder.bindSubject(item, c, editable);
        final int post = pos;
        if (editable) {
            viewHolder.fileRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FileMessage path = files.get(post);
                    files.remove(post);
                    FileManager.deleteFile(path.getLocalPath());
                    notifyDataSetChanged();
                }
            });
        } else {
            viewHolder.fileCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FileMessage path = files.get(post);
                    path.startOrDownload(c, self);
                    notifyDataSetChanged();
                }
            });
        }

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