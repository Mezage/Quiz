package com.example.quiz;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class Timers extends Service {

    public static final int Notif_ID = 555;
    public static final String CHANNEL_ID = "channel";
    private int timer = -1, sec = 0, bigSec = 0;
    private boolean big = false, small = false;             //flags for clocks
    private boolean running = false, bigRunning = false;

    //private Handler mHandler;
    private final IBinder binder = new TimersBinder();      //for bound service

    public class TimersBinder extends Binder {
        Timers getTimer() {
            return Timers.this;
        }
    }

    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //Log.d("ANSWER", "" + choice + ", " + choice2);

        //added for bound
        createNotificationChannel();
        smallTimer();
        bigTimer();

    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void smallTimer() {                  //for questions
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                //Log.d("Running?", "" + sec + ", " + running);
                if (running) {
                    sec++;
                }
                handler.postDelayed(this, 1000);    //wait a sec before running again

            }
        });
    }

    private void bigTimer() {                   //overall time
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (bigRunning){                //if flag to start is enabled
                    bigSec++;
                }

                if(timer != -1 && timer < bigSec){
                    Toast.makeText(Timers.this, "Out of Time!", Toast.LENGTH_SHORT).show();
                    showNotif();
                }else {
                    handler.postDelayed(this, 1000);    //wait a sec before running again
                }
            }
        });
    }

    private void showNotif() {
        NotificationCompat.Builder builder = new
                NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentTitle(getString(R.string.outTime))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[]{0, 1000})
                .setAutoCancel(true);

        //call to Setting's activity
        Intent intent = new Intent(this, settings.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pending = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);  //,maybe change back to update?
        builder.setContentIntent(pending);

        //create notification
        NotificationManagerCompat notifMan = NotificationManagerCompat.from(this);
        notifMan.notify(Notif_ID, builder.build());

    }

    public boolean getRunning(){
        return running;
    }

    public int getSec() {
        return sec;
    }

    public int getBigSec() {
        return bigSec;
    }

    public void setTimer(int limit) {
        timer = limit;
    }

    public void setRunning(boolean state){
        running = state;
    }

    public void setBigRunning(boolean state){
        bigRunning = state;
    }

    public void resetQuestion(){
        sec = 0;
    }

    public void resetAll(){
        setRunning(false);
        setBigRunning(false);
        sec = 0;
        bigSec = 0;
        timer = -1;
    }
}