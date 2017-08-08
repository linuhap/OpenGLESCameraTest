package hznu.example.openglestest;

import android.hardware.Camera;

/**
 * Created by 1215 on 7/27/2017.
 */

public interface ICaptureDataCallback {
    public void onPreviewCaptured(byte[] data, Camera camera);
    //public void onJPEGDataCaptured(byte[] data, Camera camera);
}
