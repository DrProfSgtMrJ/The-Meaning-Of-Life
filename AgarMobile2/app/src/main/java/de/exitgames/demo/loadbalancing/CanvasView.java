package de.exitgames.demo.loadbalancing;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

public class CanvasView extends View {
    public Paint mPaint;
    public static Canvas mCanvas;
    private int radius = 5;
    private Random color = new Random();
    private ArrayList<ColoredPoint> myPoints = new ArrayList<ColoredPoint>();
    private ArrayList<ColoredPoint> otherPoints = new ArrayList<ColoredPoint>();

    //constructor
    public CanvasView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
    }

    public ArrayList<ColoredPoint> drawCircle(ArrayList<ColoredPoint> p) {
        myPoints = p;
        int minX = radius * 2;
        int maxX = getWidth() - (radius *2 );
        int minY = radius * 2;
        int maxY = getHeight() - (radius *2 );

        //Generate random numbers for x and y locations of the circle on screen
        Random random = new Random();
        p.add(new ColoredPoint(new Point(random.nextInt(maxX - minX + 1) + minX, random.nextInt(maxY - minY + 1) + minY)));

        //important. Refreshes the view by calling onDraw function
        invalidate();
        return p;
    }

    public void drawCircleOther(ArrayList<ColoredPoint> p) {
        otherPoints = p;

        //important. Refreshes the view by calling onDraw function
        invalidate();
    }

    //what I want to draw is here
    protected void onDraw(Canvas canvas) {
        mCanvas = canvas;
        super.onDraw(mCanvas);
        canvas.drawColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
        for(int i = 0; i < myPoints.size(); i++) {
            mPaint.setARGB(255, myPoints.get(i).getR(), myPoints.get(i).getG(), myPoints.get(i).getB());
            canvas.drawCircle(myPoints.get(i).getPoint().x, myPoints.get(i).getPoint().y, radius, mPaint);
        }
        for(int i = 0; i < otherPoints.size(); i++) {
            mPaint.setARGB(255, otherPoints.get(i).getR(), otherPoints.get(i).getG(), otherPoints.get(i).getB());
            canvas.drawCircle(otherPoints.get(i).getPoint().x, otherPoints.get(i).getPoint().y, radius, mPaint);
        }
    }
}
