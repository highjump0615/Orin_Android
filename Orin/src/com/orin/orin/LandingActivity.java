/**
 * @author Ry
 * @date 2013.12.12
 * @filename LandingActivity.java
 */

package com.orin.orin;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.model.GraphObject;
import com.google.android.gcm.GCMRegistrar;
import com.orin.orin.api.API_Manager;
import com.orin.orin.gcm.CommonUtilities;
import com.orin.orin.util.Config;
import com.orin.orin.util.Utils;

import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.util.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LandingActivity extends LoginSignActivity {

    private static final String TAG = LandingActivity.class.getSimpleName();

    private int mCurrentAction = 0;
    private boolean mButtonPressed = false;
    private boolean mConnected = false;

    // Facebook
    private Session.StatusCallback statusCallback = new SessionStatusCallback();

    // Twitter
    // SocialAuth Component
    private SocialAuthAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_landing);

        initProfileVariables();
        initDeviceToken();
        initGCM();
        initViews();
        initFacebookSettings(savedInstanceState);
        initTwitterSettings();

        mUsername = Config.loadUserName(this);
        if (!TextUtils.isEmpty(mUsername)) {
            processLogin();
        }
    }

    private void initDeviceToken() {
        Config.mRegistrationId = Config.loadRegistrationId(this);
    }

    @Override
    protected void initViews() {
        // Logo image
        ImageView imageView = (ImageView) findViewById(R.id.img_logo);

        RelativeLayout.LayoutParams layoutParams = null;
        LinearLayout.LayoutParams params = null;

        if (Utils.isTablet(this)) {
            params = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        }
        else {
            layoutParams = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        }

        final Resources res = getResources();
        int width = (int) (res.getDimension(R.dimen.landing_logo_size) * Config.mScaleFactor);
        int marginTop;

        if (Utils.isTablet(this)) {
            params.width = width;
            params.height = width;

            imageView.setLayoutParams(params);
        }
        else {
            layoutParams.width = width;
            layoutParams.height = width;

            int marginRight = (int) (res.getDimension(R.dimen.landing_logo_margin_right) * Config.mScaleFactor);
            marginTop = (int) (res.getDimension(R.dimen.landing_logo_margin_top) * Config.mScaleFactor);
            layoutParams.topMargin = marginTop;
            layoutParams.rightMargin = marginRight;

            imageView.setLayoutParams(layoutParams);
        }

        // Facebook login button
        Button button = (Button) findViewById(R.id.btn_facebook_login);
        button.setOnClickListener(this);

        width = (int) (res.getDimension(R.dimen.landing_button_width) * Config.mScaleFactor);
        int height = (int) (res.getDimension(R.dimen.landing_button_height) * Config.mScaleFactor);
        marginTop = (int) (res.getDimension(R.dimen.landing_button_margin_top) * Config.mScaleFactor);

        params = (LinearLayout.LayoutParams) button.getLayoutParams();
        params.width = width;
        params.height = height;
        params.topMargin = marginTop;
        button.setLayoutParams(params);

        // Twitter login button
        button = (Button) findViewById(R.id.btn_twitter_login);
        button.setOnClickListener(this);

        params = (LinearLayout.LayoutParams) button.getLayoutParams();
        params.width = width;
        params.height = height;
        params.topMargin = marginTop;
        button.setLayoutParams(params);

        // Email register button
        button = (Button) findViewById(R.id.btn_email_register);
        button.setOnClickListener(this);

        params = (LinearLayout.LayoutParams) button.getLayoutParams();
        params.width = width;
        params.height = height;
        params.topMargin = marginTop;
        button.setLayoutParams(params);

        // Login button
        button = (Button) findViewById(R.id.btn_login_here);
        button.setOnClickListener(this);

        width = (int) (res.getDimension(R.dimen.landing_small_button_width) * Config.mScaleFactor);
        height = (int) (res.getDimension(R.dimen.landing_small_button_height) * Config.mScaleFactor);
        marginTop = (int) (res.getDimension(R.dimen.landing_small_button_margin_top) * Config.mScaleFactor);
        int marginBottom = (int) (res.getDimension(R.dimen.landing_small_button_margin_bottom) * Config.mScaleFactor);

        params = (LinearLayout.LayoutParams) button.getLayoutParams();
        params.width = width;
        params.height = height;
        params.topMargin = marginTop;
        params.bottomMargin = marginBottom;
        button.setLayoutParams(params);
    }

    /**
     * Initialize module for GCM
     */
    private void initGCM() {
        try {
            // Make sure the device has the proper dependencies.
            GCMRegistrar.checkDevice(this);

            // Make sure the manifest was properly set - comment out this line
            // while developing the app, then uncomment it when it's ready.
            GCMRegistrar.checkManifest(this);

            // Get GCM registration id
            final String regId = GCMRegistrar.getRegistrationId(this);

            // Check if registration id already presents
            if (regId.equals("")) {
                if (Config.DEBUG) Log.i(TAG, "registering in GCM");
                GCMRegistrar.register(this, CommonUtilities.SENDER_ID);
            } else {
                if (Config.DEBUG) Log.i(TAG, "registration id=" + regId);
                Config.mRegistrationId = regId;
            }
        } catch (UnsupportedOperationException e) {
        }
    }

    private void initFacebookSettings(Bundle savedInstanceState) {
        Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);

        Session session = Session.getActiveSession();
        if (session == null) {
            if (savedInstanceState != null) {
                session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(this);
            }
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
            }
        }
    }

    private void initTwitterSettings() {
        // Add Bar to library
        adapter = new SocialAuthAdapter(new ResponseListener());

        // Please note : Update status functionality is only supported by
        // Facebook, Twitter, Linkedin, MySpace, Yahoo and Yammer.

        // Add providers
        //adapter.addProvider(Provider.FACEBOOK, R.drawable.facebook);
        adapter.addProvider(Provider.TWITTER, 0);
        //adapter.addProvider(Provider.LINKEDIN, R.drawable.linkedin);

        // Add email and mms providers
        //adapter.addProvider(Provider.EMAIL, R.drawable.email);
        //adapter.addProvider(Provider.MMS, R.drawable.mms);

        // For twitter use add callback method. Put your own callback url here.
        adapter.addCallBack(Provider.TWITTER, "http://socialauth.in/socialauthdemo/socialAuthSuccessAction.do");

        // Add keys and Secrets
        try {
            //adapter.addConfig(Provider.FACEBOOK, "392808544102529", "a11c13e793b2b6696bbc036ef5d3ff8f", null);
            adapter.addConfig(Provider.TWITTER, TWITTER_CONSUMER_KEY, TWITTER_CONSUMER_SECRET, null);
            //adapter.addConfig(Provider.LINKEDIN, "bh82t52rdos6", "zQ1LLrGbhDZ36fH8", null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Enable Provider
        adapter.enableOneSocial((Button) findViewById(R.id.btn_twitter_login));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mButtonPressed = false;
    }

    @Override
    protected void onDestroy() {
        onFacebookLogout();
        if (mConnected)
            adapter.signOut(this, Constants.TWITTER);
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
        Session.getActiveSession().addCallback(statusCallback);
    }

    @Override
    public void onStop() {
        super.onStop();
        Session.getActiveSession().removeCallback(statusCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (mCurrentAction) {
            case FACEBOOK:
                Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
    }

    @Override
    public void onClick(View view) {
        if (mButtonPressed) return;

        mButtonPressed = true;
        int id = view.getId();

        switch (id) {
            case R.id.btn_facebook_login:
                mCurrentAction = FACEBOOK;
                onFacebookLogin();
                break;

            case R.id.btn_twitter_login:
                mCurrentAction = TWITTER;
                break;

            case R.id.btn_email_register:
                mCurrentAction = EMAIL_REGISTER;
                moveNextActivity(SignUpActivity.class);
                break;

            case R.id.btn_login_here:
                mCurrentAction = EMAIL_LOGIN;
                moveNextActivity(LoginActivity.class);
                break;
        }
    }

    private void loginFromFacebook() {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("name", mUsername);
        params.put("email", mEmail);
        params.put("fullname", mFullName);
        params.put("regid", Config.mRegistrationId);

        API_Manager.getInstance().runWebService(API_Manager.FACEBOOK_LOGIN_ACTION, params, LandingActivity.this, true);
    }

    private void loginFromTwitter() {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("name", mUsername);
        params.put("regid", Config.mRegistrationId);
        API_Manager.getInstance().runWebService(API_Manager.TWITTER_LOGIN_ACTION, params, LandingActivity.this, true);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Facebook
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void onFacebookLogin() {
        Session session = Session.getActiveSession();
        if (!session.isOpened() && !session.isClosed()) {
            session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
        } else {
            Session.openActiveSession(this, true, statusCallback);
        }
    }

    private void onFacebookLogout() {
        Session session = Session.getActiveSession();
        if (!session.isClosed()) {
            session.closeAndClearTokenInformation();
        }
    }

    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            if (session.isOpened()) {
                getMyInformationWithFQL();
            }
        }
    }

    private void getMyInformationWithFQL() {
        String fqlQuery = "SELECT uid, name, username, email, pic_square FROM user WHERE uid = me()";
        Bundle params = new Bundle();
        params.putString("q", fqlQuery);
        Session session = Session.getActiveSession();
        Request request = new Request(session,
                "/fql",
                params,
                HttpMethod.GET,
                new Request.Callback(){
                    public void onCompleted(Response response) {
                        Log.i(TAG, "Result: " + response.toString());

                        GraphObject graphObject = response.getGraphObject();
                        FacebookRequestError error = response.getError();

                        initProfileVariables();
                        if (error == null) {
                            JSONArray dataArray = (JSONArray) graphObject.getProperty("data");
                            if (dataArray.length() > 0) {
                                try {
                                    JSONObject jsonObject = (JSONObject) dataArray.get(0);
                                    mEmail = (String) jsonObject.get("email");
                                    mUsername = (String) jsonObject.get("username");

                                    if (!TextUtils.isEmpty(mUsername) && !TextUtils.isEmpty(mEmail)) {
                                        mFullName = (String) jsonObject.get("name");
                                        loginFromFacebook();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            Utils.createErrorAlertDialog(LandingActivity.this, "Alert",
                                    "An error occurred while fetching user data").show();
                            onFacebookLogout();
                        }
                    }
                });
        Request.executeBatchAsync(request);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // twitter
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private final static String TWITTER_CONSUMER_KEY = "2b8bGR4gG4PFNd8sVa6GA";
    private final static String TWITTER_CONSUMER_SECRET = "9Pyh7t8m6JSl5K8DDmLntUWalAnAHvxtdVIleME9E";

    /**
     * Listens Response from Library
     *
     */
    private final class ResponseListener implements DialogListener {

        @Override
        public void onComplete(Bundle values) {
            Log.d(TAG, "Authentication Successful");
        }

        @Override
        public void onComplete(Bundle values, String username) {
            Log.d(TAG, "Authentication Successful, username=" + username);

            // Get name of provider after authentication
            final String providerName = values.getString(SocialAuthAdapter.PROVIDER);
            Log.d(TAG, "Provider Name = " + providerName);

            Toast.makeText(LandingActivity.this, providerName + " connected", Toast.LENGTH_LONG).show();

            if (providerName != null && providerName.equalsIgnoreCase("twitter")) {
                mConnected = true;
                if (!TextUtils.isEmpty(username)) {
                    mUsername = username;
                    loginFromTwitter();
                }
            }
        }

        @Override
        public void onError(SocialAuthError error) {
            Log.d(TAG, "Authentication Error: " + error.getMessage());
        }

        @Override
        public void onCancel() {
            Log.d(TAG, "Authentication Cancelled");
        }

        @Override
        public void onBack() {
            Log.d(TAG, "Dialog Closed by pressing Back Key");
        }

    }

}
