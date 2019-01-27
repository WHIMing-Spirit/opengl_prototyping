package com.whim.demo;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class WhimRenderer implements GLSurfaceView.Renderer {

    static final int BYTES_PER_FLOAT = 4;
    private static int VERTEX_COUNT = 5;

    private FloatBuffer vertexBuffer;


    @Override
    public void onDrawFrame(GL10 gl) {
        // This is called whenever it's time to draw a new frame

        // clear the color
        GLES20.glClearColor(0.8f, 0.0f, 0.0f, 1.0f);

        // clear the depth
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // This method is called when the surface is first created. It will also be called
        // if we lose our surface context and it is later recreated by the system
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // This is called whenever the surface changes; i.e. switching from portrait to landscape.
        // It is also called after the surface has been created.
    }

    public void newFftData(FloatBuffer fftData, int vCount){
        vertexBuffer = fftData;
        VERTEX_COUNT = vCount;
    }
}
