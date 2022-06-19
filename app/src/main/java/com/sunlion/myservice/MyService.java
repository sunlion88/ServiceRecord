package com.sunlion.myservice;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MyService extends Service {

    private static final String TAG = "MyService";

    public static final int SAMPLE_RATE_HZ = 16000;

    public static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO;

    public static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private boolean isRecording;
    private AudioRecord audioRecord;
    private AudioTrack audioTrack;
    private byte[] audioData;
    private FileInputStream fileInputStream;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,MyService.class.getName()+" onCreate");
        startRecord();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,MyService.class.getName()+" onDestroy");
        stopRecord();

    }

    @SuppressLint("MissingPermission")
    public void startRecord()
    {
        final int minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE_HZ,CHANNEL_CONFIG,AUDIO_FORMAT);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,SAMPLE_RATE_HZ,CHANNEL_CONFIG,AUDIO_FORMAT,minBufferSize);
        final byte data[] = new byte[minBufferSize];
        final File file = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC),"Service_Record.pcm");
        if(!file.mkdir())
            Log.e(TAG, "PCM File Directory already exist");
        if(file.exists())
            file.delete();
        audioRecord.startRecording();
        isRecording = true;

        Log.d(TAG,"StartRecord Record File Save "+file.getAbsolutePath());

        new Thread(new Runnable() {
            @Override
            public void run() {
                FileOutputStream os = null;
                try
                {
                    os = new FileOutputStream(file);
                }catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }

                if(os != null)
                {
                    while (isRecording)
                    {
                        int read = audioRecord.read(data,0,minBufferSize);
                        // 如果读取音频数据没有出现错误，就将数据写入到文件
                        if(AudioRecord.ERROR_INVALID_OPERATION != read)
                        {
                            try
                            {
                                os.write(data);
                            }catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                    try
                    {
                        os.close();
                    } catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void stopRecord()
    {
        isRecording =false;
        if(audioRecord != null)
        {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
        PcmToWavUtil pcmToWavUtil = new PcmToWavUtil(SAMPLE_RATE_HZ,CHANNEL_CONFIG,AUDIO_FORMAT);
        File pcmFile = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC),"Service_Record.pcm");
        File wavFile = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC),"Service_Record.wav");
        if(!wavFile.mkdir())
            Log.e(TAG, "WAV File Directory already exist");
        if(wavFile.exists())
            wavFile.delete();
        pcmToWavUtil.PcmToWav(pcmFile.getAbsolutePath(),wavFile.getAbsolutePath());
        Log.d(TAG,"StopRecord And PCM File To WAV File save "+wavFile.getAbsolutePath());
    }
}