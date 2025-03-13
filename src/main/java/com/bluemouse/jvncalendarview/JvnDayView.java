package com.bluemouse.jvncalendarview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.Date;

public class JvnDayView extends View {

    private Date date;
    private boolean isToday;
    private boolean isSelected;
    private boolean hasEvent;

    private Paint textPaint;
    private Paint bgPaint;
    private Paint eventPaint;

    public JvnDayView(Context context) {
        super(context);
        init();
    }

    public JvnDayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(40f);
        textPaint.setTextAlign(Paint.Align.CENTER);

        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        eventPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        eventPaint.setColor(Color.RED);
    }

    public void setDate(Date date, boolean isToday, boolean isSelected, boolean hasEvent) {
        this.date = date;
        this.isToday = isToday;
        this.isSelected = isSelected;
        this.hasEvent = hasEvent;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();

        // Background color for selected or today
        if (isSelected) {
            bgPaint.setColor(Color.BLUE);
        } else if (isToday) {
            bgPaint.setColor(Color.LTGRAY);
        } else {
            bgPaint.setColor(Color.WHITE);
        }

        canvas.drawRect(new RectF(0, 0, width, height), bgPaint);

        // Draw event marker
        if (hasEvent) {
            float centerX = width / 2f;
            float centerY = height - 20;
            canvas.drawCircle(centerX, centerY, 10, eventPaint);
        }

        // Draw day number
        if (date != null) {
            String dayNumber = String.valueOf(date.getDate());
            canvas.drawText(dayNumber, width / 2f, height / 2f, textPaint);
        }
    }
}
