package hznu.example.openglestest;

import android.content.Context;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by 1215 on 7/27/2017.
 */

public class TestRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "TestRenderer";

    private int mProgram;
    private Squre mSqure;
    private boolean getFrameData = false;
    private int positionHandle, texCoordHandle, textureYHandle, textureUVHandle;
    private static int textureId = -1;
    int[] id_y = new int[1];
    int[] id_uv = new int[1];
    private FloatBuffer vertexBuffer = null;
    private FloatBuffer textureBuffer = null;
    private ByteBuffer yuvBuffer = null;

    private int frameWidth, frameHeight;

    private final String vertexShaderCode =
            "attribute vec4 vPosition;" +
                    "attribute vec2 aTexCoord;" +
                    "varying vec2 vTextureCoord;" +
                    "void main() {" +
                    "gl_Position = vPosition;" +
                    "vTextureCoord = aTexCoord;" +
                    "}";
    private final String fragmentShaderCode =
            "precision mediump float;" +
                    "varying vec2 vTextureCoord;" +
                    "uniform sampler2D sTexture;" +
                    "void main() {" +
                    "vec2 flipped_texcoord = vec2(vTextureCoord.x, 1.0 - vTextureCoord.y);" +
                    "gl_FragColor = texture2D(sTexture, flipped_texcoord);" + //贴图相反
                    "}";


    private final String fragmentShaderCode2 =
            "precision mediump float;" +
                    "uniform sampler2D mGLUniformTexture;" +
                    "uniform sampler2D mGLUniformTexture1;" +
                    "varying highp vec2 vTextureCoord;" +
                    "const mat3 yuv2rgb = mat3(" +
                    "1, 0, 1.2802," +
                    "1, -0.214821, -0.380589," +
                    "1, 2.127982, 0" +
                    ");" +
                    "void main() {" +
                    "vec2 flippedTexCoord = vec2(1.0 - vTextureCoord.x ,vTextureCoord.y);" +
                    "vec3 yuv = vec3(" +
                    "1.1643 * (texture2D(mGLUniformTexture, flippedTexCoord).r - 0.0625)," +
                    "texture2D(mGLUniformTexture1, flippedTexCoord).a - 0.5," +
                    "texture2D(mGLUniformTexture1, flippedTexCoord).r - 0.5" +
                    ");" +
                    "vec3 rgb = yuv * yuv2rgb;" +
                    "gl_FragColor = vec4(rgb, 1);" +
                    "}";

    public TestRenderer() {

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //背景颜色
        GLES20.glClearColor(0.1f, 0.5f, 0.5f, 0.5f);
        mSqure = new Squre();

        //启动纹理
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        mProgram = OpenGLUtils.loadProgram(vertexShaderCode, fragmentShaderCode2);

        //获取指向vertex shander 的成员vPosition的handle
        positionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        texCoordHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoord");
        textureYHandle = GLES20.glGetUniformLocation(mProgram, "mGLUniformTexture");
        textureUVHandle = GLES20.glGetUniformLocation(mProgram, "mGLUniformTexture1");

        GLES20.glUseProgram(mProgram);

        GLES20.glUniform1i(textureYHandle, 0);
        GLES20.glUniform1i(textureUVHandle, 1);

        //启用一个指向三角形的顶点数组的handle
        GLES20.glEnableVertexAttribArray(positionHandle);
        GLES20.glEnableVertexAttribArray(texCoordHandle);

        //准备三角形的坐标数据
        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 3 * 4, vertexBuffer);
        GLES20.glVertexAttribPointer(texCoordHandle, 2, GLES20.GL_FLOAT, false, 2 * 4, textureBuffer);

        //创建纹理
        TextureUtils.createTexture(320, 240, GLES20.GL_LUMINANCE, id_y);
        TextureUtils.createTexture(320, 240, GLES20.GL_LUMINANCE_ALPHA, id_uv);

//        InputStream ins = null;
//        try {
//            ins = mContext.getAssets().open("luna.jpg");
//            textureId = TextureUtils.loadTexture(ins);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        // 重绘背景色
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        if (yuvBuffer != null) {
            yuvBuffer.position(0);
            yuvBuffer.limit(frameWidth * frameHeight - 1);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id_y[0]);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE, frameWidth, frameHeight, 0, GLES20.GL_LUMINANCE, GLES20.GL_UNSIGNED_BYTE, yuvBuffer);


            yuvBuffer.limit(frameWidth * frameHeight * 3 / 2 - 1);
            yuvBuffer.position(frameWidth * frameHeight);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id_uv[0]);
            GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_LUMINANCE_ALPHA, frameWidth / 2, frameHeight / 2, 0, GLES20.GL_LUMINANCE_ALPHA, GLES20.GL_UNSIGNED_BYTE, yuvBuffer);

            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        }
    }

    public void changeYUVFrame(byte[] data, int width, int height) {
        frameWidth = width;
        frameHeight = height;

        yuvBuffer = ByteBuffer.allocateDirect(width * height * 3 / 2);
        yuvBuffer.order(ByteOrder.nativeOrder());
        yuvBuffer.put(data);
    }

    //方形 屏幕四个点

    class Squre {

        float squreCoords[] = {
                -0.5f, 0.5f, 0.0f, //top left
                0.5f, 0.5f, 0.0f, //top right
                -0.5f, -0.5f, 0.0f, //bottom left

//                0.5f, 0.5f, 0.0f, //top right
//                -0.5f, -0.5f, 0.0f, //bottom left
                0.5f, -0.5f, 0.0f  //bottom right
        };

        //纹理空间坐标S,T
        float texCoords[] = {
                1.0f, 1.0f,
                1.0f, 0.0f,
                0.0f, 1.0f,
                0.0f, 0.0f,
        };

        //设置颜色，RGBA
        float color[] = {0.1f, 0.5f, 0.5f, 0.5f};

        public Squre() {
            //存放形状的坐标，初始化顶点字节缓冲
            ByteBuffer bb = ByteBuffer.allocateDirect(squreCoords.length * 4);
            //设置字节顺序，ByteOrder.nativeOrder()是获取本机字节顺序
            bb.order(ByteOrder.nativeOrder());
            vertexBuffer = bb.asFloatBuffer();
            //把坐标加入到FloatBuffer中
            vertexBuffer.put(squreCoords);
            //设置buffer,从第一个坐标开始读
            vertexBuffer.position(0);

            //纹理坐标
            ByteBuffer cb = ByteBuffer.allocateDirect(texCoords.length * 4);
            cb.order(ByteOrder.nativeOrder());
            textureBuffer = cb.asFloatBuffer();
            textureBuffer.put(texCoords);
            textureBuffer.position(0);
        }

        public void draw() {

            //画正方形
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
            //GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 1, 3);

            //禁用指向三角形的顶点数组
            GLES20.glDisableVertexAttribArray(positionHandle);

        }

    }


}
