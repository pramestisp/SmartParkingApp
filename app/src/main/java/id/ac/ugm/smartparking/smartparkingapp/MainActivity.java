package id.ac.ugm.smartparking.smartparkingapp;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import id.ac.ugm.smartparking.smartparkingapp.model.CheckSlot;
import id.ac.ugm.smartparking.smartparkingapp.model.CheckSlotResponse;
import id.ac.ugm.smartparking.smartparkingapp.model.GetAllSlotsResponse;
import id.ac.ugm.smartparking.smartparkingapp.model.ReservationRequestModel;
import id.ac.ugm.smartparking.smartparkingapp.model.ReservationResponse;
import id.ac.ugm.smartparking.smartparkingapp.network.Network;
import id.ac.ugm.smartparking.smartparkingapp.services.BookingReminderService;
import id.ac.ugm.smartparking.smartparkingapp.utils.SmartParkingSharedPreferences;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    public NumberFormat RpFormat;

    private EditText etFromTime, etToTime;

    private TextView tvName, tvEmail;

    private Button bCheck;

    private Network network;

    private AlertDialog.Builder mBuilder;

    long fromMillis, toMillis, diff;

    float price;

    int hour, min, bookFee, feePerHour, feePer30Min, idSlot;

    String df_string_from, df_string_to, drawer_name, drawer_email;

    AlertDialog timeDialog, confirmDialog;

    ProgressDialog loading;

    RecyclerViewMainAdapter adapter;

    private SmartParkingSharedPreferences prefManager;

    private boolean reserved = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        loading = new ProgressDialog(this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.getMenu().getItem(0). setChecked(true);
        onNavigationItemSelected(navigationView.getMenu().getItem(0));

        View headerView = navigationView.getHeaderView(0);
        getIntent();

        network = new Network(this);

        tvName = headerView.findViewById(R.id.tvName);
        tvEmail = headerView.findViewById(R.id.tvEmail);

        prefManager = new SmartParkingSharedPreferences(this);
        drawer_name = prefManager.getString(SmartParkingSharedPreferences.PREF_USER_NAME);
        drawer_email = prefManager.getString(SmartParkingSharedPreferences.PREF_EMAIL);

        tvName.setText(drawer_name);
        tvEmail.setText(drawer_email);

        final Button bBook = findViewById(R.id.bBook);

        mBuilder = new AlertDialog.Builder(MainActivity.this);

        bBook.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                View mView = getLayoutInflater().inflate(R.layout.dialog_choose_time, null);

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
                        TimePickerDialog fromTimePickerDialog = new TimePickerDialog(MainActivity.this,
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
                                }, hourFrom +2, minuteFrom, true);
                        fromTimePickerDialog.show();
                    }
                });

                etToTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar c2 = Calendar.getInstance();
                        int hourTo = c2.get(Calendar.HOUR_OF_DAY);
                        int minuteTo = c2.get(Calendar.MINUTE);


                        CustomTimePickerDialog toTimePickerDialog = new CustomTimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
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
                        }, hourTo +2, minuteTo, true);
                        toTimePickerDialog.show();
                    }
                });
            }

        });

        checkCarParkSlot();
    }

    public void checkCarParkSlot() {
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
                recyclerView.setLayoutManager(new GridLayoutManager(MainActivity.this, columns));
                recyclerView.addItemDecoration(new SpacesItemDecoration(200));
                adapter = new RecyclerViewMainAdapter(MainActivity.this, data);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onError(String error) {
                loading.dismiss();
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
//        network.getAllSlot(new Network.MyCallback<GetAllSlotsResponse>() {
//            @Override
//            public void onSuccess(GetAllSlotsResponse response) {
//                loading.dismiss();
//                List<CheckSlot> slotList = response.data;
//                if (slotList.get(0).getStatus().equals(Constants.AVAILABLE)) {
//                    slot_1.setBackgroundResource(R.color.green);
//                } else {
//                    slot_1.setBackgroundResource(R.color.red);
//                }
//
//                if (slotList.get(1).getStatus().equals(Constants.AVAILABLE)) {
//                    slot_2.setBackgroundResource(R.color.green);
//                } else {
//                    slot_2.setBackgroundResource(R.color.red);
//                }
//
//                if (slotList.get(2).getStatus().equals(Constants.AVAILABLE)) {
//                    slot_3.setBackgroundResource(R.color.green);
//                } else {
//                    slot_3.setBackgroundResource(R.color.red);
//                }
//            }
//
//            @Override
//            public void onError(String error) {
//                Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
//                loading.dismiss();
//            }
//        });
    }

    private void confirmDialog(String slotName) {
        View mView = getLayoutInflater().inflate(R.layout.dialog_confirm, null);

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
                startActivity(intentView);

            }
        });

        bConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ReservationRequestModel request = new ReservationRequestModel(idSlot, df_string_from, df_string_to, price);
                loading.show();

                final Intent intent = new Intent(v.getContext(), OngoingActivity.class);

                network.Reservation(request, new Network.MyCallback<ReservationResponse>() {
                    @Override
                    public void onSuccess(ReservationResponse response) {
                        loading.dismiss();
                        Toast.makeText(MainActivity.this,
                                "Reservation success",
                                Toast.LENGTH_SHORT).show();
                        ReservationResponse.Data data = response.getData();
                        int reservation_id = data.getIdUserPark();
                        boolean arrived = false;
                        prefManager.setBoolean(SmartParkingSharedPreferences.PREF_RESERVED, reserved);
                        prefManager.setBoolean(SmartParkingSharedPreferences.PREF_ARRIVED, arrived);
                        prefManager.setInt(SmartParkingSharedPreferences.PREF_ID, reservation_id);
                        startActivity(intent);
                        startService(new Intent(MainActivity.this, BookingReminderService.class));
                    }

                    @Override
                    public void onError(String error) {
                        loading.dismiss();
                        Toast.makeText(MainActivity.this,
                                error,
                                Toast.LENGTH_SHORT).show();
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

        Log.e("tomillis", String.valueOf(toMillis));
        Log.e("fromMillis", String.valueOf(fromMillis));

        Log.e("et from time", fromTime);
        Log.e("et to time", toTime);


        if (fromTime.isEmpty() || toTime.isEmpty() || toMillis <= fromMillis) {

            Toast.makeText(MainActivity.this,
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
                    loading.dismiss();
                    idSlot = response.data.getIdSlot();
                    confirmDialog(response.data.getSlotName());
                }

                @Override
                public void onError(String error) {
                    loading.dismiss();
                    Toast.makeText(MainActivity.this,
                            error,
                            Toast.LENGTH_SHORT).show();
                }
            });
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

    public void priceCount() {
        timeDiff();

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
            Intent close = new Intent(Intent.ACTION_MAIN);
            close.addCategory(Intent.CATEGORY_HOME);
            close.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(close);
            //super.onBackPressed();
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

        if (id == R.id.nav_profile) {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        }

        if (id == R.id.nav_ongoing) {
            if(prefManager.getBoolean(SmartParkingSharedPreferences.PREF_RESERVED)) {
                Intent intent = new Intent(MainActivity.this, OngoingActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this,
                        "You don't have any ongoing reservations",
                        Toast.LENGTH_SHORT).show();
            }

        }

        if (id == R.id.nav_history) {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        }

        if (id == R.id.nav_logout) {
            mBuilder.setMessage("Are you sure?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final String token = prefManager.getString(SmartParkingSharedPreferences.PREF_TOKEN);
                            SharedPreferences.Editor editor = prefManager.clear();
                            Intent intentLogin = new Intent(MainActivity.this, RegisterLoginActivity.class);
                            startActivity(intentLogin);
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
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
