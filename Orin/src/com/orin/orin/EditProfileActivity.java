/**
 * @author Ry
 * @date 2013.12.28
 * @filename EditProfileActivity.java
 */

package com.orin.orin;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makeramen.segmented.SegmentedRadioGroup;
import com.orin.orin.api.API_Manager;
import com.orin.orin.api.ServiceResponse;
import com.orin.orin.data.UserProfile;
import com.orin.orin.util.Config;

import java.io.IOException;
import java.util.HashMap;

public class EditProfileActivity extends Activity implements  API_Manager.RunningServiceListener,
        RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private static final String TAG = EditProfileActivity.class.getSimpleName();

    private EditText mEditFullName;
    private TextView mTextUserName;
    private EditText mEditWebsite;
    private EditText mEditAboutMe;
    private EditText mEditEmail;
    private EditText mEditPhone;

    private ImageView mImagePhoto;

    SegmentedRadioGroup mSegmentGender;

    private boolean mIsMale = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_profile);
        initTitleView();
        initViews();
        loadUserProfile();
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
            case R.id.btn_edit_password:
                startActivity(new Intent(EditProfileActivity.this, ChangePasswordActivity.class));
                overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                break;

            case R.id.btn_cancel:
                onBackPressed();
                break;

            case R.id.btn_done:
                onDone();
                break;

            case R.id.img_photo:
                onUserPhoto();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri chosenImageUri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), chosenImageUri);
                mImagePhoto.setImageBitmap(bitmap);
            } catch (IOException e) {
                if (Config.DEBUG) e.printStackTrace();
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        if (radioGroup == mSegmentGender) {
            if (checkedId == R.id.btn_male) {
                mIsMale = true;
            } else if (checkedId == R.id.btn_female) {
                mIsMale = false;
            }
        }
    }

    private void initTitleView() {
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
        textTitle.setText("Edit Profile");
        textSize = res.getDimension(R.dimen.title_bar_small_text_size) * Config.mFontScaleFactor;
        textTitle.setTextSize(textSize);
    }

    private void initViews() {
        final Resources res = getResources();

        // Background
        ImageView imageView = (ImageView) findViewById(R.id.img_back);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        params.height = (int) (res.getDimension(R.dimen.edit_profile_back_height) * Config.mScaleFactor);
        imageView.setLayoutParams(params);

        // Photo
        mImagePhoto = (ImageView) findViewById(R.id.img_photo);
        mImagePhoto.setOnClickListener(this);
        params = (RelativeLayout.LayoutParams) mImagePhoto.getLayoutParams();
        int width = (int) (res.getDimension(R.dimen.edit_profile_photo_size) * Config.mScaleFactor);
        params.height = params.width = width;
        params.rightMargin = (int) (res.getDimension(R.dimen.edit_profile_photo_margin_right) * Config.mScaleFactor);
        params.topMargin = (int) (res.getDimension(R.dimen.edit_profile_photo_margin_top) * Config.mScaleFactor);
        mImagePhoto.setLayoutParams(params);

        int height = (int) (res.getDimension(R.dimen.edit_profile_text_height) * Config.mScaleFactor);
        int marginLeft = (int) (res.getDimension(R.dimen.edit_profile_text_left) * Config.mScaleFactor);

        // FullName
        LinearLayout editlayout = (LinearLayout) findViewById(R.id.layout_full_name);
        params = (RelativeLayout.LayoutParams) editlayout.getLayoutParams();
        params.topMargin = (int) (res.getDimension(R.dimen.edit_profile_text_full_name_margin_top) * Config.mScaleFactor);
        params.leftMargin = marginLeft;
        editlayout.setLayoutParams(params);

        ImageView editIcon = (ImageView) findViewById(R.id.img_full_name);
        LinearLayout.LayoutParams editlayoutParams = (LinearLayout.LayoutParams) editIcon.getLayoutParams();
        editlayoutParams.width = height;
        editlayoutParams.height = height;
        editIcon.setLayoutParams(editlayoutParams);

        mEditFullName = (EditText) findViewById(R.id.edit_full_name);
        editlayoutParams = (LinearLayout.LayoutParams) mEditFullName.getLayoutParams();
        width = (int) (res.getDimension(R.dimen.edit_profile_text_small_width) * Config.mScaleFactor);

        editlayoutParams.width = width;
        editlayoutParams.height = height;
        editlayoutParams.leftMargin = marginLeft;
        float textSize = res.getDimension(R.dimen.edit_profile_text_size) * Config.mFontScaleFactor;
        mEditFullName.setLayoutParams(editlayoutParams);
        mEditFullName.setTextSize(textSize);

        // UserName
        editlayout = (LinearLayout) findViewById(R.id.layout_user_name);
        params = (RelativeLayout.LayoutParams) editlayout.getLayoutParams();
        params.topMargin = (int) (res.getDimension(R.dimen.edit_profile_text_user_name_margin_top) * Config.mScaleFactor);
        params.leftMargin = marginLeft;
        editlayout.setLayoutParams(params);

        editIcon = (ImageView) findViewById(R.id.img_user_name);
        editlayoutParams = (LinearLayout.LayoutParams) editIcon.getLayoutParams();
        editlayoutParams.width = height;
        editlayoutParams.height = height;
        editIcon.setLayoutParams(editlayoutParams);

        mTextUserName = (TextView) findViewById(R.id.text_user_name);
        editlayoutParams = (LinearLayout.LayoutParams) mTextUserName.getLayoutParams();
        editlayoutParams.width = width;
        editlayoutParams.height = height;
        editlayoutParams.leftMargin = marginLeft;
        mTextUserName.setLayoutParams(editlayoutParams);
        mTextUserName.setTextSize(textSize);

        // Website
        editlayout = (LinearLayout) findViewById(R.id.layout_web_site);
        params = (RelativeLayout.LayoutParams) editlayout.getLayoutParams();
        params.topMargin = (int) (res.getDimension(R.dimen.edit_profile_text_web_site_margin_top) * Config.mScaleFactor);
        params.leftMargin = marginLeft;
        editlayout.setLayoutParams(params);

        editIcon = (ImageView) findViewById(R.id.img_web_site);
        editlayoutParams = (LinearLayout.LayoutParams) editIcon.getLayoutParams();
        editlayoutParams.width = height;
        editlayoutParams.height = height;
        editIcon.setLayoutParams(editlayoutParams);

        mEditWebsite = (EditText) findViewById(R.id.edit_web_site);
        editlayoutParams = (LinearLayout.LayoutParams) mEditWebsite.getLayoutParams();
        width = (int) (res.getDimension(R.dimen.edit_profile_text_large_width) * Config.mScaleFactor);
        height = (int) (res.getDimension(R.dimen.edit_profile_text_height) * Config.mScaleFactor);
        editlayoutParams.width = width;
        editlayoutParams.height = height;
        editlayoutParams.leftMargin = marginLeft;
        mEditWebsite.setLayoutParams(editlayoutParams);
        mEditWebsite.setTextSize(textSize);

        // About me
        editlayout = (LinearLayout) findViewById(R.id.layout_about_me);
        params = (RelativeLayout.LayoutParams) editlayout.getLayoutParams();
        params.topMargin = (int) (res.getDimension(R.dimen.edit_profile_text_about_me_margin_top) * Config.mScaleFactor);
        params.leftMargin = marginLeft;
        editlayout.setLayoutParams(params);

        editIcon = (ImageView) findViewById(R.id.img_about_me);
        editlayoutParams = (LinearLayout.LayoutParams) editIcon.getLayoutParams();
        editlayoutParams.width = height;
        editlayoutParams.height = height;
        editIcon.setLayoutParams(editlayoutParams);

        mEditAboutMe = (EditText) findViewById(R.id.edit_about_me);
        editlayoutParams = (LinearLayout.LayoutParams) mEditAboutMe.getLayoutParams();
        editlayoutParams.width = width;
        editlayoutParams.height = height;
        editlayoutParams.leftMargin = marginLeft;
        mEditAboutMe.setLayoutParams(editlayoutParams);
        mEditAboutMe.setTextSize(textSize);

        // Email
        editlayout = (LinearLayout) findViewById(R.id.layout_email);
        params = (RelativeLayout.LayoutParams) editlayout.getLayoutParams();
        params.topMargin = (int) (res.getDimension(R.dimen.edit_profile_text_email_margin_top) * Config.mScaleFactor);
        params.leftMargin = marginLeft;
        editlayout.setLayoutParams(params);

        editIcon = (ImageView) findViewById(R.id.img_email);
        editlayoutParams = (LinearLayout.LayoutParams) editIcon.getLayoutParams();
        editlayoutParams.width = height;
        editlayoutParams.height = height;
        editIcon.setLayoutParams(editlayoutParams);

        mEditEmail = (EditText) findViewById(R.id.edit_email);
        editlayoutParams = (LinearLayout.LayoutParams) mEditEmail.getLayoutParams();
        editlayoutParams.width = width;
        editlayoutParams.height = height;
        editlayoutParams.leftMargin = marginLeft;
        mEditEmail.setLayoutParams(editlayoutParams);
        mEditEmail.setTextSize(textSize);

        // Phone
        editlayout = (LinearLayout) findViewById(R.id.layout_phone);
        params = (RelativeLayout.LayoutParams) editlayout.getLayoutParams();
        params.topMargin = (int) (res.getDimension(R.dimen.edit_profile_text_phone_margin_top) * Config.mScaleFactor);
        params.leftMargin = marginLeft;
        editlayout.setLayoutParams(params);

        editIcon = (ImageView) findViewById(R.id.img_phone);
        editlayoutParams = (LinearLayout.LayoutParams) editIcon.getLayoutParams();
        editlayoutParams.width = height;
        editlayoutParams.height = height;
        editIcon.setLayoutParams(editlayoutParams);

        mEditPhone = (EditText) findViewById(R.id.edit_phone);
        editlayoutParams = (LinearLayout.LayoutParams) mEditPhone.getLayoutParams();
        editlayoutParams.width = width;
        editlayoutParams.height = height;
        editlayoutParams.leftMargin = marginLeft;
        mEditPhone.setLayoutParams(editlayoutParams);
        mEditPhone.setTextSize(textSize);

        // Edit Password button
        editlayout = (LinearLayout) findViewById(R.id.layout_change_password);
        params = (RelativeLayout.LayoutParams) editlayout.getLayoutParams();
        params.topMargin = (int) (res.getDimension(R.dimen.edit_profile_text_change_password_margin_top) * Config.mScaleFactor);
        params.leftMargin = marginLeft;
        editlayout.setLayoutParams(params);

        editIcon = (ImageView) findViewById(R.id.img_change_password);
        editlayoutParams = (LinearLayout.LayoutParams) editIcon.getLayoutParams();
        editlayoutParams.width = height;
        editlayoutParams.height = height;
        editIcon.setLayoutParams(editlayoutParams);

        TextView labelChgPswd = (TextView) findViewById(R.id.text_change_password);
        editlayoutParams = (LinearLayout.LayoutParams) labelChgPswd.getLayoutParams();
        editlayoutParams.width = width;
        editlayoutParams.height = height;
        editlayoutParams.leftMargin = marginLeft;
        labelChgPswd.setLayoutParams(editlayoutParams);
        labelChgPswd.setTextSize(textSize);

        Button button = (Button) findViewById(R.id.btn_edit_password);
        button.setOnClickListener(this);
        params = (RelativeLayout.LayoutParams) button.getLayoutParams();
        params.height = (int) (res.getDimension(R.dimen.edit_profile_button_height) * Config.mScaleFactor);
        params.topMargin = (int) (res.getDimension(R.dimen.edit_profile_button_margin_top) * Config.mScaleFactor);
        textSize = res.getDimension(R.dimen.edit_profile_button_text_size) * Config.mFontScaleFactor;
        button.setLayoutParams(params);
        button.setTextSize(textSize);

        // Segmented
        editlayout = (LinearLayout) findViewById(R.id.layout_gender);
        params = (RelativeLayout.LayoutParams) editlayout.getLayoutParams();
        params.topMargin = (int) (res.getDimension(R.dimen.edit_profile_segmented_ctrl_top_margin) * Config.mScaleFactor);
        params.leftMargin = marginLeft;
        editlayout.setLayoutParams(params);

        editIcon = (ImageView) findViewById(R.id.img_gender);
        editlayoutParams = (LinearLayout.LayoutParams) editIcon.getLayoutParams();
        editlayoutParams.width = height;
        editlayoutParams.height = height;
        editIcon.setLayoutParams(editlayoutParams);

        mSegmentGender = (SegmentedRadioGroup) findViewById(R.id.segment_gender);
        mSegmentGender.setOnCheckedChangeListener(this);
        editlayoutParams = (LinearLayout.LayoutParams) mSegmentGender.getLayoutParams();
        editlayoutParams.width = (int) (res.getDimension(R.dimen.edit_profile_segmented_ctrl_width) * Config.mScaleFactor);
        editlayoutParams.height = (int) (res.getDimension(R.dimen.edit_profile_segmented_ctrl_height) * Config.mScaleFactor);
        editlayoutParams.leftMargin = marginLeft;
        mSegmentGender.setLayoutParams(editlayoutParams);
    }

    private void loadUserProfile() {
        UserProfile profile = Config.mUserProfile;
        mEditFullName.setText(profile.fullName);
        mTextUserName.setText(profile.userName);
        mEditWebsite.setText(profile.website);
        mEditAboutMe.setText(profile.aboutme);
        mEditEmail.setText(profile.email);
        mEditPhone.setText(profile.phone);
        mSegmentGender.check(profile.gender == 0 ? R.id.btn_male : R.id.btn_female);

        if (profile.imageProfile != null)
            mImagePhoto.setImageBitmap(profile.imageProfile);
        else
            mImagePhoto.setBackgroundResource(R.drawable.unknown_character);
    }

    private void onDone() {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("name", mTextUserName.getText().toString());
        params.put("email", mEditEmail.getText().toString());
        params.put("fullname", mEditFullName.getText().toString());
        params.put("website", mEditWebsite.getText().toString());
        params.put("aboutme", mEditAboutMe.getText().toString());
        params.put("phone", mEditPhone.getText().toString());
        params.put("gender", mIsMale ? "0" : "1");

        API_Manager.getInstance().runWebService(API_Manager.SETUP_PROFILE_ACTION, params, this, true);
    }

    private void onUserPhoto() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, 1);
    }

    @Override
    public void finishedRunningServiceListener(ServiceResponse response, String result) {
        Log.d(TAG, "receivedJson: " + result);

        if (TextUtils.isEmpty(response.serviceName))
            return;

        if (response.serviceName.equals(API_Manager.SETUP_PROFILE_ACTION)) {
            if (response.status.equals("OK")) {
                UserProfile profile = Config.mUserProfile;
                profile.userName = mTextUserName.getText().toString();
                profile.email = mEditEmail.getText().toString();
                profile.fullName = mEditFullName.getText().toString();
                profile.website = mEditWebsite.getText().toString();
                profile.aboutme = mEditAboutMe.getText().toString();
                profile.phone = mEditPhone.getText().toString();
                profile.gender = mIsMale ? 0 : 1;

                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("photo", mImagePhoto);
                params.put("name", profile.userName);
                API_Manager.getInstance().runWebService(API_Manager.UPLOAD_PHOTO_ACTION, params, this, true);
            }
        } else if (response.serviceName.equals(API_Manager.UPLOAD_PHOTO_ACTION)) {
            if (response.status.equals("OK"))
                onBackPressed();
        }
    }

}
