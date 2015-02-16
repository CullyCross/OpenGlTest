package cullycross.airhockeygame;

import android.content.Context;
import android.opengl.GLSurfaceView;

import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.Matrix.*;

import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cullycross.airhockeygame.utils.MatrixHelper;
import cullycross.airhockeygame.utils.ShaderHelper;
import cullycross.airhockeygame.utils.TextResourceReader;

/**
 * Created by cullycross on 2/14/15.
 */
public class AirHockeyRenderer implements GLSurfaceView.Renderer {

    private static final int POSITION_COMPONENT_COUNT = 4;
    private static final int BYTES_PER_FLOAT = 4;

    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;

    private final Context mContext;

    private final FloatBuffer mVertexData;

    private String mVertexShaderSource;
    private String mFragmentShaderSource;

    private int mProgram;

    private static final String A_COLOR = "a_Color";
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int STRIDE =
            (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;

    private int aColorLocation;

    private static final String U_MATRIX = "u_Matrix";
    private final float [] mProjectionMatrix = new float[16];
    private int uMatrixLocation;

    private final float [] mModelMatrix = new float[16];

    public AirHockeyRenderer(Context context) {

        this.mContext = context;

        float [] tableVertices = {

            //first triangle
            -0.51f, -0.81f, 0f, 1f, 0f, 1f, 0f,
             0.51f,  0.81f, 0f, 2f, 0f, 1f, 0f,
            -0.51f,  0.81f, 0f, 2f, 0f, 1f, 0f,

            //second triangle
            -0.51f, -0.81f, 0f, 1f, 0f, 1f, 0f,
             0.51f, -0.81f, 0f, 1f, 0f, 1f, 0f,
             0.51f,  0.81f, 0f, 2f, 0f, 1f, 0f,

            // Triangle Fan
               0f,    0f,   0f, 1.5f,     1f,   1f,   1f,
            -0.5f, -0.8f,   0f,   1f,   0.7f, 0.7f, 0.7f,
             0.5f, -0.8f,   0f,   1f,   0.7f, 0.7f, 0.7f,
             0.5f,  0.8f,   0f,   2f,   0.7f, 0.7f, 0.7f,
            -0.5f,  0.8f,   0f,   2f,   0.7f, 0.7f, 0.7f,
            -0.5f, -0.8f,   0f,   1f,   0.7f, 0.7f, 0.7f,

            //line 1
            -0.5f, 0f, 0f, 1.5f, 1f, 0f, 0f,
             0.5f, 0f, 0f, 1.5f, 1f, 0f, 0f,

            //mallets
            0f, -0.4f, 0f, 1.25f, 0f, 0f, 1f,
            0f,  0.4f, 0f, 1.75f, 1f, 0f, 0f,

            //puck
            0f, 0f, 0f, 1.5f, 0f, 1f, 0f
        };

        mVertexData = ByteBuffer
                .allocateDirect(tableVertices.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        mVertexData.put(tableVertices);

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        mVertexShaderSource = TextResourceReader
                .readTextFileFromResource(mContext, R.raw.simple_vertex_shader);

        mFragmentShaderSource = TextResourceReader
                .readTextFileFromResource(mContext, R.raw.simple_fragment_shader);

        int vertexShader = ShaderHelper
                .compileVertexShader(mVertexShaderSource);
        int fragmentShader = ShaderHelper
                .compileFragmentShader(mFragmentShaderSource);

        mProgram = ShaderHelper.linkProgram(vertexShader, fragmentShader);

        glUseProgram(mProgram);

        aColorLocation = glGetAttribLocation(mProgram, A_COLOR);
        aPositionLocation = glGetAttribLocation(mProgram, A_POSITION);

        mVertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT,
                GL_FLOAT, false, STRIDE, mVertexData);
        glEnableVertexAttribArray(aPositionLocation);

        mVertexData.position(POSITION_COMPONENT_COUNT);
        glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT,
                false, STRIDE, mVertexData);
        glEnableVertexAttribArray(aColorLocation);

        uMatrixLocation = glGetUniformLocation(mProgram, U_MATRIX);


    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0 ,0 , width, height);

        MatrixHelper.perspectiveM(mProjectionMatrix, 45,
                (float) width / (float) height, 1f, 10f);

        setIdentityM(mModelMatrix, 0);
        translateM(mModelMatrix, 0, 0f, 0f, -2.5f);
        rotateM(mModelMatrix, 0, -60f, 1f, 0f, 0f);

        final float [] temp = new float[16];
        multiplyMM(temp, 0, mProjectionMatrix,
                0, mModelMatrix, 0);
        System.arraycopy(temp, 0,
                mProjectionMatrix, 0, temp.length);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);
        glUniformMatrix4fv(uMatrixLocation, 1, false, mProjectionMatrix, 0);

        glDrawArrays(GL_TRIANGLES, 0, 6);

        glDrawArrays(GL_TRIANGLE_FAN, 6, 6);

        glDrawArrays(GL_LINES, 12, 2);

        glDrawArrays(GL_POINTS, 14, 1);

        glDrawArrays(GL_POINTS, 15, 1);

        glDrawArrays(GL_POINTS, 16, 1);

    }
}
