package id.ac.ugm.smartparking.smartparkingapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

import id.ac.ugm.smartparking.smartparkingapp.utils.SmartParkingSharedPreferences;

/**
 * Created by Shindy on 30-Apr-18.
 */

public class ProfileActivity extends AppCompatActivity {

    private ImageView ivProfile;

    private TextView tvName, tvEmail, tvCarType, tvCarNo;

    private String name, email, car_type, car_no;

    private SmartParkingSharedPreferences prefManager;

    private static final int RESULT_LOAD_IMAGE = 1;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        prefManager = new SmartParkingSharedPreferences(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ivProfile = findViewById(R.id.ivProfilePhoto);
        tvName = findViewById(R.id.tvProfileName);
        tvEmail = findViewById(R.id.tvProfileEmail);
        tvCarType = findViewById(R.id.tvProfileCarType);
        tvCarNo = findViewById(R.id.tvProfileCarNo);

        prefManager = new SmartParkingSharedPreferences(this);
        name = prefManager.getString(SmartParkingSharedPreferences.PREF_USER_NAME);
        email = prefManager.getString(SmartParkingSharedPreferences.PREF_EMAIL);
        car_type = prefManager.getString(SmartParkingSharedPreferences.PREF_CAR_TYPE);
        car_no = prefManager.getString(SmartParkingSharedPreferences.PREF_CAR_NO);

        tvName.setText(name);
        tvEmail.setText(email);
        tvCarType.setText(car_type);
        tvCarNo.setText(car_no);

//        ivProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(galleryIntent, RESULT_LOAD_IMAGE);
//            }
//        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            ivProfile.setImageURI(selectedImage);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
    }

}
