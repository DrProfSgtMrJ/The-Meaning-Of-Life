package de.exitgames.demo.loadbalancing;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class TrafficStatsView extends Activity
{
    private Button m_BackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.traffic_stats);
        this.m_BackBtn = (Button) findViewById(R.id.statsBtnBack);

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {

                while(true)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            TrafficStatsUpdater tsu = new TrafficStatsUpdater();
                            tsu.update();

                        }
                    });

                    try
                    {
                        Thread.sleep(1000);
                    } catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }

                }
            }
        }).start();

        this.m_BackBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                // return to last view
                finish();
            }
        });
    }

    public void update()
    {
        this.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                TrafficStatsUpdater tsu = new TrafficStatsUpdater();
                tsu.update();
            }
        });
    }

class TrafficStatsUpdater
{
    private TextView data;

    public TrafficStatsUpdater()
    {
        this.data = (TextView) findViewById(R.id.data);
    }

    public void update()
    {
        this.data.setText("" + ApplicationManager.getClient().loadBalancingPeer.VitalStatsToString(true));

    }
}
}
