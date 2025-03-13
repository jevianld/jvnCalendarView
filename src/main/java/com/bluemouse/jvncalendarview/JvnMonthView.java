package com.bluemouse.jvncalendarview;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.GridLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class JvnMonthView extends GridLayout {
    private Context context;
    private List<JvnEvent> events = new ArrayList<>();
    private Calendar currentMonth;

    public JvnMonthView(Context context) {
        super(context);
        init(context);
    }

    public JvnMonthView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        this.setColumnCount(7); // 7 days per week
        this.setRowCount(6); // Max weeks in a month view
        this.setBackgroundColor(Color.WHITE);
        currentMonth = Calendar.getInstance();
        generateMonthView();
    }

    private void generateMonthView() {
        this.removeAllViews();
        Calendar tempCal = (Calendar) currentMonth.clone();
        tempCal.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK) - 1; // Adjust for 0-index
        tempCal.add(Calendar.DAY_OF_MONTH, -firstDayOfWeek); // Start from previous month if needed

        for (int i = 0; i < 42; i++) { // 6 weeks × 7 days
            addDayView(tempCal.getTime());
            tempCal.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private void addDayView(Date date) {
        TextView dayView = (TextView) LayoutInflater.from(context).inflate(R.layout.day_view_item, this, false);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        dayView.setText(String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)));

        if (isToday(date)) {
            dayView.setBackgroundColor(Color.LTGRAY);
        }

        for (JvnEvent event : events) {
            if (isSameDay(event.getDate(), date)) {
                dayView.setTextColor(event.getColor()); // Highlight event days
            }
        }

        this.addView(dayView);
    }

    public void setMonth(Calendar month) {
        this.currentMonth = month;
        generateMonthView();
    }

    public void setEvents(List<JvnEvent> events) {
        this.events = events;
        generateMonthView();
    }

    private boolean isToday(Date date) {
        Calendar today = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return today.get(Calendar.YEAR) == calendar.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR);
    }

    private boolean isSameDay(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance(), c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }
}
