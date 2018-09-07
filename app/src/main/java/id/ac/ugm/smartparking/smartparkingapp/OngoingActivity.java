package id.ac.ugm.smartparking.smartparkingapp;

import android.app.AlarmManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import id.ac.ugm.smartparking.smartparkingapp.model.ArrivedCheckSlotResponse;
import id.ac.ugm.smartparking.smartparkingapp.model.ReservationRequestModel;
import id.ac.ugm.smartparking.smartparkingapp.model.ReservationResponse;
import id.ac.ugm.smartparking.smartparkingapp.network.Network;
import id.ac.ugm.smartparking.smartparkingapp.services.BookingReminderService;
import id.ac.ugm.smartparking.smartparkingapp.services.ParkReminderService;
import id.ac.ugm.smartparking.smartparkingapp.utils.SmartParkingSharedPreferences;
import okhttp3.ResponseBody;

/**
 * Created by Shindy on 31-Mar-18.
 */
public class OngoingActivity extends AppCompatActivity {

    TextView tvTimeCountdown, tvTimeRemains, tvSlotNo, tvArrivalTime, tvLeavingTime, tvPrice, tvSlotNoNew;
    Button bQR, bViewSlot, bCancel, bArrived;

    private Network network;

    private SmartParkingSharedPreferences prefManager;

    private long fromTime, toTime, currentTime, timeLeft, start, stop;
    float price, charge, newPrice;
    int idSlot, hour, min, idUser, idReservation;
    boolean arrived, reserved;
    private static String slotNo;
    Intent intentHome, intentBookingReminder, intentParkReminder;
    AlarmManager alarmManager;
    AlertDialog.Builder builder;
    AlertDialog confirmDialog;
    ProgressDialog loading;
    CountDownTimer timer;
    Vibrator mVibrator;

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
//        bQR = findViewById(R.id.bShowQR);
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

        idUser = prefManager.getInt(SmartParkingSharedPreferences.PREF_USER_ID);
        idReservation = prefManager.getInt(SmartParkingSharedPreferences.PREF_ID);

        isArrived();

        fromTime = prefManager.getLong(SmartParkingSharedPreferences.PREF_TIME_FROM);
        toTime = prefManager.getLong(SmartParkingSharedPreferences.PREF_TIME_TO);
        currentTime = Calendar.getInstance().getTimeInMillis();

        timeLeft = fromTime - currentTime;

        Log.e("fromMillis", String.valueOf(fromTime));
        Log.e("currentMillis", String.valueOf(currentTime));
        Log.e("timeLeft/difference", String.valueOf(timeLeft));
        Log.e("arrived?", Boolean.toString(prefManager.getBoolean(SmartParkingSharedPreferences.PREF_ARRIVED)));

        if(System.currentTimeMillis() >= fromTime + 600000 && !prefManager.getBoolean(SmartParkingSharedPreferences.PREF_ARRIVED)) {
            cancelRes();
        } else {

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
                }
            }.start();

            bArrived.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!prefManager.getBoolean(SmartParkingSharedPreferences.PREF_ARRIVED)) {
                        if(System.currentTimeMillis() <= (fromTime - 600000)) {
                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(OngoingActivity.this);
                            mBuilder.setMessage("We didn't expect that you come earlier. You will be charged Rp3.000/hour. Continue?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            loading.setMessage("Checking slot");
                                            loading.show();
                                            checkSlot();
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    })
                                    .create()
                                    .show();

                        } else {
                            loading.show();
                            checkSlot();
                        }

                    } else {
//                    unregisterReceiver(brCheck);
                        stopService(intentParkReminder);
                        stop = System.currentTimeMillis();
                        countTime(stop);
//                    mainActivity.timeDiff(start, stop);
                        //TODO: dialog durasi parkir
                        builder.setMessage("Thank you for using our service. Have a nice day!")
                                .setNeutralButton("Back to home", new DialogInterface.OnClickListener() {
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
                }
            });




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
            slotNo = prefManager.getString(SmartParkingSharedPreferences.PREF_SLOT_NAME);
            tvSlotNo.setText(slotNo);


            bViewSlot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String slotName = prefManager.getString(SmartParkingSharedPreferences.PREF_SLOT_NAME);
                    viewSlot(slotName);
                }
            });

            bCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.setMessage("Are you sure?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    cancel();
                                }
                            })
                            .setNegativeButton("No", null)
                            .create()
                            .show();

                }
            });
        }


    }

    private void countCharge() {
        if(System.currentTimeMillis() <= (fromTime - 600000)) {
            float chargePerHour = 3000;
            double hour = (double) timeLeft / 3600000;
            double f = 0.5;
            double hour_rounded = f * Math.ceil(hour / f);
            Log.e("hour rounded", String.valueOf(hour_rounded));
            if (hour_rounded <= 0.5 ) {
                charge = chargePerHour;
            } else {
                charge = (float) (hour_rounded * chargePerHour);
            }
            Log.e("charge", String.valueOf(charge));
        } else {
            charge = 0;
        }
    }


    private void whenArrived() {
        arrived = true;
        prefManager.setBoolean(SmartParkingSharedPreferences.PREF_ARRIVED, arrived);
        tvTimeRemains.setVisibility(View.GONE);
        tvTimeCountdown.setVisibility(View.GONE);
        bCancel.setVisibility(View.GONE);
        bArrived.setText("I've left");
        timer.cancel();
        stopService(intentBookingReminder);
        startService(intentParkReminder);
        long arrival = System.currentTimeMillis();
        prefManager.setLong(SmartParkingSharedPreferences.PREF_TIME_FROM, arrival);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date dateFrom = new Date(arrival);
        tvArrivalTime.setText(sdf.format(dateFrom));
    }

    private void countTime(long stop) {
        start = prefManager.getLong(SmartParkingSharedPreferences.PREF_TIME_START);
        long diff = stop - start;
        long diffSec = TimeUnit.MILLISECONDS.toSeconds(diff);
        hour = (int) (diffSec / (60*60));
        int minremaining = (int) (diffSec % (60 * 60));
        min = (int) (minremaining / 60);
        int secondsRemaining = (int) (minremaining % (60));
    }

    private void checkSlot() {
        network.arrivedCheckSlot(idReservation, new Network.MyCallback<ArrivedCheckSlotResponse>() {
            @Override
            public void onSuccess(ArrivedCheckSlotResponse response) {
                loading.dismiss();
                ArrivedCheckSlotResponse.Data data = response.getData();
                String newSlot = data.getSlotName();
                int idSlot = data.getIdSlot();
                Log.e("slot old", String.valueOf(slotNo));
                Log.e("slot new", String.valueOf(newSlot));
                Log.e("slot id", String.valueOf(idSlot));
                if(!newSlot.equals(slotNo)) {
                    confirmChangeDialog(newSlot, idSlot);

                } else {
                    loading.show();
                    countCharge();
                    final ReservationRequestModel request = new ReservationRequestModel();
                    request.setPrice(charge);
                    network.addCharge(idReservation, request, new Network.MyCallback<ReservationResponse>() {
                        @Override
                        public void onSuccess(ReservationResponse response) {
                            loading.dismiss();
                            ReservationResponse.Data data = response.getData();
                            float newPrice = data.getPrice();
                            prefManager.setFloat(SmartParkingSharedPreferences.PREF_PRICE, newPrice);
                            Locale localeID = new Locale("in", "ID");
                            NumberFormat RpFormat = NumberFormat.getCurrencyInstance(localeID);
                            tvPrice.setText(RpFormat.format((double)newPrice));
                            whenArrived();
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


    private void cancel() {
        loading.setMessage("Cancelling");
        loading.show();
//        stopReminder();
        cancelRequest();

    }

    private void cancelRequest() {
        network.cancel(idReservation, new Network.MyCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody response) {
                loading.dismiss();
                timer.cancel();
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
        stopService(intentBookingReminder);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(OngoingActivity.this);
        mBuilder.setMessage("Your reservation is cancelled")
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(intentHome);
                    }
                })
                .create()
                .show();
    }

    private void confirmChangeDialog(final String newSlot, final int newSlotId) {
        View mView = getLayoutInflater().inflate(R.layout.dialog_confirm_change, null);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(OngoingActivity.this);
        mBuilder.setView(mView);
        confirmDialog = mBuilder.create();
        confirmDialog.show();

        tvSlotNoNew = mView.findViewById(R.id.tvSlotNo);
        Button bConfirm = mView.findViewById(R.id.bChangeSlot);
        Button bCancel = mView.findViewById(R.id.bDontChange);
        Button bViewSlot = mView.findViewById(R.id.bViewSlot);
        tvSlotNoNew.setText(newSlot);

        bViewSlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewSlot(newSlot);
            }
        });

        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSlotRequest(newSlot, newSlotId);
            }
        });

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

    }


    public void viewSlot(String slotName) {
        Intent intentView = new Intent(OngoingActivity.this, ViewSlotActivity.class);
        intentView.putExtra("slot_name", slotName);
        startActivity(intentView);
    }

    public void changeSlotRequest(final String newSlot, int newSlotId) {
        countCharge();
        final ReservationRequestModel request = new ReservationRequestModel();
        request.setIdSlot(newSlotId);
        request.setPrice(charge);
        network.changeSlot(idReservation, request, new Network.MyCallback<ReservationResponse>() {
                    @Override
                    public void onSuccess(ReservationResponse response) {
                        Toast.makeText(OngoingActivity.this,
                                "Change slot success",
                                Toast.LENGTH_SHORT).show();
                        ReservationResponse.Data data = response.getData();
                        float newPrice = data.getPrice();
                        prefManager.setString(SmartParkingSharedPreferences.PREF_SLOT_NAME, newSlot);
                        tvSlotNo.setText(newSlot);
                        prefManager.setFloat(SmartParkingSharedPreferences.PREF_PRICE, newPrice);
                        Locale localeID = new Locale("in", "ID");
                        NumberFormat RpFormat = NumberFormat.getCurrencyInstance(localeID);
                        tvPrice.setText(RpFormat.format((double)newPrice));
                        //close dialog
                        confirmDialog.dismiss();
                        whenArrived();
                    }

                    @Override
                    public void onError(String error) {
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

    private void isArrived() {
        if(prefManager.getBoolean(SmartParkingSharedPreferences.PREF_ARRIVED)) {
            tvTimeRemains.setVisibility(View.GONE);
            tvTimeCountdown.setVisibility(View.GONE);
            bCancel.setVisibility(View.GONE);
            bArrived.setText("I've left");
            tvSlotNo.setText(prefManager.getString(SmartParkingSharedPreferences.PREF_SLOT_NAME));
//            tvPrice(prefManager.get)
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

