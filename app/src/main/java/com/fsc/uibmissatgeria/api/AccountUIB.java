package com.fsc.uibmissatgeria.api;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.fsc.uibmissatgeria.Constants;
import com.fsc.uibmissatgeria.managers.ModelManager;
import com.fsc.uibmissatgeria.models.Avatar;
import com.fsc.uibmissatgeria.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * Class to manage auth from server and the user settings
 */
public class AccountUIB {

    private Context c;
    private User usr;
    private JSONObject avatarJSON;

    public AccountUIB(Context c) {
        this.c = c;
        this.usr = null;
    }


    private boolean addAcount(String username, String password) {
        Server s = new Server(this.c);
        Map<String, Object> result = s.doLogin(username, password);
        usr = (User) result.get("user");
        if (result.containsKey("avatar")) {
            avatarJSON = (JSONObject) result.get("avatar");
        } else {
            avatarJSON = null;
        }
        if (usr != null) {
            try {
                String token = s.getTokenRaw();
                SharedPreferences settings = c.getSharedPreferences(Constants.SP_UIB, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(Constants.ACCOUNT_TOKEN, token);
                editor.commit();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }


    private void removeAccount() {
        try {
            SharedPreferences settings = c.getSharedPreferences(Constants.SP_UIB, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.clear();
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean initalLogin(String user, String password) {
        boolean result = this.addAcount(user, password);
        if (result) {
           Boolean result2 = this.saveUser();
            if (result2) {
                Avatar avatar = usr.getAvatar();
                if(avatar != null) {
                    boolean avatarDownloaded = avatar.downloadFromServer(c);
                    if (avatarDownloaded) avatar.save();
                }
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void logOut() {
        this.removeAccount();
        ModelManager.resetDB();
    }

    public boolean isLogged() {
        String token = this.getToken();
        return (!token.equals(Constants.ACCOUNT_TOKEN_FAILED));
    }

    public String getToken() {
        try {
            SharedPreferences settings = c.getSharedPreferences(Constants.SP_UIB, 0);
            return settings.getString(Constants.ACCOUNT_TOKEN, Constants.ACCOUNT_TOKEN_FAILED);
        } catch (Exception e) {
            return Constants.ACCOUNT_TOKEN_FAILED;
        }
    }

    private boolean saveUser() {
        usr.save();
        try {
            if (avatarJSON != null) {
                Avatar avtr = new Avatar(avatarJSON.getLong("id"), usr, avatarJSON.getString("mime"));
                avtr.save();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            SharedPreferences settings = c.getSharedPreferences(Constants.SP_UIB, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(Constants.ACCOUNT_ID, usr.getIdApi());
            editor.putString(Constants.SP_UPDATE, Constants.SP_UPDATE_DEFAULT);
            editor.putBoolean(Constants.SP_ONLY_TEACHER, Constants.SP_ONLY_TEACHER_DEFAULT);
            editor.putBoolean(Constants.SP_FIRST_NOTIFICATIONS, Constants.SP_FIRST_NOTIFICATIONS_DEFAULT);
            editor.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public User getUser() {
        try {
            SharedPreferences settings = c.getSharedPreferences(Constants.SP_UIB, 0);
            int id = settings.getInt(Constants.ACCOUNT_ID, 0);
            if (id!=0) {
                List<User> users = User.find(
                        User.class,
                        "ID_API = ?",
                        Integer.toString(id)
                );
                if (!users.isEmpty()) {
                    return users.get(0);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get notification's period check in milliseconds
     * @return Long
     */
    public Long getPeriodMS() {
        SharedPreferences settings = c.getSharedPreferences(Constants.SP_UIB, 0);
        String freqStr = settings.getString(Constants.SP_UPDATE, Constants.SP_UPDATE_DEFAULT);
        return Long.parseLong(freqStr) * 1000;
    }

    public void startNotificationService() {
        if (isLogged() && getPeriodMS() > 0) {
            Intent serviceIntent = new Intent(c, NotificationService.class);
            Boolean isRunning = NotificationService.isRunning();
            if(!isRunning) c.startService(serviceIntent);
        }
    }

    public void startNotificationServiceFromSettings(long period) {
        if (isLogged() && period > 0) {
            Intent serviceIntent = new Intent(c, NotificationService.class);
            Boolean isRunning = NotificationService.isRunning();
            if(!isRunning) c.startService(serviceIntent);
        }
    }

    /**
     * Get Only Teacher setting
     * @return boolean
     */
    public boolean OnlyTeacherNotifications() {
        SharedPreferences settings = c.getSharedPreferences(Constants.SP_UIB, 0);
        return settings.getBoolean(Constants.SP_ONLY_TEACHER, false);
    }


    public boolean markFirstNotifications() {
        try {
            SharedPreferences settings = c.getSharedPreferences(Constants.SP_UIB, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(Constants.SP_FIRST_NOTIFICATIONS, false);
            editor.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isFirstNotifications() {
        SharedPreferences settings = c.getSharedPreferences(Constants.SP_UIB, 0);
        return settings.getBoolean(Constants.SP_FIRST_NOTIFICATIONS, false);
    }

}
