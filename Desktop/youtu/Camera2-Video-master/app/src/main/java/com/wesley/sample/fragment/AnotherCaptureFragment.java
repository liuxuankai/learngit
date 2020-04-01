package com.wesley.sample.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.camera2.CameraCharacteristics;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.wesley.camera2.fragment.Camera2Fragment;
import com.wesley.camera2.util.Camera2Listener;
import com.wesley.sample.Player.MyPlayer;
import com.wesley.sample.R;

import java.io.File;
import java.util.Calendar;
import java.util.Stack;

public class AnotherCaptureFragment extends Camera2Fragment implements Camera2Listener, View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    private String basePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/LipRecorder/MatlabWav/";
    private MediaPlayer mediaSingle;
    protected ImageView button;
    protected ImageView coverButton;
    private String fileName;
    private RadioGroup radioGroup;
    private boolean lightOn = false;
    private boolean coverButtonOn = true;

    private RadioGroup time;
    private int recordingTime = 6;

    private RadioGroup numFreSel;
    private int numFre = 3;

    private TextView textView;

    private TextView recordTimes;
    private int recordTimesCount = 0;

    private Button resetCount;

    private String lastFileName;

    private EditText experiment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRationaleMessage("Hey man, we need to use your camera please!");
        // 获取屏幕的刷新率
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        float refreshRate = display.getRefreshRate();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_capture, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        button = (ImageView) view.findViewById(R.id.camera_control);
        coverButton = (ImageView) view.findViewById(R.id.coverButton);
        setCameraFacing(CameraCharacteristics.LENS_FACING_FRONT);

//        radioGroup = (RadioGroup) view.findViewById(R.id.LigthSelection);
        time = (RadioGroup) view.findViewById(R.id.time);

        numFreSel = (RadioGroup) view.findViewById(R.id.numFre);

        textView = (TextView) view.findViewById(R.id.label);

        textView.setText("MatlabWav");

        experiment = (EditText) view.findViewById(R.id.experiment);

        recordTimes = (TextView) view.findViewById(R.id.recordTimes);

        resetCount = (Button) view.findViewById(R.id.reset);

        resetCount.setOnClickListener(this);
        button.setOnClickListener(this);
        time.setOnCheckedChangeListener(this);
        numFreSel.setOnCheckedChangeListener(this);
    }

    @Override
    public int getTextureResource() {
        return R.id.camera_preview;
    }

    private int getWavNumber() {
        File file = new File(basePath + experiment.getText() + "/");
        if(!file.exists()) {
            return 0;
        }
        int count = 0;
        for(String f : file.list()) {
            if(f.endsWith(".wav") && !f.equals("player.wav")) {
                count++;
            }
        }
        return count;
    }

    @Override
    public File getVideoFile(Context context) {
        File file;
        createBaseDir(basePath + experiment.getText() + "/");
        File location = new File(basePath);
        Calendar c = Calendar.getInstance();
        fileName = "" + (c.get(Calendar.MONTH) + 1) + "-" + c.get(Calendar.DAY_OF_MONTH) + "-" + c.get(Calendar.HOUR_OF_DAY) +
                "-" + c.get(Calendar.MINUTE) + "-" + c.get(Calendar.SECOND);
        file = new File(location,fileName + ".mp4");
        switch (numFre) {
            case 3:
                mediaSingle = MediaPlayer.create(context, R.raw.signal3);
                break;
            case 4:
                mediaSingle = MediaPlayer.create(context, R.raw.signal4);
                break;
            case 5:
                mediaSingle = MediaPlayer.create(context, R.raw.signal5);
                break;
            case 6:
                mediaSingle = MediaPlayer.create(context, R.raw.signal6);
                break;
        }

        return file;
    }

    public void disableRadioGroup(RadioGroup testRadioGroup) {
        for (int i = 0; i < testRadioGroup.getChildCount(); i++) {
            testRadioGroup.getChildAt(i).setEnabled(false);
        }
    }

    public void enableRadioGroup(RadioGroup testRadioGroup) {
        for (int i = 0; i < testRadioGroup.getChildCount(); i++) {
            testRadioGroup.getChildAt(i).setEnabled(true);
        }
    }

    public void onCameraControlClick(ImageView v) {
        if (!isRecording()) {
            mediaSingle.start();
            experiment.setEnabled(false);
            resetCount.setEnabled(false);
            disableRadioGroup(time);
            disableRadioGroup(numFreSel);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startRecordingVideo();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopRecordingVideo();
                    button.setImageResource(R.drawable.ic_record);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mediaSingle.stop();
//                    mediaSingle.release();
                    Toast.makeText(getActivity(), "结束！！！", Toast.LENGTH_SHORT).show();

                    recordTimesCount++;
                    recordTimes.setText(experiment.getText() + ": "+ recordTimesCount);
                    showNormalDialog();
                    if(coverButtonOn)
                        coverButton.setVisibility(View.GONE);
                    experiment.setEnabled(true);
                    resetCount.setEnabled(true);
                    enableRadioGroup(time);
                    enableRadioGroup(numFreSel);
                }
            }, recordingTime*1000);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "请开始说话！！！", Toast.LENGTH_SHORT).show();
                }
            },500);
            v.setImageResource(R.drawable.ic_pause);
        }
    }

    private boolean createBaseDir(String path) {
        File baseFile = new File(path);
        return baseFile.exists() || baseFile.mkdirs();
    }

    private void deleteFile(String name) {
        File wavFile = new File(basePath+experiment.getText() + "/" + name+".wav");
        File mp4File = new File(basePath+experiment.getText() + "/"+name+".mp4");
        wavFile.delete();
        mp4File.delete();
    }

    public boolean moveFile(String srcPath, String destPath, String name) {

        File srcFile = new File(srcPath + name);
        if(!srcFile.exists() || !srcFile.isFile())
            return false;

        File destDir = new File(destPath);
        if (!destDir.exists())
            destDir.mkdirs();

        return srcFile.renameTo(new File(destPath + name));
    }

    private void showNormalDialog(){
        /* @setIcon 设置对话框图标
         * @setTitle 设置对话框标题
         * @setMessage 设置对话框消息提示
         * setXXX方法返回Dialog对象，因此可以链式设置属性
         */
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(getActivity());
        normalDialog.setTitle("提示框");
        normalDialog.setMessage("是否保存此次采样文件?");
        normalDialog.setPositiveButton("yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
                        moveFile(basePath, basePath+experiment.getText()+"/", lastFileName+".wav");
                        moveFile(basePath, basePath+experiment.getText()+"/", lastFileName+".mp4");
                    }
                });
        normalDialog.setNegativeButton("no",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //...To-do
//                        deleteFile(lastFileName);
                        recordTimesCount--;
                        recordTimes.setText(experiment.getText() + ": "+ recordTimesCount);
//                        Log.i(TAG, "delete");
                    }
                });
        // 显示
        normalDialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.reset:
                recordTimesCount = getWavNumber();
                recordTimes.setText(experiment.getText() + ": "+ recordTimesCount);
                Toast.makeText(getActivity(), "切换目录！", Toast.LENGTH_SHORT).show();
                break;
            case R.id.camera_control:
                if (lightOn) {
                    coverButton.setVisibility(View.VISIBLE);
                    coverButtonOn = true;
                } else {
                    coverButtonOn = false;
                }
                lastFileName = fileName;
                onCameraControlClick((ImageView) view);
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (radioGroup.getId()) {
            case R.id.time:
                switch (i) {
                    case R.id.four:
                        recordingTime = 4;
                        reset(4);
                        break;
                    case R.id.six:
                        recordingTime = 6;
                        reset(6);
                        break;
                    case R.id.eight:
                        recordingTime = 9;
                        reset(8);
                        break;
                    case R.id.ten:
                        recordingTime = 10;
                        reset(10);
                        break;
                    case R.id.twelve:
                        recordingTime = 12;
                        reset(12);
                        break;
                }
                break;
            case R.id.numFre:
                switch (i) {
                    case R.id.fourFre:
                        numFre = 4;
                        break;
                    case R.id.fiveFre:
                        numFre = 5;
                        break;
                    case R.id.threeFre:
                        numFre = 3;
                        break;
                    case R.id.sixFre:
                        numFre = 6;
                        break;
                }
                break;
        }
    }
}
