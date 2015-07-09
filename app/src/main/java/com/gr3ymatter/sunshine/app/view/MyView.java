package com.gr3ymatter.sunshine.app.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.gr3ymatter.sunshine.app.R;

/**
 * Created by Afzal on 6/30/15.
 */
public class MyView extends View {


    int myHeight;
    Context mContext;
    int myWidth;

    public MyView (Context context){
        super(context);
        mContext = context;
    }

    public MyView (Context context, AttributeSet attributeSet){
        super(context, attributeSet);
        mContext = context;
    }

    public MyView (Context context, AttributeSet attributeSet, int defaultStyle){
        super(context, attributeSet, defaultStyle);
        mContext = context;

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();

        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(10f);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(myWidth / 2, myHeight / 2, getHeight() / 2 - 5f, paint);
        paint.setColor(mContext.getResources().getColor(R.color.sunshine_dark_blue));
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new RadialGradient(myWidth / 2, myHeight / 2, getHeight() / 2, mContext.getResources().getColor(R.color.sunshine_dark_blue), mContext.getResources().getColor(R.color.sunshine_light_blue), Shader.TileMode.REPEAT));
        canvas.drawCircle(myWidth/2, myHeight/2, getHeight()/2 - 10f, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2f);
        paint.setColor(Color.RED);
        Path path = new Path();
        path.moveTo(0, -10);
        path.lineTo(5, 0);
        path.lineTo(-5, 0);
        path.close();
        path.offset(10, 40);
        canvas.drawPath(path, paint);
        path.offset(50, 100);
        canvas.drawPath(path, paint);
// offset is cumlative
// next draw displaces 50,100 from previous
        path.offset(50, 100);
        canvas.drawPath(path, paint);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int hSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        myHeight = hSpecSize;

        if(hSpecMode == MeasureSpec.EXACTLY){
            myHeight = hSpecSize;
        }
        else if (hSpecMode == MeasureSpec.AT_MOST)
        {
            //wrap content
        }

        int wSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSpecSize = MeasureSpec.getSize(widthMeasureSpec);

        myWidth = wSpecSize;

        if(wSpecMode == MeasureSpec.EXACTLY){
            myWidth = hSpecSize;
        }
        else if (hSpecMode == MeasureSpec.AT_MOST)
        {
            //wrap content
        }


        setMeasuredDimension(myWidth,myHeight);

    }
}
