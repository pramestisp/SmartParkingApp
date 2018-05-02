package id.ac.ugm.smartparking.smartparkingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import id.ac.ugm.smartparking.smartparkingapp.network.Network;

/**
 * Created by Shindy on 06-Nov-17.
 */

public class ViewSlotActivity extends AppCompatActivity {
    private Network network;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_a_slot);

        getIntent();
        //TODO: make the selected slot green and blink


    }

}
