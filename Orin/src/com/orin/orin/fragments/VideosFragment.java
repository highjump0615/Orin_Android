/**
 * @author Ry
 * @date 2013.12.12
 * @filename VideosFragment.java
 */

package com.orin.orin.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.orin.orin.R;
import com.orin.orin.api.API_Manager;
import com.orin.orin.api.ServiceResponse;
import com.orin.orin.data.VideoData;
import com.orin.orin.util.Config;
import com.orin.orin.util.Utils;
import com.orin.orin.view.VideoItemView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class VideosFragment extends Fragment implements API_Manager.RunningServiceListener {

    private static final String TAG = VideosFragment.class.getSimpleName();

    private LinearLayout mContainer;

    private int mTotalCount;
    private int mCurItem = 0;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        loadVideos();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_videos, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(params);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mContainer = (LinearLayout) view.findViewById(R.id.list_container);
    }

    private void loadVideos() {
        API_Manager.getInstance().runWebService(API_Manager.YOUTUBE_FEED_API, this, true);
    }

    @Override
    public void finishedRunningServiceListener(ServiceResponse response, String result) {
        Log.d(TAG, "receivedJson: " + result);

        if (response.serviceName == null) {
            try {
                JSONArray jsonArray = new JSONObject(result).getJSONObject("data").getJSONArray("items");
                mTotalCount = jsonArray.length();

                mContainer.removeAllViews();
                mCurItem = 0;

                LinearLayout rowlayout = null;

                for (int i = 0; i < mTotalCount; i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    VideoData videoData = new VideoData();

                    videoData.url = jsonObject.getJSONObject("player").getString("mobile");
                    //videoData.url = jsonObject.getJSONObject("content").getString("1");
                    videoData.title = jsonObject.getString("title");
                    videoData.pubDate = getAdjustDateFormat(jsonObject.getString("updated"));
                    videoData.thumbnail = jsonObject.getJSONObject("thumbnail").getString("sqDefault");
                    videoData.youtubeID = jsonObject.getString("id");

                    VideoItemView itemView = new VideoItemView(this, videoData);

                    if (Utils.isTablet(this.getActivity())) {
                        if (i % 3 == 0) {
                            rowlayout = new LinearLayout(this.getActivity());
                            rowlayout.setOrientation(LinearLayout.HORIZONTAL);
                            mContainer.addView(rowlayout);

//                        Resources res = activity.getResources();
//                        rowlayout.setOrientation(LinearLayout.HORIZONTAL);
//                        int padding = (int) (res.getDimension(R.dimen.chart_view_padding) * Config.mScaleFactor);
//                        rowlayout.setPadding(padding, padding, padding, padding);
                        }

                        if (rowlayout != null) {
                            rowlayout.addView(itemView);
                        }
                    }
                    else {
                        mContainer.addView(itemView);
                    }

                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put(API_Manager.USER_NAME, Config.mUserProfile.userName);
                    params.put(API_Manager.YOUTUBE_ID, videoData.youtubeID);
                    params.put(API_Manager.TAG_NUMBER, String.valueOf(i));

                    API_Manager.getInstance().runWebService(API_Manager.GET_VIDEO_LIKE_COUNT_ACTION,
                            params, VideosFragment.this, true);
                }
            } catch (JSONException e) {
                if (Config.DEBUG) e.printStackTrace();
            }
        } else if (response.serviceName.equals(API_Manager.GET_VIDEO_LIKE_COUNT_ACTION)) {
            try {
                JSONObject jsonObject = new JSONObject(result).getJSONObject("info");
                int likeNum = Integer.valueOf(jsonObject.getString("likes"));
                int commentNum = Integer.valueOf(jsonObject.getString("comments"));
                int tagNum = Integer.valueOf(jsonObject.getString("tagnumber"));
                boolean liked = (jsonObject.getInt("liked") == 1);

                JSONArray usersArray = jsonObject.getJSONArray("likedusers");
                int count = usersArray.length();
                String[] users = new String[count];
                for (int i = 0; i < count; i++) {
                    users[i] = (String) usersArray.get(i);
                }

                VideoItemView itemView;

                if (Utils.isTablet(getActivity())) {
                    LinearLayout rowlayout = (LinearLayout) mContainer.getChildAt(tagNum / 3);
                    itemView = (VideoItemView) rowlayout.getChildAt(tagNum % 3);
                }
                else {
                    itemView = (VideoItemView) mContainer.getChildAt(tagNum);
                }
                if (itemView != null) {
                    itemView.setLikeCount(likeNum);
                    itemView.setCommentCounts(commentNum);
                    itemView.setIsLiked(liked);
                    itemView.setLikedUsers(users);
                }

                mCurItem++;
            } catch (JSONException e) {
                if (Config.DEBUG) e.printStackTrace();
            }
        }

    }

    private String getAdjustDateFormat(String strDate) {
        // "2013-12-10T13:29:32.000Z" -> "2013-12-10 13:29:32"
        strDate = strDate.substring(0, 19);
        return strDate.replace("T", " ");
    }

}
