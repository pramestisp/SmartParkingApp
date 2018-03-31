package id.ac.ugm.smartparking.smartparkingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * Created by Shindy on 06-Nov-17.
 */

public class ViewSlotActivity extends AppCompatActivity {

    final View slot_3 = findViewById(R.id.slot_3);
    final View slot_4 = findViewById(R.id.slot_4);
    final View slot_5 = findViewById(R.id.slot_5);
    final View slot_8 = findViewById(R.id.slot_8);
    final View slot_9 = findViewById(R.id.slot_9);
    final View slot_10 = findViewById(R.id.slot_10);
    final View slot_14 = findViewById(R.id.slot_14);
    final View slot_15 = findViewById(R.id.slot_15);
    final View slot_16 = findViewById(R.id.slot_16);

    //final Button bChoose = findViewById(R.id.bChoose);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_a_slot);

        getIntent();

        //bChoose.setVisibility(View.INVISIBLE);


//
//        bChoose.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                Intent intentTime = new Intent(v.getContext(), ChooseTimeActivity.class);
//                startActivity(intentTime);
//            }
//        });

        // Get the Intent that started this activity and extract the string
        //Intent intentSlot = getIntent();

    }

}
