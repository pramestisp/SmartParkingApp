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
import android.util.Log;

import id.ac.ugm.smartparking.smartparkingapp.Notif;
import id.ac.ugm.smartparking.smartparkingapp.OngoingActivity;
import id.ac.ugm.smartparking.smartparkingapp.R;
import id.ac.ugm.smartparking.smartparkingapp.network.Network;
import id.ac.ugm.smartparking.smartparkingapp.utils.SmartParkingSharedPreferences;
import okhttp3.ResponseBody;

public class BookingReminderService extends Service {
    SmartParkingSharedPreferences prefManager;
    BroadcastReceiver brReminder, brTimeUp, brOvertime;
    IntentFilter intentFilter1, intentFilter2, intentFilter3;
    PendingIntent pendingIntent1, pendingIntent2, pendingIntent3;
    AlarmManager alarmManager;
    Network network;
    Notif notif;
    long fromTime;
    int idReservation;

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
        pendingIntent1 = PendingIntent.getBroadcast(this, 0,
                new Intent("id.ac.ugm.smartparking.smartparkingapp-book" + fromTime), PendingIntent.FLAG_ONE_SHOT);
        pendingIntent2 = PendingIntent.getBroadcast(this, 0,
                new Intent("id.ac.ugm.smartparking.smartparkingapp-bookTimeUp"), PendingIntent.FLAG_ONE_SHOT);
        pendingIntent3 = PendingIntent.getBroadcast(this, 0,
                new Intent("id.ac.ugm.smartparking.smartparkingapp-bookOvertime"), PendingIntent.FLAG_ONE_SHOT);

//        notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);


//        ongoingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        brReminder = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                notif.updateNotif(context,
                        "Arrival - 15 minutes left",
                        "You got 15 minutes left",
                        300,
                        0);

            }
        };

        brTimeUp = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                notif.updateNotif(context,
                        "Arrival - Time's up",
                        "Your time is up. The booking will be cancelled in 10 minutes.",
                        300,
                        0);

                Log.e("time's up", "time's up");
            }
        };

        brOvertime = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, Intent intent) {
                idReservation = prefManager.getInt(SmartParkingSharedPreferences.PREF_ID);
                network.resExpired(idReservation, new Network.MyCallback<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody response) {
                        notif.updateNotif(context,
                                "Arrival - Cancelled",
                                "Your booking is cancelled",
                                500,
                                0);
                    }

                    @Override
                    public void onError(String error) {
                        Log.e("update slot status", String.valueOf(error));
                        notif.updateNotif(context,
                                "Arrival - Cancelled",
                                "Your booking is cancelled but failed when updating slot status",
                                500,
                                0);
                    }
                });

            }
        };

//        builder.setContentIntent(piOngoing);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, fromTime - 900000, pendingIntent1);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, fromTime, pendingIntent2);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, fromTime + 600000, pendingIntent3);
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() +10000, pendingIntent1);
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 20000, pendingIntent2);

        intentFilter1 = new IntentFilter("id.ac.ugm.smartparking.smartparkingapp-book" + fromTime);
        intentFilter1.setPriority(100);
        intentFilter2 = new IntentFilter("id.ac.ugm.smartparking.smartparkingapp-bookTimeUp");
        intentFilter2.setPriority(100);
        intentFilter3 = new IntentFilter("id.ac.ugm.smartparking.smartparkingapp-bookOvertime");
        intentFilter3.setPriority(100);

        registerReceiver(brReminder, intentFilter1);
        registerReceiver(brTimeUp, intentFilter2);
        registerReceiver(brOvertime, intentFilter3);

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        unregisterReceiver(brReminder);
        unregisterReceiver(brTimeUp);
        unregisterReceiver(brOvertime);
    }

}

