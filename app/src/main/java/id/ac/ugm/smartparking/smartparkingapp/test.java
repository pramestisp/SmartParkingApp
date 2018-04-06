package id.ac.ugm.smartparking.smartparkingapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.sql.Time;

public class test extends AppCompatActivity {

    private View view;
    private boolean available;

    EditText etText;
    Button bQR, bNotif;
    ImageView ivQR;
    String textQR;
    TextView tvCountdown;

    NotificationCompat.Builder builder;
    PendingIntent pendingIntent;
    TaskStackBuilder taskStackBuilder;
    Intent intent;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Timer();

        etText = findViewById(R.id.etTextToGenerate);
        bQR = findViewById(R.id.bShowQR);
        ivQR  = findViewById(R.id.ivQR);
        tvCountdown = findViewById(R.id.tvCountdown);
        bNotif = findViewById(R.id.bNotif);

        builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_notifications_black_24dp);
        builder.setContentTitle("Example Notification");
        builder.setContentText("detail");

        intent = new Intent(this, test.class);
        taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addParentStack(test.this);

        taskStackBuilder.addNextIntent(intent);
        pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        final NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        bNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationManager.notify(1, builder.build());
            }
        });
//
//        public void sendNotif(View view) {
//            NotificationCompat.Builder builder =
//                    new NotificationCompat.Builder(this)
//                    .setSmallIcon(R.drawable.ic_notifications_black_24dp)
//                    .setContentTitle("Notification")
//                    .setContentText("Hello world");
//
//            NotificationManager notificationManager =
//                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//            notificationManager.notify();
//            notificationManager.notify(001, builder.build());
//
//    }

        bNotif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                addNotif();
            }

//            private void addNotif() {
//                int uniqueID = 001;
//                NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
//                        .setSmallIcon(R.drawable.ic_notifications_black_24dp)
//                        .setContentTitle("Notif Example")
//                        .setContentText("This is  a test notif");
//            }
        });

        bQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textQR = etText.getText().toString().trim();
                MultiFormatWriter mfw = new MultiFormatWriter();
                try {
                    BitMatrix bm = mfw.encode(textQR, BarcodeFormat.QR_CODE, 200,200);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bm);
                    ivQR.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void Timer() {
        new CountDownTimer(5000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                tvCountdown.setText("Timer is " + millisUntilFinished/1000 + " s");
            }

            @Override
            public void onFinish() {
                tvCountdown.setText("Finished!");
            }
        }.start();
    }

    public void showNotif(View view) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                        .setContentTitle("My notification")
                        .setContentText("Hello World!");

        Intent intent = new Intent(view.getContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        mBuilder.setContentIntent(pendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(001, mBuilder.build());
    }
}
