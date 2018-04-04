package id.ac.ugm.smartparking.smartparkingapp;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import id.ac.ugm.smartparking.smartparkingapp.MainActivity;

/**
 * Created by Shindy on 31-Mar-18.
 */

public class TimeRemainingActivity extends AppCompatActivity {

    TextView tvTimeCountdown;

    private long timeLeft;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_remaining);

        tvTimeCountdown = findViewById(R.id.tvTime);
        final EditText etQRtext = findViewById(R.id.etTextToGenerate);
        Button bQR = findViewById(R.id.bShowQR);

        getIntent();

        long fromTime = MainActivity.fromMillis;
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
                //TODO: notification & alert ketika countdown 15 menit
                if (millisUntilFinished == 900000) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(TimeRemainingActivity.this);
                    builder.setMessage("You got 15 minutes left")
                            .setPositiveButton("OK", null).show();
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
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(TimeRemainingActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_qr_code, null);

                mBuilder.setView(mView);
                AlertDialog QRDialog = mBuilder.create();
                QRDialog.show();

                ImageView ivQR = mView.findViewById(R.id.ivQR);

                String textQR = etQRtext.getText().toString().trim();
                MultiFormatWriter mfw = new MultiFormatWriter();
                try {
                    BitMatrix bm = mfw.encode(textQR, BarcodeFormat.QR_CODE, 200, 200);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bm);
                    ivQR.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });
    }

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
//        int min = (int) (timeLeft / 1000) / 60;
//        int sec = (int) (timeLeft / 1000) % 60;
//        int hour = min / 60;

        String timeLeftFormatted = String.format(Locale.getDefault(),
                "%02d:%02d:%02d", hour, min, sec);

        tvTimeCountdown.setText(timeLeftFormatted);
    }
}

