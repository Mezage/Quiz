package com.example.quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.ToggleButton;

public class settings extends AppCompatActivity {
    public Intent intent;

    public void onClickStart(View view){
        Switch world = (Switch) findViewById(R.id.worldTog);
        Switch question = (Switch) findViewById(R.id.quesTog);

        boolean specialB = ((ToggleButton) findViewById(R.id.buttonStyle)).isChecked();
        boolean timerOnW = world.isChecked();
        boolean timerOnQ = question.isChecked();

        if (timerOnW){
            intent.putExtra(MultipleChoice.togW, timerOnW);
        }if (timerOnQ){
            intent.putExtra(MultipleChoice.togQ, timerOnQ);
        }if(specialB){
            intent.putExtra(MultipleChoice.style, specialB);
        }


        EditText limit = (EditText) findViewById(R.id.timeAmount);
        String timeLimit = limit.getText().toString();
        if (timeLimit.matches("")){
            timeLimit = "-1";
        }

        intent.putExtra(MultipleChoice.limit, Integer.parseInt(timeLimit));
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        intent = new Intent(this, MultipleChoice.class);

    }
}