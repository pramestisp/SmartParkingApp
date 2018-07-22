package id.ac.ugm.smartparking.smartparkingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import id.ac.ugm.smartparking.smartparkingapp.model.BalanceResponse;
import id.ac.ugm.smartparking.smartparkingapp.model.HistoryResponse;
import id.ac.ugm.smartparking.smartparkingapp.network.Network;
import id.ac.ugm.smartparking.smartparkingapp.utils.SmartParkingSharedPreferences;

/**
 * Created by Shindy on 21-May-18.
 */

public class TransactionsActivity extends AppCompatActivity {
    ProgressDialog loading;
    Network network;
    SmartParkingSharedPreferences prefManager;
    TextView tvBalance;
    int params;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getIntent();

        loading = new ProgressDialog(this);
        network = new Network(this);
        prefManager = new SmartParkingSharedPreferences(this);

        tvBalance = findViewById(R.id.tvBalance);

        params = prefManager.getInt(SmartParkingSharedPreferences.PREF_USER_ID);

        loading.setMessage("Loading");
        loading.show();

        showBalance();
        showHistory();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            loading.setMessage("Loading");
            loading.show();
            showBalance();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showBalance() {
        network.getBalance(params, new Network.MyCallback<BalanceResponse>() {
            @Override
            public void onSuccess(BalanceResponse response) {
                loading.dismiss();
                BalanceResponse.Data data = response.getData();
                float balance = data.getBalance();
                Locale localeID = new Locale("in", "ID");
                NumberFormat RpFormat = NumberFormat.getCurrencyInstance(localeID);
                tvBalance.setText(RpFormat.format((double)balance));
            }

            @Override
            public void onError(String error) {
                loading.dismiss();
                Toast.makeText(TransactionsActivity.this, error, Toast.LENGTH_SHORT).show();
                tvBalance.setText("Can't get balance");
            }
        });
    }

    private void showHistory() {
        network.getHistory(params, new Network.MyCallback<HistoryResponse>() {
            @Override
            public void onSuccess(HistoryResponse response) {
                loading.dismiss();
                List<HistoryResponse.DataItem> historyList = response.getData();
                ListView listView = findViewById(R.id.lvHistory);
                listView.setAdapter(new ListViewAdapter(TransactionsActivity.this, historyList));
            }

            @Override
            public void onError(String error) {
                loading.dismiss();
                Toast.makeText(TransactionsActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(TransactionsActivity.this, MainActivity.class));
    }
}
