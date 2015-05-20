package com.fsc.uibmissatgeria.models;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;

import com.fsc.uibmissatgeria.Constants;
import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.managers.FileManager;
import com.fsc.uibmissatgeria.managers.ImageManager;
import com.fsc.uibmissatgeria.ui.adapters.FileAdapter;
import com.orm.SugarRecord;

import java.io.File;

/**
 * Created by xiscosastre on 16/5/15.
 */
public class FileMessage extends SugarRecord<FileMessage> {

    private long idApi;
    private String localPath;
    private String mimeType;
    private Message message;

    public FileMessage(long idApi, String mimeType, Message message) {
        this.idApi = idApi;
        this.mimeType = mimeType;
        this.message = message;
        this.localPath = "";
    }

    public FileMessage(String local_path, String mimeType) {
        this.localPath = local_path;
        this.mimeType = mimeType;
        this.idApi = -1;
    }

    public FileMessage() {

    }

    public Boolean haveFile() {
        return (!localPath.equals("") && (new File(localPath)).exists());
    }

    public long getIdApi() {
        return idApi;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setIdApi(long idApi) {
        this.idApi = idApi;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    private void startIntent(Context c) {
        if (haveFile())
        {
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            File file = new File(localPath);
            intent.setDataAndType(Uri.fromFile(file), mimeType);
            c.startActivity(intent);
        }
    }


    public void startOrDownload(Context c, FileAdapter adapter) {
        if (haveFile()) {
            startIntent(c);
        }  else {
            DownloadFileTask task = new DownloadFileTask(c, this, adapter);
            task.execute();
        }
    }

    public boolean isImage() {
        return FileManager.isImageFromMime(mimeType);
    }

    public String getName() {
        return FileManager.getFileName(this.localPath);
    }

    public String getSizeMB() {
        return FileManager.getSizeInMB(this.localPath);
    }

    public Bitmap getBitmap(Context c) {
        if (isImage()) {
            return (new ImageManager(c)).getBitmap(this.localPath);
        } else {
            return null;
        }
    }


    public boolean downloadFromServer(Context c) {
        FileManager manager;
        if (isImage()) {
            manager = new ImageManager(c);
        } else {
            manager = new FileManager(c);
        }
        String result = manager.downloadMedia(this.idApi, this.mimeType);
        if (result != null) {
            localPath = result;
            return true;
        } else {
            return false;
        }
    }

    private class DownloadFileTask extends AsyncTask<Void, Void, Boolean> {

        FileMessage fm;
        Context ctx;
        ProgressDialog pDialog;
        FileAdapter adapter;

        public DownloadFileTask(Context ctx, FileMessage fm, FileAdapter fileAdapter) {
            super();
            this.fm = fm;
            this.ctx = ctx;
            this.adapter = fileAdapter;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return fm.downloadFromServer(ctx);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            pDialog.dismiss();
            if (result) {
                fm.save();
                adapter.notifyDataSetChanged();
                fm.startIntent(ctx);
            } else {
                Constants.showToast(ctx, ctx.getString(R.string.error_downloading));
            }

        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(ctx);
            pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            pDialog.setMessage(ctx.getString(R.string.downloading));
            pDialog.setCancelable(false);
            pDialog.setMax(100);
            pDialog.setProgress(0);
            pDialog.show();
        }
    }
}
