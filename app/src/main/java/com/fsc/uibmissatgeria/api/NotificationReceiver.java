package com.fsc.uibmissatgeria.api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {
    public NotificationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        AccountUIB accountUIB = new AccountUIB(context);
        accountUIB.startNotificationService();
    }
}
