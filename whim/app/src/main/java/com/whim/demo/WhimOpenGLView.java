package com.whim.demo;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class WhimOpenGLView extends GLSurfaceView {
    private static float amp = 0.0001f;

    private WhimRenderer whimRenderer;
    private int captureSize;
    private float density;

    public WhimOpenGLView(Context context) {
        super(context);
    }

    public WhimOpenGLView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setRenderer(WhimRenderer whimRenderer, float density, int captureSize){
        super.setRenderer(whimRenderer);

        this.whimRenderer = whimRenderer;
        this.density = density;
        this.captureSize = captureSize;
    }

    public void onFftDataCapture(byte[] fft) {
        // This's the func we would use

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

        FloatBuffer fftInput = ByteBuffer.allocateDirect(fftFinal.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        fftInput.put(fftFinal).position(0);

        whimRenderer.newFftData(fftInput, fftFinal.length/7);
    }

    public void onWaveFormDataCapture(byte[] waveform){
        // Don't add anything within this func, we won't use it.
    }
}
