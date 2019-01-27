package com.example.leonl.whimpresentationdemo;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements Visualizer.OnDataCaptureListener {
    private static final int REQUEST_PERMISSION = 101;

    private MediaPlayer mediaPlayer;
    private Visualizer visualizer;

    private WhimSurfaceView whimSurfaceView;
    private WhimRenderer whimRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check Audio Record Permission
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(this, "RECORD_AUDIO permission is required.", Toast.LENGTH_SHORT).show();

            }

            // If no permission then request it to the user
            else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO},
                        REQUEST_PERMISSION);
            }

        } else {
            init();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION: {
                // If Permission is granted then start the initialization
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init();
                }
            }
        }
    }

    public void init(){
        int audioSampleSize = Visualizer.getCaptureSizeRange()[1];

        if(audioSampleSize > 512){
            audioSampleSize = 512;
        }

        final DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        whimSurfaceView = new WhimSurfaceView(this);

        // Check if the system supports OpenGL ES 2.0.
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        if (supportsEs2)
        {
            // Request an OpenGL ES 2.0 compatible context.
            whimSurfaceView.setEGLContextClientVersion(2);

            // Pause all the preserve EGL context
            whimSurfaceView.setPreserveEGLContextOnPause(true);

            whimRenderer = new WhimRenderer(audioSampleSize);
            // Set the renderer to our demo renderer, defined below.
            whimSurfaceView.setRenderer(whimRenderer, displayMetrics.density, audioSampleSize);
        }
        else
        {
            // This is where you could create an OpenGL ES 1.x compatible
            // renderer if you wanted to support both ES 1 and ES 2.
            Log.e("OpenGLES2", "Your device doesn't support ES2. ("+configurationInfo.reqGlEsVersion+")");
            return;
        }


        mediaPlayer = MediaPlayer.create(this, R.raw.ritual);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        visualizer = new Visualizer(mediaPlayer.getAudioSessionId());
        visualizer.setCaptureSize(audioSampleSize);
        visualizer.setDataCaptureListener(this, Visualizer.getMaxCaptureRate(), true, true);
        visualizer.setEnabled(true);

        setContentView(whimSurfaceView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        whimSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        whimSurfaceView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            visualizer.setEnabled(false);
            mediaPlayer.release();
        }
    }

    @Override
    public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
        // What in the fuck do i do with the waveform
    }

    @Override
    public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
        whimSurfaceView.updateFft(fft);
    }
}
