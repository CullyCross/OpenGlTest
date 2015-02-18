package cullycross.airhockeygame;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;


public class AirHockeyActivity extends ActionBarActivity {

    private GLSurfaceView mGLSurfaceView;
    private boolean mRenderSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGLSurfaceView = new GLSurfaceView(this);
        final ActivityManager activityManager =
                (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo =
                activityManager.getDeviceConfigurationInfo();
        final boolean supportEs2 =
                configurationInfo.reqGlEsVersion >= 0x20000;
        final AirHockeyRenderer airHockeyRenderer =
                new AirHockeyRenderer(this);


        if(supportEs2) {
            //Request an Open ES 2.0 compatible context.
            mGLSurfaceView.setEGLContextClientVersion(2);

            //Assign our renderer.
            mGLSurfaceView.setRenderer(airHockeyRenderer);
            mRenderSet = true;
        } else {
            Toast.makeText(this, "This device does not support OpenGL ES 2.0.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        mGLSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event != null) {
                    final float normalizedX =
                            (event.getX() / (float) v.getWidth()) * 2 - 1;
                    final float normalizedY =
                            -((event.getY() / (float) v.getHeight()) * 2 - 1);

                    if(event.getAction() == MotionEvent.ACTION_DOWN) {
                        mGLSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                airHockeyRenderer.handleTouchPress(
                                    normalizedX, normalizedY
                                );
                            }
                        });
                    } else if(event.getAction() == MotionEvent.ACTION_MOVE) {
                        mGLSurfaceView.queueEvent(new Runnable() {
                            @Override
                            public void run() {
                                airHockeyRenderer.handleTouchDrag(
                                    normalizedX, normalizedY
                                );
                            }
                        });
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });

        setContentView(mGLSurfaceView);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mRenderSet) {
            mGLSurfaceView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(mRenderSet) {
            mGLSurfaceView.onPause();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
