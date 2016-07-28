/**
 * @author Ry
 * @date 2014.12.28
 * @filename ChangePasswordActivity.java
 */

package com.orin.orin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orin.orin.api.API_Manager;
import com.orin.orin.api.ServiceResponse;
import com.orin.orin.data.UserProfile;
import com.orin.orin.util.Config;
import com.orin.orin.util.Utils;

import java.util.HashMap;

public class ChangePasswordActivity extends Activity implements View.OnClickListener,
        API_Manager.RunningServiceListener {

    private static final String TAG = ChangePasswordActivity.class.getSimpleName();

    private EditText mEditOldPassword;
    private EditText mEditNewPassword;
    private EditText mEditRetypePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_change_pwd);
        initViews();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pop_in, R.anim.pop_out);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.btn_cancel:
                onBackPressed();
                break;

            case R.id.btn_done:
                onDone();
                break;
        }
    }

    private void initViews() {
        final Resources res = getResources();

        // TitleBar
        RelativeLayout titleBar = (RelativeLayout) findViewById(R.id.layout_title_bar);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) titleBar.getLayoutParams();
        layoutParams.height = (int) (res.getDimension(R.dimen.title_bar_height) * Config.mScaleFactor);
        titleBar.setLayoutParams(layoutParams);

        // Back Button
        Button btnBack = (Button) findViewById(R.id.btn_cancel);
        btnBack.setOnClickListener(this);
        float textSize = res.getDimension(R.dimen.title_bar_button_text_size) * Config.mFontScaleFactor;
        int padding = (int) (res.getDimension(R.dimen.title_bar_button_padding) * Config.mScaleFactor);
        btnBack.setTextSize(textSize);
        btnBack.setPadding(padding, 0, 0, 0);

        //
        Button button = (Button) findViewById(R.id.btn_done);
        button.setOnClickListener(this);
        button.setTextSize(textSize);
        button.setPadding(0, 0, padding, 0);

        // Title TextView
        TextView textTitle = (TextView) findViewById(R.id.text_title);
        textTitle.setText("Change Password");
        textSize = res.getDimension(R.dimen.title_bar_small_text_size) * Config.mFontScaleFactor;
        textTitle.setTextSize(textSize);

        mEditOldPassword = (EditText) findViewById(R.id.edit_old_password);
        mEditNewPassword = (EditText) findViewById(R.id.edit_new_password);
        mEditRetypePassword = (EditText) findViewById(R.id.edit_retype_password);
    }

    private void onDone() {
        UserProfile profile = Config.mUserProfile;

        String strOldPass = mEditOldPassword.getText().toString();
        String strNewPass = mEditNewPassword.getText().toString();
        String strRetypePass = mEditRetypePassword.getText().toString();

        if ((!TextUtils.isEmpty(profile.password) && TextUtils.isEmpty(strOldPass))
                || TextUtils.isEmpty(strNewPass) || TextUtils.isEmpty(strRetypePass)) {
            Utils.createErrorAlertDialog(this, "Type all passwords").show();
            return;
        }

        if (!strNewPass.equals(strRetypePass)) {
            Utils.createErrorAlertDialog(this, "Retype password").show();
            return;
        }

        if (!profile.password.equals(strOldPass)) {
            Utils.createErrorAlertDialog(this, "Old Password is incorrect").show();
            return;
        }

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("name", profile.userName);
        params.put("password", strNewPass);

        API_Manager.getInstance().runWebService(API_Manager.CHANGE_PASSWORD_ACTION, params, this, true);
    }

    @Override
    public void finishedRunningServiceListener(ServiceResponse response, String result) {
        Log.d(TAG, "receivedJson: " + result);

        if (TextUtils.isEmpty(response.serviceName))
            return;

        if (response.serviceName.equals(API_Manager.CHANGE_PASSWORD_ACTION)) {
            if (response.status.equals("OK")) {
                Config.mUserProfile.password = mEditNewPassword.getText().toString();

                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Success")
                        .setMessage("Password has been changed successfully.")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                onBackPressed();
                            }
                        }).create();
                dialog.show();
            }
        }
    }

}
