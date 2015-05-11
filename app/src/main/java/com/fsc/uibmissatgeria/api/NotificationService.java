package com.fsc.uibmissatgeria.api;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.Toast;

import com.fsc.uibmissatgeria.Constants;
import com.fsc.uibmissatgeria.R;
import com.fsc.uibmissatgeria.models.Conversation;
import com.fsc.uibmissatgeria.models.MessageConversation;
import com.fsc.uibmissatgeria.models.ModelsManager;
import com.fsc.uibmissatgeria.models.Subject;
import com.fsc.uibmissatgeria.models.SubjectGroup;
import com.fsc.uibmissatgeria.ui.activities.ConversationActivity;
import com.fsc.uibmissatgeria.ui.activities.MessagesActivity;
import com.fsc.uibmissatgeria.ui.activities.PrincipalActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends Service {
    private static NotificationService instance  = null;
    private AccountUIB accountUIB;
    private  Timer timer;
    private  TimerTask ttask;
    private Server server;


    public NotificationService() {

    }

    public static boolean isRunning() {
        return instance != null;
    }

    public static void disableRun() {
        instance = null;
    }



    @Override
    public void onCreate() {
        super.onCreate();
        this.accountUIB = new AccountUIB(getApplicationContext());
        this.server = new Server(getApplicationContext());
        instance = this;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelTimeReload();
        instance = null;
    }

    @Override
    public void onStart(Intent intent, int startid) {
        super.onStart(intent, startid);
        if (accountUIB.getPeriodMS()>0) {
            prepareTimer();
        } else {
            stopSelf();
        }

    }


    private void prepareTimer() {
        final Long current = accountUIB.getPeriodMS();
        final Handler handler = new Handler();
        timer = new Timer();
        ttask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        Boolean isLogged = accountUIB.isLogged();
                        if (isLogged && accountUIB.getPeriodMS().equals(current)) {
                            (new ConversationTask()).execute();
                        } else if (isLogged && accountUIB.getPeriodMS()>0) {
                            cancelTimeReload();
                            prepareTimer();
                        } else {
                            cancelTimeReload();
                            stopSelf();
                        }
                    }
                });
            }
        };
        timer.schedule(ttask, 0, current);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    public void cancelTimeReload() {
        if (timer!= null && ttask != null) {
            timer.cancel();
            timer.purge();
            ttask.cancel();
        }
    }

    private class ConversationTask extends AsyncTask<Void, Void, Map<String, Object>> {

        @Override
        protected Map<String, Object> doInBackground(Void... params) {
            return server.getNotifications();
        }

        @Override
        protected void onPostExecute(Map<String, Object> notifications) {

            if (notifications.containsKey(Constants.RESULT_CONVERSATIONS)) {
                notificateConversations((List<Conversation>) notifications.get(Constants.RESULT_CONVERSATIONS));
            }

            if (notifications.containsKey(Constants.RESULT_SUBJECTS) && notifications.containsKey(Constants.RESULT_GROUPS)) {
                notificateGroups((List<SubjectGroup>) notifications.get(Constants.RESULT_SUBJECTS), (List<SubjectGroup>) notifications.get(Constants.RESULT_GROUPS));
            }

        }

    }

    private void notificateGroups(List<SubjectGroup> subjects, List<SubjectGroup> groups) {
        List<SubjectGroup> toNotificate = new ArrayList<>();
        if (!subjects.isEmpty() || !groups.isEmpty()) {
            for (SubjectGroup sg : subjects) {
                toNotificate.add(sg);
            }
            for (SubjectGroup sg : groups) {
                toNotificate.add(sg);
            }

            Intent resultIntent;
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.drawable.ic_stat_action_speaker_notes)
                    .setAutoCancel(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setDefaults(Notification.DEFAULT_VIBRATE);

            if (toNotificate.size()>1) {
                String new_messages = getResources().getString(R.string.new_messages_group);
                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                inboxStyle.setBigContentTitle(getResources().getString(R.string.new_messages)+":");
                for(SubjectGroup g: toNotificate) {
                    String nameGroup;
                    if (g.getIdApi() == Constants.DEFAULT_GROUP_ID) {
                        nameGroup = getResources().getString(R.string.general);
                    } else {
                        nameGroup = g.getName();
                    }
                    inboxStyle.addLine(g.getSubject().getName() +" - " +nameGroup);
                }

                mBuilder.setContentTitle(new_messages)
                        .setContentText(getResources().getString(R.string.new_messages_group_you))
                        .setStyle(inboxStyle);
                resultIntent = new Intent(getApplicationContext(), PrincipalActivity.class);
                stackBuilder.addParentStack(PrincipalActivity.class);
            } else {
                String new_message = getResources().getString(R.string.new_message_group);
                SubjectGroup g = toNotificate.get(0);
                String nameGroup;
                if (g.getIdApi() == Constants.DEFAULT_GROUP_ID) {
                    nameGroup = getResources().getString(R.string.general);
                } else {
                    nameGroup = g.getName();
                }
                mBuilder.setContentTitle(new_message)
                        .setContentText(g.getSubject().getName() +" - " +nameGroup);

                resultIntent = new Intent(getApplicationContext(), MessagesActivity.class);
                resultIntent.putExtra(Constants.SUBJECT_OBJ, g.getSubject().getId());
                resultIntent.putExtra(Constants.GROUP_OBJ, g.getId());
                stackBuilder.addParentStack(MessagesActivity.class);

            }
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
            mNotificationManager.notify(1, mBuilder.build());



        }
    }


    private void notificateConversations(List<Conversation> conversations) {
        if (!conversations.isEmpty()) {
            Intent resultIntent;
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.drawable.ic_stat_action_speaker_notes)
                    .setAutoCancel(true)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setDefaults(Notification.DEFAULT_VIBRATE);

            if (conversations.size()>1) {
                String new_messages = getResources().getString(R.string.new_messages);
                String you_have = getResources().getString(R.string.you_new_messages);
                String converString = getResources().getString(R.string.conversations);
                NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
                inboxStyle.setBigContentTitle(new_messages+":");
                for(Conversation c: conversations) {
                    inboxStyle.addLine(c.getPeerName());
                }
                mBuilder.setContentTitle(new_messages)
                        .setContentText(you_have + " " + conversations.size() + " "  +converString)
                        .setStyle(inboxStyle);
                resultIntent = new Intent(getApplicationContext(), PrincipalActivity.class);
                resultIntent.putExtra(Constants.NOTIFICATION_CONVERSATIONS, true);
                stackBuilder.addParentStack(PrincipalActivity.class);
            } else {
                String new_message = getResources().getString(R.string.new_message_from);
                Conversation c = conversations.get(0);
                MessageConversation m = c.getLastMessage();
                mBuilder.setContentTitle(new_message + " "+c.getPeerName())
                        .setContentText(m.getBody());

                resultIntent = new Intent(getApplicationContext(), ConversationActivity.class);
                resultIntent.putExtra(Constants.CONVERSATION_OBJ, c.getId());
                stackBuilder.addParentStack(ConversationActivity.class);

            }
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
            mNotificationManager.notify(1, mBuilder.build());
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
