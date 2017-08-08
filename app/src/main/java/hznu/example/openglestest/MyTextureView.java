package hznu.example.openglestest;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.TextureView;

import java.io.IOException;

/**
 * Created by 1215 on 7/27/2017.
 */

public class MyTextureView extends TextureView implements TextureView.SurfaceTextureListener {

    private Thread mProducerThread = null;
    private MyGL20Renderer mRenderer;

    public MyTextureView(Context context) {
        super(context);
        this.setSurfaceTextureListener(this);
        mRenderer = new MyGL20Renderer();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mProducerThread = null;
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        // Invoked every time there's a new Camera preview frame
    }
}
