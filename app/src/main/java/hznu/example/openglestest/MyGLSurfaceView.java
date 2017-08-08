package hznu.example.openglestest;

import android.content.Context;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;


/**
 * Created by 1215 on 7/27/2017.
 */

public class MyGLSurfaceView extends GLSurfaceView implements ICaptureDataCallback{

    private TestRenderer mRenderer;


    public MyGLSurfaceView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        mRenderer = new TestRenderer();
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public void onPreviewCaptured(byte[] data, Camera camera) {
        int width = camera.getParameters().getPreviewSize().width;
        int height = camera.getParameters().getPreviewSize().height;
        mRenderer.changeYUVFrame(data, width, height);
        requestRender();
    }
}
