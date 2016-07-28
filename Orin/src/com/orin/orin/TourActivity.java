/**
 * @author Ry
 * @date 2013.12.30
 * @filename TourActivity.java
 */

package com.orin.orin;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.orin.orin.viewpager_indicator.TourFragmentAdapter;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

import java.util.TimerTask;

public class TourActivity extends FragmentActivity {

    private static final String TAG = TourActivity.class.getSimpleName();

    private TourFragmentAdapter mAdapter;

    private ViewPager mPager;
    private PageIndicator mIndicator;

    private Handler mHandler = new Handler();
    private Runnable mRunnable;

    private int mCurPage;
    private boolean mIsTourFinished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour);

        mAdapter = new TourFragmentAdapter(getSupportFragmentManager());

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);

        //We set this on the indicator, NOT the pager
        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                Log.i(TAG, "Changed to page " + position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mCurPage = 0;
        final int pageCount = mAdapter.getCount();

        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (mCurPage < pageCount) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mPager.setCurrentItem(mCurPage++, true);
                        }
                    });

                    mHandler.postDelayed(mRunnable, 4000);
                } else {
                    mIsTourFinished = true;
                }
            }
        };
        mRunnable.run();
    }

    @Override
    public void onBackPressed() {
        if (mIsTourFinished) {
            super.onBackPressed();
            overridePendingTransition(R.anim.pop_in, R.anim.pop_out);
        }
    }

}
