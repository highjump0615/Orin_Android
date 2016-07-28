/**
 * @author Ry
 * @date 2013.12.12
 * @filename SplashActivity.java
 */

package com.orin.orin;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.orin.orin.util.Config;
import com.orin.orin.util.Utils;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends Activity {

    static final String TAG = SplashActivity.class.getSimpleName();
    public Timer mTransitionTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utils.checkScreen(this);

        Config.calculateScaleFactor(this);

        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
        setContentView(R.layout.activity_splash);
        initViews();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LandingActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                finish();
            }
        };
        mTransitionTimer = new Timer();
        mTransitionTimer.schedule(task, 4000);
    }

    @Override
    protected void onDestroy() {
        if (mTransitionTimer != null) {
            mTransitionTimer.cancel();
            mTransitionTimer = null;
        }

        super.onDestroy();
    }

    private void initViews() {
        final Resources res = getResources();

        // Logo image
        ImageView imageView = (ImageView) findViewById(R.id.img_logo);

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        int width = (int) (res.getDimension(R.dimen.login_logo_size) * Config.mScaleFactor);
        params.height = params.width = width;
        imageView.setLayoutParams(params);
    }

}
