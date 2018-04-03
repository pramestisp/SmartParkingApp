package id.ac.ugm.smartparking.smartparkingapp;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

/**
 * Created by Shindy on 31-Mar-18.
 */

public class TimeRemainingActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_remaining);

        TextClock tcTimeRemains = findViewById(R.id.tcTime);
        final EditText etQRtext = findViewById(R.id.etTextToGenerate);
        Button bQR = findViewById(R.id.bShowQR);

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
}

