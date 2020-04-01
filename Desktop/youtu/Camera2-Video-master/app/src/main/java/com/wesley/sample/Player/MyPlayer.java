package com.wesley.sample.Player;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;

import com.wesley.camera2.util.RecordUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class MyPlayer {
    private AudioTrack mAudioTrack = null;

    private MediaPlayer mPlayer;

    private int bufferSize = AudioTrack.getMinBufferSize(48000, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);

    private byte[] data;
    private int[] fres;

    private int startFre = 18000;
    private int endFre = 21000;
    private int interval = 300;
    private int numFre = 6;

    public MyPlayer(String basepath, String filename, int num) {
        numFre = num;
        genFre(basepath, filename);
        initBuffer(basepath, filename);
        writeWav(basepath);

        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(basepath + "player.wav");
            mPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        initAudioTrack();
    }

    public void play() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mPlayer.start();
            }
        }).start();

//        mAudioTrack.play();
    }

    public void stop() {
        mPlayer.stop();
        mPlayer.release();
//        mAudioTrack.stop();
//        mAudioTrack.release();
    }

    private void initAudioTrack() {

        mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 48000, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT, 48000*4, AudioTrack.MODE_STATIC);
        mAudioTrack.write(data, 0, data.length);
        mAudioTrack.setLoopPoints(0, data.length/4, -1);
        mAudioTrack.setVolume(AudioTrack.getMaxVolume());
    }

    private void initBuffer(String basepath, String filename) {
        double[] sample = new double[48000];
        for(int i = 0; i < 48000; i++) {
            for(int j = 0; j < numFre; j++) {
                sample[i] += Math.cos(2*Math.PI*fres[j]*i/48000);
            }
            sample[i] /= numFre;
        }

        data = new byte[4*48000];
        int idx = 0;
        for(int i = 0; i < 48000; i++) {
            final short val = (short) ((sample[i] * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            data[idx++] = (byte) (val & 0x00ff);
            data[idx++] = (byte) ((val & 0xff00) >>> 8);
            data[idx++] = 0;
            data[idx++] = 0;


        }
    }

    private void genFre(String basepath, String filename) {
        Random random = new Random(System.currentTimeMillis());
//        fres = new int[]{18350, 18700, 19050, 19400, 19750, 20100};
        fres = new int[numFre];
        fres[0] = startFre + random.nextInt(endFre-startFre-(numFre-1)*interval);
        for(int i = 1; i < numFre; i++) {
            fres[i] = fres[i-1] + interval + random.nextInt(endFre - (fres[i-1]+interval) - interval*(numFre-i-1));
        }
        File location = new File(basepath);
        File file = new File(location,filename + ".txt");

        FileWriter os = null;
        try {
            if(file.exists()) {
                file.delete();
            }
            file.createNewFile();
            os = new FileWriter(file);
            for(int i = 0; i < numFre; i++) {
                os.write(fres[i] + " ");
            }
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeWav(String basepath) {
        File location = new File(basepath);
        File file = new File(location, "player.wav");
        try {
            file.createNewFile();
            FileOutputStream out = new FileOutputStream(file);
            long totalAudioLen = data.length * 15;
            long totalDataLen = totalAudioLen + 36;
            long longSampleRate = 48000;
            int channels = 2;
            long byteRate = 16 * 48000 * channels / 8;
            WriteWaveFileHeader(out, totalAudioLen, totalDataLen, longSampleRate, channels, byteRate);
            for(int i = 0; i < 15; i++) {
                out.write(data);
            }
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen, long totalDataLen, long longSampleRate,
                                     int channels, long byteRate) throws IOException {
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);//数据大小
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';//WAVE
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        //FMT Chunk
        header[12] = 'f'; // 'fmt '
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';//过渡字节
        //数据大小
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        //编码方式 10H为PCM编码格式
        header[20] = 1; // format = 1
        header[21] = 0;
        //通道数
        header[22] = (byte) channels;
        header[23] = 0;
        //采样率，每个通道的播放速度
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        //音频数据传送速率,采样率*通道数*采样深度/8
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        // 确定系统一次要处理多少个这样字节的数据，确定缓冲区，通道数*采样位数
        header[32] = (byte) (1 * 16 / 8);
        header[33] = 0;
        //每个样本的数据位数
        header[34] = 16;
        header[35] = 0;
        //Data chunk
        header[36] = 'd';//data
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        out.write(header, 0, 44);
    }
}
