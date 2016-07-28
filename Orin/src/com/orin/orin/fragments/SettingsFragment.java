/**
 * @author Ry
 * @date 2013.12.27
 * @filename SettingsFragment.java
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

import com.orin.orin.DocViewActivity;
import com.orin.orin.LandingActivity;
import com.orin.orin.R;
import com.orin.orin.TourActivity;
import com.orin.orin.util.Config;
import com.orin.orin.util.Utils;

import br.com.dina.ui.model.BasicItem;
import br.com.dina.ui.widget.UIButton;
import br.com.dina.ui.widget.UIButtonWithSwitch;
import br.com.dina.ui.widget.UITableView;

public class SettingsFragment extends Fragment {

    private static final String TAG = SettingsFragment.class.getSimpleName();

    private static final int TAKE_TOUR = 0;
    private static final int SEND_FEEDBACK = 1;

    public static final int PRIVACY_POLICY = 0;
    public static final int TERMS_OF_SERVICE = 1;
    public static final int FAQS = 2;

    private FragmentActivity mActivity;

    private UITableView mTableEtc;
    private UITableView mTableDoc;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (FragmentActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_settings, null);
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
        UIButtonWithSwitch btnShare = (UIButtonWithSwitch) view.findViewById(R.id.btn_share);
        btnShare.setToggleState(Config.loadShareOption(mActivity));
        UIButtonWithSwitch btnPush = (UIButtonWithSwitch) view.findViewById(R.id.btn_push);
        btnPush.setToggleState(Config.loadPushOption(mActivity));

        UIButtonWithSwitchClickListener listener = new UIButtonWithSwitchClickListener();
        btnShare.addClickListener(listener);
        btnPush.addClickListener(listener);

        UIButton btnLogout = (UIButton) view.findViewById(R.id.btn_logout);
        UIButtonClickListener uiButtonClickListener = new UIButtonClickListener();
        btnLogout.addClickListener(uiButtonClickListener);

        mTableEtc = (UITableView) view.findViewById(R.id.tableView_etc);
        mTableDoc = (UITableView) view.findViewById(R.id.tableView_doc);
        createList();
    }

    private void createList() {
        mTableEtc.setClickListener(new UITableView.ClickListener() {
            @Override
            public void onClick(int index) {
                switch (index) {
                    case TAKE_TOUR:
                        mActivity.startActivity(new Intent(mActivity, TourActivity.class));
                        mActivity.overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                        break;

                    case SEND_FEEDBACK:
                        Utils.sendMail(mActivity, "hello@orin.io", "Send Feedback", "", false);
                        break;
                }
            }
        });
        BasicItem item = new BasicItem("Take a tour");
        mTableEtc.addBasicItem(item);

        item = new BasicItem("Send feedback");
        mTableEtc.addBasicItem(item);
        mTableEtc.commit();

        mTableDoc.setClickListener(new UITableView.ClickListener() {
            @Override
            public void onClick(int index) {
                Intent intent = new Intent(mActivity, DocViewActivity.class);
                intent.putExtra(DocViewActivity.DOC_TYPE, index);
                mActivity.startActivity(intent);
                mActivity.overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
            }
        });
        item = new BasicItem("Privacy Policy");
        mTableDoc.addBasicItem(item);
        item = new BasicItem("Terms of Service");
        mTableDoc.addBasicItem(item);
        item = new BasicItem("FAQs");
        mTableDoc.addBasicItem(item);
        mTableDoc.commit();
    }

    private class UIButtonWithSwitchClickListener implements UIButtonWithSwitch.ClickListener {
        @Override
        public void onClick(View view) {
            int id = view.getId();

            switch (id) {
                case R.id.btn_share:
                    boolean shareOption = !Config.loadShareOption(mActivity);
                    Config.saveShareOption(mActivity, shareOption);
                    break;

                case R.id.btn_push:
                    boolean pushOption = !Config.loadPushOption(mActivity);
                    Config.savePushOption(mActivity, pushOption);
                    break;
            }
        }
    }

    private class UIButtonClickListener implements UIButton.ClickListener {
        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.btn_logout) {
                Config.mUserProfile = null;
                Config.saveUserName(mActivity, "");
                Config.saveLoginState(mActivity, false);

                mActivity.startActivity(new Intent(mActivity, LandingActivity.class));
                mActivity.finish();
                mActivity.overridePendingTransition(R.anim.pop_in, R.anim.pop_out);
            }
        }
    }

}
