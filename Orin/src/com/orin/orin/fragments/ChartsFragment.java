/**
 * @author Ry
 * @date 2013.12.12
 * @filename ChartsFragment.java
 */

package com.orin.orin.fragments;

import android.app.Activity;
import android.content.res.Resources;
import android.media.AsyncPlayer;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.orin.orin.R;
import com.orin.orin.api.API_Manager;
import com.orin.orin.api.GetMusicResult;
import com.orin.orin.api.ServiceResponse;
import com.orin.orin.data.SongData;
import com.orin.orin.data.UserProfile;
import com.orin.orin.util.Config;
import com.orin.orin.util.Utils;
import com.orin.orin.view.ChartItemView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

public class ChartsFragment extends Fragment implements API_Manager.RunningServiceListener {

    private static final String TAG = ChartsFragment.class.getSimpleName();
    private static final int GET_MUSIC_NUM = 3;

    private PullToRefreshScrollView mPullRefreshScrollView;
    private ScrollView mScrollView;
    private LinearLayout mContainer;

    private static Activity activity;
    private int mCurCount;
    private int mTotalCount;

    public static boolean mIsPlaying = false;
    public static boolean mSuccess = true;
    public static int mCurPlayPosition = 0;
    private static MediaPlayer mMediaPlayer;
    public static Button mCurPlayingButton;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ChartsFragment.activity = activity;

        loadMusic(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_ptr_scrollview, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(params);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPullRefreshScrollView = (PullToRefreshScrollView) view.findViewById(R.id.pull_refresh_scrollview);
        mPullRefreshScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                new GetDataTask().execute();
            }
        });

        mScrollView = mPullRefreshScrollView.getRefreshableView();
        mContainer = (LinearLayout) view.findViewById(R.id.list_container);
    }

    @Override
    public void onDestroyView() {
        stopMusic();
        super.onDestroyView();
    }

    /**
     * Load Music data from web server
     *
     * @param bShowWaiting
     */
    private void loadMusic(boolean bShowWaiting) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("username", Config.mUserProfile.userName);

        API_Manager.getInstance().runWebService(API_Manager.GET_MUSIC_ACTION, params, ChartsFragment.this, bShowWaiting);
    }

    @Override
    public void finishedRunningServiceListener(ServiceResponse response, String result) {
        Log.d(TAG, "receivedJson: " + result);

        if (TextUtils.isEmpty(response.serviceName))
            return;

        if (response.serviceName.equals(API_Manager.GET_MUSIC_ACTION)) {
            if (response.count == 0) return;

            GetMusicResult[] items = API_Manager.parseDataArray(result, GetMusicResult[].class);

            int nPrevCount = mCurCount;
            mTotalCount = response.count;
            mCurCount = Math.min(mCurCount + GET_MUSIC_NUM, mTotalCount);
            int nCount = -1;

            if (Utils.isTablet(activity)) {
                if (mCurCount == GET_MUSIC_NUM)
                    mCurCount += GET_MUSIC_NUM;
            }

            API_Manager.initProgressDialog(getActivity());
            API_Manager.showProgress();

            LinearLayout rowlayout = null;

            for (GetMusicResult item : items) {
                nCount++;

                if (nCount < nPrevCount)
                    continue;

                if (nCount >= mCurCount)
                    break;

                SongData data = new SongData();
                data.url = item.itunelink;
                data.title = item.name;
                data.pubDate = item.time;
                data.likeCount = item.likes;
                data.commentCount = item.comments;
                data.strImage = item.imagefile;
                data.nIndex = item.id;
                data.strSong = item.songfile;
                data.liked = item.liked;
                data.likedusers = item.likedusers;

                ChartItemView itemView = new ChartItemView(this, data);

                if (Utils.isTablet(activity)) {
                    if (nCount % 3 == 0) {
                        rowlayout = new LinearLayout(activity);
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
            }
            API_Manager.dismissProgress();

        } else if (response.serviceName.equals(API_Manager.LIKE_MUSIC_ACTION)) {
            if (response.status.equals("OK")) {

                UserProfile profile = Config.mUserProfile;
                int nValue = Integer.parseInt((String) response.info);
                if (nValue == 0)
                    profile.otherLikeCount++;
                else if (nValue == 1)
                    profile.hiphopCount++;
                else if (nValue == 2)
                    profile.rnbCount++;
                else if (nValue == 3)
                    profile.afrobeatCount++;
            }
        } else if (response.serviceName.equals(API_Manager.UNLIKE_MUSIC_ACTION)) {
            if (response.status.equals("OK")) {
                UserProfile profile = Config.mUserProfile;
                int nValue = Integer.parseInt((String) response.info);
                if (nValue == 0)
                    profile.otherLikeCount--;
                else if (nValue == 1)
                    profile.hiphopCount--;
                else if (nValue == 2)
                    profile.rnbCount--;
                else if (nValue == 3)
                    profile.afrobeatCount--;
            }
        }
    }

    private class GetDataTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("username", Config.mUserProfile.userName);

            return API_Manager.getInstance().runWebService(API_Manager.GET_MUSIC_ACTION, params);
        }

        @Override
        protected void onPostExecute(String result) {
            // Do some stuff here
            finishedRunningServiceListener(API_Manager.mServiceResponse, result);

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScrollView.smoothScrollBy(0, mScrollView.getBottom() / 4);
                }
            }, 100);

            // Call onRefreshComplete when the list has been refreshed.
            mPullRefreshScrollView.onRefreshComplete();

            super.onPostExecute(result);
        }
    }

    /**
     * If the user has specified a local url, then we download the
     * url stream to a temporary location and then call the setDataSource
     * for that local file
     *
     * @param path
     * @throws IOException
     */
    private static void setDataSource(String path) throws IOException {
        if (!URLUtil.isNetworkUrl(path)) {
            mMediaPlayer.setDataSource(path);
        } else {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepareAsync();

//            URL url = new URL(path);
//            URLConnection cn = url.openConnection();
//            cn.connect();
//            InputStream stream = cn.getInputStream();
//            if (stream == null)
//                throw new RuntimeException("stream is null");
//            File temp = File.createTempFile("mediaplayertmp", "dat");
//            String tempPath = temp.getAbsolutePath();
//            FileOutputStream out = new FileOutputStream(temp);
//            byte buf[] = new byte[128];
//            do {
//                int numread = stream.read(buf);
//                if (numread <= 0)
//                    break;
//                out.write(buf, 0, numread);
//            } while (true);
//            mMediaPlayer.setDataSource(tempPath);
//            try {
//                stream.close();
//            }
//            catch (IOException ex) {
//                Log.e(TAG, "error: " + ex.getMessage(), ex);
//            }
        }
    }

    public static void initPlayer(String musicPath) {
        Log.i(TAG, "music url = " + musicPath);

        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }

        mMediaPlayer = new MediaPlayer();

        try {
            setDataSource(musicPath);
            API_Manager.initProgressDialog(activity);
            API_Manager.showProgress();
            mSuccess = true;

            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    API_Manager.dismissProgress();

                    mMediaPlayer.start();
                    mCurPlayPosition = 0;
                    mIsPlaying = true;
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mCurPlayingButton.performClick();
                    mIsPlaying = false;
                    mSuccess = true;
                    mCurPlayPosition = 0;
                }
            });
            mMediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {
                }
            });
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                    API_Manager.dismissProgress();

                    Utils.createErrorAlertDialog(activity, "Alert", "Failed to load music").show();

                    mSuccess = false;
                    return false;
                }
            });
        } catch (IOException e) {
            if (Config.DEBUG) e.printStackTrace();
        }
    }

    public static void playMusic() {
        if (mMediaPlayer != null) {
            mIsPlaying = true;
            mMediaPlayer.seekTo(mCurPlayPosition);
            mMediaPlayer.start();
        }
    }

    public static void pauseMusic() {
        if (mMediaPlayer != null) {
            mIsPlaying = false;
            mCurPlayPosition = mMediaPlayer.getCurrentPosition();
            mMediaPlayer.pause();
        }
    }

    public static void stopMusic() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

}
