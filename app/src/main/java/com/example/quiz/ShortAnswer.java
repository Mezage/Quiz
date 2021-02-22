package com.example.quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ShortAnswer extends AppCompatActivity {
    static int counter = 0, timer = -1;
    private boolean big, small, stylized;
    private int sec = 0, bigSec = 0;
    public static final String newBig = "NEWBIG";
    public static final String togW = "worldToggle";
    public static final String togQ = "questionToggle";
    public static final String limit = "timeLimit";
    public static final String style = "ButtonStyle";

    //to bind service to this activity
    private Timers boundTimer;
    private boolean bound = false;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Timers.TimersBinder binder = (Timers.TimersBinder) service;
            boundTimer = binder.getTimer();
            bound = true;
            boundTimer.resetQuestion();

            if (small) {
                boundTimer.setRunning(true);
                Log.d("Running?", "" + boundTimer.getSec() + ", " + boundTimer.getRunning());
                smallTimer();
            }if (big || (timer != -1)) {
                boundTimer.setBigRunning(true);
                boundTimer.setTimer(timer);
                bigTimer();
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    public void onClickSubmit(View view) {
        Intent intent = new Intent(this, Results.class);
        if (counter < 2) {
            EditText input = (EditText) findViewById(R.id.fruitInput);
            String answer2 = input.getText().toString();

            if (answer2.equalsIgnoreCase("peach")) {
                intent.putExtra(Results.FAILED, false);
                startActivity(intent);
            }
            counter++;
        }if(counter == 2) {
            intent.putExtra(Results.FAILED, true);
            startActivity(intent);


        }
    }

    private void bigTimer(){
        TextView overall = (TextView) findViewById(R.id.overallTime2);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = (bigSec + boundTimer.getBigSec())/3600;
                int min = ((bigSec + boundTimer.getBigSec()) % 3600)/60;
                int seconds = (bigSec+ boundTimer.getBigSec()) % 60;
                String time = String.format("Overall time: %d:%02d:%02d", hours, min, seconds);

                if (big) {
                    overall.setText(time);
                }
                if(timer != -1 && timer < boundTimer.getBigSec() + bigSec){             //check to see if time's up
                    Intent intent = new Intent(ShortAnswer.this, Results.class);
                    intent.putExtra(Results.FAILED, true);
                    startActivity(intent);
                }else {
                    handler.postDelayed(this, 500);          //wait a 1/2 sec before running again
                }
            }
        });
    }

    private void smallTimer(){
        final TextView overall = (TextView) findViewById(R.id.questionTime2);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                //compensate for bound service restarting
                int hours = (sec + boundTimer.getSec()) / 3600;
                int min = ((sec + boundTimer.getSec()) % 3600) / 60;
                int seconds = (sec + boundTimer.getSec()) % 60;
                String time = String.format("Question time: %d:%02d:%02d", hours, min, seconds);

                overall.setText(time);

                handler.postDelayed(this, 500);

            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("secs", sec + boundTimer.getSec());
        savedInstanceState.putInt("bigSecs", bigSec + boundTimer.getBigSec());
        savedInstanceState.putInt("tries", counter);    //so user doesn't cheat
        savedInstanceState.putBoolean("big",big);
        savedInstanceState.putBoolean("small", small);
        savedInstanceState.putInt("timer", timer);
    }

    @Override
    protected void onStart(){       //start/stop for binding service
        super.onStart();
        if (bound){
            boundTimer.setRunning(true);
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        if(bound){
            boundTimer.setRunning(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bound){
            boundTimer.resetAll();
            unbindService(connection);
            bound = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_short_answer);

//        try {
//            Thread.sleep(500);
//        }catch ()

        //create connection first
        Intent intentb = new Intent(this, Timers.class);
        bindService(intentb, connection, Context.BIND_AUTO_CREATE);

        Intent intent = getIntent();                //grab the global timer from last activity
        bigSec = intent.getIntExtra(newBig, 0);

        small = intent.getBooleanExtra(togQ, false);
        big = intent.getBooleanExtra(togW, false);
        timer = intent.getIntExtra(limit, -1);
        stylized = intent.getBooleanExtra(style, false);

        if (savedInstanceState != null){
            sec = savedInstanceState.getInt("secs");
            bigSec = savedInstanceState.getInt("bigSecs");
            counter = savedInstanceState.getInt("tries");
            big = savedInstanceState.getBoolean("big");
            small = savedInstanceState.getBoolean("small");
            timer = savedInstanceState.getInt("timer");
            stylized = savedInstanceState.getBoolean("style");
        }

        if(stylized){                  //if stylized button, set normal button to gone
            findViewById(R.id.button2).setVisibility(View.GONE);
        }else{
            findViewById(R.id.imButt2).setVisibility(View.GONE);
        }
    }
}