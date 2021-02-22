package com.example.quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

public class MultipleChoice extends AppCompatActivity {
    private int sec = 0, bigSec = 0, timer;
    private boolean big, small;
    private boolean stylized = false;
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
            boundTimer.resetAll();

            if (small) {                        //if flag for question timer is true
                boundTimer.setRunning(true);
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

    public void onClickSubmit(View view){           //when button clicked, check spinner input
        Intent intent;

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        Spinner spinner1 = (Spinner) findViewById(R.id.spinner2);
        String choice = String.valueOf(spinner.getSelectedItem());
        String choice2 = String.valueOf(spinner1.getSelectedItem());

        if ((!choice.equals("They cropped in another person's arm for the scenes")) || (!choice2.equals("Bear"))){
            intent = new Intent(this, Results.class);
            intent.putExtra(Results.FAILED, true);
        }else {
            intent = new Intent(this, ShortAnswer.class);
            intent.putExtra(ShortAnswer.newBig, bigSec);
        }

        intent.putExtra(ShortAnswer.togW, big);
        intent.putExtra(ShortAnswer.togQ, small);
        intent.putExtra(ShortAnswer.style, stylized);
        intent.putExtra(ShortAnswer.limit, timer);
        finish();
        startActivity(intent);

    }

    private void bigTimer(){                     //overall time + timer
        TextView overall = (TextView) findViewById(R.id.overallTime);
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
                    Intent intent = new Intent(MultipleChoice.this, Results.class);
                    intent.putExtra(Results.FAILED, true);
                    startActivity(intent);
                }else {
                    handler.postDelayed(this, 500);          //wait a 1/2 sec before running again
                }
            }
        });
    }

    private void smallTimer(){                  //for questions
        final TextView overall = (TextView) findViewById(R.id.questionTime);
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

                handler.postDelayed(this, 500);    //wait a 1/2 sec before running again

            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("secs", sec + boundTimer.getSec());
        savedInstanceState.putInt("bigSecs", bigSec + boundTimer.getBigSec());
        //savedInstanceState.putBoolean("run", running);
        savedInstanceState.putBoolean("big",big);
        savedInstanceState.putBoolean("small", small);
        savedInstanceState.putInt("timer", timer);
        savedInstanceState.putBoolean("style", stylized);
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
            boundTimer.setRunning(true);
            boundTimer.resetQuestion();
            unbindService(connection);
            bound = false;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intentb = new Intent(this, Timers.class);
        bindService(intentb, connection, Context.BIND_AUTO_CREATE);

        Intent intent = getIntent();
        small = intent.getBooleanExtra(togQ, false);
        big = intent.getBooleanExtra(togW, false);
        timer = intent.getIntExtra(limit, -1);
        stylized = intent.getBooleanExtra(style, false);

        if (savedInstanceState != null){
            sec = savedInstanceState.getInt("secs");
            bigSec = savedInstanceState.getInt("bigSecs");
            //running = savedInstanceState.getBoolean("run");
            big = savedInstanceState.getBoolean("big");
            small = savedInstanceState.getBoolean("small");
            timer = savedInstanceState.getInt("timer");
            stylized = savedInstanceState.getBoolean("style");
        }

       if(stylized){                  //if stylized button, set normal button to gone
            findViewById(R.id.button).setVisibility(View.GONE);
        }else{
            findViewById(R.id.imButt).setVisibility(View.GONE);
        }
    }
}