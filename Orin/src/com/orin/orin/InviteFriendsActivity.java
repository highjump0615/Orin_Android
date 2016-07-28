/**
 * @author Ry
 * @date 2014.01.06
 * @filename InviteFriendsActivity.java
 */

package com.orin.orin;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orin.orin.api.API_Manager;
import com.orin.orin.util.Config;
import com.orin.orin.util.Utils;

public class InviteFriendsActivity extends Activity implements View.OnClickListener {

    private static final String TAG = InviteFriendsActivity.class.getSimpleName();

    private EditText mEditEmailAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_invite_friends);
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

            case R.id.btn_send:
                onSend();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Config.mUserProfile.inviteCount++;
                API_Manager.getInstance().updateCountValue();

                Utils.createErrorAlertDialog(this, "Success", "Invitation has been sent successfully.").show();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Utils.createErrorAlertDialog(this, "Fail", "Invitation failed.").show();
            }
        }
    }

    private void initViews() {
        mEditEmailAddress = (EditText) findViewById(R.id.edit_email_address);
        findViewById(R.id.btn_send).setOnClickListener(this);

        final Resources res = getResources();

        // TitleBar
        RelativeLayout titleBar = (RelativeLayout) findViewById(R.id.layout_title_bar);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) titleBar.getLayoutParams();
        layoutParams.height = (int) (res.getDimension(R.dimen.title_bar_height) * Config.mScaleFactor);
        titleBar.setLayoutParams(layoutParams);

        // Back Button
        Button btnBack = (Button) findViewById(R.id.btn_cancel);
        btnBack.setOnClickListener(this);
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
        textTitle.setText("Invite Friends");
        textSize = res.getDimension(R.dimen.title_bar_small_text_size) * Config.mFontScaleFactor;
        textTitle.setTextSize(textSize);
    }

    private void onSend() {
        String destAddress = mEditEmailAddress.getText().toString();
        String content = String.format("Your friend %s has invited you to try out Orin https://itunes.apple.com/us/app/orin-music/id729548913?ls=1&mt=8 %s",
                Config.mUserProfile.userName, "");

        Utils.sendMail(this, destAddress, "Invitation", content, false);
    }

}
