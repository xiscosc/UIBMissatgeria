package com.fsc.uibmissatgeria.api;

import android.content.Context;
import android.content.SharedPreferences;

import com.fsc.uibmissatgeria.Constants;

import java.util.Map;

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
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public int getMaxChar() {
        SharedPreferences settings = c.getSharedPreferences(Constants.SP_UIB, 0);
        return  settings.getInt(Constants.SP_MAX_CHAR, Constants.SP_MAX_CHAR_DEFAULT);
    }


}
