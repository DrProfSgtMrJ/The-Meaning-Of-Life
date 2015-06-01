package de.exitgames.demo.loadbalancing;

import android.graphics.Point;

import java.util.Random;

public class ColoredPoint {

    private Point point;
    private int r;
    private int g;
    private int b;

    public ColoredPoint(Point p) {
        point = p;
        Random color = new Random();
        r = color.nextInt(256);
        g = color.nextInt(256);
        b = color.nextInt(256);
    }

    public ColoredPoint(int x, int y, int re, int gr, int bl){
        point = new Point(x,y);
        r = re;
        g = gr;
        b = bl;
    }

    public int getX(){ return point.x; }
    public int getY(){ return point.y; }
    public int getR(){ return r; }
    public int getG(){ return g; }
    public int getB(){ return b; }
    public Point getPoint(){ return point; }
}
