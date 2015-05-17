package com.fsc.uibmissatgeria.managers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import com.fsc.uibmissatgeria.Constants;
import com.fsc.uibmissatgeria.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;


/**
 * Created by xiscosastre on 16/5/15.
 */
public class ImageManager extends FileManager {


    String imagesFolder;

    public ImageManager(Context c) {
        super(c);
        imagesFolder = filesFolder+Constants.FOLDER_NAME_IMAGES+File.separator;
    }


    public String saveImageToStorage(Uri imgUri) {
        if (isAllowed(imgUri)) {
            File file = null;
            String result = null;
            try {
                createDirs();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(c.getContentResolver(), imgUri);
                OutputStream out;
                long unixTime = System.currentTimeMillis() / 1000L;
                String fileName = imagesFolder+unixTime+"_img.jpg";
                file = new File(fileName);
                file.createNewFile();
                out = new FileOutputStream(file);
                if (bitmap.getWidth()>4096 || bitmap.getHeight()>4096) {
                    bitmap = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth()*0.5), (int) (bitmap.getHeight()*0.5), true);
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG, 35, out);
                out.close();
                return file.getAbsolutePath();
            } catch (IOException e) {
                Constants.showToast(c, c.getResources().getString(R.string.error_image_copy));
            }
            return result;
        } else {
            Constants.showToast(c, c.getResources().getString(R.string.file_not_allowed));
            return null;
        }
    }

    @Override
    protected void createDirs() {
        super.createDirs();
        File createDir = new File(imagesFolder);
        if(!createDir.exists()) {
            createDir.mkdir();
        }
    }

    public Bitmap getBitmap(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeFile(path);
    }


    public Bitmap getCroppedBitmap(String path) {

        Bitmap bitmap = getBitmap(path);

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


}
