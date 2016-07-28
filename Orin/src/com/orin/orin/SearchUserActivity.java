/**
 * @author Ry
 * @date 2014.01.06
 * @filename SearchUserActivity.java
 */

package com.orin.orin;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orin.orin.api.API_Manager;
import com.orin.orin.api.LikeCountResult;
import com.orin.orin.api.ProfileResult;
import com.orin.orin.api.ServiceResponse;
import com.orin.orin.data.UserProfile;
import com.orin.orin.fragments.FindFriendsFragment;
import com.orin.orin.util.Config;
import com.orin.orin.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;

import br.com.dina.ui.model.BasicItem;
import br.com.dina.ui.widget.UITableView;

public class SearchUserActivity extends Activity implements API_Manager.RunningServiceListener {

    private static final String TAG = SearchUserActivity.class.getSimpleName();

    public static final String SEARCH_TYPE = "search_type";

    private int mSearchType = -1;
    private int mCurrentRow = 0;
    private UITableView mTableFriends;

    ArrayList<String> mUserList = new ArrayList<String>();
    public static UserProfile mUserProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null) {
            mSearchType = intent.getIntExtra(SEARCH_TYPE, -1);
        }

        setContentView(R.layout.activity_search_users);
        initViews();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.pop_in, R.anim.pop_out);
    }

    private void initViews() {
        final Resources res = getResources();

        int size = (int) (res.getDimension(R.dimen.title_bar_height) * Config.mScaleFactor);
        int margin = (int) (res.getDimension(R.dimen.title_bar_margin) * Config.mScaleFactor);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.layout_title_bar);
        ViewGroup.LayoutParams layoutParams = layout.getLayoutParams();
        layoutParams.height = size;
        layout.setLayoutParams(layoutParams);

        // Menu Image
        ImageView imageView = (ImageView) findViewById(R.id.btn_back);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        params.width = params.height = size;
        params.leftMargin = margin;
        imageView.setLayoutParams(params);

        // Logo ImageView
        imageView = (ImageView) findViewById(R.id.img_logo);
        params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        params.width = params.height = size;
        imageView.setLayoutParams(params);

        final EditText editSearch = (EditText) findViewById(R.id.edit_search);
        editSearch.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        editSearch.setImeActionLabel("Search", KeyEvent.KEYCODE_ENTER);
        editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    searchUser(editSearch.getText().toString());
                    return true;
                }
                return false;
            }
        });

        String hint = "";
        switch (mSearchType) {
            case FindFriendsFragment.USERNAME_SEARCH:
                hint = "Search for a user";
                break;

            case FindFriendsFragment.FACEBOOK_FRIENDS_SEARCH:
                hint = "Search for a facebook user";
                break;

            case FindFriendsFragment.TWITTER_FRIENDS_SEARCH:
                hint = "Search for a twitter user";
                break;
        }
        editSearch.setHint(hint);

        mTableFriends = (UITableView) findViewById(R.id.tableView_users);
    }

    private void searchUser(String keyword) {
        if (TextUtils.isEmpty(keyword)) {
            Utils.createErrorAlertDialog(this, "Alert", "Input keyword to search.").show();
            return;
        }

        mUserList.clear();

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("name", keyword);
        params.put("usertype", String.valueOf(mSearchType));
        API_Manager.getInstance().runWebService(API_Manager.SEARCH_USER_ACTION, params, this, true);
    }

    private void createList() {
        TableItemClickListener listener = new TableItemClickListener();
        mTableFriends.setClickListener(listener);
        mTableFriends.clear();

        if (mUserList.size() > 0) {
            for (String username : mUserList) {
                BasicItem item = new BasicItem(username);
                mTableFriends.addBasicItem(item);
            }
        }

        mTableFriends.commit();
    }

    private class TableItemClickListener implements UITableView.ClickListener {
        @Override
        public void onClick(int index) {
            mCurrentRow = index;
            String strName = mUserList.get(index);
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("name", strName);
            API_Manager.getInstance().runWebService(API_Manager.GET_USER_PROFILE_ACTION, params, SearchUserActivity.this, true);
        }
    }

    @Override
    public void finishedRunningServiceListener(ServiceResponse response, String result) {
        if (response.serviceName == null) return;

        if (response.serviceName.equals(API_Manager.SEARCH_USER_ACTION)) {
            if (response.status.equals("OK")) {
                String[] users = API_Manager.parseDataArray(result, String[].class);

                if (users.length > 0) {
                    for (String strName : users) {
                        if (!strName.equals(Config.mUserProfile.userName)) {
                            mUserList.add(strName);
                        }
                    }
                }

                createList();
            }
        }
        else if (response.serviceName.equals(API_Manager.GET_USER_PROFILE_ACTION)) {
            if (response.status.equals("OK")) {
                ProfileResult profileResult = API_Manager.parseData(result, ProfileResult.class);

                if (profileResult != null) {
                    mUserProfile = new UserProfile();
                    UserProfile profile = mUserProfile;
                    profile.userName = mUserList.get(mCurrentRow);
                    profile.email = profileResult.email;
                    profile.fullName = profileResult.fullname;
                    profile.website = profileResult.website;
                    profile.aboutme = profileResult.aboutme;
                    profile.phone = profileResult.phone;
                    profile.gender = profileResult.gender;

                    profile.followers = profileResult.follower;
                    profile.following = profileResult.following;

                    profile.inviteCount = profileResult.invitecount;
                    profile.downloadCount = profileResult.itunecount;
                    profile.shareCount = profileResult.sharecount;
                    new Utils.GetUserImageTask(profile).execute(String.format("%s/userimage/%s.jpg",
                            API_Manager.API_PATH, mUserProfile.userName));

                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("name", profile.userName);
                    API_Manager.getInstance().runWebService(API_Manager.GET_MUSIC_LIKE_COUNT_ACTION, params, this, true);
                }
            }
        }
        else if (response.serviceName.equals(API_Manager.GET_MUSIC_LIKE_COUNT_ACTION)) {
            if (response.status.equals("OK")) {
                LikeCountResult likeCountResult = API_Manager.parseData(result, LikeCountResult.class);

                UserProfile profile = mUserProfile;
                profile.hiphopCount = likeCountResult.hiphop;
                profile.rnbCount = likeCountResult.rnb;
                profile.afrobeatCount = likeCountResult.afrobeat;
                profile.otherLikeCount = response.count - profile.hiphopCount - profile.rnbCount - profile.afrobeatCount;

                startActivity(new Intent(this, OtherProfileActivity.class));
                overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
            }
        }
    }

}
