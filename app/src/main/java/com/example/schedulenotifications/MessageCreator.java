package com.example.schedulenotifications;

import android.content.Context;
import com.example.schedulenotifications.models.Contact;
import java.util.Calendar;
import static com.example.schedulenotifications.Converters.convertLongToDate;

// class with static methods to handle creating recurring notification tasks
class MessageCreator {

    // create (or cancel, if they're inactive) a recurring notification task for a contact
    static void setMessageTask(Context context, Contact contact) {
        NotificationScheduler notificationScheduler = new NotificationScheduler();
        if (contact.getStatus() != 1) {
            notificationScheduler.cancelAlarm(context, contact);
            return;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(convertLongToDate(contact.startDate));
        int mRepeatNo = contact.frequency;
        long milDay = 60000L;     //60000L is a minute //86400000L is a day
        long mRepeatTime = mRepeatNo * milDay;

        long nextTimeStamp = getNextAlarmTime(contact.startDate, mRepeatTime);
        notificationScheduler.cancelAlarm(context, contact);
        notificationScheduler.setRepeatAlarm(context, nextTimeStamp, mRepeatTime, contact);
    }

    // gets the next scheduled notification time (excluding snoozed notifications) to display in setMessageTask & in ContactEdit
    static long getNextAlarmTime(long startTime, long repeatTime) {
        Calendar cal = Calendar.getInstance();
        long now =  cal.getTimeInMillis();
        long nextTime = startTime;

        while (nextTime < now) {
            nextTime = nextTime + repeatTime;
        }

        return nextTime;
    }
}