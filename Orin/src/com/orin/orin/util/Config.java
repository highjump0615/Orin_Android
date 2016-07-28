/**
 * @author Ry
 * @date 2013.12.17
 * @filename Config.java
 */

package com.orin.orin.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.orin.orin.data.UserProfile;

import java.util.ArrayList;
import java.util.HashMap;

public class Config {

    public static final boolean DEBUG = true;

    private static final int DESIGN_WIDTH_PHONE = 320;
    private static final int DESIGN_WIDTH_TABLET = 800;

    /**
     * Please replace this with a valid API key which is enabled for the
     * YouTube Data API v3 service. Go to the
     * <a href="https://code.google.com/apis/console/">Google APIs Console</a> to
     * register a new developer key.
     */
    public static final String YOUTUBE_DEVELOPER_KEY = "AIzaSyBifZ79tKAFTTNC7gIvqHST_GEX_ahLS2s";

    private static final String PREF_NAME = "orin_pref";
    private static final String PREF_USERNAME = "username";
    private static final String PREF_SHARE_UNLOCK_LEVEL = "share_unlocks_levels";
    private static final String PREF_PUSH_NOTIFICATION = "push_notification";

    public static String mRegistrationId = null;

    public static UserProfile mUserProfile;

    public static HashMap<String, String> mBadgeMap;

    public static float mScaleFactor = 0.0f;
    public static float mFontScaleFactor = 0.0f;

    static {
        mBadgeMap = new HashMap<String, String>();
        mBadgeMap.put("badge_newbie", "NewBie");
        mBadgeMap.put("badge_rising_star", "Rising Star");
        mBadgeMap.put("badge_super_star", "Super Star");
        mBadgeMap.put("badge_connoisseur", "Connoisseur");
        mBadgeMap.put("badge_maestro", "Maestro");
        mBadgeMap.put("badge_pro", "Pro");
        mBadgeMap.put("badge_crunked", "Crunked");
        mBadgeMap.put("badge_hip_hop", "Hip-Hop");
        mBadgeMap.put("badge_rnb", "RnB");
        mBadgeMap.put("badge_afro_beat", "Afro-beat");
        mBadgeMap.put("badge_buyer", "Buyer");
        mBadgeMap.put("badge_sharer", "Sharer");
    }


    public static String loadRegistrationId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("token_storage", Context.MODE_PRIVATE);
        return preferences.getString("gcm_id", "");
    }

    public static void saveRegistrationId(Context context, String regId) {
        SharedPreferences preferences = context.getSharedPreferences("token_storage", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("gcm_id", regId);
        editor.commit();
    }

    public static void saveLoginState(Context context, boolean isLogin) {
        SharedPreferences preferences = context.getSharedPreferences("token_storage", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("logged_in", isLogin);
        editor.commit();
    }

    public static String loadUserName(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getString(PREF_USERNAME, "");
    }

    public static void saveUserName(Context context, String username) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_USERNAME, username);
        editor.commit();
    }

    public static boolean loadShareOption(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(PREF_SHARE_UNLOCK_LEVEL, true);
    }

    public static void saveShareOption(Context context, boolean on) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PREF_SHARE_UNLOCK_LEVEL, on);
        editor.commit();
    }

    public static boolean loadPushOption(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(PREF_PUSH_NOTIFICATION, true);
    }

    public static void savePushOption(Context context, boolean on) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PREF_PUSH_NOTIFICATION, on);
        editor.commit();
    }

    public static ArrayList<String> getBadges(UserProfile profile) {
        ArrayList<String> aryBadges = new ArrayList<String>();

        if (profile == null) return aryBadges;

        aryBadges.add("badge_newbie");
//        aryBadges.add("badge_rising_star");
//        aryBadges.add("badge_super_star");
//        aryBadges.add("badge_connoisseur");
//        aryBadges.add("badge_maestro");
//        aryBadges.add("badge_pro");
//        aryBadges.add("badge_crunked");
//        aryBadges.add("badge_hip_hop");
//        aryBadges.add("badge_rnb");
//        aryBadges.add("badge_afro_beat");
//        aryBadges.add("badge_buyer");
//        aryBadges.add("badge_sharer");

        if (profile.inviteCount >= 20)
            aryBadges.add("badge_rising_star");
        if (profile.following >= 100)
            aryBadges.add("badge_super_star");

        int nLikeCount = profile.hiphopCount + profile.rnbCount + profile.afrobeatCount + profile.otherLikeCount;
        if (nLikeCount >= 50)
            aryBadges.add("badge_connoisseur");
        if (nLikeCount >= 100)
            aryBadges.add("badge_maestro");
        if (nLikeCount >= 300)
            aryBadges.add("badge_pro");
        if (nLikeCount >= 500)
            aryBadges.add("badge_crunked");

        if (profile.hiphopCount >= 100)
            aryBadges.add("badge_hip_hop");
        if (profile.rnbCount >= 100)
            aryBadges.add("badge_rnb");
        if (profile.afrobeatCount >= 100)
            aryBadges.add("badge_afro_beat");

        if (profile.downloadCount >= 50)
            aryBadges.add("badge_buyer");
        if (profile.shareCount >= 100)
            aryBadges.add("badge_sharer");

        return aryBadges;
    }

    public static void calculateScaleFactor(Activity activity) {
        final Resources resources = activity.getResources();

        DisplayMetrics metrics = resources.getDisplayMetrics();
        if (Utils.isTablet(activity)) {
            mScaleFactor = metrics.widthPixels / (DESIGN_WIDTH_TABLET * metrics.scaledDensity);
        }
        else {
            mScaleFactor = metrics.widthPixels / (DESIGN_WIDTH_PHONE * metrics.scaledDensity);
        }
        mFontScaleFactor = mScaleFactor / metrics.density;
    }

}
