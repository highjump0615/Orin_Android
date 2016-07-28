/**
 * @author Ry
 * @date 2013.12.27
 * @filename BadgesFragment.java
 */

package com.orin.orin.fragments;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orin.orin.R;
import com.orin.orin.util.Config;
import com.orin.orin.util.Utils;

import java.util.ArrayList;

public class BadgesFragment extends Fragment {

    private static final String TAG = BadgesFragment.class.getSimpleName();

    private FragmentActivity mActivity;

    private RelativeLayout mBadgeContainer;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (FragmentActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_badges, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        assert view != null;

        view.setLayoutParams(params);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        initBadgeView();
    }

    private void initViews(View view) {
        mBadgeContainer = (RelativeLayout) view.findViewById(R.id.achievements_container);
    }

    private void initBadgeView() {
        ArrayList<String> badges = Config.getBadges(Config.mUserProfile);

        if (Config.loadShareOption(mActivity)) {
            int nIndex = 0;

            final Resources res = mActivity.getResources();
            int width = (int) (res.getDimension(R.dimen.profile_activity_image_badge_size) * Config.mScaleFactor);
            int height1 = (int) (res.getDimension(R.dimen.profile_activity_image_badge_size) * Config.mScaleFactor);
            int height2 = (int) (res.getDimension(R.dimen.profile_activity_text_badge_height) * Config.mScaleFactor);
            int horizontal_gap = (int) (res.getDimension(R.dimen.profile_activity_badge_layout_horizontal_gap_size) * Config.mScaleFactor);
            int vertical_gap = (int) (res.getDimension(R.dimen.profile_activity_badge_layout_vertical_gap_size) * Config.mScaleFactor);

            float textSize = res.getDimension(R.dimen.profile_activity_text_badge_text_size) * Config.mFontScaleFactor;

            for (String strImage : badges) {
                int resId = res.getIdentifier(strImage, "drawable", mActivity.getPackageName());

                LinearLayout layout = new LinearLayout(mActivity);
                layout.setOrientation(LinearLayout.VERTICAL);

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height1);
                ImageView imageView = new ImageView(mActivity);
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView.setBackgroundResource(resId);
                imageView.setLayoutParams(params);
                layout.addView(imageView);

                params = new RelativeLayout.LayoutParams(width, height2);
                TextView textBadge = new TextView(mActivity);
                textBadge.setLayoutParams(params);
                textBadge.setGravity(Gravity.CENTER);
                textBadge.setTextColor(Color.BLACK);
                textBadge.setTextSize(textSize);
                textBadge.setText(Config.mBadgeMap.get(strImage));
                layout.addView(textBadge);

                int nRow = nIndex / 3;
                int nCol = nIndex % 3;

                if (Utils.isTablet(getActivity())) {
                    nRow = nIndex / 6;
                    nCol = nIndex % 6;
                }

                int x = horizontal_gap + nCol * (horizontal_gap + width);
                int y = vertical_gap + nRow * (height1 + height2 + vertical_gap);

                Log.i(TAG, "x=" + x + ", y=" + y);

                params = new RelativeLayout.LayoutParams(width, height1 + height2 + vertical_gap);
                params.setMargins(x, y, 0, 0);
                layout.setLayoutParams(params);

                mBadgeContainer.addView(layout);

                nIndex++;
            }
        }
    }

}
