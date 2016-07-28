/**
 * @author Ry
 * @date 2013.12.12
 * @filename MainActivity.java
 */

package com.orin.orin;

import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.orin.orin.api.API_Manager;
import com.orin.orin.fragments.BadgesFragment;
import com.orin.orin.fragments.ChartsFragment;
import com.orin.orin.fragments.FindFriendsFragment;
import com.orin.orin.fragments.MyProfileFragment;
import com.orin.orin.fragments.SettingsFragment;
import com.orin.orin.fragments.VideosFragment;
import com.orin.orin.gcm.CommonUtilities;
import com.orin.orin.gcm.MessageHandlerReceiver;
import com.orin.orin.util.Config;
import com.orin.orin.util.Utils;

public class MainActivity extends FragmentActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String CURRENT_ACTION = "current_action";

    private SlidingMenu menu;
    private TextView mTextTitle;

    private RelativeLayout mLayoutContent = null;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    private RelativeLayout mPhotoLayout;
    private TextView mTextUserName;

    public int mAction = StaticMainClass.CHARTS;

    MessageHandlerReceiver mHandleMessageReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((savedInstanceState != null) && savedInstanceState.containsKey(CURRENT_ACTION)) {
            mAction = savedInstanceState.getInt(CURRENT_ACTION);
        }

        Config.saveLoginState(this, true);
        mHandleMessageReceiver = new MessageHandlerReceiver();
        registerReceiver(mHandleMessageReceiver, new IntentFilter(CommonUtilities.DISPLAY_MESSAGE_ACTION));

        setContentView(R.layout.activity_main);

        initTitleBar();
        initSlidingMenu(savedInstanceState);

        mLayoutContent = (RelativeLayout) findViewById(R.id.layout_content);
        mFragmentManager = getSupportFragmentManager();
        onNavigate();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy()");
        unregisterReceiver(mHandleMessageReceiver);
        //GCMRegistrar.onDestroy(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (StaticMainClass.prevClass == -1) {
            moveTaskToBack(false);
        } else {
            mAction = StaticMainClass.prevClass;
            StaticMainClass.prevClass = -1;
            onNavigate();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        ImageView imagePhoto = (ImageView) mPhotoLayout.getChildAt(0);
        if (imagePhoto != null && Config.mUserProfile.imageProfile != null)
            imagePhoto.setImageBitmap(Utils.getCroppedBitmap(Config.mUserProfile.imageProfile, 200));

        mTextUserName.setText(Config.mUserProfile.userName);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("menu", menu.isMenuShowing());
        outState.putInt(CURRENT_ACTION, mAction);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.user_image_frame:
                mAction = StaticMainClass.MY_PROFILE;
                menu.toggle();
                onNavigate();
                break;

            case R.id.btn_menu:
                menu.toggle(true);
                break;

            case R.id.btn_charts:
                mAction = StaticMainClass.CHARTS;
                menu.toggle(true);
                onNavigate();
                break;

            case R.id.btn_videos:
                mAction = StaticMainClass.MUSIC_VIDEOS;
                menu.toggle(true);
                onNavigate();
                break;

            case R.id.btn_badges:
                mAction = StaticMainClass.BADGES;
                menu.toggle(true);
                onNavigate();
                break;

            case R.id.btn_friends:
                mAction = StaticMainClass.FIND_FRIENDS;
                menu.toggle(true);
                onNavigate();
                break;

            case R.id.btn_settings:
                mAction = StaticMainClass.SETTINGS;
                menu.toggle(true);
                onNavigate();
                break;
        }
    }

    /**
     * When users click an item on slide menu, go to selected screen.
     */
    public void onNavigate() {
        if (mAction == StaticMainClass.mainClass) return;

        switch (mAction) {
            case StaticMainClass.MY_PROFILE:
                onMyProfile();
                break;

            case StaticMainClass.CHARTS:
                onCharts();
                break;

            case StaticMainClass.MUSIC_VIDEOS:
                onMusicVideos();
                break;

            case StaticMainClass.BADGES:
                onBadges();
                break;

            case StaticMainClass.FIND_FRIENDS:
                onFindFriends();
                break;

            case StaticMainClass.SETTINGS:
                onSettings();
                break;
        }

        if (mAction >= 0 && mAction <= StaticMainClass.MENU_ITEMS.length)
            mTextTitle.setText(StaticMainClass.MENU_ITEMS[mAction]);

        StaticMainClass.mainClass = mAction;
    }

    /**
     * Go to My Profile screen
     */
    private void onMyProfile() {
        StaticMainClass.prevClass = -1;
        mLayoutContent.removeAllViews();

        MyProfileFragment myProfileFragment = new MyProfileFragment();

        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(mLayoutContent.getId(), myProfileFragment).show(myProfileFragment);
        mFragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * Go to Charts screen
     */
    private void onCharts() {
        StaticMainClass.prevClass = -1;
        mLayoutContent.removeAllViews();

        ChartsFragment chartsFragment = new ChartsFragment();

        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(mLayoutContent.getId(), chartsFragment).show(chartsFragment);
        mFragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * Go to Music Videos screen
     */
    private void onMusicVideos() {
        StaticMainClass.prevClass = -1;
        mLayoutContent.removeAllViews();

        VideosFragment videosFragment = new VideosFragment();

        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(mLayoutContent.getId(), videosFragment).show(videosFragment);
        mFragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * Go to Badges screen
     */
    private void onBadges() {
        StaticMainClass.prevClass = -1;
        mLayoutContent.removeAllViews();

        BadgesFragment badgesFragment = new BadgesFragment();

        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(mLayoutContent.getId(), badgesFragment).show(badgesFragment);
        mFragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * Go to Find Friends screen
     */
    private void onFindFriends() {
        StaticMainClass.prevClass = -1;
        mLayoutContent.removeAllViews();

        FindFriendsFragment findFriendsFragment = new FindFriendsFragment();

        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(mLayoutContent.getId(), findFriendsFragment).show(findFriendsFragment);
        mFragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * Go to Settings screen
     */
    private void onSettings() {
        StaticMainClass.prevClass = -1;
        mLayoutContent.removeAllViews();

        SettingsFragment settingsFragment = new SettingsFragment();

        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(mLayoutContent.getId(), settingsFragment).show(settingsFragment);
        mFragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * Initialize TitleBar
     */
    private void initTitleBar() {
        final Resources res = getResources();

        int size = (int) (res.getDimension(R.dimen.title_bar_height) * Config.mScaleFactor);
        int margin = (int) (res.getDimension(R.dimen.title_bar_margin) * Config.mScaleFactor);
        float textSize = res.getDimension(R.dimen.title_bar_text_size) * Config.mFontScaleFactor;

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout_title_bar);
        ViewGroup.LayoutParams layoutParams = layout.getLayoutParams();
        layoutParams.height = size;
        layout.setLayoutParams(layoutParams);

        // Menu Image
        ImageView imageView = (ImageView) findViewById(R.id.btn_menu);
        imageView.setOnClickListener(this);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        params.width = params.height = size;
        params.leftMargin = margin;
        imageView.setLayoutParams(params);

        // Logo ImageView
        imageView = (ImageView) findViewById(R.id.img_logo);
        params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        params.width = params.height = size;
        imageView.setLayoutParams(params);

        // Title TextView
        mTextTitle = (TextView) findViewById(R.id.text_title);
        mTextTitle.setTextSize(textSize);
    }

    /**
     * Initialize SlidingMenu
     */
    private void initSlidingMenu(Bundle restore) {
        final Resources res = getResources();
        int shadow_width = (int) (res.getDimension(R.dimen.shadow_width) * Config.mScaleFactor);
        int sliding_menu_offset = (int) (res.getDimension(R.dimen.sliding_menu_offset) * Config.mScaleFactor);

        menu = new SlidingMenu(this);
        menu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
        menu.setMode(SlidingMenu.LEFT);
        menu.setSlidingEnabled(false);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        //menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowWidth(shadow_width);
        menu.setShadowDrawable(R.drawable.menu_shadow);
        //menu.setBehindOffsetRes(R.dimen.sliding_menu_offset);
        menu.setBehindOffset(sliding_menu_offset);
        menu.setFadeDegree(0.35f);
        menu.setMenu(R.layout.sliding_menu);

        // Avatar button
        mPhotoLayout = (RelativeLayout) menu.findViewById(R.id.user_image_frame);
        mPhotoLayout.setOnClickListener(this);
        new Utils.LoadImageTask(mPhotoLayout).execute(String.format("%s/userimage/%s.jpg",
                API_Manager.API_PATH, Config.mUserProfile.userName));

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mPhotoLayout.getLayoutParams();
        int width = (int) (res.getDimension(R.dimen.menu_avatar_button_size) * Config.mScaleFactor);
        int margin = (int) (res.getDimension(R.dimen.menu_avatar_button_margin_top) * Config.mScaleFactor);
        params.height = params.width = width;
        params.topMargin = margin;
        mPhotoLayout.setLayoutParams(params);

        // Username TextView
        mTextUserName = (TextView) menu.findViewById(R.id.text_name);
        mTextUserName.setText(Config.mUserProfile.userName);

        params = (LinearLayout.LayoutParams) mTextUserName.getLayoutParams();
        width = (int) (res.getDimension(R.dimen.menu_name_text_width) * Config.mScaleFactor);
        int height = (int) (res.getDimension(R.dimen.menu_name_text_height) * Config.mScaleFactor);
        margin = (int) (res.getDimension(R.dimen.menu_name_text_margin_top) * Config.mScaleFactor);
        float textSize = res.getDimension(R.dimen.menu_name_text_size) * Config.mFontScaleFactor;
        params.width = width;
        params.height = height;
        params.topMargin = margin;
        mTextUserName.setLayoutParams(params);
        mTextUserName.setTextSize(textSize);

        // Charts Button
        Button button = (Button) menu.findViewById(R.id.btn_charts);
        button.setOnClickListener(this);

        params = (LinearLayout.LayoutParams) button.getLayoutParams();
        width = (int) (res.getDimension(R.dimen.menu_item_button_width) * Config.mScaleFactor);
        height = (int) (res.getDimension(R.dimen.menu_item_button_height) * Config.mScaleFactor);
        margin = (int) (res.getDimension(R.dimen.menu_chart_button_margin_top) * Config.mScaleFactor);
        params.width = width;
        params.height = height;
        params.topMargin = margin;
        button.setLayoutParams(params);

        // Videos Button
        button = (Button) menu.findViewById(R.id.btn_videos);
        button.setOnClickListener(this);

        params = (LinearLayout.LayoutParams) button.getLayoutParams();
        params.width = width;
        params.height = height;
        button.setLayoutParams(params);

        // Badges Button
        button = (Button) menu.findViewById(R.id.btn_badges);
        button.setOnClickListener(this);

        params = (LinearLayout.LayoutParams) button.getLayoutParams();
        params.width = width;
        params.height = height;
        button.setLayoutParams(params);

        // Find Friends Button
        button = (Button) menu.findViewById(R.id.btn_friends);
        button.setOnClickListener(this);

        params = (LinearLayout.LayoutParams) button.getLayoutParams();
        params.width = width;
        params.height = height;
        button.setLayoutParams(params);

        // Settings Button
        button = (Button) menu.findViewById(R.id.btn_settings);
        button.setOnClickListener(this);

        params = (LinearLayout.LayoutParams) button.getLayoutParams();
        params.width = width;
        params.height = height;
        button.setLayoutParams(params);

        if (restore != null) {
            boolean isShown = restore.getBoolean("menu");
            if (isShown) {
                menu.toggle();
            }
        }
    }

}
