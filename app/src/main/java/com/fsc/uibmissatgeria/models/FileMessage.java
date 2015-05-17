package com.fsc.uibmissatgeria.models;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.orm.SugarRecord;

import java.io.File;

/**
 * Created by xiscosastre on 16/5/15.
 */
public class FileMessage extends SugarRecord<FileMessage> {

    private long idApi;
    private String local_path;
    private String mimeType;
    private Message message;

    public FileMessage(long idApi, String local_path, String mimeType, Message message) {
        this.idApi = idApi;
        this.local_path = local_path;
        this.mimeType = mimeType;
        this.message = message;
    }

    public FileMessage(String local_path, String mimeType, Message message) {
        this.local_path = local_path;
        this.mimeType = mimeType;
        this.message = message;
        this.idApi = -1;
    }

    public FileMessage() {

    }

    public Boolean haveFile() {
        return (!local_path.equals("") && (new File(local_path).exists()));
    }



    public void startIntent(Context c) {
        if (haveFile())
        {
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            File file = new File(local_path);
            intent.setDataAndType(Uri.fromFile(file), mimeType);
            c.startActivity(intent);
        }
    }
}
