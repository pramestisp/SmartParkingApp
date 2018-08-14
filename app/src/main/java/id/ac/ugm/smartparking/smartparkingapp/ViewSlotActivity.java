package id.ac.ugm.smartparking.smartparkingapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
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
        setContentView(R.layout.activity_view_slot);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getIntent();
        network = new Network(this);
        network.getAllSlot(new Network.MyCallback<GetAllSlotsResponse>() {
            @Override
            public void onSuccess(GetAllSlotsResponse response) {
                List<CheckSlot> slotList = response.getData();
                String slotName = getIntent().getStringExtra("slot_name");
                generateSlot(slotList, slotName);
            }

            @Override
            public void onError(String error) {
                Toast.makeText(ViewSlotActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateSlot(List<CheckSlot> data, String slotName) {
        RecyclerView recyclerView = findViewById(R.id.rvSlots);
        int columns = 1;
        recyclerView.setLayoutManager(new GridLayoutManager(ViewSlotActivity.this, columns));
        recyclerView.addItemDecoration(new SpacesItemDecoration(200));
        adapter = new RecyclerViewSlotAdapter(ViewSlotActivity.this, data, slotName);
        recyclerView.setAdapter(adapter);
    }
}
