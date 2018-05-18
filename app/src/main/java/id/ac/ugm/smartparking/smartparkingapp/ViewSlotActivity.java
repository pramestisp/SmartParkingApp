package id.ac.ugm.smartparking.smartparkingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

import id.ac.ugm.smartparking.smartparkingapp.model.CheckSlot;
import id.ac.ugm.smartparking.smartparkingapp.model.GetAllSlotsResponse;
import id.ac.ugm.smartparking.smartparkingapp.network.Network;

/**
 * Created by Shindy on 06-Nov-17.
 */

public class ViewSlotActivity extends AppCompatActivity {
    private Network network;
    RecyclerViewSlotAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_a_slot);

        getIntent();
        network = new Network(this);
        network.getAllSlot(new Network.MyCallback<GetAllSlotsResponse>() {
            @Override
            public void onSuccess(GetAllSlotsResponse response) {
                List<CheckSlot> slotList = response.getData();
                generateSlot(slotList);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ViewSlotActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });



    }

    private void generateSlot(List<CheckSlot> data) {
        RecyclerView recyclerView = findViewById(R.id.rvSlots);
        int columns = 1;
        recyclerView.setLayoutManager(new GridLayoutManager(ViewSlotActivity.this, columns));
        recyclerView.addItemDecoration(new SpacesItemDecoration(200));
        adapter = new RecyclerViewSlotAdapter(ViewSlotActivity.this, data);
        recyclerView.setAdapter(adapter);
    }
}
