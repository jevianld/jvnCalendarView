package com.bluemouse.jvncalendarview;

import java.util.Date;

public class JvnDay {
    private Date date;
    private boolean isCurrentMonth;
    private boolean isToday;

    public JvnDay(Date date, boolean isCurrentMonth, boolean isToday) {
        this.date = date;
        this.isCurrentMonth = isCurrentMonth;
        this.isToday = isToday;
    }

    public Date getDate() {
        return date;
    }

    public boolean isCurrentMonth() {
        return isCurrentMonth;
    }

    public boolean isToday() {
        return isToday;
    }

    public String getFormattedDate() {
        return JvnDateUtils.formatDate(getCalendar());
    }

    private java.util.Calendar getCalendar() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }
}
