package hznu.example.openglestest;

import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private FrameLayout mFrameLayout;
    private FrameLayout mCameraLayout;
    private MyCameraSurfaceView mMyCameraSurfaceView;
    private MyGLSurfaceView mMyGLSurfaceView;
    private boolean changedYUV = false;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());

        mFrameLayout = (FrameLayout) findViewById(R.id.framelayout);
        mCameraLayout = (FrameLayout) findViewById(R.id.cameralayout);

        mMyGLSurfaceView = new MyGLSurfaceView(this);

        mMyCameraSurfaceView = new MyCameraSurfaceView(this);
        mMyCameraSurfaceView.setICaptureDataCallback(mMyGLSurfaceView);

        //mMyCameraSurfaceView.setZOrderMediaOverlay(true);
        //mMyGLSurfaceView.setZOrderMediaOverlay(true);
        mFrameLayout.addView(mMyGLSurfaceView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mCameraLayout.addView(mMyCameraSurfaceView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        //mFrameLayout.addView(mMyCameraSurfaceView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    @Override
    protected void onPause() {
        super.onPause();
        //mMyCameraSurfaceView.exitCamera();
    }
}
