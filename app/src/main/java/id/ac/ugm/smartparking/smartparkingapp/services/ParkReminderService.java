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

public class ParkReminderService extends Service {
    SmartParkingSharedPreferences prefManager;
    BroadcastReceiver brReminder, brTimeUp, brCharge;
    IntentFilter intentFilter1, intentFilter2, intentFilter3;
    PendingIntent pendingIntent1, pendingIntent2, pendingIntent3;
    AlarmManager alarmManager;
    Vibrator vibrator;
    Network network;
    Notif notif;
    long toTime;
    int id_user;
    public ParkReminderService() {
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
        network = new Network(this);
        notif = new Notif();
        vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        toTime = prefManager.getLong(SmartParkingSharedPreferences.PREF_TIME_TO);
        pendingIntent1 = PendingIntent.getBroadcast(this, 0,
                new Intent("id.ac.ugm.smartparking.smartparkingapp-park" + toTime), PendingIntent.FLAG_ONE_SHOT);
        pendingIntent2 = PendingIntent.getBroadcast(this, 0,
                new Intent("id.ac.ugm.smartparking.smartparkingapp-park"), PendingIntent.FLAG_ONE_SHOT);

        brReminder = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                notif.updateNotif(context,
                        "Leaving - 15 minutes left",
                        "You got 15 minutes left to leave the parking slot",
                        300,
                        1);

            }
        };

//        brTimeUp = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                notif.updateNotif(context,
//                        "Leaving - Time's up",
//                        "Your time is up. You'll be charged Rp15.000",
//                        300,
//                        1);
//
//            }
//        };

        brTimeUp = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, Intent intent) {
                id_user = prefManager.getInt(SmartParkingSharedPreferences.PREF_USER_ID);
                network.balanceCharged(id_user, new Network.MyCallback<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody response) {
                        vibrator.vibrate(100);
                        Log.e("update balance", String.valueOf(response));
                        notif.updateNotif(context,
                                "Leaving - Time's up",
                                "Your time is up. You'll be charged Rp15.000",
                                300,
                                1 );
                    }

                    @Override
                    public void onError(String error) {
                        Log.e("update balance", String.valueOf(error));
                    }
                });

            }
        };

//        Intent ongoingIntent = new Intent(this, OngoingActivity.class);
//        ongoingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

//        PendingIntent piOngoing = PendingIntent.getActivity(this, 0, ongoingIntent, 0);

//        builder.setContentIntent(piOngoing);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, toTime - 900000, pendingIntent1);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, toTime, pendingIntent2);

//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, pendingIntent1);
//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 20000, pendingIntent2);

        intentFilter1 = new IntentFilter("id.ac.ugm.smartparking.smartparkingapp-park" + toTime);
        intentFilter1.setPriority(100);
        intentFilter2 = new IntentFilter("id.ac.ugm.smartparking.smartparkingapp-park");
        intentFilter2.setPriority(100);

        registerReceiver(brReminder, intentFilter1);
        registerReceiver(brTimeUp, intentFilter2);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(brReminder);
        unregisterReceiver(brTimeUp);
    }
}
