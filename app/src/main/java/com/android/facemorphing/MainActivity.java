package com.android.facemorphing;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private DLibResult dLibResult;

    Button testActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dLibResult = new DLibResult(
                this,
                "shape_predictor_68_face_landmarks_GTX.dat"
        );

        testActivity = findViewById(R.id.buttonTestActivity);
        testActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToTestPage();
            }
        });
    }

    private void switchToTestPage() {
        Intent switchActivityIntent = new Intent(this, Test.class);
        startActivity(switchActivityIntent);
    }
}