package com.fsc.uibmissatgeria.models;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
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
    private String local_path;
    private User user;
    private String mimeType;


    public Avatar(String local_path, User user, String mimeType) {
        this.local_path = local_path;
        this.user = user;
        this.idApi = -1;
        this.mimeType = mimeType;

    }

    public Avatar(long idApi,  User user, String mimeType) {
        this.idApi = idApi;
        this.user = user;
        this.local_path = "";
        this.mimeType = mimeType;
    }

    public Avatar() {

    }

    public void mergeFromServerAvatar(Avatar avatar) {
        this.idApi = avatar.idApi;
        this.mimeType = avatar.mimeType;
    }


    public void createAvatarFromIntent(Uri intent, User user, Context c) {
        updateAvatarFromIntent(intent, c);
        this.user = user;
        this.idApi = -1;
    }

    public Bitmap getBitmap(Context c) {
        ImageManager imageManager = new ImageManager(c);
        return  imageManager.getBitmap(this.local_path);
    }

    public void updateAvatarFromIntent(Uri intent, Context c) {
        ImageManager imageManager = new ImageManager(c);
        this.local_path = imageManager.saveImageToStorage(intent);
    }

    public BitmapDrawable getCircleBitmap(Context c) {
        ImageManager imageManager = new ImageManager(c);
        Bitmap m = imageManager.getCroppedBitmap(this.local_path);
        return new BitmapDrawable(c.getResources(), m);
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
            intent.setDataAndType(Uri.fromFile(file), "image/*");
            c.startActivity(intent);
        }

    }

    public String getLocal_path() {
        return local_path;
    }
}
