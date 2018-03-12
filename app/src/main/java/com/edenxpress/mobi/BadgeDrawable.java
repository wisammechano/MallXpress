package com.edenxpress.mobi;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

/**
 * Created by ICT on 12/27/2016.
 */

public class BadgeDrawable extends Drawable {
    private Paint mBadgePaint;
    private Paint mBadgePaint1;
    private final Context mContext;
    private String mCount = "";
    private Paint mTextPaint;
    private float mTextSize;
    private Rect mTxtRect = new Rect();
    private boolean mWillDraw = false;

    public BadgeDrawable(Context context) {
        this.mContext = context;
        this.mTextSize = R.dimen.badge_text_size;
        this.mBadgePaint = new Paint();
        this.mBadgePaint.setColor(Color.RED);
        this.mBadgePaint.setAntiAlias(true);
        this.mBadgePaint.setStyle(Paint.Style.FILL);
        this.mBadgePaint1 = new Paint();
        this.mBadgePaint1.setColor(Color.parseColor("#EEEEEE"));
        this.mBadgePaint1.setAntiAlias(true);
        this.mBadgePaint1.setStyle(Paint.Style.FILL);
        this.mTextPaint = new Paint();
        this.mTextPaint.setColor(Color.WHITE);
        this.mTextPaint.setTypeface(Typeface.DEFAULT);
        this.mTextPaint.setTextSize(this.mTextSize);
        this.mTextPaint.setAntiAlias(true);
        this.mTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void draw(Canvas canvas) {
        if (this.mWillDraw) {
            Rect bounds = getBounds();
            float width = (float) (bounds.right - bounds.left);
            float height = (float) (bounds.bottom - bounds.top);
            float radius = (Math.max(width, height) / 2.0f) / 2.0f;
            float centerX = ((width - radius) - 1.0f) + 10.0f;
            float centerY = radius - 5.0f;
            if (this.mContext.getResources().getDisplayMetrics().xdpi < 200.0f) {
                this.mTextSize = this.mContext.getResources().getDimension(R.dimen.badge_text_size_low);
                this.mTextPaint.setTextSize(this.mTextSize);
                this.mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
                centerX -= 2.0f;
                centerY -= 2.0f;
                if (this.mCount.length() <= 2) {
                    canvas.drawCircle(centerX, centerY, 5.0f + radius, this.mBadgePaint1);
                    canvas.drawCircle(centerX, centerY, 3.0f + radius, this.mBadgePaint);
                } else {
                    canvas.drawCircle(centerX, centerY, 5.0f + radius, this.mBadgePaint1);
                    canvas.drawCircle(centerX, centerY, 4.0f + radius, this.mBadgePaint);
                }
            } else if (this.mCount.length() <= 2) {
                canvas.drawCircle(centerX, centerY, 9.0f + radius, this.mBadgePaint1);
                canvas.drawCircle(centerX, centerY, 7.0f + radius, this.mBadgePaint);
            } else {
                canvas.drawCircle(centerX, centerY, 10.0f + radius, this.mBadgePaint1);
                canvas.drawCircle(centerX, centerY, 8.0f + radius, this.mBadgePaint);
            }
            this.mTextPaint.getTextBounds(this.mCount, 0, this.mCount.length(), this.mTxtRect);
            float textY = centerY + (((float) (this.mTxtRect.bottom - this.mTxtRect.top)) / 2.0f);
            if (this.mCount.length() > 2) {
                canvas.drawText("99+", centerX, textY, this.mTextPaint);
            } else {
                canvas.drawText(this.mCount, centerX, textY, this.mTextPaint);
            }
        }
    }

    public void setCount(String count) {
        this.mCount = count;
        this.mWillDraw = !count.equalsIgnoreCase("0");
        invalidateSelf();
    }

    public void setAlpha(int alpha) {
    }

    public void setColorFilter(ColorFilter cf) {
    }

    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }

}
