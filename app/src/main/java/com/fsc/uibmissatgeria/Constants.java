package com.fsc.uibmissatgeria;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by xiscosastrecabot on 22/3/15.
 */
public class Constants {

    public static final String SUBJECT_OBJ = "SUBJECT";
    public static final String GROUP_OBJ = "GROUP";
    public static final String CONVERSATION_OBJ = "CONVERSATION";

    public static final String SP_UIB = "shpr_uib";
    public static final String SP_UPDATE = "prefUpdateFrequency";

    public static final String RESULT_ERROR = "result_error";
    public static final String RESULT_TOTAL = "result_total";
    public static final String RESULT_MESSAGES = "result_messages";
    public static final String RESULT_SUBJECTS = "result_subjects";
    public static final String RESULT_CONVERSATIONS = "result_conversations";
    public static final String RESULT_PEERS = "result_peers";

    public static final String ACCOUNT_TOKEN =  "UIB_T_ACCOUNT";
    public static final String ACCOUNT_ID = "UIB_ID_ACCOUNT";
    public static final String ACCOUNT_TOKEN_FAILED = "token_failed";

    public static final int MAX_CHAR = 400;
    public static final int DEFAULT_GROUP_ID = -1;
    public static final int MAX_LIST_SIZE = 25;
    public static final int MAX_LIST_OLDER_SIZE = 25;
    public static final int MAX_LIST_SIZE_CONVERSATION = 15;
    public static final int MAX_LIST_OLDER_SIZE_CONVERSATION = 15;

    public static final String DATE_FORMAT_SERVER = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT = "HH:mm dd-MM-yyyy";


    public static final int TYPE_TEACHER = 1;
    public static final int TYPE_STUDENT = 2;


    public static void showToast(Context ctx, String message) {
        Toast toast = Toast.makeText(ctx, message, Toast.LENGTH_SHORT);
        toast.show();
    }

}


