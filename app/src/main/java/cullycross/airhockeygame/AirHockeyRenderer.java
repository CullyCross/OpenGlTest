package cullycross.airhockeygame;

import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by cullycross on 2/14/15.
 */
public class AirHockeyRenderer implements GLSurfaceView.Renderer {

    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int BYTES_PER_FLOAT = 4;

    private final FloatBuffer mVertexData;

    public AirHockeyRenderer() {

        float [] tableVertices = {

            //first triangle
            0f, 0f,
            9f, 14f,
            0f, 14f,

            //second triangle
            0f, 0f,
            9f, 0f,
            9f, 14f,

            //line 1
            0f, 7f,
            9f, 7f,

            //mallets
            4.5f, 2f,
            4.5f, 12f
        };

        mVertexData = ByteBuffer
                .allocateDirect(tableVertices.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mVertexData.put(tableVertices);

    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(1.0f, 0.0f, 0.0f, 0.0f);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0 ,0 , width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);
    }
}
