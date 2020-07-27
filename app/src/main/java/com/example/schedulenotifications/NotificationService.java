package com.example.schedulenotifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.example.schedulenotifications.models.Contact;

// JobIntentService to handle creating notifications
public class NotificationService extends JobIntentService {

    public static final String CHANNEL_ID = "AppNotificationChannel";
    public static final String CONTACT = "contact";
    static final int JOB_ID = 1000;
    final private Handler mHandler = new Handler();
    private Context context;
    Contact contact;

    // called from NotificationBroadcastReceiver to queue up work which is picked up in onHandleWork
    static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, NotificationService.class, JOB_ID, work);
    }

    public NotificationService() {
        super();
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        context = getApplicationContext();
        String bitmapString = intent.getStringExtra(CONTACT);
        Contact contact = Converters.deserializeFromJson(bitmapString);

        if (contact == null) { return; }     // there's nothing to do here if we don't have a contact

        createNotification(context, contact);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        toast();
    }

    // helper for showing toasts
    void toast() {
        mHandler.post(() -> {
            if (contact != null) {
                Toast.makeText(context, getResources().getString(R.string.notification_time_toast), Toast.LENGTH_LONG).show();
            }
        });
    }

    // creates a notification for a contact with a pending intent to go to Call or MessageSelect based on their preference, as well as a snooze action
    private void createNotification(Context context, Contact contact) {
        if (!isNotificationChannelEnabled(context, CHANNEL_ID)) {
            createNotificationChannel(context);     // if the notification channel wasn't enabled, we need to create it
        }

        NotificationCompat.Builder builder = notificationBuilder(context, contact);
        Notification notification = builder.build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(contact.getId(), notification);
    }

    // this builds a notification for a contact based on their preference
    public NotificationCompat.Builder notificationBuilder(Context context, Contact contact) {

        Intent callIntent = new Intent(context, Call.class);
        String bitmapString = Converters.serializeToJson(contact);
        callIntent.putExtra(CONTACT, bitmapString);
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent callPendingIntent = PendingIntent.getActivity(
                context,
                contact.getId(),
                callIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
        );

        return new NotificationCompat.Builder(this.context, CHANNEL_ID)
                .setContentTitle(getString(R.string.notification_time_for) + " " + contact.getName() + "!")
                .setContentText(getString(R.string.call_them))
                .setSmallIcon(R.drawable.ic_alarm_white_48dp)
                .setColor(getColor(R.color.primary))
                .setContentIntent(callPendingIntent)
                .setAutoCancel(true);
    }

    // checks if the notification channel with the passed in channelId is enabled - we'll only create the channel if this returns false
    public boolean isNotificationChannelEnabled(Context context, @Nullable String channelId){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if(!TextUtils.isEmpty(channelId)) {
                NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                assert manager != null;
                NotificationChannel channel = manager.getNotificationChannel(channelId);
                if (channel != null) {
                    return channel.getImportance() != NotificationManager.IMPORTANCE_NONE;
                }
            }
            return false;
        } else {
            return NotificationManagerCompat.from(context).areNotificationsEnabled();
        }
    }

    // creates a notification channel with ID CHANNEL_ID
    private void createNotificationChannel(Context context) {
        // create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getResources().getString(R.string.notification_channel_name);
            String description = context.getResources().getString(R.string.notification_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
    }
}