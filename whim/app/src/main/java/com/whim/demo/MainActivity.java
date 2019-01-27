package com.whim.demo;

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
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements Visualizer.OnDataCaptureListener {

    private static final int RECORD_AUDIO_PERMISSION_CODE = 101;

    private WhimOpenGLView whimOpenGLView;
    private WhimRenderer whimRenderer;

    private MediaPlayer mediaPlayer;
    private Visualizer visualizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // save the previous state of the machine
        super.onCreate(savedInstanceState);

        // check permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            // RECORD_AUDIO permissions is already available, open app.
            init();
        } else {
            // RECORD_AUDIO permission has not been granted.

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(this, "RECORD_AUDIO permission is required.", Toast.LENGTH_SHORT).show();
            }

            // Request RECORD_AUDIO permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RECORD_AUDIO_PERMISSION_CODE: {
                // If Permission is granted then start the initialization
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init();
                }
            }
        }
    }

    public void init() {
        // Create an object that Android uses on its widget system
        // Or we can understand as the object contain the stuff render from our renderer
        whimOpenGLView = new WhimOpenGLView(this);

        // Get the audio capture size
        int audioSampleSize = Visualizer.getCaptureSizeRange()[1];

        // Check if the system supports OpenGL ES 2.0.
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

//        final DisplayMetrics displayMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        if(audioSampleSize > 512){
            audioSampleSize = 512;
        }

        if (supportsEs2) {
            // Create a renderer, which is where it does all the drawing and such
//            whimRenderer = new WhimRenderer(audioSampleSize);
            whimRenderer = new WhimRenderer();

            // Request an OpenGL ES 2.0 compatible context.
            whimOpenGLView.setEGLContextClientVersion(2);

            // Pause all the preserve EGL context
            whimOpenGLView.setPreserveEGLContextOnPause(true);

            // Set the renderer to our demo renderer, defined below.
//            whimOpenGLView.setRenderer(whimRenderer, displayMetrics.density, audioSampleSize);
            whimOpenGLView.setRenderer(whimRenderer);

            mediaPlayer = MediaPlayer.create(this, R.raw.ritual);
            mediaPlayer.setLooping(true);
            mediaPlayer.start();

            visualizer = new Visualizer(mediaPlayer.getAudioSessionId());
            visualizer.setCaptureSize(audioSampleSize);
            visualizer.setDataCaptureListener(this, Visualizer.getMaxCaptureRate(), true, true);
            visualizer.setEnabled(true);

            // set our OpenGL view to main service view
            this.setContentView(whimOpenGLView);
        } else {
            Log.e("OpenGLES2", "Your device doesn't support ES2. ("+configurationInfo.reqGlEsVersion+")");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        whimOpenGLView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        whimOpenGLView.onPause();
    }

    @Override
    public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
        // This's the func we would use
        whimOpenGLView.onFftDataCapture(fft);
    }

    @Override
    public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
        // Don't add anything within this func, we won't use it.
        whimOpenGLView.onWaveFormDataCapture(waveform);
    }
}
