package id.ac.ugm.smartparking.smartparkingapp;

        import android.app.TimePickerDialog;
        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
        import android.text.InputType;
        import android.util.Log;
        import android.view.View;
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

/**
 * Created by Shindy on 24-Dec-17.
 */

public class ChooseTimeActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etFromTime;
    private EditText etToTime;

    private TimePickerDialog fromTimePickerDialog;
    private TimePickerDialog toTimePickerDialog;

    private SimpleDateFormat fromTimeFormat;

    private Button bGetTime;

    private TextView tvTime;

    private Calendar c1, c2;

    long fromMillis, toMillis, diff, diffSec, diffMin;

    int min, hour;

    //long diff = toMillis - fromMillis;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_time);

        getIntent();

        etToTime = (EditText) findViewById(R.id.etToTime);

        tvTime = (TextView) findViewById(R.id.tvTime);

        etFromTime = (EditText) findViewById(R.id.etFromTime);
        etFromTime.setInputType(InputType.TYPE_NULL);
        etFromTime.requestFocus();

        etFromTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c1 = Calendar.getInstance();
                int hourFrom = c1.get(Calendar.HOUR_OF_DAY);
                int minuteFrom = c1.get(Calendar.MINUTE);
                final Date fromTime = c1.getTime();

//                fromMillis = c1.getTimeInMillis();


                fromTimePickerDialog = new TimePickerDialog(ChooseTimeActivity.this, TimePickerDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
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
//                        String df_string = df.format(fromTime);
                        String df_string = format.format(fromTime);
//                        try {
                            ////String fromTime = String.valueOf(hourOfDay) + ":" + String.valueOf(minute);
                            ////String format = DateFormat.getDateInstance(DateFormat.SHORT).format(fromTime);
                            //long fromTimeValue = format.parse(fromTime).getTime();
                            //SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                            //java.sql.Time fromTimeValue = new java.sql.Time(format.parse(fromTime).getTime());
                            //etFromTime.setText(String.valueOf(df_string));
                        etFromTime.setText(df_string);
//                        } catch (ParseException e) {
//                            etFromTime.setText(e.getMessage().toString());
//                        }
                        //Calendar c = Calendar.getInstance();
                        //etFromTime.setText(String.format("%02d:%02d", hourOfDay, minute));

                    }
                }, hourFrom, minuteFrom, true);
                fromTimePickerDialog.show();
            }
        });

        etToTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c2 = Calendar.getInstance();
                int hourTo = c2.get(Calendar.HOUR_OF_DAY);
                int minuteTo = c2.get(Calendar.MINUTE);

//                toMillis = c2.getTimeInMillis();

                toTimePickerDialog = new TimePickerDialog(ChooseTimeActivity.this, TimePickerDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
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
                        String df_string = format.format(toTime);
                        etToTime.setText(df_string);

                    }
                }, hourTo, minuteTo, true);
                toTimePickerDialog.show();
            }
        });
        bGetTime = (Button) findViewById(R.id.bGetTime);

        bGetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValue();
                //tvTime.setText("From " + etFromTime.getText() + " To " + etToTime.getText());
            }

            private void timeDiff() {

                diff = toMillis - fromMillis;
                long diffSec = TimeUnit.MILLISECONDS.toSeconds(diff);
                hour = (int) (diffSec / (60*60));
                int minremaining = (int) (diffSec % (60 * 60));
                min = (int) (minremaining / 60);
                int secondsRemaining = (int) (minremaining % (60));

                //diffSec = diff / 1000;
                //diffMin = diff / (60*1000);
                //diffMin = TimeUnit.MILLISECONDS.toMinutes(fromMillis);

                //diffMin = (diff / (1000 * 60)) % 60;

                Log.e("tomillis", String.valueOf(toMillis));
                Log.e("frommillis", String.valueOf(fromMillis));
                Log.e("difference", String.valueOf(diff));
                Log.e("hour", String.valueOf(hour));
                Log.e("min", String.valueOf(min));
                Log.e("sec", String.valueOf(secondsRemaining));
            }

            private void checkValue() {
                String fromTime = etFromTime.getText().toString();
                String toTime = etToTime.getText().toString();

                Log.e("tomillis", String.valueOf(toMillis));
                Log.e("fromMillis", String.valueOf(fromMillis));

                Log.e("et from time", fromTime);
                Log.e("et to time", toTime);


                if (fromTime.isEmpty() || toTime.isEmpty() || toMillis < fromMillis) {

                    Toast.makeText(ChooseTimeActivity.this,
                            "Invalid time",
                            Toast.LENGTH_SHORT).show();
                }

                else {
                    timeDiff();
                    tvTime.setText("From " + etFromTime.getText() + " To " + etToTime.getText() +
                    "Time difference: " + hour + "hours" + min + "min"
                    //        + diffSec + "sec"
                    );
//                            "    " + diff
//                    );
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        if(v == etFromTime) {
            fromTimePickerDialog.show();
        }
        else if(v == etToTime) {
            toTimePickerDialog.show();
        }


    }



//    private TimePickerDialog setTimeField() {
//        etFromTime.setOnClickListener(this);
//        etToTime.setOnClickListener(this);
//
//        Calendar c = Calendar.getInstance();
//
//        fromTimePickerDialog = new TimePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, this, int hour, int minute, true) {
////            int hour = c.get(Calendar.HOUR_OF_DAY);
////            int minute = c.get(Calendar.MINUTE);
//        }
//
//        //etFromTime.setText(fmtDate.format(c.getTime()));
//       // return new TimePickerDialog (this, AlertDialog.THEME_HOLO_LIGHT, (TimePickerDialog.OnTimeSetListener) this,  hour, minute, true);
//    }

}
