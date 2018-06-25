package id.ac.ugm.smartparking.smartparkingapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import id.ac.ugm.smartparking.smartparkingapp.utils.SmartParkingSharedPreferences;

/**
 * Created by Shindy on 30-Apr-18.
 */

public class ProfileActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, tvCarType, tvCarNo;

    private String name, email, car_type, car_no;

    private SmartParkingSharedPreferences prefManager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        prefManager = new SmartParkingSharedPreferences(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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

    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
    }
}
