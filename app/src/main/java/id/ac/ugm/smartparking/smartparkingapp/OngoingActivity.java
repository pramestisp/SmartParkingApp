package id.ac.ugm.smartparking.smartparkingapp;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import id.ac.ugm.smartparking.smartparkingapp.model.CheckSlotResponse;
import id.ac.ugm.smartparking.smartparkingapp.model.CheckSlotStatusResponse;
import id.ac.ugm.smartparking.smartparkingapp.model.ReservationNewRequestModel;
import id.ac.ugm.smartparking.smartparkingapp.model.ReservationResponse;
import id.ac.ugm.smartparking.smartparkingapp.network.Network;
import id.ac.ugm.smartparking.smartparkingapp.services.BookingReminderService;
import id.ac.ugm.smartparking.smartparkingapp.services.CheckSlotService;
import id.ac.ugm.smartparking.smartparkingapp.services.ParkReminderService;
import id.ac.ugm.smartparking.smartparkingapp.utils.Constants;
import id.ac.ugm.smartparking.smartparkingapp.utils.SmartParkingSharedPreferences;
import okhttp3.ResponseBody;

/**
 * Created by Shindy on 31-Mar-18.
 */
public class OngoingActivity extends AppCompatActivity {

    TextView tvTimeCountdown, tvTimeRemains, tvSlotNo, tvArrivalTime, tvLeavingTime, tvPrice, tvSlotNoNew;
    Button bQR, bViewSlot, bCancel, bYes, bNo, bArrived, bLeft, bCheckSlot;

    private Network network;

    private SmartParkingSharedPreferences prefManager;

    private long fromTime, toTime, currentTime, timeLeft, start, stop, arriveTime, leftTime, parkTime;
    float price;
    int idSlot;
    boolean arrived, reserved;
    Intent intentHome, intentBookingReminder, intentParkReminder;
    AlarmManager alarmManager;
    AlertDialog.Builder builder;
    ProgressDialog loading;
    CountDownTimer timer;
    NotificationCompat.Builder mBuilder;
    PendingIntent pendingIntentReminder, pendingIntentPark, pendingIntentCheck;
    BroadcastReceiver brReminder, brCheck;
    Vibrator mVibrator;
    MainActivity mainActivity;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mainActivity = new MainActivity();
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
        bCancel = findViewById(R.id.bCancelRes);
        bArrived = findViewById(R.id.bArrived);
//        bLeft = findViewById(R.id.bLeft);
//        bCheckSlot = findViewById(R.id.bCheckSlot);

        mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        alarmManager = (AlarmManager)OngoingActivity.this.getSystemService(Context.ALARM_SERVICE);

        intentHome = new Intent(OngoingActivity.this, MainActivity.class);
        intentBookingReminder = new Intent(OngoingActivity.this, BookingReminderService.class);
        intentParkReminder = new Intent(OngoingActivity.this, ParkReminderService.class);

        getIntent();

        builder = new AlertDialog.Builder(OngoingActivity.this);

        loading = new ProgressDialog(this);

        isArrived();
//        startService(new Intent(OngoingActivity.this, CheckSlotService.class));

//        checkSlot();

        fromTime = prefManager.getLong(SmartParkingSharedPreferences.PREF_TIME_FROM);
        toTime = prefManager.getLong(SmartParkingSharedPreferences.PREF_TIME_TO);
        currentTime = Calendar.getInstance().getTimeInMillis();

        timeLeft = fromTime - currentTime;

        Log.e("fromMillis", String.valueOf(fromTime));
        Log.e("currentMillis", String.valueOf(currentTime));
        Log.e("timeLeft/difference", String.valueOf(timeLeft));

        timer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                updateText();
                //checkTime();
                //Alert(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                tvTimeCountdown.setText("Time's up");
                stopService(intentBookingReminder);
//                stopReminder();
                cancelRes();
//                Notifications(getApplicationContext());
//                mBuilder.setContentTitle("Time's up")
//                        .setContentText("Your time is up");
//                mVibrator.vibrate(500);
            }
        }.start();



        bArrived.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!arrived) {
                    arrived = true;
                    prefManager.setBoolean(SmartParkingSharedPreferences.PREF_ARRIVED, arrived);
                    tvTimeRemains.setVisibility(View.GONE);
                    tvTimeCountdown.setVisibility(View.GONE);
                    bCancel.setVisibility(View.GONE);
                    bArrived.setText("I've left");
                    stopService(intentBookingReminder);
                    startService(intentParkReminder);
                    start = System.currentTimeMillis();
                } else {
                    unregisterReceiver(brCheck);
                    stopService(intentParkReminder);
                    stop = System.currentTimeMillis();
                    mainActivity.timeDiff(start, stop);
                    //TODO: dialog durasi parkir
                    builder.setMessage("Thank you! You've parked for " + mainActivity.hour + " hours " + mainActivity.min + " minutes")
                            .setPositiveButton("Back to home", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    reserved = false;
                                    prefManager.setBoolean(SmartParkingSharedPreferences.PREF_RESERVED, reserved);
                                    startActivity(intentHome);
                                }
                            })
                            .create()
                            .show();


                }
//                toggleArrived();
//                if(arrived) {
//                    bArrived.setText("I've left");
//                    stopService(intentBookingReminder);
//                    startService(intentParkReminder);
//                }

//                unregisterReceiver(brCheck);
//                stopReminder();
//                startAlarm();
            }
        });

//        checkTime();
        //TODO: Check slot terlalu advanced q ta sanggup
//        checkSlot();

//        if(arrived = false) {
////            Timer();
//
////        timeLeft = 1000000;
////        boolean reserved = true;
//
//
//        }

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date dateFrom = new Date(fromTime);
        tvArrivalTime.setText(sdf.format(dateFrom));
        Date dateTo = new Date(toTime);
        tvLeavingTime.setText(sdf.format(dateTo));

        price = prefManager.getFloat(SmartParkingSharedPreferences.PREF_PRICE);
        Log.e("price", String.valueOf(price));
        Locale localeID = new Locale("in", "ID");
        NumberFormat RpFormat = NumberFormat.getCurrencyInstance(localeID);
        tvPrice.setText(RpFormat.format((double)price));
        tvSlotNo.setText(prefManager.getString(SmartParkingSharedPreferences.PREF_SLOT_NAME));

//        Summary();
        bQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View mView = getLayoutInflater().inflate(R.layout.dialog_qr_code, null);
                builder.setView(mView);
                final AlertDialog QRDialog = builder.create();
                QRDialog.show();

                ImageView ivQR = mView.findViewById(R.id.ivQR);

                int id_reservation = prefManager.getInt(SmartParkingSharedPreferences.PREF_ID);
//                int id_reservation = 123;

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


            }
        });

        bViewSlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentView = new Intent(v.getContext(), ViewSlotActivity.class);
                startActivity(intentView);
            }
        });

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setMessage("Are you sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                stopService(intentBookingReminder);
                                cancel();
                            }
                        })
                        .setNegativeButton("No", null)
                        .create()
                        .show();

            }
        });

    }

    private void Timer() {
        fromTime = prefManager.getLong(SmartParkingSharedPreferences.PREF_TIME_FROM);
        toTime = prefManager.getLong(SmartParkingSharedPreferences.PREF_TIME_TO);
        currentTime = Calendar.getInstance().getTimeInMillis();

        timeLeft = fromTime - currentTime;
//        timeLeft = 1000000;
//        boolean reserved = true;

        Log.e("fromMillis", String.valueOf(fromTime));
        Log.e("currentMillis", String.valueOf(currentTime));
        Log.e("timeLeft/difference", String.valueOf(timeLeft));

        timer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                updateText();
                //checkTime();
                //Alert(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                tvTimeCountdown.setText("Time's up");
//                stopReminder();
                cancelRes();
                Notifications(getApplicationContext());
                mBuilder.setContentTitle("Time's up")
                        .setContentText("Your time is up");
                mVibrator.vibrate(500);
            }
        }.start();
    }

    private void cancel() {
        loading.setMessage("Cancelling");
        loading.show();
//        stopReminder();
        cancelRequest();

    }

    private void cancelRequest() {
        int params = prefManager.getInt(SmartParkingSharedPreferences.PREF_ID);
        network.cancel(params, new Network.MyCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody response) {
                loading.dismiss();
                cancelRes();
            }

            @Override
            public void onError(String error) {
                loading.dismiss();
                Toast.makeText(OngoingActivity.this,
                        error,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cancelRes() {
        reserved = false;
        prefManager.setBoolean(SmartParkingSharedPreferences.PREF_RESERVED, reserved);
        builder.setMessage("Your reservation is cancelled")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(intentHome);
                    }
                })
                .create()
                .show();
    }
    //TODO: Perbaiki alert, cek status slot
//    private void alert() {
//            builder.setMessage("You got 15 minutes left")
//                    .setPositiveButton("OK", null)
//                    .create()
//                    .show();
//    }


    private void startAlarm() {
        Intent myIntent = new Intent(OngoingActivity.this, AlarmReceiver.class);
        pendingIntentPark = PendingIntent.getBroadcast(OngoingActivity.this, 0, myIntent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, toTime - 90000, pendingIntentPark);
    }

    public void Notifications(Context context) {
        mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp);
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, mBuilder.build());
    }


    public void checkSlot() {
        pendingIntentCheck = PendingIntent.getBroadcast(this, 0,
                new Intent("id.ac.ugm.smartparking.smartparkingapp" + currentTime), PendingIntent.FLAG_ONE_SHOT);
        brCheck = new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, Intent intent) {
//                startService(new Intent(OngoingActivity.this, CheckSlotService.class));
//                int params = prefManager.getInt(SmartParkingSharedPreferences.PREF_ID);
//                network.getSlotStatus(params, new Network.MyCallback<CheckSlotStatusResponse>() {
//                    @Override
//                    public void onSuccess(CheckSlotStatusResponse response) {
//                        if (response.getStatus() == Constants.PARKED) {
//                            Notifications(context);
//                            mBuilder.setContentTitle("Your slot is not available")
//                                    .setContentText("Please confirm to change selected slot");
//                            builder.setMessage("Your slot is not available")
//                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            loading.show();
//                                            checkSlotRequest();
//                                        }
//                                    })
//                                    .create()
//                                    .show();
//                        }
//                    }
//
//                    @Override
//                    public void onError(String error) {
//                        Toast.makeText(OngoingActivity.this,
//                                error,
//                                Toast.LENGTH_SHORT).show();
//                    }
//                });
            }

        };

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, fromTime - 900000, 1000 * 60* 5,pendingIntentCheck);
        IntentFilter intentFilter = new IntentFilter("id.ac.ugm.smartparking.smartparkingapp" + fromTime);

        registerReceiver(brCheck, intentFilter);
    }

    public void Toast(String error) {
        Toast.makeText(this,
                error,
                Toast.LENGTH_SHORT).show();
    }

    private void confirmDialog(final String slotName) {
        View mView = getLayoutInflater().inflate(R.layout.dialog_confirm_change, null);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(OngoingActivity.this);
        mBuilder.setView(mView);
        AlertDialog confirmDialog = mBuilder.create();
        confirmDialog.show();

        tvSlotNoNew = mView.findViewById(R.id.tvSlotNo);
        bYes = mView.findViewById(R.id.bYes);
        bNo = mView.findViewById(R.id.bNo);

        tvSlotNoNew.setText(slotName);
        bYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.show();
                final ReservationNewRequestModel request = new ReservationNewRequestModel(idSlot);
                network.reservationNew(idSlot, request, new Network.MyCallback<ReservationResponse>() {
                    @Override
                    public void onSuccess(ReservationResponse response) {
                        loading.dismiss();
                        Toast.makeText(OngoingActivity.this,
                                "Reservation success",
                                Toast.LENGTH_SHORT).show();
                        ReservationResponse.Data data = response.getData();
                        int reservation_id = data.getIdUserPark();
                        prefManager.setInt(SmartParkingSharedPreferences.PREF_ID, reservation_id);
                    }

                    @Override
                    public void onError(String error) {
                        loading.dismiss();
                        Toast.makeText(OngoingActivity.this,
                                error,
                                Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }

//    private void Alert(long millis) {

//            //TODO: if parked, panggil api checkslot, reservasi ulang

//
//
////                    Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
////                    mVibrator.vibrate(300);
//            builder.setMessage("You got 15 minutes left")
//                    .setPositiveButton("OK", null).show();
//            showNotif();
//        }
//
//    }
    public void checkSlotRequest() {
        String params = String.valueOf(fromTime) + "-" + String.valueOf(toTime);
        network.getSlot(params, new Network.MyCallback<CheckSlotResponse>() {
            @Override
            public void onSuccess(CheckSlotResponse response) {
                loading.dismiss();
                String slotName = response.data.getSlotName();
                //tvSlotNo.setText(slotName);
                idSlot = response.data.getIdSlot();
                //reserve ulang
                confirmDialog(slotName);
            }

            @Override
            public void onError(String error) {
                loading.dismiss();
                Toast.makeText(OngoingActivity.this,
                        error,
                        Toast.LENGTH_SHORT).show();
            }
        });
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


    private void toggleArrived() {
        if(bArrived.getVisibility() == View.VISIBLE) {
            bArrived.setVisibility(View.GONE);
            tvTimeRemains.setVisibility(View.GONE);
            tvTimeCountdown.setVisibility(View.GONE);
            bCancel.setVisibility(View.GONE);
//            bLeft.setVisibility(View.VISIBLE);
            arrived = true;
            prefManager.setBoolean(SmartParkingSharedPreferences.PREF_ARRIVED, arrived);
            timer.cancel();
            Log.i("time_to", String.valueOf(toTime));
//            startAlarm();
        } else {
            bArrived.setVisibility(View.VISIBLE);
            tvTimeRemains.setVisibility(View.VISIBLE);
            tvTimeCountdown.setVisibility(View.VISIBLE);
            bCancel.setVisibility(View.VISIBLE);
//            bLeft.setVisibility(View.GONE);
            arrived = false;
            prefManager.setBoolean(SmartParkingSharedPreferences.PREF_ARRIVED, arrived);
        }
    }

    private void Summary() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date dateFrom = new Date(fromTime);
        tvArrivalTime.setText(sdf.format(dateFrom));
        Date dateTo = new Date(toTime);
        tvLeavingTime.setText(sdf.format(dateTo));

        price = prefManager.getFloat(SmartParkingSharedPreferences.PREF_PRICE);
        Log.e("price", String.valueOf(price));
        Locale localeID = new Locale("in", "ID");
        NumberFormat RpFormat = NumberFormat.getCurrencyInstance(localeID);
        tvPrice.setText(RpFormat.format((double)price));
        tvSlotNo.setText(prefManager.getString(SmartParkingSharedPreferences.PREF_SLOT_NAME));
    }

    private void isArrived() {
        if(prefManager.getBoolean(SmartParkingSharedPreferences.PREF_ARRIVED)) {
            tvTimeRemains.setVisibility(View.GONE);
            tvTimeCountdown.setVisibility(View.GONE);
            bCancel.setVisibility(View.GONE);
            bArrived.setText("I've left");
//            bLeft.setVisibility(View.VISIBLE);
        } else {
            tvTimeRemains.setVisibility(View.VISIBLE);
            tvTimeCountdown.setVisibility(View.VISIBLE);
            bCancel.setVisibility(View.VISIBLE);
//            bLeft.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(intentHome);
    }
}

