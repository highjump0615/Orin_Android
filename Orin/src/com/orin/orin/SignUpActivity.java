/**
 * @author Ry
 * @date 2013.12.12
 * @filename SignUpActivity.java
 */

package com.orin.orin;

import android.app.AlertDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.orin.orin.util.Config;
import com.orin.orin.util.Utils;

public class SignUpActivity extends LoginSignActivity {

    private static final String TAG = SignUpActivity.class.getSimpleName();

    private EditText mEditEmailAddress;
    private EditText mEditUserName;
    private EditText mEditPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        initViews();
        Log.i(TAG, "onCreate()");
    }

    @Override
    protected void initViews() {
        final Resources res = getResources();

        // Logo image
        ImageView imageView = (ImageView) findViewById(R.id.img_logo);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        int width = (int) (res.getDimension(R.dimen.login_logo_size) * Config.mScaleFactor);
        params.height = params.width = width;
        imageView.setLayoutParams(params);

        // Sign up button
        Button button = (Button) findViewById(R.id.btn_signup);
        button.setOnClickListener(this);

        params = (LinearLayout.LayoutParams) button.getLayoutParams();
        width = (int) (res.getDimension(R.dimen.login_button_width) * Config.mScaleFactor);
        int height = (int) (res.getDimension(R.dimen.login_button_height) * Config.mScaleFactor);
        int marginTop = (int) (res.getDimension(R.dimen.login_button_margin_top) * Config.mScaleFactor);
        params.width = width;
        params.height = height;
        params.topMargin = marginTop;
        button.setLayoutParams(params);

        // Login button
        button = (Button) findViewById(R.id.btn_to_login);
        button.setOnClickListener(this);

        params = (LinearLayout.LayoutParams) button.getLayoutParams();
        width = (int) (res.getDimension(R.dimen.login_small_button_width) * Config.mScaleFactor);
        height = (int) (res.getDimension(R.dimen.login_small_button_height) * Config.mScaleFactor);
        marginTop = (int) (res.getDimension(R.dimen.login_small_button_margin_top) * Config.mScaleFactor);

        params.width = width;
        params.height = height;
        params.topMargin = marginTop;

        try {
            int marginBottom = (int) (res.getDimension(R.dimen.signup_small_button_margin_bottom) * Config.mScaleFactor);
            params.bottomMargin = marginBottom;
        } catch (Resources.NotFoundException e) {
            Log.d(TAG, "Bottom Margin does not exist");
        }

        button.setLayoutParams(params);

        // Email EditText
        LinearLayout layout = (LinearLayout) findViewById(R.id.layout_email);
        params = (LinearLayout.LayoutParams) layout.getLayoutParams();
        width = (int) (res.getDimension(R.dimen.login_edit_text_width) * Config.mScaleFactor);
        height = (int) (res.getDimension(R.dimen.login_edit_text_height) * Config.mScaleFactor);
        marginTop = (int) (res.getDimension(R.dimen.login_edit_text_margin_top) * Config.mScaleFactor);
        params.width = width;
        params.height = height;
        params.topMargin = marginTop;
        layout.setLayoutParams(params);

        mEditEmailAddress = (EditText) findViewById(R.id.edit_email_address);
        params = (LinearLayout.LayoutParams) mEditEmailAddress.getLayoutParams();
        int marginLeft = (int) (res.getDimension(R.dimen.login_edit_text_padding_start) * Config.mScaleFactor);
        int marginRight = (int) (res.getDimension(R.dimen.login_edit_text_padding_end) * Config.mScaleFactor);
        float textSize = res.getDimension(R.dimen.login_edit_text_size) * Config.mFontScaleFactor;
        params.leftMargin = marginLeft;
        params.rightMargin = marginRight;
        mEditEmailAddress.setLayoutParams(params);
        mEditEmailAddress.setTextSize(textSize);

        // Username EditText
        layout = (LinearLayout) findViewById(R.id.layout_username);
        params = (LinearLayout.LayoutParams) layout.getLayoutParams();
        params.width = width;
        params.height = height;
        params.topMargin = marginTop;
        layout.setLayoutParams(params);

        mEditUserName = (EditText) findViewById(R.id.text_user_name);
        params = (LinearLayout.LayoutParams) mEditUserName.getLayoutParams();
        params.leftMargin = marginLeft;
        params.rightMargin = marginRight;
        mEditUserName.setLayoutParams(params);
        mEditUserName.setTextSize(textSize);

        // Password EditText
        layout = (LinearLayout) findViewById(R.id.layout_password);
        params = (LinearLayout.LayoutParams) layout.getLayoutParams();
        params.width = width;
        params.height = height;
        params.topMargin = marginTop;
        layout.setLayoutParams(params);

        mEditPassword = (EditText) findViewById(R.id.edit_password);
        params = (LinearLayout.LayoutParams) mEditPassword.getLayoutParams();
        params.leftMargin = marginLeft;
        params.rightMargin = marginRight;
        mEditPassword.setLayoutParams(params);
        mEditPassword.setTextSize(textSize);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.btn_signup:
                String email = mEditEmailAddress.getText().toString();
                String username = mEditUserName.getText().toString();
                String password = mEditPassword.getText().toString();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                    builder.setTitle("Alert")
                            .setMessage("Fill user name, password and mail address")
                            .setPositiveButton(android.R.string.ok, null)
                            .create().show();
                } else {
                    doSignUp(username, password, email);
                }
                break;

            case R.id.btn_to_login:
                moveNextActivity(LoginActivity.class);
                break;
        }
    }

}