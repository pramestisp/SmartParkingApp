package id.ac.ugm.smartparking.smartparkingapp;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shindy on 14-Mar-18.
 */

public class CustomTimePickerDialog extends TimePickerDialog {
    private final static int TIME_PICKER_INTERVAL = 30;
    private TimePicker mTimePicker;
    private final OnTimeSetListener mTimeSetListener;
    private TimePicker timePicker;

    private int currentHour = 0;
    private int currentMinute = 0;


    CustomTimePickerDialog(Context context, OnTimeSetListener listener,
                                  int hourOfDay, int minute, boolean is24HourView) {
        super(context, TimePickerDialog.THEME_HOLO_LIGHT, null, hourOfDay, minute /
        TIME_PICKER_INTERVAL, is24HourView);
        mTimeSetListener = listener;
        currentHour = hourOfDay;
        currentMinute = minute;
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

    @SuppressLint("DefaultLocale")
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        try {
            @SuppressLint("PrivateApi")
            Class<?> classForId = Class.forName("com.android.internal.R$id");
            Field timePickerField = classForId.getField("timePicker");
            mTimePicker = findViewById(timePickerField.getInt(null));
            Field minuteField = classForId.getField("minute");
            Field hourField = classForId.getField("hour");

            NumberPicker minuteSpinner = mTimePicker
                    .findViewById(minuteField.getInt(null));
            minuteSpinner.setMinValue(0);
            minuteSpinner.setMaxValue((60 / TIME_PICKER_INTERVAL) - 1);
            List<String> displayedValues = new ArrayList<>();
            for (int i = 0; i < 60; i += TIME_PICKER_INTERVAL) {
                displayedValues.add(String.format("%02d", i));
            }
            minuteSpinner.setDisplayedValues(displayedValues
                    .toArray(new String[displayedValues.size()]));
            NumberPicker hourSpinner = mTimePicker.findViewById(hourField.getInt(null));
            hourSpinner.setMinValue(currentHour);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
