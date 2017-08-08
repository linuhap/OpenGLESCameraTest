package hznu.example.openglestest;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by 1215 on 7/26/2017.
 */

public class MyCameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback {

    private Context mContext;
    private Camera mCamera;
    private SurfaceHolder mHolder;
    private ICaptureDataCallback mICaptureDataCallback;

    public MyCameraSurfaceView(Context context) {
        super(context);
        mHolder = getHolder();
        mHolder.addCallback(this);
    }

    public void exitCamera() {
        mCamera.release();
    }

    //SurfaceHolder.Callback
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mCamera = Camera.open();
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.setPreviewCallback(this);
            mCamera.setDisplayOrientation(90);
            mCamera.startPreview();
        } catch (IOException e) {
            mCamera.release();
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        synchronized (this) {
            if (mICaptureDataCallback != null) {
                mICaptureDataCallback.onPreviewCaptured(data, mCamera);
            }
        }
        mCamera.addCallbackBuffer(data);
    }

    public void setICaptureDataCallback(ICaptureDataCallback ICaptureDataCallback) {
        mICaptureDataCallback = ICaptureDataCallback;
    }

}
