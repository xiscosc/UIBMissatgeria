package com.fsc.uibmissatgeria.managers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
public class ImageManager {

    Context c;
    String filesFolder;
    String imagesFolder;

    public ImageManager(Context c) {
        this.c = c;
        String root = Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
        filesFolder = root+Constants.FOLDER_NAME_EXTERNAL+File.separator;
        imagesFolder = filesFolder+Constants.FOLDER_NAME_IMAGES+File.separator;

    }


    public String saveImageToStorage(Uri imgUri) {
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
    }

    private void createDirs() {
        File createDir = new File(filesFolder);
        if(!createDir.exists()) {
            createDir.mkdir();
        }
        createDir = new File(imagesFolder);
        if(!createDir.exists()) {
            createDir.mkdir();
        }

    }

    public Bitmap getBitmap(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeFile(path);
    }

    public String getFileName(String path) {
        return (new File(path)).getName();
    }

    public String getSizeInMB(String path) {
        File file = new File(path);
        double sizeInBytes = file.length();
        double sizeInMb = sizeInBytes / (1024 * 1024);
        return new DecimalFormat("##.##").format(sizeInMb);
    }


}
