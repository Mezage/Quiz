package com.example.quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Results extends AppCompatActivity {
    public static final String FAILED = "failed";
    public static int loss = 0;
    public static int win = 0;

    public void onClickWinRatio(View view){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Failed: " + loss + "\nPassed: " + win);

        String chooseTitle = getString(R.string.choose);
        Intent choose = Intent.createChooser(intent, chooseTitle);
        startActivity(choose);
    }

    public void onClickRetry(View view){
        Intent intent = new Intent(this, settings.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        TextView result = (TextView) findViewById(R.id.textView6);
        Intent intent = getIntent();
        boolean failed = intent.getBooleanExtra(FAILED, true);
        if (failed){
            result.setText("Sorry\nYou Failed");
            loss++;
        }else {
            result.setText("Congrats!\nYou Passed");
            win++;
        }
    }
}