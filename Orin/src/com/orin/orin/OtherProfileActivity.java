/**
 * @author Ry
 * @date 2014.01.03
 * @filename OtherProfileActivity.java
 */

package com.orin.orin;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orin.orin.api.API_Manager;
import com.orin.orin.api.ServiceResponse;
import com.orin.orin.data.UserProfile;
import com.orin.orin.util.Config;
import com.orin.orin.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class OtherProfileActivity extends Activity implements API_Manager.RunningServiceListener {

    public static final String TAG = OtherProfileActivity.class.getSimpleName();

    private Button mBtnFollow;

    private ImageView mImagePhoto;
    private TextView mTextBadges;
    private TextView mTextFollowers;
    private TextView mTextFollowing;

    private TextView mTextFullName;
    private TextView mTextAboutMe;
    private TextView mTextWebSite;
    private RelativeLayout mBadgeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_other_profile);

        checkFollowing();
        initViews();
        initBadgeView();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pop_in, R.anim.pop_out);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshView();
    }

    private void checkFollowing() {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("name", Config.mUserProfile.userName);
        params.put("following", SearchUserActivity.mUserProfile.userName);
        API_Manager.getInstance().runWebService(API_Manager.CHECK_FOLLOWING_ACTION, params, this, true);
    }

    private void initViews() {
        final Resources res = getResources();

        // TitleBar
        RelativeLayout titleBar = (RelativeLayout) findViewById(R.id.title_bar);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
        params.height = (int) (res.getDimension(R.dimen.title_bar_height) * Config.mScaleFactor);
        titleBar.setLayoutParams(params);

        // Back Button
        Button btnBack = (Button) findViewById(R.id.btn_cancel);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnBack.setText("Back");
        float textSize = res.getDimension(R.dimen.title_bar_button_text_size) * Config.mFontScaleFactor;
        int padding = (int) (res.getDimension(R.dimen.title_bar_button_padding) * Config.mScaleFactor);
        btnBack.setTextSize(textSize);
        btnBack.setPadding(padding, 0, 0, 0);

        //
        Button button = (Button) findViewById(R.id.btn_done);
        button.setVisibility(View.INVISIBLE);
        button.setTextSize(textSize);
        button.setPadding(0, 0, padding, 0);

        // Title TextView
        TextView textTitle = (TextView) findViewById(R.id.text_title);
        textTitle.setText("Profile");
        textSize = res.getDimension(R.dimen.title_bar_small_text_size) * Config.mFontScaleFactor;
        textTitle.setTextSize(textSize);

        // EditProfile Button
        mBtnFollow = (Button) findViewById(R.id.btn_follow);
        mBtnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("name", Config.mUserProfile.userName);
                params.put("following", SearchUserActivity.mUserProfile.userName);
                API_Manager.getInstance().runWebService(
                        SearchUserActivity.mUserProfile.isFollowed ? API_Manager.UNFOLLOWING_ACTION : API_Manager.FOLLOWING_ACTION,
                        params, OtherProfileActivity.this, true);
            }
        });

        // Follow Button
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mBtnFollow.getLayoutParams();
        layoutParams.width = (int) (res.getDimension(R.dimen.profile_activity_button_width) * Config.mScaleFactor);
        layoutParams.height = (int) (res.getDimension(R.dimen.profile_activity_button_height) * Config.mScaleFactor);
        textSize = res.getDimension(R.dimen.profile_activity_button_text_size) * Config.mFontScaleFactor;
        mBtnFollow.setLayoutParams(layoutParams);
        mBtnFollow.setTextSize(textSize);

        LinearLayout layout = (LinearLayout) findViewById(R.id.layout_button);
        params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
        params.topMargin = (int) (res.getDimension(R.dimen.profile_activity_button_margin_top) * Config.mScaleFactor);
        layout.setLayoutParams(params);

        // Info Layout
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.layout_info);
        relativeLayout.setPadding((int) (res.getDimension(R.dimen.profile_activity_photo_margin_left) * Config.mScaleFactor),
                (int) (res.getDimension(R.dimen.profile_activity_photo_margin_top) * Config.mScaleFactor),
                (int) (res.getDimension(R.dimen.profile_activity_photo_margin_left) * Config.mScaleFactor), 0);

        // User Photo ImageView
        mImagePhoto = (ImageView) findViewById(R.id.img_photo);
        params = (RelativeLayout.LayoutParams) mImagePhoto.getLayoutParams();
        params.height = params.width = (int) (res.getDimension(R.dimen.profile_activity_photo_size) * Config.mScaleFactor);
        mImagePhoto.setLayoutParams(params);

        // Value Layout
        layout = (LinearLayout) findViewById(R.id.layout_value);
        params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
        params.height = (int) (res.getDimension(R.dimen.profile_activity_text_view_height) * Config.mScaleFactor);
        params.leftMargin = (int) (res.getDimension(R.dimen.profile_activity_text_badges_margin_left) * Config.mScaleFactor);
        params.topMargin = (int) (res.getDimension(R.dimen.profile_activity_value_text_view_margin_top) * Config.mScaleFactor);
        layout.setLayoutParams(params);
        textSize = res.getDimension(R.dimen.profile_activity_value_text_size) * Config.mFontScaleFactor;

        mTextBadges = (TextView) findViewById(R.id.text_badges_value);
        mTextBadges.setTextSize(textSize);

        mTextFollowers = (TextView) findViewById(R.id.text_followers_value);
        mTextFollowers.setTextSize(textSize);

        mTextFollowing = (TextView) findViewById(R.id.text_following_value);
        mTextFollowing.setTextSize(textSize);

        // Label Layout
        layout = (LinearLayout) findViewById(R.id.layout_value_label);
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
        layout = (LinearLayout) findViewById(R.id.layout_privacy);
        params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
        params.topMargin = (int) (res.getDimension(R.dimen.profile_activity_gap_height) * Config.mScaleFactor);
        params.bottomMargin = (int) (res.getDimension(R.dimen.profile_activity_gap_height) * Config.mScaleFactor);
        layout.setLayoutParams(params);

        mTextFullName = (TextView) findViewById(R.id.text_full_name);
        layoutParams = (LinearLayout.LayoutParams) mTextFullName.getLayoutParams();
        int height = (int) (res.getDimension(R.dimen.profile_activity_text_view_height) * Config.mScaleFactor);
        layoutParams.height = height;
        mTextFullName.setLayoutParams(layoutParams);
        mTextFullName.setTextSize(textSize);

        mTextAboutMe = (TextView) findViewById(R.id.text_about_me);
        layoutParams = (LinearLayout.LayoutParams) mTextAboutMe.getLayoutParams();
        layoutParams.height = height;
        mTextAboutMe.setLayoutParams(layoutParams);
        mTextAboutMe.setTextSize(textSize);

        mTextWebSite = (TextView) findViewById(R.id.text_website);
        layoutParams = (LinearLayout.LayoutParams) mTextWebSite.getLayoutParams();
        layoutParams.height = height;
        mTextWebSite.setLayoutParams(layoutParams);
        mTextWebSite.setTextSize(textSize);

        // line View
        View lineView = findViewById(R.id.profile_line);
        params = (RelativeLayout.LayoutParams) lineView.getLayoutParams();
        params.topMargin = (int) (res.getDimension(R.dimen.profile_activity_line_margin_top) * Config.mScaleFactor);

        // My Achievement TextView
        textView = (TextView) findViewById(R.id.text_my_achievement);
        params = (RelativeLayout.LayoutParams) textView.getLayoutParams();
        params.height = (int) (res.getDimension(R.dimen.profile_activity_text_my_achievements_height) * Config.mScaleFactor);
        textView.setLayoutParams(params);
        textView.setTextSize(res.getDimension(R.dimen.profile_activity_text_my_achievements_text_size) * Config.mFontScaleFactor);

        mBadgeContainer = (RelativeLayout) findViewById(R.id.achievements_container);
    }

    private void initBadgeView() {
        ArrayList<String> badges = Config.getBadges(SearchUserActivity.mUserProfile);
        int nIndex = 0;

        final Resources res = getResources();
        int width = (int) (res.getDimension(R.dimen.profile_activity_image_badge_size) * Config.mScaleFactor);
        int height1 = (int) (res.getDimension(R.dimen.profile_activity_image_badge_size) * Config.mScaleFactor);
        int height2 = (int) (res.getDimension(R.dimen.profile_activity_text_badge_height) * Config.mScaleFactor);
        int horizontal_gap = (int) (res.getDimension(R.dimen.profile_activity_badge_layout_horizontal_gap_size) * Config.mScaleFactor);
        int vertical_gap = (int) (res.getDimension(R.dimen.profile_activity_badge_layout_vertical_gap_size) * Config.mScaleFactor);

        float textSize = res.getDimension(R.dimen.profile_activity_text_badge_text_size) * Config.mFontScaleFactor;

        for (String strImage : badges) {
            int resId = res.getIdentifier(strImage, "drawable", getPackageName());

            LinearLayout layout = new LinearLayout(this);
            layout.setOrientation(LinearLayout.VERTICAL);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height1);
            ImageView imageView = new ImageView(this);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setBackgroundResource(resId);
            imageView.setLayoutParams(params);
            layout.addView(imageView);

            params = new RelativeLayout.LayoutParams(width, height2);
            TextView textBadge = new TextView(this);
            textBadge.setLayoutParams(params);
            textBadge.setGravity(Gravity.CENTER);
            textBadge.setTextColor(Color.BLACK);
            textBadge.setTextSize(textSize);
            textBadge.setText(Config.mBadgeMap.get(strImage));
            layout.addView(textBadge);

            int nRow = nIndex / 3;
            int nCol = nIndex % 3;

            if (Utils.isTablet(this)) {
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
        UserProfile profile = SearchUserActivity.mUserProfile;

        mTextFullName.setText(profile.fullName);
        mTextAboutMe.setText(profile.aboutme);
        mTextWebSite.setText(profile.website);

        mTextFollowers.setText(String.valueOf(profile.followers));
        mTextFollowing.setText(String.valueOf(profile.following));
        if (profile.imageProfile != null)
            mImagePhoto.setImageBitmap(Utils.getCroppedBitmap(profile.imageProfile, 200));
        else
            mImagePhoto.setBackgroundResource(R.drawable.unknown_character);

        mBtnFollow.setText(profile.isFollowed ? "UnFollow" : "Follow");
    }

    @Override
    public void finishedRunningServiceListener(ServiceResponse response, String result) {
        if (response.serviceName == null) return;

        if (response.serviceName.equals(API_Manager.CHECK_FOLLOWING_ACTION)) {
            if (response.status.equals("OK")) {
                double followValue = (Double) response.info;
                SearchUserActivity.mUserProfile.isFollowed = followValue > 0;
                refreshView();
            }
        }
        else if (response.serviceName.equals(API_Manager.UNFOLLOWING_ACTION)) {
            if (response.status.equals("OK")) {
                SearchUserActivity.mUserProfile.followers--;
                SearchUserActivity.mUserProfile.isFollowed = false;
                refreshView();
            }
        }
        else if (response.serviceName.equals(API_Manager.FOLLOWING_ACTION)) {
            if (response.status.equals("OK")) {
                SearchUserActivity.mUserProfile.followers++;
                SearchUserActivity.mUserProfile.isFollowed = true;
                refreshView();
            }
        }
    }

}
