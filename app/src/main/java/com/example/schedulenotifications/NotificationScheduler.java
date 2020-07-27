package com.example.schedulenotifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import com.example.schedulenotifications.models.Contact;

// class to handle scheduling & cancelling of repeat alarms
class NotificationScheduler {

    void setRepeatAlarm(Context context, long alarmTime, long repeatTime, Contact contact) {
        AlarmManager manager = getAlarmManager(context);
        PendingIntent broadcastOperation = NotificationBroadcastReceiver.getReminderPendingIntent(context, contact, false);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, alarmTime, repeatTime, broadcastOperation);
    }

    private AlarmManager getAlarmManager(Context context) {
        return (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    void cancelAlarm(Context context, Contact contact) {
        AlarmManager manager = getAlarmManager(context);
        PendingIntent broadcastOperation = NotificationBroadcastReceiver.getReminderPendingIntent(context, contact, false);
        manager.cancel(broadcastOperation);
    }
}
