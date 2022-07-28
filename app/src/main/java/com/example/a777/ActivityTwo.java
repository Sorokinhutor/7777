package com.example.a777;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityTwo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two);
        Button buttonBack = findViewById(R.id.buttonNext);

        buttonBack.setOnClickListener((View.OnClickListener) this);
        finish();

    }


}