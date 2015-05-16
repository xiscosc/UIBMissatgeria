package com.fsc.uibmissatgeria.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;

import com.fsc.uibmissatgeria.managers.ImageManager;
import com.orm.SugarRecord;

/**
 * Created by xiscosastre on 16/5/15.
 */
public class Avatar extends SugarRecord<Avatar> {

    private long idApi;
    private String remote_path;
    private String local_path;
    private User user;


    public Avatar(String local_path, User user) {
        this.local_path = local_path;
        this.user = user;
        this.idApi = -1;
        this.remote_path = "";

    }

    public Avatar(long idApi, String remote_path, User user) {
        this.idApi = idApi;
        this.remote_path = remote_path;
        this.user = user;
        this.local_path = "";
    }

    public Avatar() {

    }


    public void createAvatarFromIntent(Uri intent, User user, Context c) {
        updateAvatarFromIntent(intent, c);
        this.user = user;
        this.idApi = -1;
        this.remote_path = "";
    }

    public Bitmap getBitmap(Context c) {
        ImageManager imageManager = new ImageManager(c);
        return  imageManager.getBitmap(this.local_path);
    }

    public void updateAvatarFromIntent(Uri intent, Context c) {
        ImageManager imageManager = new ImageManager(c);
        this.local_path = imageManager.saveImageToStorage(intent);
    }

    public Bitmap getCroppedBitmap(Context c) {


        Bitmap bitmap = this.getBitmap(c);

        int min = bitmap.getWidth();
        if (min > bitmap.getHeight()) min = bitmap.getHeight();

        final Bitmap output = Bitmap.createBitmap(min,
                min, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.WHITE;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, min, min);
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }

    public Boolean haveFile() {
        return (!this.local_path.equals(""));
    }

}
