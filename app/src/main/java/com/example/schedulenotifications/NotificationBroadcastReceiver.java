package com.example.schedulenotifications;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import java.util.List;
import java.util.Objects;
import com.example.schedulenotifications.models.Contact;
import com.example.schedulenotifications.repositories.ContactRepository;

// BroadcastReceiver that starts NotificationService to create notifications for each contact
// and starting notifications up when the user's phone boots up
public class NotificationBroadcastReceiver extends BroadcastReceiver {

    private ContactRepository contactRepository;
    public static final String CONTACT = "contact";
    public Context context;

    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context.getApplicationContext();
        NotificationService.enqueueWork(context, intent);           // enqueue work in NotificationService for a contact (stored in the intent's extra)
        if (Objects.equals(intent.getAction(), Intent.ACTION_BOOT_COMPLETED)) {         // if the phone is starting up, start the notification tasks
            contactRepository = new ContactRepository((Application) this.context);
            StartNotificationsAsyncTask startNotifications = new StartNotificationsAsyncTask(this.context);
            startNotifications.execute();
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class StartNotificationsAsyncTask extends AsyncTask<Integer, Void, Void> {

        Context context;

        StartNotificationsAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Integer... params) {
            List<Contact> contactList = contactRepository.getAllContacts();

            for (Contact contact : contactList) {
                MessageCreator.setMessageTask(context, contact);
            }
            return null;
        }
    }

    // this is called by NotificationScheduler to create an intent to enter into this BroadcastReceiver;
    // this is a deep link intent, and needs the task stack
    public static PendingIntent getReminderPendingIntent(Context context, Contact contact, boolean isOneTime) {
        int notificationID = isOneTime ? -contact.getId() : contact.getId();    // we need to keep one off notifications (snoozed notifications * ones triggered "now" from the options menu) separate
        Intent broadcastAction = new Intent(context, NotificationBroadcastReceiver.class);
        String bitmapString = Converters.serializeToJson(contact);
        broadcastAction.putExtra(CONTACT, bitmapString);
        return PendingIntent.getBroadcast(context, notificationID, broadcastAction, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}