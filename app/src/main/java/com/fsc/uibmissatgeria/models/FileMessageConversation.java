package com.fsc.uibmissatgeria.models;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import com.fsc.uibmissatgeria.managers.FileManager;
import com.fsc.uibmissatgeria.managers.ImageManager;
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

    public FileMessageConversation(long idApi, String mimeType, MessageConversation message) {
        this.idApi = idApi;
        this.localPath = "";
        this.mimeType = mimeType;
        this.message = message;
    }

    public FileMessageConversation(String local_path, String mimeType) {
        this.localPath = local_path;
        this.mimeType = mimeType;
        this.message = null;
        this.idApi = -1;
    }

    public FileMessageConversation() {
    }

    public Boolean haveFile() {
        return (!localPath.equals("") && (new File(localPath).exists()));
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



    public void startIntent(Context c) {
        if (haveFile())
        {
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            File file = new File(localPath);
            intent.setDataAndType(Uri.fromFile(file), mimeType);
            c.startActivity(intent);
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
}
