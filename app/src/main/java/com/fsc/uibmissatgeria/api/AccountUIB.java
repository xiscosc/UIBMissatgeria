package com.fsc.uibmissatgeria.api;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.fsc.uibmissatgeria.Constants;
import com.fsc.uibmissatgeria.models.ModelsManager;
import com.fsc.uibmissatgeria.models.User;

import java.util.List;

/**
 * Created by xiscosastrecabot on 9/4/15.
 */
public class AccountUIB {

    private Context c;
    private User usr;

    public AccountUIB(Context c) {
        this.c = c;
        this.usr = null;
    }


    private boolean addAcount(String username, String password) {
        Server s = new Server(this.c);
        User usr = s.doLogin(username, password);
        if (usr != null) {
            try {
                String token = s.getTokenRaw();
                SharedPreferences settings = c.getSharedPreferences(Constants.SP_UIB, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(Constants.ACCOUNT_TOKEN, token);
                editor.commit();
                this.usr = usr;
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
        if (result) return this.saveUser();
        return false;
    }

    public void logOut() {
        this.removeAccount();
        ModelsManager.resetDB();
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
            SharedPreferences settings = c.getSharedPreferences(Constants.SP_UIB, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(Constants.ACCOUNT_ID, usr.getIdApi());
            editor.putString(Constants.SP_UPDATE, Constants.SP_UPDATE_DEFAULT);
            editor.putBoolean(Constants.SP_ONLY_TEACHER, false);
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

    public boolean OnlyTeacherNotifications() {
        SharedPreferences settings = c.getSharedPreferences(Constants.SP_UIB, 0);
        return settings.getBoolean(Constants.SP_ONLY_TEACHER, false);
    }

}
