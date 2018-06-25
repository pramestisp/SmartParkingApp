package id.ac.ugm.smartparking.smartparkingapp.services;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import id.ac.ugm.smartparking.smartparkingapp.R;
import id.ac.ugm.smartparking.smartparkingapp.utils.SmartParkingSharedPreferences;

public class ReminderService extends Service {
    SmartParkingSharedPreferences prefManager;
    BroadcastReceiver brReminder;
    IntentFilter intentFilter;
    AlarmManager alarmManager;
    long fromTime;

    public ReminderService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        prefManager = new SmartParkingSharedPreferences(this);
        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        fromTime = prefManager.getLong(SmartParkingSharedPreferences.PREF_TIME_FROM);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                new Intent("id.ac.ugm.smartparking.smartparkingapp" + fromTime), PendingIntent.FLAG_ONE_SHOT);

        brReminder = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                        .setContentTitle("15 minutes left")
                        .setContentText("You got 15 minutes left");
                NotificationManager notificationManager = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0, builder.build());

                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(300);
            }
        };
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, fromTime - 900000, pendingIntent);
        intentFilter = new IntentFilter("id.ac.ugm.smartparking.smartparkingapp" + fromTime);
        intentFilter.setPriority(100);
        registerReceiver(brReminder, intentFilter);

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        unregisterReceiver(brReminder);
    }
}

