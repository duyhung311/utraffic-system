package com.hcmut.admin.utrafficsystem.business;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.hcmut.admin.utrafficsystem.util.TimeUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeDatePicker {
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private String resultDate;
    private String resultTime;

    private TimeDateSetCallback callback;

    public TimeDatePicker(Context context, TimeDateSetCallback callback) {
        this.callback = callback;
        config(context);
    }

    public void showDateTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        datePickerDialog.updateDate(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        timePickerDialog.updateTime(
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE));
        datePickerDialog.show();
    }

    private void config(Context context) {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                showTimePickerDialog();
                resultDate = TimeUtil.formatDay(dayOfMonth) + "-" + TimeUtil.formatMonth(month + 1) + "-" + year + " ";
            }
        };
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay,
                                  int minute) {
                resultTime = TimeUtil.formatHour(hourOfDay) + ":" + TimeUtil.formatMinute(minute);
                String date = resultDate + resultTime;
                DateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                long dateValue = 0;
                try {
                    Date parsedDate = df.parse(date);
                    dateValue = parsedDate.getTime();
                    if (callback != null) {
                        callback.onTimeDateSet(dateValue);
                    }
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }
            }
        };
        Calendar calendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(
                context,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        timePickerDialog = new TimePickerDialog(
                context,
                timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true);
    }

    public interface TimeDateSetCallback {
        void onTimeDateSet(long time);
    }
}
