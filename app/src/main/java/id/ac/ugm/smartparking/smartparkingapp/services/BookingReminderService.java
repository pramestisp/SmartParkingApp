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

import id.ac.ugm.smartparking.smartparkingapp.Notif;
import id.ac.ugm.smartparking.smartparkingapp.OngoingActivity;
import id.ac.ugm.smartparking.smartparkingapp.R;
import id.ac.ugm.smartparking.smartparkingapp.utils.SmartParkingSharedPreferences;

public class BookingReminderService extends Service {
    SmartParkingSharedPreferences prefManager;
    BroadcastReceiver brReminder, brTimeUp;
    IntentFilter intentFilter;
    AlarmManager alarmManager;
    Vibrator vibrator;
    NotificationCompat.Builder builder;
    NotificationManager notificationManager;
    Notif notif;
    long fromTime;

    public BookingReminderService() {
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
        notif = new Notif();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        fromTime = prefManager.getLong(SmartParkingSharedPreferences.PREF_TIME_FROM);

//        Intent ongoingIntent = new Intent(this, OngoingActivity.class);
//        PendingIntent piOngoing = PendingIntent.getActivity(this, 0, ongoingIntent, 0);
        PendingIntent piReminder = PendingIntent.getBroadcast(this, 0,
                new Intent("id.ac.ugm.smartparking.smartparkingapp" + fromTime), PendingIntent.FLAG_ONE_SHOT);
//        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);


//        ongoingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        brReminder = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                notif.updateNotif(context,
                        "15 minutes left",
                        "You got 15 minutes left",
                        300);
//                builder = new NotificationCompat.Builder(context);
//                builder.setSmallIcon(R.drawable.ic_notifications_black_24dp)
//                        .setContentTitle("15 minutes left")
//                        .setContentText("You got 15 minutes left");
//                NotificationManager notificationManager = (NotificationManager) context
//                        .getSystemService(Context.NOTIFICATION_SERVICE);
//                notificationManager.notify(0, builder.build());
//                vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//                vibrator.vibrate(300);
            }
        };

        brTimeUp = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //TODO: tes notif baru, kalo berhasil tambahin park reminder
                //TODO: tambahin service cek slot, set repeating
                notif.updateNotif(context,
                        "Time's up",
                        "Your time is up. The booking will be cancelled.",
                        500);
//                builder.setSmallIcon(R.drawable.ic_notifications_black_24dp)
//                        .setContentTitle("Time's up")
//                        .setContentText("Your time is up. The booking will be cancelled.");
//                NotificationManager notificationManager = (NotificationManager) context
//                        .getSystemService(Context.NOTIFICATION_SERVICE);
//                notificationManager.notify(0, builder.build());
//
//                vibrator.vibrate(500);
            }
        };

//        builder.setContentIntent(piOngoing);

//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, fromTime - 900000, piReminder);
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, fromTime, piReminder);
        //TODO: KENAPA CUMA NOTIF TERAKHIR YG MUNCUL???
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() +10000, piReminder);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 20000, piReminder);

        intentFilter = new IntentFilter("id.ac.ugm.smartparking.smartparkingapp" + fromTime);
        intentFilter.setPriority(100);

        registerReceiver(brReminder, intentFilter);
        registerReceiver(brTimeUp, intentFilter);

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        unregisterReceiver(brReminder);
        unregisterReceiver(brTimeUp);
    }

}

