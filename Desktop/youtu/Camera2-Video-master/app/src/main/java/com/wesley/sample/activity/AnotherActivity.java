package com.wesley.sample.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.wesley.sample.R;
import com.wesley.sample.fragment.AnotherCaptureFragment;
import com.wesley.sample.fragment.CaptureFragment;

public class AnotherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another);




    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        switch (intent.getIntExtra("source", 0)) {
            case 1:
                getFragmentManager().beginTransaction().add(R.id.content, new AnotherCaptureFragment()).commit();
                break;
            case 2:
                getFragmentManager().beginTransaction().add(R.id.content, new CaptureFragment()).commit();
                break;
        }
    }
}
