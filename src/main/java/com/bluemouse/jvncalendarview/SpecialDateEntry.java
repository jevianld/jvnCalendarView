package com.bluemouse.jvncalendarview;

import java.util.Calendar;

public class SpecialDateEntry {
    private  String category;
    private Calendar date;
    private String title;
    private int textColor;
    private int backgroundColor;
    private boolean isUnderlined;
    private int priority;
    private String description;

    public SpecialDateEntry(String category, Calendar date, String title, int textColor, int backgroundColor, boolean isUnderlined, int priority) {
        this.category = category;
        this.date = date;
        this.title = title;
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
        this.isUnderlined = isUnderlined;
        this.priority = priority;
    }

    // Getters
    public Calendar getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public int getTextColor() {
        return textColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public boolean isUnderlined() {
        return isUnderlined;
    }

    public int getPriority() {
        return priority;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setUnderlined(boolean underlined) {
        isUnderlined = underlined;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
