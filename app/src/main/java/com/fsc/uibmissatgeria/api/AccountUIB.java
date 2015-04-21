package com.fsc.uibmissatgeria.api;

import android.content.Context;
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

    public AccountUIB(Context c) {
        this.c = c;
    }


    private boolean addAcount(String username, String password) {
        Server s = new Server(this.c);
        boolean result = s.doLogin(username, password);
        if (result) {
            try {
                String token = s.getTokenRaw();
                SharedPreferences settings = c.getSharedPreferences(Constants.SP_UIB, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(Constants.ACCOUNT_TOKEN, token);
                editor.commit();
                result = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
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
        Server s = new Server(this.c);
        User usr = s.getUserByToken();
        //USER DB MUST BE EMPTY
        usr.save();
        try {
            SharedPreferences settings = c.getSharedPreferences(Constants.SP_UIB, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(Constants.ACCOUNT_ID, usr.getIdApi());
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

}
