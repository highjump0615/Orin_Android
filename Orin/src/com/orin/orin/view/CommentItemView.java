/**
 * @author Ry
 * @Date 2013.12.22
 * @FileName CommentItemView.java
 *
 */

package com.orin.orin.view;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orin.orin.R;
import com.orin.orin.api.API_Manager;
import com.orin.orin.api.CommentResult;
import com.orin.orin.util.Config;
import com.orin.orin.util.Utils;

public class CommentItemView extends LinearLayout {

    public CommentItemView(final Context context, final CommentResult commentInfo) {
        super(context);

        final Resources res = context.getResources();

        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.comment_list_item, null);

        assert layout != null;
        int height = (int) (res.getDimension(R.dimen.comment_item_height) * Config.mScaleFactor);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        layout.setLayoutParams(layoutParams);
        int padding = (int) (res.getDimension(R.dimen.comment_item_padding) * Config.mScaleFactor);
        layout.setPadding(padding, 0, padding, 0);

        // Photo ImageView
        RelativeLayout layoutImage = (RelativeLayout) layout.findViewById(R.id.comment_image_frame);
        new Utils.LoadImageTask(layoutImage).execute(String.format("%s/userimage/%s.jpg", API_Manager.API_PATH, commentInfo.username));

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layoutImage.getLayoutParams();
        int width = (int) (res.getDimension(R.dimen.comment_item_image_size) * Config.mScaleFactor);
        params.height = params.width = width;
        layoutImage.setLayoutParams(params);

        // Author name
        TextView textAuthorName = (TextView) layout.findViewById(R.id.text_author_name);
        textAuthorName.setText(commentInfo.username);

        params = (RelativeLayout.LayoutParams) textAuthorName.getLayoutParams();
        params.width = (int) (res.getDimension(R.dimen.comment_item_text_name_width) * Config.mScaleFactor);
        params.height = (int) (res.getDimension(R.dimen.comment_item_text_name_height) * Config.mScaleFactor);
        params.leftMargin = (int) (res.getDimension(R.dimen.comment_item_text_name_margin_left) * Config.mScaleFactor);
        params.topMargin = (int) (res.getDimension(R.dimen.comment_item_text_name_margin_top) * Config.mScaleFactor);
        float textSize = res.getDimension(R.dimen.comment_item_text_name_text_size) * Config.mFontScaleFactor;
        textAuthorName.setLayoutParams(params);
        textAuthorName.setTextSize(textSize);

        // Comment
        TextView textComment = (TextView) layout.findViewById(R.id.text_comment);
        textComment.setText(commentInfo.content);

        params = (RelativeLayout.LayoutParams) textComment.getLayoutParams();
        params.width = (int) (res.getDimension(R.dimen.comment_item_text_comment_width) * Config.mScaleFactor);
        params.height = (int) (res.getDimension(R.dimen.comment_item_text_comment_height) * Config.mScaleFactor);
        params.topMargin = (int) (res.getDimension(R.dimen.comment_item_text_comment_margin_top) * Config.mScaleFactor);
        textSize = res.getDimension(R.dimen.comment_item_text_comment_text_size) * Config.mFontScaleFactor;
        textComment.setLayoutParams(params);
        textComment.setTextSize(textSize);

        // Date
        TextView textDate = (TextView) layout.findViewById(R.id.text_comment_time);
        textDate.setText(commentInfo.time);

        params = (RelativeLayout.LayoutParams) textDate.getLayoutParams();
        params.width = (int) (res.getDimension(R.dimen.comment_item_text_comment_time_width) * Config.mScaleFactor);
        params.height = (int) (res.getDimension(R.dimen.comment_item_text_comment_time_height) * Config.mScaleFactor);
        textSize = res.getDimension(R.dimen.comment_item_text_comment_time_text_size) * Config.mFontScaleFactor;
        textDate.setLayoutParams(params);
        textDate.setTextSize(textSize);

        addView(layout);
    }

}
