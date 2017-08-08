package hznu.example.openglestest;

import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by 1215 on 7/20/2017.
 */

public class MyGL20Renderer implements GLSurfaceView.Renderer{

    private Triangle mTriangle;

    private int mProgram;

    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "uniform mat4 uMVPMatrix;" +
                    "void main() {" +
                    "gl_Position = uMVPMatrix * vPosition;" +
                    "}";
    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "uniform vec4 vColor;" +
                    "void main() {" +
                    "gl_FragColor = vColor;" +
                    "}";
    private float[] mVMatrix;
    private float[] mProjMatrix;
    private float[] mMVPMatrix;
    private float[] mRotationMatrix;


    public static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //设置背景的颜色
        GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        mTriangle = new Triangle();
        mVMatrix = new float[16];
        mProjMatrix = new float[16];
        mMVPMatrix = new float[16];
        mRotationMatrix = new float[16];
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        float ratio = (float) width / height;

        //此投影矩阵在onDrawFrame()中将应用到对象的坐标
        Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // 重绘背景色
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        //设置相机的位置（视口矩阵）
        Matrix.setLookAtM(mVMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        //设置投影和视口变换
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);

        //旋转变换
        long time = SystemClock.uptimeMillis() % 4000L;
        float angle = 0.090f * ((int) time);
        Matrix.setRotateM(mRotationMatrix, 0, angle, 0, 0, -1.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mRotationMatrix, 0, mMVPMatrix, 0);

        //绘制
        mTriangle.draw(mMVPMatrix);


    }

    class Triangle {

        private FloatBuffer vertexBuffer;

        //顶点坐标数
        static final int COORDS_PER_VERTEX = 3;
        float triangleCoords[] = {
                0.0f, 0.622008459f, 0.0f, //top
                -0.5f, -0.311f, 0.0f, //bottom left
                0.5f, -0.311f, 0.0f  //bottom right
        };

        //设置颜色，RGBA
        float color[] = {0.636f, 0.769f, 0.222f, 1.0f};

        public Triangle() {
            //存放形状的坐标，初始化顶点字节缓冲
            ByteBuffer bb = ByteBuffer.allocateDirect(triangleCoords.length * 4);
            //设置字节顺序，ByteOrder.nativeOrder()是获取本机字节顺序
            bb.order(ByteOrder.nativeOrder());
            vertexBuffer = bb.asFloatBuffer();
            //把坐标加入到FloatBuffer中
            vertexBuffer.put(triangleCoords);
            //设置buffer,从第一个坐标开始读
            vertexBuffer.position(0);

            int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
            int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

            mProgram = GLES20.glCreateProgram();
            GLES20.glAttachShader(mProgram, vertexShader);
            GLES20.glAttachShader(mProgram, fragmentShader);
            GLES20.glLinkProgram(mProgram);
        }

        public void draw(float[] mvpMatrix) {
            GLES20.glUseProgram(mProgram);
            //获取指向vertex shander 的成员vPosition的handle
            int mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

            //启用一个指向三角形的顶点数组的handle
            GLES20.glEnableVertexAttribArray(mPositionHandle);

            //准备三角形的坐标数据
            GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false, 0, vertexBuffer);

            int mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
            GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);

            int mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
            //设置三角形颜色
            GLES20.glUniform4fv(mColorHandle, 1, color, 0);


            //画三角形
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);

            //禁用指向三角形的顶点数组
            GLES20.glDisableVertexAttribArray(mPositionHandle);

        }

    }

}
