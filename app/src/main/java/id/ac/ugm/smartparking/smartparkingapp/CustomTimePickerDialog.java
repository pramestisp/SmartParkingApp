package id.ac.ugm.smartparking.smartparkingapp;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import java.lang.reflect.Field;

/**
 * Created by Shindy on 14-Mar-18.
 */

public class CustomTimePickerDialog extends TimePickerDialog {
    private final  static int TIME_PICKER_INTERVAL = 30;
    private TimePicker mTimePicker;
    private final OnTimeSetListener mTimeSetListener;
    private TimePicker timePicker;


    public CustomTimePickerDialog(Context context, int themeHoloLight, OnTimeSetListener listener,
                                  int hourOfDay, int minute, boolean is24HourView) {
        super(context, TimePickerDialog.THEME_HOLO_LIGHT, null, hourOfDay, minute /
        TIME_PICKER_INTERVAL, is24HourView);
        mTimeSetListener = listener;
        mTimePicker = new TimePicker(context);
        timePicker = new TimePicker(context);
    }

    @Override
    public void updateTime(int hourOfDay, int minuteOfHour) {
        mTimePicker.setCurrentHour(hourOfDay);
        mTimePicker.setCurrentMinute(minuteOfHour / TIME_PICKER_INTERVAL);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case BUTTON_POSITIVE:
                if(mTimeSetListener != null) {
                    mTimeSetListener.onTimeSet(mTimePicker, mTimePicker.getCurrentHour(),
                            mTimePicker.getCurrentMinute() * TIME_PICKER_INTERVAL);
                }
                break;
            case BUTTON_NEGATIVE:
                cancel();
                break;
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        try {
            Class<?> classForid = Class.forName("com.android.internal.R$id");
            Field timePickerField = classForid.getField("timePicker");
            this.timePicker = findViewById(timePickerField
                    .getInt(null));
            Field field = classForid.getField("hour");

            final NumberPicker mHourSpinner = findViewById(field.getInt(null));

            mHourSpinner.setMinValue(8);
            mHourSpinner.setMaxValue(19);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
