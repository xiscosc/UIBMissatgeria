package com.fsc.uibmissatgeria.models;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;

import com.fsc.uibmissatgeria.managers.ImageManager;
import com.orm.SugarRecord;

import java.io.File;

/**
 * Created by xiscosastre on 16/5/15.
 */
public class Avatar extends SugarRecord<Avatar> {

    private long idApi;
    private String localPath;
    private User user;
    private String mimeType;


    public Avatar(String local_path, User user, String mimeType) {
        this.localPath = local_path;
        this.user = user;
        this.idApi = -1;
        this.mimeType = mimeType;

    }

    public Avatar(long idApi,  User user, String mimeType) {
        this.idApi = idApi;
        this.user = user;
        this.localPath = "";
        this.mimeType = mimeType;
    }

    public Avatar() {

    }

    public void mergeFromServerAvatar(Avatar avatar) {
        this.idApi = avatar.idApi;
        this.mimeType = avatar.mimeType;
    }


    public Bitmap getBitmap(Context c) {
        ImageManager imageManager = new ImageManager(c);
        return  imageManager.getBitmap(this.localPath);
    }


    public BitmapDrawable getCircleBitmap(Context c) {
        ImageManager imageManager = new ImageManager(c);
        Bitmap m = imageManager.getCroppedBitmap(this.localPath);
        return new BitmapDrawable(c.getResources(), m);
    }

    public Boolean hasFile() {
        return (!localPath.equals("") && (new File(localPath).exists()));
    }

    public void startIntent(Context c) {
        if (hasFile())
        {
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            File file = new File(localPath);
            intent.setDataAndType(Uri.fromFile(file), "image/*");
            c.startActivity(intent);
        }

    }

    public boolean downloadFromServer(Context c) {
        ImageManager imageManager = new ImageManager(c);
        String result = imageManager.downloadMedia(this.idApi, this.mimeType);
        if (result != null) {
            localPath = result;
            return true;
        } else {
            return false;
        }
    }

    public String getLocalPath() {
        return localPath;
    }

    @Override
    public boolean equals(Object other){
        if (other == null) return false;
        if (other == this) return true;
        if (!(other instanceof Avatar)) return false;
        Avatar otherMyClass = (Avatar) other;
        return (this.idApi == otherMyClass.idApi);
    }
}
