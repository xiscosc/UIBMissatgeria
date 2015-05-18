package com.fsc.uibmissatgeria.managers;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import com.fsc.uibmissatgeria.Constants;
import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.api.ServerSettings;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;



/**
 * Created by xiscosastre on 16/5/15.
 */
public class FileManager {

    Context c;
    String filesFolder;
    String documentsFolder;

    public FileManager(Context c) {
        this.c = c;
        String root = Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
        filesFolder = root+Constants.FOLDER_NAME_EXTERNAL+File.separator;
        documentsFolder = filesFolder+Constants.FOLDER_NAME_FILES+File.separator;

    }





    protected void createDirs() {
        File createDir = new File(filesFolder);
        if(!createDir.exists()) {
            createDir.mkdir();
        }
        createDir = new File(documentsFolder);
        if(!createDir.exists()) {
            createDir.mkdir();
        }

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

    protected boolean isAllowed(Uri route) {
        String type = getMimeType(route.getPath());
        if (type == null) {
            ContentResolver cR = c.getContentResolver();
            type = cR.getType(route);
        }
        List<String> allowedMimeTypes = (new ServerSettings(c)).getMimeTypes();

        return allowedMimeTypes.contains(type);

    }

    public static void deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }

    public static boolean isImage(String path) {
            String mimetype = getMimeType(path);
            if (mimetype == null) return false;
            String type = mimetype.split("/")[0];
            return type.equals("image");
    }

    public static boolean isImageFromUri(Uri path, Context context) {
        String mimetype = getMimeType(path, context);
        if (mimetype == null) return false;
        String type = mimetype.split("/")[0];
        return type.equals("image");
    }


    private static String getMimeType(Uri path, Context c) {
        String mimetype = null;
        String extension = getExtension(path.getPath());
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            mimetype = mime.getMimeTypeFromExtension(extension);
        }
        if (mimetype == null) {
            ContentResolver cR = c.getContentResolver();
            mimetype = cR.getType(path);
        }
        return mimetype;
    }

    private static String getMimeType(String path) {
        String mimetype = null;
        String extension = getExtension(path);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            mimetype = mime.getMimeTypeFromExtension(extension);
        }
        return mimetype;
    }


    private static String getExtension(String path) {
        String extension = null;
        int i = path.lastIndexOf('.');
        int p = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));
        if (i > p) {
            extension = path.substring(i+1);
        }
        return extension;
    }


    public String saveFileToStorage(Uri route) {
        if (isAllowed(route)) {


            long unixTime = System.currentTimeMillis() / 1000L;
            String destinationFilename = documentsFolder+unixTime+"_file."+ getMimeType(route, c).split("/")[1];

            /*if ((new File(sourceFilename)).length()>(1024*1024*2)) { //TODO: CONFIG
                Constants.showToast(c, c.getResources().getString(R.string.error_file_max_size));
                return null;
            }*/

            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;

            try {
                ContentResolver cR = c.getContentResolver();
                createDirs();
                bis = new BufferedInputStream(cR.openInputStream(route));
                bos = new BufferedOutputStream(new FileOutputStream(destinationFilename, false));
                byte[] buf = new byte[1024];
                bis.read(buf);
                do {
                    bos.write(buf);
                } while(bis.read(buf) != -1);
            } catch (IOException e) {
                e.printStackTrace();
                Constants.showToast(c, c.getResources().getString(R.string.error_file_copy));
                destinationFilename = null;
            } finally {
                try {
                    if (bis != null) bis.close();
                    if (bos != null) bos.close();
                } catch (IOException e) {

                }
            }
            return destinationFilename;
        } else {
            Constants.showToast(c, c.getResources().getString(R.string.file_not_allowed));
            return null;
        }

    }


}
