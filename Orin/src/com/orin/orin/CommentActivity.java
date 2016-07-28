/**
 * @author Ry
 * @date 2013.12.21
 * @filename CommentActivity.java
 */

package com.orin.orin;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orin.orin.api.API_Manager;
import com.orin.orin.api.CommentResult;
import com.orin.orin.api.ServiceResponse;
import com.orin.orin.util.Config;
import com.orin.orin.util.Utils;
import com.orin.orin.view.ChartItemView;
import com.orin.orin.view.CommentItemView;
import com.orin.orin.view.VideoItemView;

import java.util.HashMap;

public class CommentActivity extends Activity
        implements View.OnClickListener, API_Manager.RunningServiceListener {

    private static final String TAG = CommentActivity.class.getSimpleName();

    public static final String IS_CHART_ITEM = "is_chart_item";

    private Button mBtnComment;
    private TextView mTextCommentStatus;
    private EditText mEditComment;
    private LinearLayout mCommentList;

    private boolean mIsChartItem = true;
    private String mMusicId;
    private String mVideoId;

    private int mCommentCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null) {
            mMusicId = intent.getStringExtra(API_Manager.MUSIC_ID);
            mVideoId = intent.getStringExtra(API_Manager.YOUTUBE_ID);
            mIsChartItem = intent.getBooleanExtra(IS_CHART_ITEM, true);
        }

        setContentView(R.layout.activity_comment);
        initViews();
        loadComments();
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
            case R.id.btn_back:
                onBackPressed();
                break;

            case R.id.btn_comment:
                onBtnComment();
                break;
        }
    }

    private void initViews() {
        final Resources res = getResources();

        // Title bar layout
        RelativeLayout titleBar = (RelativeLayout) findViewById(R.id.layout_title_bar);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) titleBar.getLayoutParams();
        params.height = (int) (res.getDimension(R.dimen.title_bar_height) * Config.mScaleFactor);
        titleBar.setLayoutParams(params);

        // Back button
        Button button = (Button) findViewById(R.id.btn_back);
        button.setOnClickListener(this);

        params = (RelativeLayout.LayoutParams) button.getLayoutParams();
        int width = (int) (res.getDimension(R.dimen.title_bar_height) * Config.mScaleFactor);
        params.height = params.width = width;
        button.setLayoutParams(params);

        // Comment Button
        mBtnComment = (Button) findViewById(R.id.btn_comment);
        mBtnComment.setOnClickListener(this);
        mBtnComment.setEnabled(false);

        params = (RelativeLayout.LayoutParams) mBtnComment.getLayoutParams();
        int height = width;
        width = (int) (res.getDimension(R.dimen.comment_activity_comment_button_width) * Config.mScaleFactor);
        params.width = width;
        params.height = height;
        mBtnComment.setLayoutParams(params);

        // Status Comment
        mTextCommentStatus = (TextView) findViewById(R.id.text_comment_status);
        mTextCommentStatus.setVisibility(View.GONE);
        float textSize = res.getDimension(R.dimen.comment_activity_comment_status_text_size) * Config.mFontScaleFactor;
        mTextCommentStatus.setTextSize(textSize);

        // Write Comment EditText
        mCommentList = (LinearLayout) findViewById(R.id.comment_list);
        mEditComment = (EditText) findViewById(R.id.edit_comment);
        mEditComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                mBtnComment.setEnabled(charSequence.length() > 0);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        params = (RelativeLayout.LayoutParams) mEditComment.getLayoutParams();
        int padding = (int) (res.getDimension(R.dimen.comment_activity_comment_edit_padding) * Config.mScaleFactor);
        textSize = res.getDimension(R.dimen.comment_activity_comment_edit_text_size) * Config.mFontScaleFactor;
        params.height = (int) (res.getDimension(R.dimen.comment_activity_comment_edit_height) * Config.mScaleFactor);
        mEditComment.setLayoutParams(params);
        mEditComment.setPadding(padding, padding, padding, padding);
        mEditComment.setTextSize(textSize);
    }

    private void loadComments() {
        if (!mIsChartItem) {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put(API_Manager.YOUTUBE_ID, mVideoId);
            API_Manager.getInstance().runWebService(API_Manager.GET_VIDEO_COMMENT_ACTION, params, CommentActivity.this, true);
        } else {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put(API_Manager.MUSIC_ID, mMusicId);
            API_Manager.getInstance().runWebService(API_Manager.GET_MUSIC_COMMENT_ACTION, params, CommentActivity.this, true);
        }
    }

    private void onBtnComment() {
        if (TextUtils.isEmpty(mEditComment.getText().toString())) {
            Utils.createErrorAlertDialog(CommentActivity.this, "Fill Comment").show();
            return;
        }

        if (!mIsChartItem) {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put(API_Manager.USER_NAME, Config.mUserProfile.userName);
            params.put(API_Manager.YOUTUBE_ID, mVideoId);
            params.put(API_Manager.CONTENT, mEditComment.getText().toString());
            API_Manager.getInstance().runWebService(API_Manager.ADD_VIDEO_COMMENT_ACTION, params, CommentActivity.this, true);
        } else {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put(API_Manager.USER_NAME, Config.mUserProfile.userName);
            params.put(API_Manager.MUSIC_ID, mMusicId);
            params.put(API_Manager.CONTENT, mEditComment.getText().toString());
            API_Manager.getInstance().runWebService(API_Manager.ADD_MUSIC_COMMENT_ACTION, params, CommentActivity.this, true);
        }
    }

    @Override
    public void finishedRunningServiceListener(ServiceResponse response, String result) {
        Log.d(TAG, "receivedJson: " + result);

        if (TextUtils.isEmpty(response.serviceName))
            return;

        if (response.serviceName.equals(API_Manager.GET_MUSIC_COMMENT_ACTION)
                || response.serviceName.equals(API_Manager.GET_VIDEO_COMMENT_ACTION)) {
            mCommentCount = response.count;

            if (response.count == 0) {
                mTextCommentStatus.setVisibility(View.VISIBLE);
                return;
            }

            mCommentList.removeAllViews();

            CommentResult[] items = API_Manager.parseDataArray(result, CommentResult[].class);
            for (CommentResult item : items) {
                item.time = Utils.makeCommentTimeString(item.time);
                mCommentList.addView(new CommentItemView(this, item));
            }
        } else if (response.serviceName.equals(API_Manager.ADD_MUSIC_COMMENT_ACTION)
                || response.serviceName.equals(API_Manager.ADD_VIDEO_COMMENT_ACTION)) {
            mEditComment.setText("");
            loadComments();
            if (mIsChartItem) {
                ChartItemView.setCommentCount(++mCommentCount);
            } else {
                VideoItemView.setCommentCount(++mCommentCount);
            }
        }
    }

}
