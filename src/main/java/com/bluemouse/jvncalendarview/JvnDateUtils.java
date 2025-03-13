package com.bluemouse.jvncalendarview;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class JvnDateUtils {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    // Get list of days for a given month
    public static List<JvnDay> getMonthDays(Calendar month) {
        List<JvnDay> days = new ArrayList<>();
        Calendar calendar = (Calendar) month.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;

        // Move calendar to start of week
        calendar.add(Calendar.DAY_OF_MONTH, -firstDayOfWeek);

        for (int i = 0; i < 42; i++) { // 6 weeks x 7 days
            boolean isCurrentMonth = calendar.get(Calendar.MONTH) == month.get(Calendar.MONTH);
            boolean isToday = isToday(calendar);
            days.add(new JvnDay(calendar.getTime(), isCurrentMonth, isToday));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return days;
    }

    // Check if a date is today
    public static boolean isToday(Calendar calendar) {
        Calendar today = Calendar.getInstance();
        return today.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                today.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) &&
                today.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH);
    }

    // Format date as string
    public static String formatDate(Calendar calendar) {
        return DATE_FORMAT.format(calendar.getTime());
    }

    // Get full month name (e.g., "March 2025")
    public static String getMonthName(Calendar currentCalendar) {
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        return monthFormat.format(currentCalendar.getTime());
    }
}
