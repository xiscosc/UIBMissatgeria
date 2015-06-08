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
import com.fsc.uibmissatgeria.api.AccountUIB;
import com.fsc.uibmissatgeria.models.Avatar;
import com.fsc.uibmissatgeria.models.FileMessage;
import com.fsc.uibmissatgeria.models.FileMessageConversation;
import com.fsc.uibmissatgeria.models.User;

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
    String avatarsFolder;

    public ImageManager(Context c) {
        super(c);
        imagesFolder = filesFolder+Constants.FOLDER_NAME_IMAGES+File.separator;
        avatarsFolder = imagesFolder+Constants.FOLDER_NAME_AVATARS+File.separator;
    }


    private String saveImageToStorage(Uri imgUri, User user) {
        if (isAllowed(imgUri)) {
            File file = null;
            String result = null;
            try {
                createDirs();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(c.getContentResolver(), imgUri);
                OutputStream out;
                long unixTime = System.currentTimeMillis() / 1000L;

                int idUser = 0;
                if (user != null) idUser = user.getIdApi();


                String fileName = imagesFolder+idUser+"_"+unixTime+"_img.jpeg";
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

    public String makeMiniature(String path, User user) {
        File file;
        OutputStream out;
        try {
            createDirs();
            Bitmap srcBmp = getBitmap(path);
            Bitmap dstBmp;
            if (srcBmp.getWidth() >= srcBmp.getHeight()){

                dstBmp = Bitmap.createBitmap(
                        srcBmp,
                        srcBmp.getWidth() / 2 - srcBmp.getHeight() / 2,
                        0,
                        srcBmp.getHeight(),
                        srcBmp.getHeight()
                );
            }else{

                dstBmp = Bitmap.createBitmap(
                        srcBmp,
                        0,
                        srcBmp.getHeight() / 2 - srcBmp.getWidth() / 2,
                        srcBmp.getWidth(),
                        srcBmp.getWidth()
                );
            }

            dstBmp = Bitmap.createScaledBitmap(dstBmp, 67, 67, true);
            long unixTime = System.currentTimeMillis() / 1000L;


            int idUser = 0;
            if (user != null) idUser = user.getIdApi();

            String fileName = avatarsFolder+idUser+"_"+unixTime+"_avt.jpeg";
            file = new File(fileName);
            file.createNewFile();
            out = new FileOutputStream(file);
            dstBmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            Constants.showToast(c, c.getResources().getString(R.string.error_image_copy));
        }

        return null;
    }

    public FileMessageConversation saveImageToStorageConversation(Uri imgUri, User user) {
        String route = saveImageToStorage(imgUri, user);
        if (route != null) {
            String mRoute = makeMiniature(route, null);
            if (mRoute!=null) {
                return new FileMessageConversation(route, mRoute, "image/jpeg");
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public FileMessage saveImageToStorageGroup(Uri imgUri) {
        String route = saveImageToStorage(imgUri, null);
        if (route != null) {
            String mRoute = makeMiniature(route, null);
            if (mRoute!=null) {
                return new FileMessage(route, mRoute, "image/jpeg");
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public Avatar saveAvatarToStorage(Uri imgUri, User user) {
        String route = saveImageToStorage(imgUri, user);
        if (route != null) {
            String mRoute = makeMiniature(route, user);
            if (mRoute!=null) {
                return new Avatar(route, mRoute, user, "image/jpeg");
            } else {
                return null;
            }
        } else {
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
        createDir = new File(avatarsFolder);
        if(!createDir.exists()) {
            createDir.mkdir();
        }
    }

    public Bitmap getBitmap(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeFile(path);
    }

    /**
     * Generates a cropped circular miniature bitmap from a file
     * @param path
     * @return
     */
    public Bitmap getCroppedBitmap(String path) {

        Bitmap srcBmp = getBitmap(path);
        Bitmap dstBmp;
        if (srcBmp.getWidth() >= srcBmp.getHeight()){

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    srcBmp.getWidth() / 2 - srcBmp.getHeight() / 2,
                    0,
                    srcBmp.getHeight(),
                    srcBmp.getHeight()
            );
        }else{

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    0,
                    srcBmp.getHeight() / 2 - srcBmp.getWidth() / 2,
                    srcBmp.getWidth(),
                    srcBmp.getWidth()
            );
        }

        Bitmap output = Bitmap.createBitmap(dstBmp.getWidth(),
                dstBmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, dstBmp.getWidth(),
                dstBmp.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(dstBmp.getWidth() / 2,
                dstBmp.getHeight() / 2, dstBmp.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(dstBmp, rect, rect, paint);
        return output;
    }

    @Override
    protected String generateLocalPath(String mime, User user) {
        String extension =  mime.split("/")[1];
        long unixTime = System.currentTimeMillis() / 1000L;
        int idUser = 0;
        if (user != null) idUser = user.getIdApi();
        return imagesFolder+idUser+"_"+unixTime+"_img."+ extension;
    }


}
