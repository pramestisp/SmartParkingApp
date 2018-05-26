package id.ac.ugm.smartparking.smartparkingapp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import id.ac.ugm.smartparking.smartparkingapp.model.CheckSlotStatusResponse;
import id.ac.ugm.smartparking.smartparkingapp.network.Network;
import id.ac.ugm.smartparking.smartparkingapp.utils.Constants;
import id.ac.ugm.smartparking.smartparkingapp.utils.SmartParkingSharedPreferences;

/**
 * Created by Shindy on 31-Mar-18.
 */
public class OngoingActivity extends AppCompatActivity {

    TextView tvTimeCountdown, tvTimeRemains, tvSlotNo, tvArrivalTime, tvLeavingTime, tvPrice;
    Button bQR, bViewSlot;

    private Network network;

    private SmartParkingSharedPreferences prefManager;

    private long fromTime, toTime, currentTime, timeLeft;
    boolean arrived;
    Intent intentHome;
    AlarmManager alarmManager;
    AlertDialog.Builder builder;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        network = new Network(this);
        prefManager = new SmartParkingSharedPreferences(this);
        tvTimeCountdown = findViewById(R.id.tvDate);
        tvTimeRemains = findViewById(R.id.tvTimeRemains);
        tvSlotNo = findViewById(R.id.tvSlotNo);
        tvArrivalTime = findViewById(R.id.tvArrivalTime);
        tvLeavingTime = findViewById(R.id.tvLeavingTime);
        tvPrice = findViewById(R.id.tvPrice);
        bQR = findViewById(R.id.bShowQR);
        bViewSlot = findViewById(R.id.bViewSlot);

        intentHome = new Intent(OngoingActivity.this, MainActivity.class);

        getIntent();

        builder = new AlertDialog.Builder(OngoingActivity.this);

//        isArrived();


        fromTime = prefManager.getLong(SmartParkingSharedPreferences.PREF_TIME_FROM);
        toTime = prefManager.getLong(SmartParkingSharedPreferences.PREF_TIME_TO);
        currentTime = Calendar.getInstance().getTimeInMillis();

//        timeLeft = fromTime - currentTime;
        timeLeft = 1000000;
//        boolean reserved = true;

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date dateFrom = new Date(fromTime);
        tvArrivalTime.setText(sdf.format(dateFrom));
        Date dateTo = new Date(toTime);
        tvLeavingTime.setText(sdf.format(dateTo));

        float price = prefManager.getFloat(SmartParkingSharedPreferences.PREF_PRICE);
        Log.e("price", String.valueOf(price));
        Locale localeID = new Locale("in", "ID");
        NumberFormat RpFormat = NumberFormat.getCurrencyInstance(localeID);
        tvPrice.setText(RpFormat.format((double)price));
        tvSlotNo.setText(prefManager.getString(SmartParkingSharedPreferences.PREF_SLOT_NAME));

        Log.e("fromMillis", String.valueOf(fromTime));
        Log.e("currentMillis", String.valueOf(currentTime));
        Log.e("timeLeft/difference", String.valueOf(timeLeft));

        final CountDownTimer timer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                updateText();
                //Alert(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                boolean reserved = false;
                prefManager.setBoolean(SmartParkingSharedPreferences.PREF_RESERVED, reserved);
                tvTimeCountdown.setText("Time's up");
                builder.setMessage("Your reservation is cancelled")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(intentHome);
                            }
                        }).show();
            }
        }.start();

        bQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View mView = getLayoutInflater().inflate(R.layout.dialog_qr_code, null);
                builder.setView(mView);
                final AlertDialog QRDialog = builder.create();
                QRDialog.show();

                ImageView ivQR = mView.findViewById(R.id.ivQR);
                Button bArrived = mView.findViewById(R.id.bArrived);

//                int id_reservation = prefManager.getInt(SmartParkingSharedPreferences.PREF_ID);
                int id_reservation = 123;

                String textQR = String.valueOf(id_reservation);
                Log.i("id", textQR);
                MultiFormatWriter mfw = new MultiFormatWriter();
                try {
                    BitMatrix bm = mfw.encode(textQR, BarcodeFormat.QR_CODE, 500, 500);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bm);
                    ivQR.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }

                if(prefManager.getBoolean(SmartParkingSharedPreferences.PREF_ARRIVED)) {
                    bArrived.setVisibility(View.GONE);
                }

                bArrived.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        arrived = true;
                        prefManager.setBoolean(SmartParkingSharedPreferences.PREF_ARRIVED, arrived);
                        isArrived();
                        timer.cancel();
                        QRDialog.dismiss();

                        //TODO: mulai alarm sampe leaving time + penalti jika lewat dari limit
                        Log.i("time_to", String.valueOf(toTime));
                        startAlarm();
                    }
                });
            }
        });

        bViewSlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentView = new Intent(v.getContext(), ViewSlotActivity.class);
                startActivity(intentView);
            }
        });

        alert();

    }

    private void alert() {
        if(currentTime == fromTime - 900000) {
            builder.setMessage("You got 15 minutes left")
                    .setPositiveButton("OK", null).show();
            showNotif();
        }
    }

    private void startAlarm() {

        alarmManager = (AlarmManager)OngoingActivity.this.getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(OngoingActivity.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(OngoingActivity.this, 0, myIntent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, toTime, pendingIntent);
    }

    private void Alert(long millis) {
        //TODO: ini gmn ya biar ga kejang2 // gmn biar ga ilang timernya ketika di-close
        if (millis == 900000) {
            int params = prefManager.getInt(SmartParkingSharedPreferences.PREF_ID);
//            network.getSlotStatus(params, new Network.MyCallback<CheckSlotStatusResponse>() {
//                @Override
//                public void onSuccess(CheckSlotStatusResponse response) {
//                    if (response.getStatus().equals(Constants.PARKED) ) {
//
//                    } else {
//
//                    }
//                }
//
//                @Override
//                public void onError(String error) {
//
//                }
//            });


//                    Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//                    mVibrator.vibrate(300);
            builder.setMessage("You got 15 minutes left")
                    .setPositiveButton("OK", null).show();
            showNotif();
        }

    }

    private void updateText() {
        long diffSec = TimeUnit.MILLISECONDS.toSeconds(timeLeft);
        int hour = (int) (diffSec / (60*60));
        int minremaining = (int) (diffSec % (60 * 60));
        int min = (int) (minremaining / 60);
        int sec = (int) (minremaining % (60));

        String timeLeftFormatted = String.format(Locale.getDefault(),
                "%02d:%02d:%02d", hour, min, sec);

        tvTimeCountdown.setText(timeLeftFormatted);
    }

    private void showNotif() {
        NotificationManager notif = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Notification mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                        .setContentTitle("Time's up!")
                        .setContentText("Your time is up").build();

        mBuilder.flags |= Notification.FLAG_AUTO_CANCEL;
        notif.notify(0, mBuilder);

        Intent intent = new Intent(this, OngoingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        );

    }

    private void isArrived() {
        if(prefManager.getBoolean(SmartParkingSharedPreferences.PREF_ARRIVED)) {
            tvTimeRemains.setVisibility(View.GONE);
            tvTimeCountdown.setVisibility(View.GONE);

        }
    }

    @Override
    public void onBackPressed() {
        startActivity(intentHome);
    }
}

