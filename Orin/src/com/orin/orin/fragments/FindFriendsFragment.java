/**
 * @author Ry
 * @date 2013.12.27
 * @filename FindFriendsFragment.java
 */

package com.orin.orin.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.orin.orin.InviteFriendsActivity;
import com.orin.orin.R;
import com.orin.orin.SearchUserActivity;

import br.com.dina.ui.model.BasicItem;
import br.com.dina.ui.widget.UIButton;
import br.com.dina.ui.widget.UITableView;

public class FindFriendsFragment extends Fragment {

    private static final String TAG = FindFriendsFragment.class.getSimpleName();

    public static final int USERNAME_SEARCH = 0;
    public static final int FACEBOOK_FRIENDS_SEARCH = 1;
    public static final int TWITTER_FRIENDS_SEARCH = 2;

    private FragmentActivity mActivity;

    private UITableView mTableFriends;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (FragmentActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_find_friends, null);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        assert view != null;
        view.setLayoutParams(params);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
    }

    private void initViews(View view) {
        UIButton btnInvite = (UIButton) view.findViewById(R.id.btn_invite_friends);
        UIButton btnSearch = (UIButton) view.findViewById(R.id.btn_search_name);

        UIButtonClickListener listener = new UIButtonClickListener();
        btnInvite.addClickListener(listener);
        btnSearch.addClickListener(listener);

        mTableFriends = (UITableView) view.findViewById(R.id.tableView_friends);
        createList();
        mTableFriends.commit();
    }

    private void createList() {
        TableItemClickListener listener = new TableItemClickListener();
        mTableFriends.setClickListener(listener);
        BasicItem item1 = new BasicItem("Facebook friends");
        mTableFriends.addBasicItem(item1);

        BasicItem item2 = new BasicItem("Twitter friends");
        //item2.setDrawable(R.drawable.user_image);
        //item2.setSubtitle("inactive");
        mTableFriends.addBasicItem(item2);
    }

    private void searchUsers(int type) {
        Intent intent = new Intent(mActivity, SearchUserActivity.class);
        intent.putExtra(SearchUserActivity.SEARCH_TYPE, type);
        mActivity.startActivity(intent);
        mActivity.overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
    }

    private class UIButtonClickListener implements UIButton.ClickListener {
        @Override
        public void onClick(View view) {
            int id = view.getId();

            switch (id) {
                case R.id.btn_invite_friends:
                    Intent intent = new Intent(mActivity, InviteFriendsActivity.class);
                    mActivity.startActivity(intent);
                    mActivity.overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                    break;

                case R.id.btn_search_name:
                    searchUsers(USERNAME_SEARCH);
                    break;
            }
        }
    }

    private class TableItemClickListener implements UITableView.ClickListener {
        @Override
        public void onClick(int index) {
            searchUsers(index == 0 ? FACEBOOK_FRIENDS_SEARCH : TWITTER_FRIENDS_SEARCH);
        }
    }

}
