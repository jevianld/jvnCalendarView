package com.bluemouse.jvncalendarview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class JvnCalendarView extends LinearLayout {
    private Context context;
    private TextView monthTitle;
    private Button btnPrev, btnNext;
    private JvnMonthView monthView;
    private Calendar currentCalendar;
    private List<JvnEvent> events = new ArrayList<>();

    public JvnCalendarView(Context context) {
        super(context);
        init(context);
    }

    public JvnCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.view_calendar, this, true);

        monthTitle = findViewById(R.id.txt_month_title);
        btnPrev = findViewById(R.id.btn_prev_month);
        btnNext = findViewById(R.id.btn_next_month);
        monthView = findViewById(R.id.month_view);

        currentCalendar = Calendar.getInstance();
        updateMonthView();

        btnPrev.setOnClickListener(v -> changeMonth(-1));
        btnNext.setOnClickListener(v -> changeMonth(1));
    }

    private void updateMonthView() {
        String monthName = JvnDateUtils.getMonthName(currentCalendar);
        monthTitle.setText(monthName);

        monthView.setMonth((Calendar) currentCalendar.clone());
        monthView.setEvents(events);
    }

    private void changeMonth(int direction) {
        currentCalendar.add(Calendar.MONTH, direction);
        updateMonthView();
    }

    public void setEvents(List<JvnEvent> eventList) {
        this.events = eventList;
        monthView.setEvents(events);
    }
}
