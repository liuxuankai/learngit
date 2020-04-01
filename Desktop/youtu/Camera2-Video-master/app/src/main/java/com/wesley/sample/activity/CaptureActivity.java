package com.wesley.sample.activity;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.wesley.sample.R;
import com.wesley.sample.fragment.AnotherCaptureFragment;
import com.wesley.sample.fragment.CaptureFragment;

/**
 * Created by wesley on 2016/03/05.
 */
public class CaptureActivity extends AppCompatActivity {

    private RadioGroup mWavSource;
    private int source = 1;
    private Button start;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWavSource = (RadioGroup) findViewById(R.id.WavSource);

        mWavSource.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.WAVMATBLE:
                        source = 1;
                        break;
                    case R.id.WAVANDROID:
                        source = 2;
                        break;
                }
            }
        });

        start = (Button) findViewById(R.id.start);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CaptureActivity.this, AnotherActivity.class);
                intent.putExtra("source", source);
                startActivity(intent);

            }
        });



    }
}
