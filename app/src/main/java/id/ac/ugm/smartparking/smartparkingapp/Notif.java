package id.ac.ugm.smartparking.smartparkingapp;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

/**
 * Created by Shindy on 26-Jun-18.
 */

public class Notif {
    public void updateNotif(Context context, String title, String text, long vibrate) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(title)
                .setContentText(text);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());

        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(vibrate);

    }
}
