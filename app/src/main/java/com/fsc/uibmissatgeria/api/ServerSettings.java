package com.fsc.uibmissatgeria.api;

import android.content.Context;
import android.content.SharedPreferences;

import com.fsc.uibmissatgeria.Constants;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by xiscosastre on 4/5/15.
 */
public class ServerSettings {
    Context c;

    public ServerSettings(Context c) {
        this.c = c;
    }

    public void loadSettings() {
        Server s = new Server(c);
        try {
            SharedPreferences settings = c.getSharedPreferences(Constants.SP_UIB, 0);
            SharedPreferences.Editor editor = settings.edit();
            Map<String, Object> settingsServer = s.getSettings();
            if (settingsServer.containsKey("message_max_length")) {
                editor.putInt(Constants.SP_MAX_CHAR,  (int) settingsServer.get("message_max_length"));
            }
            if (settingsServer.containsKey("max_file_size")) {
                editor.putInt(Constants.SP_MAX_FILE_SIZE,  (int) settingsServer.get("max_file_size"));
            }
            if (settingsServer.containsKey("max_message_files")) {
                editor.putInt(Constants.SP_MAX_FILES,  (int) settingsServer.get("max_message_files"));
            }
            if (settingsServer.containsKey("allowed_mime_types")) {
                Set<String> mimes = new HashSet<>((List<String>) settingsServer.get("allowed_mime_types"));
                editor.putStringSet(Constants.SP_MIMETYPES, mimes);
            }
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public int getMaxChar() {
        SharedPreferences settings = c.getSharedPreferences(Constants.SP_UIB, 0);
        return  settings.getInt(Constants.SP_MAX_CHAR, Constants.SP_MAX_CHAR_DEFAULT);
    }

    public List<String> getMimeTypes() {
        SharedPreferences settings = c.getSharedPreferences(Constants.SP_UIB, 0);
        Set<String> mt = settings.getStringSet(Constants.SP_MIMETYPES, (Set<String>) Constants.SP_MIMETYPES_DEFAULT);
        return new ArrayList<>(mt);
    }

    public int getMaxFiles() {
        SharedPreferences settings = c.getSharedPreferences(Constants.SP_UIB, 0);
        return settings.getInt(Constants.SP_MAX_FILES, Constants.SP_MAX_FILES_DEFAULT);
    }

    public int getMaxFileSize() {
        SharedPreferences settings = c.getSharedPreferences(Constants.SP_UIB, 0);
        return settings.getInt(Constants.SP_MAX_FILE_SIZE, Constants.SP_MAX_FILE_SIZE_DEFAULT);
    }



}
