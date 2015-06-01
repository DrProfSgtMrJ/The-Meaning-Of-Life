package de.exitgames.demo.loadbalancing;


import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

import java.util.ArrayList;

public class GameActivity extends Activity {

    private final static int seconds = 2;
    CanvasView myCanvasView;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable(){
        @Override
        public void run() {
            myPoints = myCanvasView.drawCircle(myPoints);
            ApplicationManager.getClient().sendPoints(myPoints.get(myPoints.size() - 1));
            handler.postDelayed(runnable,seconds*1000);
        }
    };

    private ArrayList<ColoredPoint> myPoints = new ArrayList<>();
    private ArrayList<ColoredPoint> otherPoints = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ApplicationManager.registerActivity(this);

        setContentView(R.layout.game);
        myCanvasView = (CanvasView) findViewById(R.id.canvasView1);

        handler.postDelayed(runnable,seconds*1000);
    }

    public void updatePoints(ColoredPoint p){
        otherPoints.add(p);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                myCanvasView.drawCircleOther(otherPoints);
            }
        });
    }
}
