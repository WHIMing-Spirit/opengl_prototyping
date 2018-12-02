package com.example.leonl.whimpresentationdemo;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class WhimSurfaceView extends GLSurfaceView {
    private static float amp = 0.0001f;

    private static int captureSize;
    private static float density;

    private WhimRenderer whimRenderer;

    public WhimSurfaceView(Context context) {
        super(context);
    }

    public WhimSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setRenderer(WhimRenderer inputRenderer, float inputDensity, int captureSize){
        this.whimRenderer = inputRenderer;
        this.density = inputDensity;
        this.captureSize = captureSize;

        super.setRenderer(inputRenderer);
    }

    public void updateFft(byte[] fft){
        int arraySize = captureSize/2;
        float[] fftRender = new float[arraySize*7];

        int j = 0;
        //float plus = (float)1/(arraySize/2);
        float plus = (float)1/(arraySize/16);
        float k = -1.0f;

        for(int i = 0; i < captureSize-1; i++){
            int amplify = (fft[i]*fft[i]) + (fft[i+1]*fft[i+1]);

            fftRender[j] = k;
            fftRender[j+1] = (float)amplify*amp;
            fftRender[j+2] = 0.0f;
            fftRender[j+3] = 1.0f;
            fftRender[j+4] = 0.0f;
            fftRender[j+5] = 0.0f;
            fftRender[j+6] = 1.0f;

            k+=plus;
            i++;
            j+=7;
        }

        float[] fftFinal = new float[fftRender.length/8];
        System.arraycopy(fftRender,0,fftFinal,0,fftFinal.length);

        /*
        FloatBuffer fftInput = ByteBuffer.allocateDirect(fftRender.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        fftInput.put(fftRender).position(0);

        whimRenderer.newFftData(fftInput, captureSize/2);
        */

        FloatBuffer fftInput = ByteBuffer.allocateDirect(fftFinal.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        fftInput.put(fftFinal).position(0);

        whimRenderer.newFftData(fftInput, fftFinal.length/7);
    }

    public void updateWaveform(byte[] waveform){
    }
}
