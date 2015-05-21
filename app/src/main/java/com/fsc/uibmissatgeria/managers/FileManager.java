package com.fsc.uibmissatgeria.managers;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;

import com.fsc.uibmissatgeria.Constants;
import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.api.AccountUIB;
import com.fsc.uibmissatgeria.api.Server;
import com.fsc.uibmissatgeria.api.ServerSettings;
import com.fsc.uibmissatgeria.models.FileMessage;
import com.fsc.uibmissatgeria.models.FileMessageConversation;
import com.fsc.uibmissatgeria.models.User;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.ArrayList;
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

    public static String getFileName(String path) {
        return (new File(path)).getName();
    }

    public static String getSizeInMB(String path) {
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
        return isImageFromMime(mimetype);
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
            extension = path.substring(i + 1);
        }
        return extension;
    }


    private List<String> saveFileToStorage(Uri route) {
        if (isAllowed(route)) {
            List<String> result = new ArrayList<>();
            long unixTime = System.currentTimeMillis() / 1000L;
            String mime = getMimeType(route, c);
            User user = (new AccountUIB(c)).getUser();
            String destinationFilename = documentsFolder+user.getIdApi()+"_"+unixTime+"_file."+ mime.split("/")[1];

            try {
                ContentResolver cr = c.getContentResolver();
                InputStream is = cr.openInputStream(route);
                int size = is.available();

                if (size > (new ServerSettings(c)).getMaxFileSize()) {
                    Constants.showToast(c, c.getString(R.string.error_file_max_size));
                    return null;
                }
            } catch (IOException e) {
                Constants.showToast(c, c.getString(R.string.error_file_max_size));
                return null;
            }


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
                Constants.showToast(c, c.getString(R.string.error_file_copy));
                destinationFilename = null;
            } finally {
                try {
                    if (bis != null) bis.close();
                    if (bos != null) bos.close();
                } catch (IOException e) {

                }
            }
            result.add(destinationFilename);
            result.add(mime);
            return result;
        } else {
            Constants.showToast(c, c.getResources().getString(R.string.file_not_allowed));
            return null;
        }

    }

    public FileMessageConversation savFileToStorageConversation(Uri imgUri) {
        List<String> result = saveFileToStorage(imgUri);
        if (result != null) {
            return new FileMessageConversation(result.get(0), result.get(1));
        } else {
            return null;
        }
    }

    public FileMessage saveFileToStorageGroup(Uri imgUri) {
        List<String> result = saveFileToStorage(imgUri);
        if (result != null) {
            return new FileMessage(result.get(0), result.get(1));
        } else {
            return null;
        }
    }

    protected  String generateLocalPath(String mime, User user) {
        String extension =  mime.split("/")[1];
        long unixTime = System.currentTimeMillis() / 1000L;
        int idUser = 0;
        if (user != null) idUser = user.getIdApi();
        return documentsFolder+idUser+unixTime+"_file."+ extension;
    }

    public String downloadMedia(long idApi, String mime, User user) {
        createDirs();
        String localPath = generateLocalPath(mime, user);
        Server s = new Server(c);
        Boolean result = s.downloadFile("media/" + idApi + "/", localPath);
        if (result) {
            return localPath;
        } else {
            return null;
        }
    }

    public static boolean isImageFromMime(String mime) {
        String type = mime.split("/")[0];
        return type.equals("image");
    }


    public static void deleteAllFiles() {
        String root = Environment.getExternalStorageDirectory().getAbsolutePath()+"/";
        String filesFolder = root+Constants.FOLDER_NAME_EXTERNAL+File.separator;
        removeAllFromDir(filesFolder);
    }

    private static void removeAllFromDir(String path) {
        File f = new File(path);
        if (f.exists() && f.isDirectory()) {
            String[] children = f.list();
            for (int i = 0; i < children.length; i++) {
                File f2 = new File(f, children[i]);
                if (f2.isDirectory()) {
                    removeAllFromDir(f2.getAbsolutePath());
                }
                f2.delete();
            }
        }
    }


}
