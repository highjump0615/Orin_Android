/**
 * @author Ry
 * @date 2013.12.27
 * @filename MyProfileFragment.java
 */

package com.orin.orin.fragments;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orin.orin.EditProfileActivity;
import com.orin.orin.R;
import com.orin.orin.data.UserProfile;
import com.orin.orin.util.Config;
import com.orin.orin.util.Utils;

import java.util.ArrayList;

public class MyProfileFragment extends Fragment {

    private static final String TAG = MyProfileFragment.class.getSimpleName();

    private FragmentActivity mActivity;

    private ImageView mImagePhoto;
    private TextView mTextBadges;
    private TextView mTextFollowers;
    private TextView mTextFollowing;

    private TextView mTextFullName;
    private TextView mTextAboutMe;
    private TextView mTextWebSite;
    private RelativeLayout mBadgeContainer;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (FragmentActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_my_profile, null);
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

    @Override
    public void onResume() {
        super.onResume();
        refreshView();
    }

    private void initViews(View view) {
        final Resources res = mActivity.getResources();

        // EditProfile Button
        Button button = (Button) view.findViewById(R.id.btn_edit_profile);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, EditProfileActivity.class);
                mActivity.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
            }
        });

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) button.getLayoutParams();
        layoutParams.width = (int) (res.getDimension(R.dimen.profile_activity_button_width) * Config.mScaleFactor);
        layoutParams.height = (int) (res.getDimension(R.dimen.profile_activity_button_height) * Config.mScaleFactor);
        float textSize = res.getDimension(R.dimen.profile_activity_button_text_size) * Config.mFontScaleFactor;
        button.setLayoutParams(layoutParams);
        button.setTextSize(textSize);

        LinearLayout layout = (LinearLayout) view.findViewById(R.id.layout_button);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
        params.topMargin = (int) (res.getDimension(R.dimen.profile_activity_button_margin_top) * Config.mScaleFactor);
        layout.setLayoutParams(params);

        // Info Layout
        RelativeLayout relativeLayout = (RelativeLayout) view.findViewById(R.id.layout_info);
        relativeLayout.setPadding((int) (res.getDimension(R.dimen.profile_activity_photo_margin_left) * Config.mScaleFactor),
                (int) (res.getDimension(R.dimen.profile_activity_photo_margin_top) * Config.mScaleFactor),
                (int) (res.getDimension(R.dimen.profile_activity_photo_margin_left) * Config.mScaleFactor), 0);

        // User Photo ImageView
        mImagePhoto = (ImageView) view.findViewById(R.id.img_photo);
        params = (RelativeLayout.LayoutParams) mImagePhoto.getLayoutParams();
        params.height = params.width = (int) (res.getDimension(R.dimen.profile_activity_photo_size) * Config.mScaleFactor);
        mImagePhoto.setLayoutParams(params);

        // Value Layout
        layout = (LinearLayout) view.findViewById(R.id.layout_value);
        params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
        params.height = (int) (res.getDimension(R.dimen.profile_activity_text_view_height) * Config.mScaleFactor);
        params.leftMargin = (int) (res.getDimension(R.dimen.profile_activity_text_badges_margin_left) * Config.mScaleFactor);
        params.topMargin = (int) (res.getDimension(R.dimen.profile_activity_value_text_view_margin_top) * Config.mScaleFactor);
        layout.setLayoutParams(params);
        textSize = res.getDimension(R.dimen.profile_activity_value_text_size) * Config.mFontScaleFactor;

        mTextBadges = (TextView) view.findViewById(R.id.text_badges_value);
        mTextBadges.setTextSize(textSize);

        mTextFollowers = (TextView) view.findViewById(R.id.text_followers_value);
        mTextFollowers.setTextSize(textSize);

        mTextFollowing = (TextView) view.findViewById(R.id.text_following_value);
        mTextFollowing.setTextSize(textSize);

        // Label Layout
        layout = (LinearLayout) view.findViewById(R.id.layout_value_label);
        params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
        params.height = (int) (res.getDimension(R.dimen.profile_activity_text_view_height) * Config.mScaleFactor);
        layout.setLayoutParams(params);
        textSize = res.getDimension(R.dimen.profile_activity_label_text_size) * Config.mFontScaleFactor;

        TextView textView = (TextView) layout.getChildAt(0);
        textView.setTextSize(textSize);

        textView = (TextView) layout.getChildAt(1);
        textView.setTextSize(textSize);

        textView = (TextView) layout.getChildAt(2);
        textView.setTextSize(textSize);

        // Privacy layout
        layout = (LinearLayout) view.findViewById(R.id.layout_privacy);
        params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
        params.topMargin = (int) (res.getDimension(R.dimen.profile_activity_gap_height) * Config.mScaleFactor);
        params.bottomMargin = (int) (res.getDimension(R.dimen.profile_activity_gap_height) * Config.mScaleFactor);
        layout.setLayoutParams(params);

        mTextFullName = (TextView) view.findViewById(R.id.text_full_name);
        layoutParams = (LinearLayout.LayoutParams) mTextFullName.getLayoutParams();
        int height = (int) (res.getDimension(R.dimen.profile_activity_text_view_height) * Config.mScaleFactor);
        layoutParams.height = height;
        mTextFullName.setLayoutParams(layoutParams);
        mTextFullName.setTextSize(textSize);

        mTextAboutMe = (TextView) view.findViewById(R.id.text_about_me);
        layoutParams = (LinearLayout.LayoutParams) mTextAboutMe.getLayoutParams();
        layoutParams.height = height;
        mTextAboutMe.setLayoutParams(layoutParams);
        mTextAboutMe.setTextSize(textSize);

        mTextWebSite = (TextView) view.findViewById(R.id.text_website);
        layoutParams = (LinearLayout.LayoutParams) mTextWebSite.getLayoutParams();
        layoutParams.height = height;
        mTextWebSite.setLayoutParams(layoutParams);
        mTextWebSite.setTextSize(textSize);

        // line View
        View lineView = view.findViewById(R.id.profile_line);
        params = (RelativeLayout.LayoutParams) lineView.getLayoutParams();
        params.topMargin = (int) (res.getDimension(R.dimen.profile_activity_line_margin_top) * Config.mScaleFactor);
        lineView.setLayoutParams(params);

        // My Achievement TextView
        textView = (TextView) view.findViewById(R.id.text_my_achievement);
        params = (RelativeLayout.LayoutParams) textView.getLayoutParams();
        params.height = (int) (res.getDimension(R.dimen.profile_activity_text_my_achievements_height) * Config.mScaleFactor);
        textView.setLayoutParams(params);
        textView.setTextSize(res.getDimension(R.dimen.profile_activity_text_my_achievements_text_size) * Config.mFontScaleFactor);

        mBadgeContainer = (RelativeLayout) view.findViewById(R.id.achievements_container);
    }

    private void initBadgeView() {
        ArrayList<String> badges = Config.getBadges(Config.mUserProfile);
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

        mTextBadges.setText(String.valueOf(nIndex));
    }

    private void refreshView() {
        UserProfile profile = Config.mUserProfile;

        mTextFullName.setText(profile.fullName);
        mTextAboutMe.setText(profile.aboutme);
        mTextWebSite.setText(profile.website);

        mTextFollowers.setText(String.valueOf(profile.followers));
        mTextFollowing.setText(String.valueOf(profile.following));

        if (profile.imageProfile != null)
            mImagePhoto.setImageBitmap(Utils.getCroppedBitmap(profile.imageProfile, 100));
        else
            mImagePhoto.setBackgroundResource(R.drawable.unknown_character);
    }

}
