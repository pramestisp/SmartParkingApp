package id.ac.ugm.smartparking.smartparkingapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import id.ac.ugm.smartparking.smartparkingapp.model.CheckSlot;
import id.ac.ugm.smartparking.smartparkingapp.model.CheckSlotResponse;
import id.ac.ugm.smartparking.smartparkingapp.model.GetAllSlotsResponse;
import id.ac.ugm.smartparking.smartparkingapp.model.ReservationRequestModel;
import id.ac.ugm.smartparking.smartparkingapp.model.ReservationResponse;
import id.ac.ugm.smartparking.smartparkingapp.network.Network;
import id.ac.ugm.smartparking.smartparkingapp.services.BookingReminderService;
import id.ac.ugm.smartparking.smartparkingapp.utils.SmartParkingSharedPreferences;

/**
 * Created by Shindy on 13-Aug-18.
 */

public class ReservationActivity extends AppCompatActivity {
    public NumberFormat RpFormat;

    private EditText etFromTime, etToTime;

    private TextView tvName, tvEmail;

    private Button bCheck;

    private Network network;

    private AlertDialog.Builder mBuilder, builder;

    long fromMillis, toMillis, diff;

    float price, time;

    public int hour, min, millis, bookFee, feePerHour, idSlot;

    String df_string_from, df_string_to, drawer_name, drawer_email;

//    AlertDialog timeDialog, confirmDialog;

    Dialog timeDialog, confirmDialog;

    ProgressDialog loading;

    RecyclerViewMainAdapter adapter;

    private SmartParkingSharedPreferences prefManager;

    private boolean reserved = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Tower 1 - GF");
        getIntent();

        loading = new ProgressDialog(this);
        network = new Network(this);
        prefManager = new SmartParkingSharedPreferences(this);
        mBuilder = new AlertDialog.Builder(ReservationActivity.this);
        final Button bBook = findViewById(R.id.bBook);

        bBook.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(prefManager.getBoolean(SmartParkingSharedPreferences.PREF_RESERVED)) {
                    Toast.makeText(ReservationActivity.this,
                            "You have an ongoing reservation",
                            Toast.LENGTH_SHORT).show();
                } else {
                    View mView = getLayoutInflater().inflate(R.layout.dialog_choose_time, null, false);

                    etFromTime = mView.findViewById(R.id.etFromTime);
                    etToTime = mView.findViewById(R.id.etToTime);
                    bCheck = mView.findViewById(R.id.bGetTime);

                    mBuilder.setView(mView);
                    timeDialog = mBuilder.create();
                    timeDialog.show();

                    bCheck.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checkValue();
                        }
                    });

                    etFromTime.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Calendar c1 = Calendar.getInstance();
                            int hourFrom = c1.get(Calendar.HOUR_OF_DAY);
                            int minuteFrom = c1.get(Calendar.MINUTE);
                            final Date fromTime = c1.getTime();
                            CustomTimePickerDialog fromTimePickerDialog = new CustomTimePickerDialog(ReservationActivity.this,
                                    new CustomTimePickerDialog.OnTimeSetListener() {
                                        @Override
                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                            Calendar cal = Calendar.getInstance();
                                            cal.set(
                                                    cal.get(Calendar.YEAR),
                                                    cal.get(Calendar.MONTH),
                                                    cal.get(Calendar.DAY_OF_MONTH),
                                                    hourOfDay,
                                                    minute
                                            );
                                            fromMillis = cal.getTimeInMillis();
                                            prefManager.setLong(SmartParkingSharedPreferences.PREF_TIME_FROM, fromMillis);

                                            Date fromTime = cal.getTime();

                                            DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
                                            SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
                                            df_string_from = format.format(fromTime);

                                            etFromTime.setText(df_string_from);
                                        }
                                    }, hourFrom, minuteFrom, true);
                            fromTimePickerDialog.show();
                        }
                    });

                    etToTime.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Calendar c2 = Calendar.getInstance();
                            int hourTo = c2.get(Calendar.HOUR_OF_DAY);
                            int minuteTo = c2.get(Calendar.MINUTE);


                            CustomTimePickerDialog toTimePickerDialog = new CustomTimePickerDialog(ReservationActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    Calendar cal = Calendar.getInstance();
                                    cal.set(
                                            cal.get(Calendar.YEAR),
                                            cal.get(Calendar.MONTH),
                                            cal.get(Calendar.DAY_OF_MONTH),
                                            hourOfDay,
                                            minute
                                    );
                                    toMillis = cal.getTimeInMillis();
                                    prefManager.setLong(SmartParkingSharedPreferences.PREF_TIME_TO, toMillis);

                                    Date toTime = cal.getTime();

                                    DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
                                    SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
                                    df_string_to = format.format(toTime);
                                    etToTime.setText(df_string_to);

                                }
                            }, hourTo, minuteTo, true);
                            toTimePickerDialog.show();
                        }
                    });

                }

            }

        });

        checkCarParkSlot();
    }

    private void checkCarParkSlot() {
        loading.setMessage("Checking Park Slot");
        loading.show();
        network.getAllSlot(new Network.MyCallback<GetAllSlotsResponse>() {
            @Override
            public void onSuccess(GetAllSlotsResponse response) {
                loading.dismiss();
                List<CheckSlot> slotList = response.getData();
                generateSlot(slotList);
            }

            private void generateSlot(List<CheckSlot> data) {
                RecyclerView recyclerView = findViewById(R.id.rvSlots);
                int columns = 1;
                recyclerView.setLayoutManager(new GridLayoutManager(ReservationActivity.this, columns));
                recyclerView.addItemDecoration(new SpacesItemDecoration(200));
                adapter = new RecyclerViewMainAdapter(ReservationActivity.this, data);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onError(String error) {
                loading.dismiss();
                Toast.makeText(ReservationActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void confirmDialog(final String slotName) {
        View mView = getLayoutInflater().inflate(R.layout.dialog_confirm, null, false);

        mBuilder.setView(mView);
        mBuilder.setCancelable(false);
        confirmDialog = mBuilder.create();
        confirmDialog.show();

        TextView tvSlotNo = mView.findViewById(R.id.tvSlotNo);
        TextView tvFromTime = mView.findViewById(R.id.tvFromTime);
        TextView tvToTime = mView.findViewById(R.id.tvToTime);
        TextView tvPrice = mView.findViewById(R.id.tvPrice);

        Button bViewSlot = mView.findViewById(R.id.bViewSlot);
        Button bConfirm = mView.findViewById(R.id.bConfirm);
        Button bCancel = mView.findViewById(R.id.bCancel);

        tvFromTime.setText(df_string_from);
        tvToTime.setText(df_string_to);
        tvSlotNo.setText(slotName);
        prefManager.setString(SmartParkingSharedPreferences.PREF_SLOT_NAME, slotName);

        priceCount();
        prefManager.setFloat(SmartParkingSharedPreferences.PREF_PRICE, price);
        Locale localeID = new Locale("in", "ID");
        RpFormat = NumberFormat.getCurrencyInstance(localeID);
        tvPrice.setText(RpFormat.format((double)price));

        bViewSlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentView = new Intent(v.getContext(), ViewSlotActivity.class);
                intentView.putExtra("slot_name", slotName);
                startActivity(intentView);

            }
        });

        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ReservationRequestModel request = new ReservationRequestModel(idSlot, df_string_from, df_string_to, price);
                loading.setMessage("");
                loading.show();

                final Intent intent = new Intent(v.getContext(), OngoingActivity.class);

                network.Reservation(request, new Network.MyCallback<ReservationResponse>() {
                    @Override
                    public void onSuccess(ReservationResponse response) {
                        loading.dismiss();
                        confirmDialog.dismiss();
                        Toast.makeText(ReservationActivity.this,
                                "Reservation success",
                                Toast.LENGTH_SHORT).show();
                        ReservationResponse.Data data = response.getData();
                        int reservation_id = data.getIdUserPark();
                        boolean arrived = false;
                        prefManager.setBoolean(SmartParkingSharedPreferences.PREF_RESERVED, reserved);
                        prefManager.setBoolean(SmartParkingSharedPreferences.PREF_ARRIVED, arrived);
                        prefManager.setInt(SmartParkingSharedPreferences.PREF_ID, reservation_id);
                        startActivity(intent);
                        startService(new Intent(ReservationActivity.this, BookingReminderService.class));
                    }

                    @Override
                    public void onError(String error) {
                        loading.dismiss();
                        Toast.makeText(ReservationActivity.this,
                                error,
                                Toast.LENGTH_SHORT).show();
                        if(error.equals("Token expired")) {
                            Logout();
                        }

                    }
                });



            }
        });

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog.dismiss();
                timeDialog.dismiss();
            }
        });
    }


    private void checkValue() {
        String fromTime = etFromTime.getText().toString();
        String toTime = etToTime.getText().toString();

        diff = toMillis - fromMillis;

        Log.e("difference", String.valueOf(diff));
        Log.e("tomillis", String.valueOf(toMillis));
        Log.e("fromMillis", String.valueOf(fromMillis));

        Log.e("et from time", fromTime);
        Log.e("et to time", toTime);


        if (fromTime.isEmpty() || toTime.isEmpty() || toMillis <= fromMillis || fromMillis < (System.currentTimeMillis() + 1800000) || diff <= 1700000) {

            Toast.makeText(ReservationActivity.this,
                    "Invalid time",
                    Toast.LENGTH_SHORT).show();
        }

        else {
            String params = etFromTime.getText().toString() + "-" + etToTime.getText().toString();
            loading.setMessage("Checking");
            loading.setCancelable(false);
            loading.show();
            network.getSlot(params, new Network.MyCallback<CheckSlotResponse>() {
                @Override
                public void onSuccess(CheckSlotResponse response) {
                    timeDialog.dismiss();
                    loading.dismiss();
                    idSlot = response.data.getIdSlot();
                    String slotName = response.data.getSlotName();
                    confirmDialog(slotName);
                }

                @Override
                public void onError(String error) {
                    loading.dismiss();
                    Toast.makeText(ReservationActivity.this,
                            error,
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void Logout() {
        final String token = prefManager.getString(SmartParkingSharedPreferences.PREF_TOKEN);
        SharedPreferences.Editor editor = prefManager.clear();
        final boolean logged = false;
        prefManager.setBoolean(SmartParkingSharedPreferences.PREF_LOGGED, logged);
        Intent intentLogin = new Intent(ReservationActivity.this, RegisterLoginActivity.class);
        startActivity(intentLogin);
    }

    private void priceCount() {
        diff = toMillis - fromMillis;

        millis = 3600000;
        bookFee = 10000;
        feePerHour = 3000;
        time = (float) diff / millis;
        BigDecimal bd = new BigDecimal(time);
        bd = bd.setScale(1, BigDecimal.ROUND_HALF_UP);
        price = bookFee + (bd.floatValue() * feePerHour);
        Log.e("diff", String.valueOf(diff));
        Log.e("time", String.valueOf(time));
        Log.e("bd", String.valueOf(bd));
        Log.e("price", String.valueOf(bd.floatValue()*feePerHour));
        Log.e("finalprice", String.valueOf(price));
    }

    @Override
    public void onBackPressed() {
        new Intent(ReservationActivity.this, MainActivity.class);
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
            checkCarParkSlot();
        }
        return super.onOptionsItemSelected(item);
    }
}
