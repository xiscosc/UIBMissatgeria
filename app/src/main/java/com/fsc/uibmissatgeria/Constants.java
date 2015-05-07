package com.fsc.uibmissatgeria;

import android.content.Context;
import android.widget.Toast;

import java.lang.ref.SoftReference;

/**
 * Created by xiscosastrecabot on 22/3/15.
 */
public class Constants {

    public static final String SUBJECT_OBJ = "SUBJECT";
    public static final String GROUP_OBJ = "GROUP";
    public static final String CONVERSATION_OBJ = "CONVERSATION";

    public static final String SP_UIB = "com.fsc.uibmissatgeria_preferences";
    public static final String SP_UPDATE = "prefUpdateFrequency";
    public static final String SP_ONLY_TEACHER = "prefOnlyTeacher";
    public static final String ACCOUNT_TOKEN =  "prefTokenAccount";
    public static final String ACCOUNT_ID = "prefIdAccount";
    public static final String ACCOUNT_TOKEN_FAILED = "token_failed";
    public static final String SP_UPDATE_DEFAULT = "60";

    public static final String SP_MAX_CHAR = "max_char";
    public static final int SP_MAX_CHAR_DEFAULT = 400;

    public static final String NOTIFICATION_CONVERSATIONS = "NOT_CONVERSATION";


    public static final int DEFAULT_GROUP_ID = -1;
    public static final int MAX_LIST_SIZE = 25;
    public static final int MAX_LIST_OLDER_SIZE = 25;
    public static final int MAX_LIST_SIZE_CONVERSATION = 15;
    public static final int MAX_LIST_OLDER_SIZE_CONVERSATION = 15;

    public static final String DATE_FORMAT_SERVER = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT = "HH:mm dd-MM-yyyy";


    public static final int TYPE_TEACHER = 1;
    public static final int TYPE_STUDENT = 2;


    public static final String RESULT_CONVERSATIONS = "result_conversations";
    public static final String RESULT_SUBJECTS = "result_subjects";
    public static final String RESULT_GROUPS = "result_groups";


    public static void showToast(Context ctx, String message) {
        Toast toast = Toast.makeText(ctx, message, Toast.LENGTH_SHORT);
        toast.show();
    }

}


