package com.bluemouse.jvncalendarview;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.*;

public class JvnCalendarView extends CardView {
    private static final String[] DAYS = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
    private static final String[] MONTHS = {"January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};

    private TextView monthYearText;
    private GridLayout calendarGrid;
    private Calendar currentDate;
    private OnDateClickListener dateClickListener;
    private List<SpecialDateEntry> specialDateList = new ArrayList<>();
    private LinearLayout specialDatesInfoLayout,legendDatesInfo;
    private GestureDetector gestureDetector;

    public void setlegend(boolean b) {
        legendDatesInfo.setVisibility(b ? VISIBLE : GONE);
    }


    // Data structure to store special dates and their properties
    private static class SpecialDate {
        Calendar date;
        int backgroundColor;
        int textColor;
        boolean isUnderlined;
        String description;
        int priority;  // Higher number means higher priority for overlapping dates

        SpecialDate(Calendar date, int backgroundColor, int textColor,
                    boolean isUnderlined, String description, int priority) {
            this.date = date;
            this.backgroundColor = backgroundColor;
            this.textColor = textColor;
            this.isUnderlined = isUnderlined;
            this.description = description;
            this.priority = priority;
        }
    }

    // Map to store different types of special dates
    private Map<String, List<SpecialDate>> specialDatesMap = new HashMap<>();

    public JvnCalendarView(Context context) {
        super(context);
        init(context);
    }

    public JvnCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public JvnCalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void addSpecialDates(List<SpecialDateEntry> specialDates) {
        if (specialDates == null || specialDates.isEmpty()) return;

        // Add all special dates to the list
        specialDateList.addAll(specialDates);

        // Trigger a refresh to display updated special dates
        updateCalendar();
        updateSpecialDatesLegend();


    }
    private void updateSpecialDatesLegend() {
        // Clear previous legend info
        specialDatesInfoLayout.removeAllViews();

        // Group special dates by category
        Map<String, Set<SpecialDateEntry>> groupedDates = new HashMap<>();

        // Loop through the special dates and group them by category
        for (SpecialDateEntry entry : specialDateList) {
            if (!groupedDates.containsKey(entry.getCategory())) {
                groupedDates.put(entry.getCategory(), new HashSet<>());
            }
            groupedDates.get(entry.getCategory()).add(entry);
        }

        // Iterate through each category and add a legend item for each
        for (Map.Entry<String, Set<SpecialDateEntry>> entry : groupedDates.entrySet()) {
            String category = entry.getKey();
            Set<SpecialDateEntry> specialDateEntries = entry.getValue();

            // Create a LinearLayout to hold the legend item for this category
            LinearLayout legendItem = new LinearLayout(getContext());
            legendItem.setOrientation(LinearLayout.HORIZONTAL);
            legendItem.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));

            // Create a TextView to display the category title (e.g., Holiday, Meeting, etc.)
            TextView categoryTitle = new TextView(getContext());
            categoryTitle.setText(category);
            categoryTitle.setTextColor(Color.WHITE);
            categoryTitle.setTextSize(14);
            legendItem.addView(categoryTitle);

            // Add a single icon (drawable resource) for the category, from the first special date in the category
            SpecialDateEntry firstEntry = specialDateEntries.iterator().next(); // Get the first entry for the category
            ImageView icon = new ImageView(getContext());
            icon.setLayoutParams(new LinearLayout.LayoutParams(dpToPx(20), dpToPx(20)));
            icon.setImageResource(firstEntry.getBackgroundColor()); // Use the resource directly from the first entry
            legendItem.addView(icon);

            // Add this item to the layout
            specialDatesInfoLayout.addView(legendItem);
        }
    }



    // Method to remove a category of special dates
    public void removeSpecialDates(String category) {
        specialDatesMap.remove(category);
        updateCalendar();
    }

    // Method to clear all special dates
    public void clearSpecialDates() {
        specialDatesMap.clear();
        updateCalendar();
    }

    private void init(Context context) {
        currentDate = Calendar.getInstance();
        setCardBackgroundColor(Color.parseColor("#1A1A1A"));
        setRadius(dpToPx(16));
        setCardElevation(dpToPx(4));

        View view = LayoutInflater.from(context).inflate(R.layout.calendar_layout, this, true);
        monthYearText = view.findViewById(R.id.monthYearText);
        calendarGrid = view.findViewById(R.id.calendarGrid);
        specialDatesInfoLayout = view.findViewById(R.id.specialDatesInfoLayout);
        legendDatesInfo = view.findViewById(R.id.legendDatesInfo);

        View prevButton = view.findViewById(R.id.prevMonth);
        View nextButton = view.findViewById(R.id.nextMonth);

        prevButton.setOnClickListener(v -> {
            currentDate.add(Calendar.MONTH, -1);
            updateCalendar();
        });

        nextButton.setOnClickListener(v -> {
            currentDate.add(Calendar.MONTH, 1);
            updateCalendar();
        });
        monthYearText.setOnClickListener(v -> showMonthYearPickerDialog());

        setupCalendarGrid();
        updateCalendar();

        // Initialize the gesture detector
        gestureDetector = new GestureDetector(context, new GestureListener());
        // Attach the gesture detector to the calendar grid
        calendarGrid.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
    }

    private void showMonthYearPickerDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_month_year_picker);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // References to UI components in the dialog
        TextView yearTextView = dialog.findViewById(R.id.yearTextView);
        RecyclerView yearRecyclerView = dialog.findViewById(R.id.yearRecyclerView);
        RecyclerView monthRecyclerView = dialog.findViewById(R.id.monthRecyclerView);
        View yearSelectorLayout = dialog.findViewById(R.id.yearSelectorLayout);
        View monthSelectorLayout = dialog.findViewById(R.id.monthSelectorLayout);

        // Set current year and month
        int currentYear = currentDate.get(Calendar.YEAR);
        int currentMonth = currentDate.get(Calendar.MONTH);

        yearTextView.setText(String.valueOf(currentYear));

        // Setup Year RecyclerView
        List<Integer> years = new ArrayList<>();
        for (int i = currentYear - 50; i <= currentYear + 50; i++) {
            years.add(i);
        }
        YearAdapter yearAdapter = new YearAdapter(years, selectedYear -> {
            yearTextView.setText(String.valueOf(selectedYear));
            yearSelectorLayout.setVisibility(View.GONE);
            monthSelectorLayout.setVisibility(View.VISIBLE);
        });
        yearRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        yearRecyclerView.setAdapter(yearAdapter);

        // Setup Month RecyclerView
        MonthAdapter monthAdapter = new MonthAdapter(MONTHS, currentMonth, selectedMonth -> {
            int selectedYear = Integer.parseInt(yearTextView.getText().toString());
            currentDate.set(Calendar.YEAR, selectedYear);
            currentDate.set(Calendar.MONTH, selectedMonth);

            updateCalendar();
            dialog.dismiss();
        });
        monthRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        monthRecyclerView.setAdapter(monthAdapter);

        // Handle year selection click
        yearTextView.setOnClickListener(v -> {
            monthSelectorLayout.setVisibility(View.GONE);
            yearSelectorLayout.setVisibility(View.VISIBLE);
        });

        dialog.show();
    }



    private void setupCalendarGrid() {
        // Setup days header (same as before)
        for (int i = 0; i < DAYS.length; i++) {
            TextView dayHeader = new TextView(getContext());
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(i, 1f);

            dayHeader.setLayoutParams(params);
            dayHeader.setText(DAYS[i]);
            dayHeader.setTextSize(14);
            dayHeader.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
            dayHeader.setGravity(android.view.Gravity.CENTER);

            if (i == 5) dayHeader.setTextColor(Color.parseColor("#629CE5"));
            else if (i == 6) dayHeader.setTextColor(Color.parseColor("#FFB392"));
            else dayHeader.setTextColor(Color.WHITE);

            calendarGrid.addView(dayHeader);
        }
    }
    private void updateCalendar() {
        // Update month and year text
        monthYearText.setText(MONTHS[currentDate.get(Calendar.MONTH)] + " " +
                currentDate.get(Calendar.YEAR));

        // Clear existing views in the calendar grid
        calendarGrid.removeAllViews();
        setupCalendarGrid();

        // Prepare calendar instance for the first day of the month
        Calendar calendar = (Calendar) currentDate.clone();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayOfMonth = calendar.get(Calendar.DAY_OF_WEEK) - 2;
        if (firstDayOfMonth < 0) firstDayOfMonth = 6;

        // Get the number of days in the current month
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        // Calculate the number of rows needed for the current month
        int totalSlots = firstDayOfMonth + daysInMonth;
        int requiredRows = (int) Math.ceil(totalSlots / 7.0);

        // Update the grid's row count dynamically
        calendarGrid.setRowCount(requiredRows + 1); // +1 for the header row

        // Get today's date
        Calendar today = Calendar.getInstance();

        // Iterate through the grid positions
        for (int i = 0; i < requiredRows * 7; i++) {
            TextView dayView = new TextView(getContext());
            setupDayViewLayout(dayView, i);

            // Determine the day number for this cell
            int dayNumber = calculateDayNumber(i, firstDayOfMonth, daysInMonth);
            if (dayNumber > 0) {
                dayView.setText(String.valueOf(dayNumber));

                // Create a calendar instance for this specific day
                Calendar dateToCheck = (Calendar) currentDate.clone();
                dateToCheck.set(Calendar.DAY_OF_MONTH, dayNumber);

                // Check if this date is today
                if (isSameDay(dateToCheck, today)) {
                    dayView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.current_day_background));
                    dayView.setTextColor(Color.BLACK);
                    dayView.setTypeface(null, Typeface.BOLD);

                } else {
                    // Apply special date styling
                    styleSpecialDate(dayView, dateToCheck);
                }

                // Set up click listener for the day
//                setupDayClickListener(dayView, dayNumber);
            }

            // Add the day view to the calendar grid
            calendarGrid.addView(dayView);
        }
    }

    private void setupDayViewLayout(TextView dayView, int position) {
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = dpToPx(40);
        params.columnSpec = GridLayout.spec(position % 7, 1f);
        params.rowSpec = GridLayout.spec(position / 7 + 1, 1f);
        params.setMargins(dpToPx(4), dpToPx(4), dpToPx(4), dpToPx(4));
        dayView.setLayoutParams(params);
        dayView.setGravity(android.view.Gravity.CENTER);
        dayView.setTextSize(14);
        dayView.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));
    }

    private void styleSpecialDate(TextView dayView, Calendar dateToCheck) {
        // Iterate through the list of special dates
        for (SpecialDateEntry entry : specialDateList) {
            if (isSameDay(dateToCheck, entry.getDate())) {
                // Apply styles from the SpecialDateEntry
                dayView.setBackgroundResource(entry.getBackgroundColor());
                dayView.setTextColor(entry.getTextColor());
                dayView.setTag(entry.getDescription()); // Store description for click handling
                dayView.setTypeface(null, Typeface.BOLD);  // Set text style to bold

                return; // Stop once a match is found
            }
        }
    }


    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

//    private void setupDayClickListener(TextView dayView, int dayNumber) {
//        dayView.setOnClickListener(v -> {
//            if (dateClickListener != null) {
//                Calendar clickedDate = (Calendar) currentDate.clone();
//                clickedDate.set(Calendar.DAY_OF_MONTH, dayNumber);
//                dateClickListener.onDateClick(clickedDate);
//
//                Intent intent = new Intent(getContext(), DutyCalendarActivity.class);
//                intent.putExtra("selected_date", clickedDate.getTimeInMillis());
//                if (dayView.getTag() != null) {
//                    intent.putExtra("date_description", dayView.getTag().toString());
//                }
//                getContext().startActivity(intent);
//            }
//        });
//    }

    private int calculateDayNumber(int position, int firstDayOfMonth, int daysInMonth) {
        if (position < firstDayOfMonth) return -1;
        if (position >= firstDayOfMonth && position < daysInMonth + firstDayOfMonth) {
            return position - firstDayOfMonth + 1;
        }
        return -1;
    }

    public void setOnDateClickListener(OnDateClickListener listener) {
        this.dateClickListener = listener;
    }

    public interface OnDateClickListener {
        void onDateClick(Calendar date);
    }

    private int dpToPx(int dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();

            // Check for horizontal swipe
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        // Swipe to the right (previous month)
                        currentDate.add(Calendar.MONTH, -1);
                    } else {
                        // Swipe to the left (next month)
                        currentDate.add(Calendar.MONTH, 1);
                    }
                    updateCalendar();
                    return true;
                }
            }
            return false;
        }
    }

    public static class YearAdapter extends RecyclerView.Adapter<YearAdapter.YearViewHolder> {
        private final List<Integer> years;
        private final OnYearSelectedListener listener;

        public interface OnYearSelectedListener {
            void onYearSelected(int year);
        }

        public YearAdapter(List<Integer> years, OnYearSelectedListener listener) {
            this.years = years;
            this.listener = listener;
        }


        @NonNull
        @Override
        public YearViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView textView = new TextView(parent.getContext());
            textView.setPadding(16, 16, 16, 16);
            textView.setTextSize(16);
            textView.setGravity(Gravity.CENTER);
            return new YearViewHolder(textView);        }

        @Override
        public void onBindViewHolder(@NonNull YearViewHolder holder, int position) {
            int year = years.get(position);
            holder.textView.setText(String.valueOf(year));
            holder.textView.setOnClickListener(v -> listener.onYearSelected(year));
        }

        @Override
        public int getItemCount() {
            return years.size();
        }

        class YearViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            YearViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = (TextView) itemView;
            }
        }
    }
    public static class MonthAdapter extends RecyclerView.Adapter<MonthAdapter.MonthViewHolder> {
        private final String[] months;
        private final int currentMonth;
        private final OnMonthSelectedListener listener;

        public interface OnMonthSelectedListener {
            void onMonthSelected(int month);
        }

        public MonthAdapter(String[] months, int currentMonth, OnMonthSelectedListener listener) {
            this.months = months;
            this.currentMonth = currentMonth;
            this.listener = listener;
        }

        @NonNull
        @Override
        public MonthViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView textView = new TextView(parent.getContext());
            textView.setPadding(16, 16, 16, 16);
            textView.setTextSize(16);
            textView.setGravity(Gravity.CENTER);
            return new MonthViewHolder(textView);
        }

        @Override
        public void onBindViewHolder(@NonNull MonthViewHolder holder, int position) {
            holder.textView.setText(months[position]);
            holder.textView.setOnClickListener(v -> listener.onMonthSelected(position));
        }

        @Override
        public int getItemCount() {
            return months.length;
        }

        static class MonthViewHolder extends RecyclerView.ViewHolder {
            TextView textView;

            MonthViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = (TextView) itemView;
            }
        }
    }

}