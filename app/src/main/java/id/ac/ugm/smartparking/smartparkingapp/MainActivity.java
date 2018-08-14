package id.ac.ugm.smartparking.smartparkingapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
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
import android.widget.Toast;


import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import id.ac.ugm.smartparking.smartparkingapp.model.BalanceResponse;
import id.ac.ugm.smartparking.smartparkingapp.model.CheckSlot;
import id.ac.ugm.smartparking.smartparkingapp.model.CheckSlotResponse;
import id.ac.ugm.smartparking.smartparkingapp.model.GetAllSlotsResponse;
import id.ac.ugm.smartparking.smartparkingapp.model.ReservationRequestModel;
import id.ac.ugm.smartparking.smartparkingapp.model.ReservationResponse;
import id.ac.ugm.smartparking.smartparkingapp.network.Network;
import id.ac.ugm.smartparking.smartparkingapp.services.BookingReminderService;
import id.ac.ugm.smartparking.smartparkingapp.utils.Constants;
import id.ac.ugm.smartparking.smartparkingapp.utils.SmartParkingSharedPreferences;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public NumberFormat RpFormat;

    private EditText etFromTime, etToTime;

    private TextView tvName, tvEmail, tvBalance, tvSlotAmount;

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
        navigationView.getMenu().getItem(0).setChecked(true);
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

//        final Button bBook = findViewById(R.id.bBook);

        mBuilder = new AlertDialog.Builder(MainActivity.this);

        CardView cardView = findViewById(R.id.card_view);
        tvBalance = findViewById(R.id.tvBalanceHome);
        tvSlotAmount = findViewById(R.id.tvSlots);

        loading.setMessage("Loading");
        loading.show();

        showBalance();
        slots();

        cardView.isClickable();

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ReservationActivity.class));
            }
        });

    }

    private void showBalance() {
        int idUser = prefManager.getInt(SmartParkingSharedPreferences.PREF_USER_ID);
        network.getBalance(idUser, new Network.MyCallback<BalanceResponse>() {
            @Override
            public void onSuccess(BalanceResponse response) {
                loading.dismiss();
                BalanceResponse.Data data = response.getData();
                float balance = data.getBalance();
                Locale localeID = new Locale("in", "ID");
                NumberFormat RpFormat = NumberFormat.getCurrencyInstance(localeID);
                tvBalance.setText(RpFormat.format((double) balance));
            }

            @Override
            public void onError(String error) {
                loading.dismiss();
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                tvBalance.setText("Can't get balance");
            }
        });
    }

    private void slots() {
        network.getAllSlot(new Network.MyCallback<GetAllSlotsResponse>() {
            @Override
            public void onSuccess(GetAllSlotsResponse response) {
                loading.dismiss();
                List<CheckSlot> slotList = response.getData();
                CheckSlot checkSlot = new CheckSlot();
                filter(slotList, checkSlot.getStatus());
            }

            @Override
            public void onError(String error) {
                loading.dismiss();
                tvSlotAmount.setText("Get slot failed");
                tvSlotAmount.setTextColor(Color.RED);
                Log.e("error", error);
            }
        });

    }

//    private static CheckSlot getSlotStatus(List<CheckSlot> slot, String status) {
//        CheckSlot result = null;
//        for(CheckSlot temp : slot) {
//            if(status.equals(temp.getStatus().equals(Constants.AVAILABLE))) {
//                result = temp;
//            }
//        }
//        return result;
//        Log.e("slot", String.valueOf(result));
//    }

    private void filter(List<CheckSlot> slots, String status) {
        List<CheckSlot> newList = new ArrayList<>();
        for (CheckSlot slot : slots) {
            if(slot.getStatus().equals(Constants.AVAILABLE)) {
                newList.add(slot);
            }
        }
        int size = newList.size();
        tvSlotAmount.setText(String.valueOf(size) + " available slots");
        Log.e("new list", String.valueOf(newList));
        Log.e("new list size", String.valueOf(size));


    }
//        bBook.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                if(prefManager.getBoolean(SmartParkingSharedPreferences.PREF_RESERVED)) {
//                    Toast.makeText(MainActivity.this,
//                            "You have an ongoing reservation",
//                            Toast.LENGTH_SHORT).show();
//                } else {
//                    View mView = getLayoutInflater().inflate(R.layout.dialog_choose_time, null, false);
//
//                    etFromTime = mView.findViewById(R.id.etFromTime);
//                    etToTime = mView.findViewById(R.id.etToTime);
//                    bCheck = mView.findViewById(R.id.bGetTime);
//
//                    mBuilder.setView(mView);
//                    timeDialog = mBuilder.create();
//                    timeDialog.show();
//
//                    bCheck.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            checkValue();
//                        }
//                    });
//
//                    etFromTime.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Calendar c1 = Calendar.getInstance();
//                            int hourFrom = c1.get(Calendar.HOUR_OF_DAY);
//                            int minuteFrom = c1.get(Calendar.MINUTE);
//                            final Date fromTime = c1.getTime();
//                            CustomTimePickerDialog fromTimePickerDialog = new CustomTimePickerDialog(MainActivity.this,
//                                    new CustomTimePickerDialog.OnTimeSetListener() {
//                                        @Override
//                                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                                            Calendar cal = Calendar.getInstance();
//                                            cal.set(
//                                                    cal.get(Calendar.YEAR),
//                                                    cal.get(Calendar.MONTH),
//                                                    cal.get(Calendar.DAY_OF_MONTH),
//                                                    hourOfDay,
//                                                    minute
//                                            );
//                                            fromMillis = cal.getTimeInMillis();
//                                            prefManager.setLong(SmartParkingSharedPreferences.PREF_TIME_FROM, fromMillis);
//
//                                            Date fromTime = cal.getTime();
//
//                                            DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
//                                            SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
//                                            df_string_from = format.format(fromTime);
//
//                                            etFromTime.setText(df_string_from);
//                                        }
//                                    }, hourFrom, minuteFrom, true);
//                            fromTimePickerDialog.show();
//                        }
//                    });
//
//                    etToTime.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Calendar c2 = Calendar.getInstance();
//                            int hourTo = c2.get(Calendar.HOUR_OF_DAY);
//                            int minuteTo = c2.get(Calendar.MINUTE);
//
//
//                            CustomTimePickerDialog toTimePickerDialog = new CustomTimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
//                                @Override
//                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                                    Calendar cal = Calendar.getInstance();
//                                    cal.set(
//                                            cal.get(Calendar.YEAR),
//                                            cal.get(Calendar.MONTH),
//                                            cal.get(Calendar.DAY_OF_MONTH),
//                                            hourOfDay,
//                                            minute
//                                    );
//                                    toMillis = cal.getTimeInMillis();
//                                    prefManager.setLong(SmartParkingSharedPreferences.PREF_TIME_TO, toMillis);
//
//                                    Date toTime = cal.getTime();
//
//                                    DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);
//                                    SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
//                                    df_string_to = format.format(toTime);
//                                    etToTime.setText(df_string_to);
//
//                                }
//                            }, hourTo, minuteTo, true);
//                            toTimePickerDialog.show();
//                        }
//                    });
//
//                }
//
//            }
//
//        });
//
//        checkCarParkSlot();
//    }

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
                    timeDialog.dismiss();
                    loading.dismiss();
                    idSlot = response.data.getIdSlot();
                    String slotName = response.data.getSlotName();
                    confirmDialog(slotName);
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

    public void timeDiff(long fromMillis, long toMillis) {
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
//        timeDiff(fromMillis, toMillis);
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

    private void Logout() {
        final String token = prefManager.getString(SmartParkingSharedPreferences.PREF_TOKEN);
        SharedPreferences.Editor editor = prefManager.clear();
        final boolean logged = false;
        prefManager.setBoolean(SmartParkingSharedPreferences.PREF_LOGGED, logged);
        Intent intentLogin = new Intent(MainActivity.this, RegisterLoginActivity.class);
        startActivity(intentLogin);
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
            Intent intent = new Intent(MainActivity.this, TransactionsActivity.class);
            startActivity(intent);
        }

        if (id == R.id.nav_logout) {
            builder = new AlertDialog.Builder(MainActivity.this);
            builder.setCancelable(false)
                    .setMessage("Are you sure?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Logout();
                        }
                    })
                    .setNegativeButton("No", null);
            builder.create();
            builder.show();
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


}
