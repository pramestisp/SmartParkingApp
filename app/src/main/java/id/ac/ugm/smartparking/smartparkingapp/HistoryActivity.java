package id.ac.ugm.smartparking.smartparkingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import id.ac.ugm.smartparking.smartparkingapp.model.HistoryResponse;
import id.ac.ugm.smartparking.smartparkingapp.network.Network;
import id.ac.ugm.smartparking.smartparkingapp.utils.SmartParkingSharedPreferences;

/**
 * Created by Shindy on 21-May-18.
 */

public class HistoryActivity extends AppCompatActivity {
    ProgressDialog loading;
    Network network;
    SmartParkingSharedPreferences prefManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getIntent();

        loading = new ProgressDialog(this);
        network = new Network(this);
        prefManager = new SmartParkingSharedPreferences(this);

        showHistory();
    }

    private void showHistory() {
        int params = prefManager.getInt(SmartParkingSharedPreferences.PREF_USER_ID);
        loading.setMessage("Loading");
        loading.show();
        network.getHistory(params, new Network.MyCallback<HistoryResponse>() {
            @Override
            public void onSuccess(HistoryResponse response) {
                loading.dismiss();
                List<HistoryResponse.DataItem> historyList = response.getData();
                ListView listView = findViewById(R.id.lvHistory);
                listView.setAdapter(new ListViewAdapter(HistoryActivity.this, historyList));
            }

            @Override
            public void onError(String error) {
                loading.dismiss();
                Toast.makeText(HistoryActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(HistoryActivity.this, MainActivity.class));
    }
}
