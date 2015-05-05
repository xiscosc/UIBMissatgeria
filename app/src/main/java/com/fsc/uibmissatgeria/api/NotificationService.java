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
import com.fsc.uibmissatgeria.ui.activities.ConversationActivity;
import com.fsc.uibmissatgeria.ui.activities.PrincipalActivity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class NotificationService extends Service {
    private static NotificationService instance  = null;
    private AccountUIB accountUIB;
    private ModelsManager mm;
    private  Timer timer;
    private  TimerTask ttask;


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
        mm = new ModelsManager(getApplicationContext());
        this.accountUIB = new AccountUIB(getApplicationContext());
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

    private class ConversationTask extends AsyncTask<Void, Void, List<Conversation>> {

        @Override
        protected List<Conversation> doInBackground(Void... params) {
            return checkConversations();
        }

        @Override
        protected void onPostExecute(List<Conversation> conversations) {
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
                    mBuilder.setContentTitle(new_messages)
                            .setContentText(you_have + " " + conversations.size() + " "  +converString);
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

    }

    private List<Conversation> checkConversations() {
        List<Conversation> currentConversations = mm.getConversations();
        List<Conversation> newConversations = mm.updateConversations();

        for (Conversation c : currentConversations) {
            if (newConversations.contains(c)) {
                int index = newConversations.indexOf(c);
                Conversation c2 = newConversations.get(index);
                if (c2.getLastMessageId().equals(c.getLastMessageId())) {
                    newConversations.remove(index);
                } else {
                    MessageConversation m = c2.getLastMessage();
                    if (m != null && m.isRead()) {
                        newConversations.remove(index);
                    }
                }

            }
        }
        return newConversations;

    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
