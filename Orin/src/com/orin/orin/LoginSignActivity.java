/**
 * @author Ry
 * @date 2013.12.12
 * @filename LoginSignActivity.java
 */

package com.orin.orin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.orin.orin.api.API_Manager;
import com.orin.orin.api.LikeCountResult;
import com.orin.orin.api.ProfileResult;
import com.orin.orin.api.ServiceResponse;
import com.orin.orin.data.UserProfile;
import com.orin.orin.util.Config;
import com.orin.orin.util.Utils;

import java.util.HashMap;

public abstract class LoginSignActivity extends Activity implements View.OnClickListener, API_Manager.RunningServiceListener  {

    protected static final int FACEBOOK = 1;
    protected static final int TWITTER = 2;
    protected static final int EMAIL_REGISTER = 3;
    protected static final int EMAIL_LOGIN = 4;

    protected String mUsername;
    protected String mPassword;
    protected String mEmail;
    protected String mFullName;

    /**
     * Initialize Activity content of view
     */
    protected abstract void initViews();

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        Utils.checkScreen(this);
//    }


    /**
     * Initialize variables for user login
     */
    protected void initProfileVariables() {
        mUsername = "";
        mPassword = "";
        mEmail = "";
        mFullName = "";
    }

    /**
     * Move to next Activity
     */
    protected void moveNextActivity(Class<?> destinationClass) {

        startActivity(new Intent(this, destinationClass));
        finish();
        overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
    }

    /**
     * Do login
     */
    protected void doLogin(String username, String password) {
        mUsername = username;
        mPassword = password;

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("name", username);
        params.put("password", password);
        params.put("regid", Config.mRegistrationId);

        API_Manager.getInstance().runWebService(API_Manager.LOGIN_ACTION, params, this, true);
    }

    /**
     * Do sign up to webservice
     */
    protected void doSignUp(String username, String password, String emailAddress) {
        mUsername = username;
        mPassword = password;
        mEmail = emailAddress;

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("name", username);
        params.put("password", password);
        params.put("email", emailAddress);
        params.put("regid", Config.mRegistrationId);

        API_Manager.getInstance().runWebService(API_Manager.REGISTER_ACTION, params, this, true);
    }

    protected void requestPassword(String emailAddress) {
        mEmail = emailAddress;

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("email", emailAddress);

        API_Manager.getInstance().runWebService(API_Manager.REQUEST_PASSWORD_ACTION, params, this, true);
    }

    /**
     * Do login to webservice
     */
    protected void processLogin() {
        UserProfile profile = new UserProfile();
        profile.userName = mUsername;
        profile.password = mPassword;
        profile.fullName = mFullName;
        Config.mUserProfile = profile;

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("name", mUsername);
        API_Manager.getInstance().runWebService(API_Manager.GET_USER_PROFILE_ACTION, params, this, true);

        // save to NSUserDefaults
        Config.saveUserName(this, mUsername);
    }

    /**
     * Process the result of webservice
     */
    @Override
    public void finishedRunningServiceListener(ServiceResponse response, String result) {
        if (response.serviceName == null) return;

        if (response.serviceName.equals(API_Manager.LOGIN_ACTION)) {
            if (response.status.equals("OK")) {
                processLogin();
            } else {
                Utils.createErrorAlertDialog(this, "Username or password is wrong").show();
            }
        }
        else if (response.serviceName.equals(API_Manager.REGISTER_ACTION)) {
            if (response.status.equals("OK")) {
                processLogin();
            } else {
                Utils.createErrorAlertDialog(this, "Your name or email is registered already").show();
            }
        }
        else if (response.serviceName.equals(API_Manager.FACEBOOK_LOGIN_ACTION)) {
            if (response.status.equals("OK")) {
                processLogin();
            } else {
                Utils.createErrorAlertDialog(this, "Login Failed").show();
            }
        }
        else if (response.serviceName.equals(API_Manager.TWITTER_LOGIN_ACTION)) {
            if (response.status.equals("OK")) {
                processLogin();
            } else {
                Utils.createErrorAlertDialog(this,
                        "Someone who has same username already exists. Please use your email to register.").show();
            }
        }
        else if (response.serviceName.equals(API_Manager.GET_USER_PROFILE_ACTION)) {
            if (response.status.equals("OK")) {
                ProfileResult profileResult = API_Manager.parseData(result, ProfileResult.class);

                if (profileResult != null) {
                    UserProfile profile = Config.mUserProfile;
                    profile.email = profileResult.email;
                    profile.fullName = profileResult.fullname;
                    profile.website = profileResult.website;
                    profile.aboutme = profileResult.aboutme;
                    profile.phone = profileResult.phone;
                    profile.gender = profileResult.gender;

                    profile.followers = profileResult.follower;
                    profile.following = profileResult.following;

                    profile.inviteCount = profileResult.invitecount;
                    profile.downloadCount = profileResult.itunecount;
                    profile.shareCount = profileResult.sharecount;

                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("name", mUsername);
                    API_Manager.getInstance().runWebService(API_Manager.GET_MUSIC_LIKE_COUNT_ACTION, params, this, true);
                }
            }
        }
        else if (response.serviceName.equals(API_Manager.GET_MUSIC_LIKE_COUNT_ACTION)) {
            if (response.status.equals("OK")) {
                LikeCountResult likeCountResult = API_Manager.parseData(result, LikeCountResult.class);

                UserProfile profile = Config.mUserProfile;
                profile.hiphopCount = likeCountResult.hiphop;
                profile.rnbCount = likeCountResult.rnb;
                profile.afrobeatCount = likeCountResult.afrobeat;
                profile.otherLikeCount = response.count - profile.hiphopCount - profile.rnbCount - profile.afrobeatCount;

                moveNextActivity(MainActivity.class);
            }
        }
        else if (response.serviceName.equals(API_Manager.REQUEST_PASSWORD_ACTION)) {
            if (response.status.equals("OK")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Success")
                        .setMessage("The password is already sent you through email.")
                        .setPositiveButton(android.R.string.ok, null)
                        .create().show();
            }
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Error")
                        .setMessage("Your email is not registered or you are registered with social platform.")
                        .setPositiveButton(android.R.string.ok, null)
                        .create().show();
            }
        }
    }

}
