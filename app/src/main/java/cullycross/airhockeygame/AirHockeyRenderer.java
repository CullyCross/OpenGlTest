package cullycross.airhockeygame;

import android.content.Context;
import android.opengl.GLSurfaceView;

import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.Matrix.*;

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
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import cullycross.airhockeygame.objects.Mallet;
import cullycross.airhockeygame.objects.Table;
import cullycross.airhockeygame.programs.ColorShaderProgram;
import cullycross.airhockeygame.programs.TextureShaderProgram;
import cullycross.airhockeygame.utils.MatrixHelper;
import cullycross.airhockeygame.utils.ShaderHelper;
import cullycross.airhockeygame.utils.TextureHelper;

/**
 * Created by cullycross on 2/14/15.
 */
public class AirHockeyRenderer implements GLSurfaceView.Renderer {

    private final Context mContext;

    private final float [] mProjectionMatrix = new float[16];
    private final float [] mModelMatrix = new float[16];

    private Table mTable;
    private Mallet mMallet;

    private TextureShaderProgram mTextureProgram;
    private ColorShaderProgram mColorProgram;

    private int mTexture;

    public AirHockeyRenderer(Context context) {

        this.mContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        mTable = new Table();
        mMallet = new Mallet();

        mTextureProgram = new TextureShaderProgram(mContext);
        mColorProgram = new ColorShaderProgram(mContext);

        mTexture = TextureHelper
                .loadTexture(mContext, R.drawable.air_hockey_surface_512x512);
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

        mTextureProgram.useProgram();
        mTextureProgram.setUniforms(mProjectionMatrix, mTexture);
        mTable.bindData(mTextureProgram);
        mTable.draw();

        mColorProgram.useProgram();
        mColorProgram.setUniforms(mProjectionMatrix);
        mMallet.bindData(mColorProgram);
        mMallet.draw();

    }
}
