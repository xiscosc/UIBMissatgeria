package com.fsc.uibmissatgeria;

import android.content.Context;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by xiscosastrecabot on 22/3/15.
 */
public class Constants {

    /** INTENT EXTRA KEYS **/
    public static final String SUBJECT_OBJ = "SUBJECT";
    public static final String GROUP_OBJ = "GROUP";
    public static final String MESSAGE_OBJ = "MESSAGE";
    public static final String CONVERSATION_OBJ = "CONVERSATION";

    /** PREFERENCES KEYS **/
    public static final String SP_UIB = "com.fsc.uibmissatgeria_preferences";
    public static final String SP_UPDATE = "prefUpdateFrequency";
    public static final String SP_ONLY_TEACHER = "prefOnlyTeacher";
    public static final String ACCOUNT_TOKEN =  "prefTokenAccount";
    public static final String ACCOUNT_ID = "prefIdAccount";
    public static final String ACCOUNT_TOKEN_FAILED = "token_failed";
    public static final String SP_MIMETYPES = "mimetypes";
    public static final String SP_MAX_FILES = "max_files";
    public static final String SP_MAX_FILE_SIZE = "max_file_size";
    public static final String SP_MAX_CHAR = "max_char";
    public static final String SP_FIRST_NOTIFICATIONS = "first_notifications";

    /** PREFERENCES DEFAULTS **/
    public static final String SP_UPDATE_DEFAULT = "60";
    public static final int SP_MAX_CHAR_DEFAULT = 400;
    public static final int SP_MAX_FILES_DEFAULT = 5;
    public static final int SP_MAX_FILE_SIZE_DEFAULT = 1024*1024*2;
    public static final Set<String> SP_MIMETYPES_DEFAULT =  new HashSet<>(Arrays.asList(
            "image/png",
            "image/jpeg",
            "image/pjpeg",
            "image/gif",
            "image/bmp" ));
    public static final boolean SP_ONLY_TEACHER_DEFAULT = false;
    public static final boolean SP_FIRST_NOTIFICATIONS_DEFAULT = true;


    /** MANAGER DEFAULTS **/
    public static final int DEFAULT_GROUP_ID = -1;
    public static final int MAX_LIST_SIZE = 25;
    public static final int MAX_LIST_OLDER_SIZE = 25;
    public static final int MAX_LIST_SIZE_CONVERSATION = 15;
    public static final int MAX_LIST_OLDER_SIZE_CONVERSATION = 15;
    public static final int MAX_BODY_MESSAGE_ON_LIST = 45;

    /** DATE FORMATS **/
    public static final String DATE_FORMAT_SERVER = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT = "HH:mm dd-MM-yyyy";

    /** USER TYPES **/
    public static final int TYPE_TEACHER = 1;
    public static final int TYPE_STUDENT = 2;

    /** RESULT KEYS FOR NOTIFICATIONS **/
    public static final String RESULT_CONVERSATIONS = "result_conversations";
    public static final String RESULT_SUBJECTS = "result_subjects";
    public static final String RESULT_GROUPS = "result_groups";
    public static final String NOTIFICATION_CONVERSATIONS_INTENT = "NOT_CONVERSATION";

    /** FOLDER NAMES **/
    public static final String FOLDER_NAME_EXTERNAL = "uib_missatgeria";
    public static final String FOLDER_NAME_IMAGES = "images";
    public static final String FOLDER_NAME_FILES = "files";
    public static final String FOLDER_NAME_AVATARS = "avatars";

    /** RESPONSE CODES SEND MESSAGE **/
    public static final int ALL_OK = 0;
    public static final int MESSSAGE_NOT_SEND = 1;
    public static final int SOME_FILES_NOT_SEND = 2;

    /** NOTIFICATION IDS **/
    public static final int NOTIFICATION_GROUPS_ID = 1;
    public static final int NOTIFICATION_CONVERSATIONS_ID = 2;


    /** SERVER **/
    public static final String SERVER_URL = "https://rhodes.joan-font.com/";
    public static final int SERVER_CERT_RAW = R.raw.cert_server;

    public static void showToast(Context ctx, String message) {
        Toast toast = Toast.makeText(ctx, message, Toast.LENGTH_SHORT);
        toast.show();
    }

}


