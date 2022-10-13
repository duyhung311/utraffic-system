package com.hcmut.admin.utrafficsystem.util;

public class TimeUtil {
    public static String formatDay(int day) {
        if (day < 10) {
            return "0" + day;
        } else {
            return String.valueOf(day);
        }
    }

    public static String formatMonth(int month) {
        if (month < 10) {
            return "0" + month;
        } else {
            return String.valueOf(month);
        }
    }

    public static String formatHour(int hour) {
        if (hour < 10) {
            return "0" + hour;
        } else {
            return String.valueOf(hour);
        }
    }

    public static String formatMinute(int minute) {
        if (minute < 10) {
            return "0" + minute;
        } else {
            return String.valueOf(minute);
        }
    }
}
