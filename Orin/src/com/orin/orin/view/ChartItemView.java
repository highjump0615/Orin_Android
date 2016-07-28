/**
 * @author Ry
 * @Date 2013.12.17
 * @FileName ChartItemView.java
 *
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
import com.orin.orin.data.SongData;
import com.orin.orin.fragments.ChartsFragment;
import com.orin.orin.util.Config;
import com.orin.orin.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;

public class ChartItemView extends LinearLayout {

    private Context mContext;
    private Fragment mFragment;

    private TextView mTextDay;
    private TextView mTextTitle;
    private RelativeLayout mLayoutImage;
    private Button mBtnPlay;
    private TextView mTextLikedUsers;
    private TextView mTextLikeCount;
    private TextView mTextComment;
    private Button mBtnDownload;
    private Button mBtnLike;
    private Button mBtnComment;

    private static TextView mTextSelectedComment = null;

    private boolean mIsLiked;

    private ArrayList<String> mLikedUsers = new ArrayList<String>();


    public ChartItemView(final Fragment fragment, final SongData itemData) {
        super(fragment.getActivity());

        mContext = fragment.getActivity();
        mFragment = fragment;

        for (int i = 0; i < itemData.likeCount; i++) {
            mLikedUsers.add(itemData.likedusers[i]);
        }
        mIsLiked = (itemData.liked == 1);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        View layout = inflater.inflate(R.layout.chart_list_item, null);

        final Resources res = mContext.getResources();
        int width = (int) (res.getDimension(R.dimen.chart_view_width) * Config.mScaleFactor);
        int height = (int) (res.getDimension(R.dimen.chart_view_height) * Config.mScaleFactor);
        LayoutParams layoutParams = new LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);

        layout.setLayoutParams(layoutParams);

        // Passed Day
        mTextDay = (TextView) layout.findViewById(R.id.text_day);
        mTextDay.setText(String.format("%d days", Utils.getPassedDays(itemData.pubDate)));

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

        // Container
        RelativeLayout container = (RelativeLayout) layout.findViewById(R.id.container);
        params = (RelativeLayout.LayoutParams) container.getLayoutParams();
        params.setMargins(margin, margin, margin, margin);
        container.setLayoutParams(params);

        // Title
        mTextTitle = (TextView) layout.findViewById(R.id.text_title);
        mTextTitle.setText(itemData.title);
        mTextTitle.setTextSize(textSize);

        // Chart Mark
        ImageView imageMark = (ImageView) layout.findViewById(R.id.img_mark);
        width = (int) (res.getDimension(R.dimen.chart_mark_image_size) * Config.mScaleFactor);
        layoutParams = new LayoutParams(width, width);
        imageMark.setLayoutParams(layoutParams);

        // Photo
        mLayoutImage = (RelativeLayout) layout.findViewById(R.id.chart_image_frame);
        new Utils.LoadImageTask(mLayoutImage).execute(String.format("%s/song/image/%s", API_Manager.API_PATH, itemData.strImage));

        width = (int) (res.getDimension(R.dimen.chart_photo_image_size) * Config.mScaleFactor);
        params = (RelativeLayout.LayoutParams) mLayoutImage.getLayoutParams();
        params.width = width;
        params.height = width;
        params.setMargins(0, margin, 0, 0);
        mLayoutImage.setLayoutParams(params);

        // Play button
        mBtnPlay = (Button) layout.findViewById(R.id.btn_play);
        mBtnPlay.setBackgroundResource(R.drawable.chart_play);
        mBtnPlay.setTag(itemData.strSong);
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
        mBtnLike.setTag(String.valueOf(itemData.nIndex));
        mBtnLike.setOnClickListener(onBtnLikeListener);

        width = (int) (res.getDimension(R.dimen.chart_like_button_size) * Config.mScaleFactor);
        LinearLayout.LayoutParams linearparams = (LinearLayout.LayoutParams) mBtnLike.getLayoutParams();
//        int nTopMargin = (int) (res.getDimension(R.dimen.chart_like_button_top_margin) * Config.mScaleFactor);
//        linearparams.topMargin = nTopMargin;
        linearparams.width = width;
        linearparams.height = width;
        mBtnLike.setLayoutParams(linearparams);

        // Like count
        mTextLikeCount = (TextView) layout.findViewById(R.id.text_like_count);
        showLikedUsers();

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
        mBtnComment.setTag(String.valueOf(itemData.nIndex));
        mBtnComment.setOnClickListener(onBtnCommentListener);

        width = (int) (res.getDimension(R.dimen.chart_comment_button_size) * Config.mScaleFactor);
        linearparams = (LinearLayout.LayoutParams) mBtnComment.getLayoutParams();
//        nTopMargin = (int) (res.getDimension(R.dimen.chart_comment_button_top_margin) * Config.mScaleFactor);
//        linearparams.topMargin = nTopMargin;
        linearparams.width = width;
        linearparams.height = width;
        mBtnComment.setLayoutParams(linearparams);

        // Comment count
        mTextComment = (TextView) layout.findViewById(R.id.text_comment_count);
        mTextComment.setText(String.valueOf(itemData.commentCount));

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
        mBtnDownload.setOnClickListener(onBtnDownloadListener);

        width = (int) (res.getDimension(R.dimen.chart_itunes_button_width) * Config.mScaleFactor);
        height = (int) (res.getDimension(R.dimen.chart_itunes_button_height) * Config.mScaleFactor);
        params = (RelativeLayout.LayoutParams) mBtnDownload.getLayoutParams();
        params.width = width;
        params.height = height;
//        params.topMargin = likemargin;
        mBtnDownload.setLayoutParams(params);


        if (mIsLiked) {
            mBtnLike.setBackgroundResource(R.drawable.like_icon);
        } else {
            mBtnLike.setBackgroundResource(R.drawable.unlike_icon);
        }

        int padding = (int) (res.getDimension(R.dimen.chart_view_padding) * Config.mScaleFactor);
        setGravity(Gravity.CENTER_HORIZONTAL);

        if (Utils.isTablet(fragment.getActivity())) {
            setPadding(padding / 2, padding, padding / 2, padding);
        }
        else {
            setPadding(0, padding, 0, padding);
        }

        addView(layout);
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

    public static void setCommentCount(int count) {
        if (mTextSelectedComment != null)
            mTextSelectedComment.setText(String.valueOf(count));
    }

    private OnClickListener onBtnPlayListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            ChartsFragment chartsFragment = ((ChartsFragment) mFragment);

            if (!chartsFragment.mSuccess) {
                if (view.equals(ChartsFragment.mCurPlayingButton))
                    view.setBackgroundResource(R.drawable.chart_play);
            }
            else {
                if (chartsFragment.mIsPlaying) {
                    if (view.equals(ChartsFragment.mCurPlayingButton)) {
                        view.setBackgroundResource(R.drawable.chart_play);
                        ChartsFragment.pauseMusic();
                    }
                } else {
                    if (!view.equals(ChartsFragment.mCurPlayingButton)) {
                        String songName = (String) view.getTag();

                        ChartsFragment.initPlayer(String.format("%s/song/%s", API_Manager.API_PATH, songName));
    //                    ChartsFragment.initPlayer("http://orin.io/webservice/song/Mind%20Reader.aac");
    //                    ChartsFragment.initPlayer("http://orin.io/webservice/song/BE SOMEBODYKaycee.mp3");

                    } else {
                        ChartsFragment.playMusic();
                    }

                    view.setBackgroundResource(R.drawable.chart_pause);
                    ChartsFragment.mCurPlayingButton = (Button) view;
                }
            }
        }
    };

    private OnClickListener onBtnDownloadListener = new OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private OnClickListener onBtnLikeListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            String songID = (String) view.getTag();
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("username", Config.mUserProfile.userName);
            params.put("musicid", songID);

            if (mIsLiked) {
                view.setBackgroundResource(R.drawable.unlike_icon);
                API_Manager.getInstance().runWebService(API_Manager.UNLIKE_MUSIC_ACTION, params, mFragment, false);
                mLikedUsers.remove(Config.mUserProfile.userName);
            } else {
                view.setBackgroundResource(R.drawable.like_icon);
                API_Manager.getInstance().runWebService(API_Manager.LIKE_MUSIC_ACTION, params, mFragment, false);
                mLikedUsers.add(Config.mUserProfile.userName);
            }

            mIsLiked = !mIsLiked;
            showLikedUsers();
        }
    };

    OnClickListener onBtnCommentListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            String songID = (String) view.getTag();

            mTextSelectedComment = mTextComment;

            Intent intent = new Intent(mContext, CommentActivity.class);
            intent.putExtra(API_Manager.MUSIC_ID, songID);
            intent.putExtra(CommentActivity.IS_CHART_ITEM, true);
            mContext.startActivity(intent);
            ((FragmentActivity) mContext).overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
        }
    };

}
