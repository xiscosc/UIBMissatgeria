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
import com.fsc.uibmissatgeria.ui.adapters.FileConversationAdapter;
import com.orm.SugarRecord;

import java.io.File;

/**
 * Created by xiscosastre on 16/5/15.
 */
public class FileMessageConversation extends SugarRecord<FileMessageConversation> {

    private long idApi;
    private String localPath;
    private String mimeType;
    private MessageConversation message;
    private String miniaturePath;

    public FileMessageConversation(long idApi, String mimeType, MessageConversation message) {
        this.idApi = idApi;
        this.localPath = "";
        this.mimeType = mimeType;
        this.message = message;
        this.miniaturePath = "";
    }

    public FileMessageConversation(String local_path, String mimeType) {
        this.localPath = local_path;
        this.mimeType = mimeType;
        this.message = null;
        this.idApi = -1;
        this.mimeType = "";
    }

    public FileMessageConversation(String local_path, String miniaturePath, String mimeType) {
        this.localPath = local_path;
        this.mimeType = mimeType;
        this.message = null;
        this.idApi = -1;
        this.mimeType = miniaturePath;
    }

    public FileMessageConversation() {
    }

    public Boolean haveFile() {
        if (!isImage()) {
            return (!localPath.equals("") && (new File(localPath)).exists());
        } else {
            boolean cond1 = (!localPath.equals("") && (new File(localPath).exists()));
            boolean cond2 = (!miniaturePath.equals("") && (new File(miniaturePath).exists()));
            return cond1 && cond2;
        }

    }

    public String getMimeType() {
        return mimeType;
    }

    public String getLocalPath() {
        return localPath;
    }

    public long getIdApi() {
        return idApi;
    }

    public void setIdApi(long idApi) {
        this.idApi = idApi;
    }

    public void setMessage(MessageConversation message) {
        this.message = message;
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


    public void startOrDownload(Context c, FileConversationAdapter adapter) {
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
        if (isImage() && !miniaturePath.equals("")) {
            return (new ImageManager(c)).getBitmap(this.miniaturePath);
        } else {
            return null;
        }
    }

    private boolean downloadFromServer(Context c) {
        FileManager manager;
        if (isImage()) {
            manager = new ImageManager(c);
        } else {
            manager = new FileManager(c);
        }
        String result = manager.downloadMedia(this.idApi, this.mimeType, message.getUser(c));
        if (result != null) {
            localPath = result;
            if (isImage()) {
                miniaturePath = (new ImageManager(c)).makeMiniature(localPath, message.getUser(c));
            }
            return true;
        } else {
            return false;
        }
    }

    private class DownloadFileTask extends AsyncTask<Void, Void, Boolean> {

        FileMessageConversation fm;
        Context ctx;
        ProgressDialog pDialog;
        FileConversationAdapter adapter;

        public DownloadFileTask(Context ctx, FileMessageConversation fm, FileConversationAdapter adapter) {
            super();
            this.fm = fm;
            this.ctx = ctx;
            this.adapter = adapter;
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
