/**
 * @author Ry
 * @date 2013.12.22
 * @filename VideoItemView.java
 */

package com.orin.orin.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orin.orin.CommentActivity;
import com.orin.orin.R;
import com.orin.orin.api.API_Manager;
import com.orin.orin.data.VideoData;
import com.orin.orin.util.Config;
import com.orin.orin.util.Utils;
import com.orin.orin.youtube.YouTubePlayerActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class VideoItemView extends LinearLayout {

    private static final String TAG = VideoItemView.class.getSimpleName();

    private Context mContext;
    private Fragment mFragment;

    private TextView mTextDay;
    private TextView mTextTitle;
    private Button mBtnPlay;
    private RelativeLayout mLayoutImage;
    private TextView mTextLikedUsers;
    private TextView mTextLikeCount;
    private TextView mTextComment;
    private Button mBtnLike;
    private Button mBtnComment;
    private Button mBtnDownload;

    private static TextView mTextSelectedComment = null;

    private boolean mIsLiked;
    private ArrayList<String> mLikedUsers = new ArrayList<String>();


    public VideoItemView(final Fragment fragment, final VideoData itemData) {
        super(fragment.getActivity());

        mContext = fragment.getActivity();
        mFragment = fragment;

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View layout;
        final Resources res = mContext.getResources();

        if (Utils.isTablet(fragment.getActivity())) {
            layout = inflater.inflate(R.layout.chart_list_item, null);

            int width = (int) (res.getDimension(R.dimen.chart_view_width) * Config.mScaleFactor);
            int height = (int) (res.getDimension(R.dimen.chart_view_height) * Config.mScaleFactor);
            LayoutParams layoutParams = new LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            layout.setLayoutParams(layoutParams);

            // Passed Day
            mTextDay = (TextView) layout.findViewById(R.id.text_day);
            width = (int) (res.getDimension(R.dimen.chart_day_text_width) * Config.mScaleFactor);
            height = (int) (res.getDimension(R.dimen.chart_day_text_height) * Config.mScaleFactor);
            int margin = (int) (res.getDimension(R.dimen.chart_view_margin) * Config.mScaleFactor);
            float textSize = res.getDimension(R.dimen.item_text_size) * Config.mFontScaleFactor;
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mTextDay.getLayoutParams();
            params.width = width;
            params.height = height;
            params.setMargins(0, 0, margin, 0);
            mTextDay.setLayoutParams(params);
            mTextDay.setTextSize(textSize);

            // Title
            mTextTitle = (TextView) layout.findViewById(R.id.text_title);
            mTextTitle.setTextSize(textSize);

            // Chart Mark
            ImageView imageMark = (ImageView) layout.findViewById(R.id.img_mark);
            width = (int) (res.getDimension(R.dimen.chart_mark_image_size) * Config.mScaleFactor);
            layoutParams = new LayoutParams(width, width);
            imageMark.setLayoutParams(layoutParams);

            // Photo
            mLayoutImage = (RelativeLayout) layout.findViewById(R.id.chart_image_frame);

            width = (int) (res.getDimension(R.dimen.chart_photo_image_size) * Config.mScaleFactor);
            params = (RelativeLayout.LayoutParams) mLayoutImage.getLayoutParams();
            params.width = width;
            params.height = width;
            params.setMargins(0, margin, 0, 0);
            mLayoutImage.setLayoutParams(params);

            // Play button
            mBtnPlay = (Button) layout.findViewById(R.id.btn_play);
            mBtnPlay.setBackgroundResource(R.drawable.chart_play);
            mBtnPlay.setTag(itemData.youtubeID);
            mBtnPlay.setOnClickListener(onBtnPlayListener);

            width = (int) (res.getDimension(R.dimen.chart_play_button_size) * Config.mScaleFactor);
            params = (RelativeLayout.LayoutParams) mBtnPlay.getLayoutParams();
            params.width = width;
            params.height = width;
            params.setMargins(margin, margin, 0, 0);
            mBtnPlay.setLayoutParams(params);

            int likemargin = (int) (res.getDimension(R.dimen.chart_like_margin) * Config.mScaleFactor);

            // Liked Users
            mTextLikedUsers = (TextView) layout.findViewById(R.id.text_liked_users);

            params = (RelativeLayout.LayoutParams) mTextLikedUsers.getLayoutParams();
            params.setMargins(0, likemargin, 0, 0);
            mTextLikedUsers.setLayoutParams(params);
            mTextLikedUsers.setTextSize(textSize);

            // State layout
            RelativeLayout stateLayout = (RelativeLayout) layout.findViewById(R.id.state_layout);
            params = (RelativeLayout.LayoutParams) stateLayout.getLayoutParams();
            params.setMargins(0, likemargin, 0, 0);
            stateLayout.setLayoutParams(params);

            // like layout
            LinearLayout linearLayout = (LinearLayout) layout.findViewById(R.id.like_layout);
            params = (RelativeLayout.LayoutParams) linearLayout.getLayoutParams();
            int nRightMargin = (int) (res.getDimension(R.dimen.chart_like_layout_right_margin) * Config.mScaleFactor);
            int nLeftPadding = (int) (res.getDimension(R.dimen.chart_like_layout_left_padding) * Config.mScaleFactor);
            params.rightMargin = nRightMargin;
            linearLayout.setPadding(nLeftPadding, 0, 0, 0);
            linearLayout.setLayoutParams(params);

            // Like Button
            mBtnLike = (Button) layout.findViewById(R.id.btn_like);

            width = (int) (res.getDimension(R.dimen.chart_like_button_size) * Config.mScaleFactor);
            LinearLayout.LayoutParams linearparams = (LinearLayout.LayoutParams) mBtnLike.getLayoutParams();
            linearparams.width = width;
            linearparams.height = width;
            mBtnLike.setLayoutParams(linearparams);

            // Like count
            mTextLikeCount = (TextView) layout.findViewById(R.id.text_like_count);

            width = (int) (res.getDimension(R.dimen.chart_like_text_width) * Config.mScaleFactor);
            height = (int) (res.getDimension(R.dimen.chart_like_text_height) * Config.mScaleFactor);
            linearparams = (LinearLayout.LayoutParams) mTextLikeCount.getLayoutParams();
            linearparams.leftMargin = (int) (res.getDimension(R.dimen.chart_like_text_left_margin) * Config.mScaleFactor);
            linearparams.width = width;
            linearparams.height = height;
            mTextLikeCount.setLayoutParams(linearparams);
            mTextLikeCount.setTextSize(textSize);

            // comment layout
            linearLayout = (LinearLayout) layout.findViewById(R.id.comment_layout);
            params = (RelativeLayout.LayoutParams) linearLayout.getLayoutParams();
            nLeftPadding = (int) (res.getDimension(R.dimen.chart_like_layout_left_padding) * Config.mScaleFactor);
            linearLayout.setPadding(nLeftPadding, 0, 0, 0);
            linearLayout.setLayoutParams(params);

            // Comment Button
            mBtnComment = (Button) layout.findViewById(R.id.btn_comment);

            width = (int) (res.getDimension(R.dimen.chart_comment_button_size) * Config.mScaleFactor);
            linearparams = (LinearLayout.LayoutParams) mBtnComment.getLayoutParams();
            linearparams.width = width;
            linearparams.height = width;
            mBtnComment.setLayoutParams(linearparams);

            // Comment count
            mTextComment = (TextView) layout.findViewById(R.id.text_comment_count);

            width = (int) (res.getDimension(R.dimen.chart_comment_text_width) * Config.mScaleFactor);
            height = (int) (res.getDimension(R.dimen.chart_comment_text_height) * Config.mScaleFactor);
            linearparams = (LinearLayout.LayoutParams) mTextComment.getLayoutParams();
            linearparams.leftMargin = (int) (res.getDimension(R.dimen.chart_like_text_left_margin) * Config.mScaleFactor);
            linearparams.width = width;
            linearparams.height = height;
            mTextComment.setLayoutParams(linearparams);
            mTextComment.setTextSize(textSize);

            // iTune link
            mBtnDownload = (Button) layout.findViewById(R.id.btn_download);
            mBtnDownload.setVisibility(View.INVISIBLE);

            width = (int) (res.getDimension(R.dimen.chart_itunes_button_width) * Config.mScaleFactor);
            height = (int) (res.getDimension(R.dimen.chart_itunes_button_height) * Config.mScaleFactor);
            params = (RelativeLayout.LayoutParams) mBtnDownload.getLayoutParams();
            params.width = width;
            params.height = height;
//        params.topMargin = likemargin;
            mBtnDownload.setLayoutParams(params);
        }
        else {
            layout = inflater.inflate(R.layout.video_list_item, null);

            int width = (int) res.getDimension(R.dimen.video_item_width);
            int height = (int) res.getDimension(R.dimen.video_item_height);
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
            layout.setLayoutParams(params);

            // Passed Day
            mTextDay = (TextView) layout.findViewById(R.id.text_video_day);

            // Title
            mTextTitle = (TextView) layout.findViewById(R.id.text_video_title);

            // Photo
            mLayoutImage = (RelativeLayout) layout.findViewById(R.id.video_image_frame);
            mLayoutImage.setClickable(true);
            mLayoutImage.setTag(itemData.youtubeID);
            mLayoutImage.setOnClickListener(onBtnPlayListener);

            // Liked Users
            mTextLikedUsers = (TextView) layout.findViewById(R.id.text_liked_users);

            // Like count
            mTextLikeCount = (TextView) layout.findViewById(R.id.text_like_count);

            // Comment count
            mTextComment = (TextView) layout.findViewById(R.id.text_comment_count);

            // Like Button
            mBtnLike = (Button) layout.findViewById(R.id.btn_like);

            // Comment Button
            mBtnComment = (Button) layout.findViewById(R.id.btn_comment);
        }


        // Passed Day
        mTextDay.setText(String.format("%d days", Utils.getPassedDays(itemData.pubDate)));

        // Title
        mTextTitle.setText(itemData.title);

        // Photo
        new Utils.LoadImageTask(mLayoutImage).execute(itemData.thumbnail);

        // Comment count
        mTextComment.setText(String.valueOf(itemData.commentCount));

        mBtnLike.setTag(String.valueOf(itemData.youtubeID));
        mBtnLike.setOnClickListener(onBtnLikeListener);

        mBtnComment.setTag(String.valueOf(itemData.youtubeID));
        mBtnComment.setOnClickListener(onBtnCommentListener);

        if (Utils.isTablet(fragment.getActivity())) {
            int padding = (int) (res.getDimension(R.dimen.chart_view_padding) * Config.mScaleFactor);
            setPadding(padding / 2, padding, padding / 2, padding);
            setGravity(Gravity.CENTER_HORIZONTAL);
        }
        else {
            int padding = (int) res.getDimension(R.dimen.chart_view_margin);
            setPadding(padding / 2, padding / 2, padding / 2, padding / 2);
            setGravity(Gravity.CENTER_HORIZONTAL);
        }

        addView(layout);
    }

    public void setLikeCount(int count) {
        mTextLikeCount.setText(String.valueOf(count));
    }

    public void setCommentCounts(int count) {
        mTextComment.setText(String.valueOf(count));
    }

    public static void setCommentCount(int count) {
        if (mTextSelectedComment != null)
            mTextSelectedComment.setText(String.valueOf(count));
    }

    public void setIsLiked(boolean liked) {
        mIsLiked = liked;

        if(liked)
            mBtnLike.setBackgroundResource(R.drawable.video_item_like_but);
        else
            mBtnLike.setBackgroundResource(R.drawable.video_item_unlike_but);
    }

    public void setLikedUsers(String[] users) {
        for (int i = 0; i < users.length; i++) {
            mLikedUsers.add(users[i]);
        }

        showLikedUsers();
    }

    private void showLikedUsers() {
        StringBuilder strShow = new StringBuilder();
        int i;
        int size = mLikedUsers.size();

        for (i = 0; i < size; i++) {
            if (i > 0) strShow.append(", ");

            if (mLikedUsers.get(i).equals(Config.mUserProfile.userName)) {
                strShow.append("I");
            } else {
                strShow.append(mLikedUsers.get(i));
            }

            if (i >= 4) break;
        }

        if (i > 0) {
            int nMore = size - i - 1;
            if (nMore > 0) {
                strShow.append(String.format(" and %d other", size - i - 1));
            }

            strShow.append(" liked it");
        }

        mTextLikedUsers.setText(strShow.toString());
        mTextLikeCount.setText(String.valueOf(size));
    }

    private OnClickListener onBtnPlayListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            String youTubeId = (String) view.getTag();
            Intent intent = new Intent(mContext, YouTubePlayerActivity.class);
            intent.putExtra(API_Manager.YOUTUBE_ID, youTubeId);
            mContext.startActivity(intent);
            ((FragmentActivity) mContext).overridePendingTransition(R.anim.anim_in, R.anim.stay);
        }
    };

    private OnClickListener onBtnLikeListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            String youtubeID = (String) view.getTag();
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put(API_Manager.USER_NAME, Config.mUserProfile.userName);
            params.put(API_Manager.YOUTUBE_ID, youtubeID);

            if (mIsLiked) {
                API_Manager.getInstance().runWebService(API_Manager.UNLIKE_VIDEO_ACTION, params, mFragment, true);
                mLikedUsers.remove(Config.mUserProfile.userName);
            } else {
                API_Manager.getInstance().runWebService(API_Manager.LIKE_VIDEO_ACTION, params, mFragment, true);
                mLikedUsers.add(Config.mUserProfile.userName);
            }

            mIsLiked = !mIsLiked;
            setIsLiked(mIsLiked);
            showLikedUsers();
        }
    };

    OnClickListener onBtnCommentListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            String youtubeID = (String) view.getTag();

            mTextSelectedComment = mTextComment;

            Intent intent = new Intent(mContext, CommentActivity.class);
            intent.putExtra(API_Manager.YOUTUBE_ID, youtubeID);
            intent.putExtra(CommentActivity.IS_CHART_ITEM, false);
            mContext.startActivity(intent);
            ((FragmentActivity) mContext).overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
        }
    };

}
