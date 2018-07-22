package id.ac.ugm.smartparking.smartparkingapp.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import id.ac.ugm.smartparking.smartparkingapp.Notif;
import id.ac.ugm.smartparking.smartparkingapp.OngoingActivity;
import id.ac.ugm.smartparking.smartparkingapp.model.CheckSlotStatusResponse;
import id.ac.ugm.smartparking.smartparkingapp.network.Network;
import id.ac.ugm.smartparking.smartparkingapp.utils.Constants;
import id.ac.ugm.smartparking.smartparkingapp.utils.SmartParkingSharedPreferences;

public class CheckSlotService extends Service {
    Network network;
    SmartParkingSharedPreferences prefManager;
    BroadcastReceiver broadcastReceiver;
    Notif notif;
    AlertDialog.Builder builder;
    OngoingActivity ongoing;
    AlarmManager alarmManager;
    long fromTime;

    public CheckSlotService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        prefManager = new SmartParkingSharedPreferences(this);
        network = new Network(this);
        ongoing = new OngoingActivity();
        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        prefManager = new SmartParkingSharedPreferences(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        fromTime = prefManager.getLong(SmartParkingSharedPreferences.PREF_TIME_FROM);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0,
                new Intent("id.ac.ugm.smartparking.smartparkingapp-check slot"), PendingIntent.FLAG_ONE_SHOT);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, Intent intent) {
                int params = prefManager.getInt(SmartParkingSharedPreferences.PREF_ID);
                network.getSlotStatus(params, new Network.MyCallback<CheckSlotStatusResponse>() {
                    @Override
                    public void onSuccess(CheckSlotStatusResponse response) {
                        if (response.getStatus() == Constants.PARKED) {
                            notif.updateNotif(context,
                                    "Your slot is occupied by other person",
                                    "Please confirm to change selected slot",
                                    500,
                                    2);
                            builder = new AlertDialog.Builder(context);
//                    mBuilder.setContentTitle("Your slot is not available")
//                            .setContentText("Please confirm to change selected slot");
                            builder.setMessage("Your slot is not available")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
//                                    loading.show();
                                            ongoing.checkSlotRequest();
                                        }
                                    })
                                    .create()
                                    .show();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Log.e("get status", String.valueOf(error));
                        //TODO: Toastnya gabisa muncul aku kudu piye???
//                        ongoing.Toast(error);
//                Toast.makeText(context,
//                        error,
//                        Toast.LENGTH_SHORT).show();
                    }
                });

            }
        };
////        PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent (this, broadcastReceiver.getClass()),0 );
//        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis(), pi);
        alarmManager.setRepeating(AlarmManager.RTC, fromTime - 900000, 60*1000, pi);
        IntentFilter intentFilter = new IntentFilter("id.ac.ugm.smartparking.smartparkingapp-check slot");
        intentFilter.setPriority(100);
        registerReceiver(broadcastReceiver, intentFilter);


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(broadcastReceiver);
    }
}
