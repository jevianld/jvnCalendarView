package com.bluemouse.jvncalendarview;

import java.util.Date;

public class JvnEvent {
    private String title;
    private Date date;
    private int color; // Color for event marking

    public JvnEvent(String title, Date date, int color) {
        this.title = title;
        this.date = date;
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
