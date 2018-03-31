package id.ac.ugm.smartparking.smartparkingapp;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private View slot_1, slot_2, slot_3;

    private EditText etFromTime;
    private EditText etToTime;

    private TextView tvTime,tvPrice;

    private Button bCheck;

    long fromMillis, toMillis, diff;

    int hour, min, bookFee, feePerHour, feePer30Min, price;

    String df_string_from, df_string_to;

    AlertDialog timeDialog, confirmDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0). setChecked(true);
        onNavigationItemSelected(navigationView.getMenu().getItem(0));

        getIntent();

        slot_1 = findViewById(R.id.slot_1);
        slot_2 = findViewById(R.id.slot_2);
        slot_3 = findViewById(R.id.slot_3);

        final boolean available = true;

        if (available) {
            slot_1.setBackgroundResource(R.color.green);
            slot_2.setBackgroundResource(R.color.green);
            slot_3.setBackgroundResource(R.color.green);
        } else {
            slot_1.setBackgroundResource(R.color.red);
            slot_2.setBackgroundResource(R.color.red);
            slot_3.setBackgroundResource(R.color.red);
        }

        final Button bBook = findViewById(R.id.bBook);

        bBook.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_choose_time, null);

                etFromTime = mView.findViewById(R.id.etFromTime);
                etToTime = mView.findViewById(R.id.etToTime);

                tvTime = mView.findViewById(R.id.tvTime);
                tvPrice = mView.findViewById(R.id.tvPrice);

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

                        TimePickerDialog fromTimePickerDialog = new TimePickerDialog(MainActivity.this, TimePickerDialog.THEME_HOLO_LIGHT,
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

//                toMillis = c2.getTimeInMillis();

                        TimePickerDialog toTimePickerDialog = new TimePickerDialog(MainActivity.this, TimePickerDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                etToTime.setText(String.format("%02d:%02d", hourOfDay, minute));

                                Calendar cal = Calendar.getInstance();
                                cal.set(
                                        cal.get(Calendar.YEAR),
                                        cal.get(Calendar.MONTH),
                                        cal.get(Calendar.DAY_OF_MONTH),
                                        hourOfDay,
                                        minute
                                );
                                toMillis = cal.getTimeInMillis();

                                Date toTime = cal.getTime();

                                DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
                                SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
//                        String df_string = df.format(fromTime);
                                df_string_to = format.format(toTime);
                                etToTime.setText(df_string_to);

                            }
                        }, hourTo, minuteTo, true);
                        toTimePickerDialog.show();
                    }
                });
//                Intent intentSlot = new Intent(v.getContext(), ChooseTimeActivity.class);
//                startActivity(intentSlot);
            }

        });


    }

    private void checkValue() {
        String fromTime = etFromTime.getText().toString();
        String toTime = etToTime.getText().toString();

        final boolean success;

        Log.e("tomillis", String.valueOf(toMillis));
        Log.e("fromMillis", String.valueOf(fromMillis));

        Log.e("et from time", fromTime);
        Log.e("et to time", toTime);


        if (fromTime.isEmpty() || toTime.isEmpty() || toMillis < fromMillis) { //compare juga if time selected =< current time

            Toast.makeText(MainActivity.this,
                    "Invalid time",
                    Toast.LENGTH_SHORT).show();
        }

        else {
            timeDiff();
//            tvTime.setText("From " + etFromTime.getText() + " To " + etToTime.getText() +
//                            "Time difference: " + hour + "hours" + min + "min");
//            priceCount();
//            tvPrice.setText("Price: Rp " + price + ",00");
//            ProgressDialog check = ProgressDialog.show(MainActivity.this, null, "Checking");
//
//            success = true;
//
//            if(success == true) {
//                check.dismiss();
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_confirm, null);

                builder.setView(mView);
                confirmDialog = builder.create();
                confirmDialog.show();

                TextView tvSlotNo = mView.findViewById(R.id.tvSlotNo);
                TextView tvFromTime = mView.findViewById(R.id.tvFromTime);
                TextView tvToTime = mView.findViewById(R.id.tvToTime);
                TextView tvPrice = mView.findViewById(R.id.tvPrice);

                Button bViewSlot = mView.findViewById(R.id.bViewSlot);
                Button bConfirm = mView.findViewById(R.id.bConfirm);
                Button bCancel = mView.findViewById(R.id.bCancel);

                //ambil value dari dialog sblmnya utk arrival & leaving time

                tvFromTime.setText(df_string_from);
                tvToTime.setText(df_string_to);

                priceCount();
                tvPrice.setText("Rp " + price + ",00");

                bViewSlot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //ke GridLayoutActivity, tunjukkan slot sesuai no
                        Intent intentView = new Intent(v.getContext(), GridLayoutActivity.class);
                        startActivity(intentView);

                    }
                });

                bConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //pindah activity, QR code dan time remaining
                    }
                });

                bCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirmDialog.dismiss();
                        timeDialog.dismiss();
                    }
                });


//            }
//            else {
//                Toast.makeText(MainActivity.this,
//                        "There's no available slot",
//                        Toast.LENGTH_SHORT).show();
//            }
        }
    }

    private void timeDiff() {
        diff = toMillis - fromMillis;
        long diffSec = TimeUnit.MILLISECONDS.toSeconds(diff);
        hour = (int) (diffSec / (60*60));
        int minremaining = (int) (diffSec % (60 * 60));
        min = (int) (minremaining / 60);
        int secondsRemaining = (int) (minremaining % (60));


        Log.e("tomillis", String.valueOf(toMillis));
        Log.e("frommillis", String.valueOf(fromMillis));
        Log.e("difference", String.valueOf(diff));
        Log.e("hour", String.valueOf(hour));
        Log.e("min", String.valueOf(min));
        Log.e("sec", String.valueOf(secondsRemaining));
    }

    private void priceCount() {
        bookFee = 10000;
        feePerHour = 3000;
        feePer30Min = 2000;
        price = bookFee + (hour * feePerHour);

        if(min == 30) {
            price += feePer30Min;
        } else {
            return;
        }


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        Fragment fragment = null;

        int id = item.getItemId();

        if (id == R.id.nav_home) {


        }

        if (id == R.id.nav_logout) {

        }

        if(fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();

            ft.replace(R.id.screen_area, fragment);

            ft.commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {

    }
}
