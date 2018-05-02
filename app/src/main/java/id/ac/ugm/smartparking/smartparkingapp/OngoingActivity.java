package id.ac.ugm.smartparking.smartparkingapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import id.ac.ugm.smartparking.smartparkingapp.network.Network;
import id.ac.ugm.smartparking.smartparkingapp.utils.SmartParkingSharedPreferences;

/**
 * Created by Shindy on 31-Mar-18.
 */
//TODO: Rapiin timeremainingactivity biar nanti bisa dilist di tab history --> Ongoing
public class OngoingActivity extends AppCompatActivity {

    TextView tvTimeCountdown;

    private Network network;

    private SmartParkingSharedPreferences prefManager;

    private long timeLeft;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_remaining);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        network = new Network(this);
        prefManager = new SmartParkingSharedPreferences(this);
        tvTimeCountdown = findViewById(R.id.tvTime);
        //final EditText etQRtext = findViewById(R.id.etTextToGenerate);
        Button bQR = findViewById(R.id.bShowQR);

        getIntent();

        long fromTime = prefManager.getLong(SmartParkingSharedPreferences.PREF_TIME_FROM);
        long currentTime = Calendar.getInstance().getTimeInMillis();

        timeLeft = fromTime - currentTime;

        Log.e("fromMillis", String.valueOf(fromTime));
        Log.e("currentMillis", String.valueOf(currentTime));
        Log.e("timeLeft/difference", String.valueOf(timeLeft));

        CountDownTimer Timer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeft = millisUntilFinished;
                updateText();
                //TODO: notification & alert ketika countdown 15 menit // gmn biar ga ilang timernya ketika di-close
                if (millisUntilFinished == 900000) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(OngoingActivity.this);
                    builder.setMessage("You got 15 minutes left")
                            .setPositiveButton("OK", null).show();

                    Vibrator mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                    mVibrator.vibrate(300);

                    showNotif();
                }
            }

            @Override
            public void onFinish() {
                tvTimeCountdown.setText("Time's up");
            }
        }.start();

        bQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(OngoingActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_qr_code, null);

                mBuilder.setView(mView);
                AlertDialog QRDialog = mBuilder.create();
                QRDialog.show();

                ImageView ivQR = mView.findViewById(R.id.ivQR);

                int id_reservation = prefManager.getInt(SmartParkingSharedPreferences.PREF_ID);
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
    }
//TODO: pikirkan ketika mobil sudah sampe, bikin alarm ketika kelamaan parkir + kena tarif penalti
//    private void Timer() {
//        countTime();
//        new CountDownTimer(timeLeft, 1000) {
//
//            @Override
//            public void onTick(long millisUntilFinished) {
//                timeLeft = millisUntilFinished;
//                updateText();
//            }
//
//            @Override
//            public void onFinish() {
//                tvTimeCountdown.setText("Finished!");
//            }
//        }.start();
//
//
//    }

//    private void countTime() {
//        MainActivity getFromMillis = new MainActivity();
//        long fromTime = getFromMillis.fromMillis;
//        long currentTime = Calendar.getInstance().getTimeInMillis();
//
//        timeLeft = fromTime - currentTime;
//    }

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
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(OngoingActivity.this, MainActivity.class));
    }
}

