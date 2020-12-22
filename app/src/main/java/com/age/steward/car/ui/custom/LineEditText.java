package com.age.steward.car.ui.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.EditText;

import androidx.core.content.ContextCompat;

import com.age.steward.car.R;


@SuppressLint("AppCompatCustomView")
public class LineEditText extends EditText {
    private Paint mPaint;
    /**
     * @param context
     * @param attrs
     */
    public LineEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(ContextCompat.getColor(context, R.color.divider_line_color));
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        TextPaint mTextPaint =this.getPaint();

        float textWidth = mTextPaint .measureText(this.getText().toString());


//      画底线
        canvas.drawLine(0,this.getHeight()-1,  textWidth<this.getWidth()?this.getWidth():textWidth, this.getHeight()-1, mPaint);
    }



}
