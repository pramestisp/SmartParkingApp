package id.ac.ugm.smartparking.smartparkingapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * Created by Shindy on 01-Nov-17.
 */

public class GridLayoutActivity extends AppCompatActivity {

    final View slot_1 = findViewById(R.id.slot_1);
    final View slot_2 = findViewById(R.id.slot_2);
    final View slot_3 = findViewById(R.id.slot_3);
    final View slot_4 = findViewById(R.id.slot_4);
    final View slot_5 = findViewById(R.id.slot_5);
    final View slot_6 = findViewById(R.id.slot_6);
    final View slot_7 = findViewById(R.id.slot_7);
    final View slot_8 = findViewById(R.id.slot_8);
    final View slot_9 = findViewById(R.id.slot_9);
    final View slot_10 = findViewById(R.id.slot_10);
    final View slot_11 = findViewById(R.id.slot_11);
    final View slot_12 = findViewById(R.id.slot_12);
    final View slot_13 = findViewById(R.id.slot_13);
    final View slot_14 = findViewById(R.id.slot_14);
    final View slot_15 = findViewById(R.id.slot_15);
    final View slot_16 = findViewById(R.id.slot_16);
    final View slot_17 = findViewById(R.id.slot_17);
    final View slot_18 = findViewById(R.id.slot_18);
    final View slot_19 = findViewById(R.id.slot_19);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_grid);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
}