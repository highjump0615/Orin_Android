package com.orin.orin.viewpager_indicator;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.orin.orin.R;

public final class TourFragment extends Fragment {

    private static final String KEY_CONTENT = "TourFragment:Content";

    public static TourFragment newInstance(int resourceId, boolean isLast) {
        TourFragment fragment = new TourFragment();
        fragment.mContentResId = resourceId;
        fragment.mIsLast = isLast;

        return fragment;
    }

    private int mContentResId = 0;
    private boolean mIsLast = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((savedInstanceState != null) && savedInstanceState.containsKey(KEY_CONTENT)) {
            mContentResId = savedInstanceState.getInt(KEY_CONTENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ImageView imageView = new ImageView(getActivity());
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        if (mContentResId != 0) imageView.setBackgroundResource(mContentResId);

        RelativeLayout layout = new RelativeLayout(getActivity());
        layout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        layout.addView(imageView);

        if (mIsLast) {
            Button button = new Button(getActivity());
            button.setBackgroundResource(R.drawable.tour_close);

            Resources res = getActivity().getResources();
            int size = (int) res.getDimension(R.dimen.tour_close_button_size);
            int margin = (int) res.getDimension(R.dimen.tour_close_button_margin);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(size, size);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            params.setMargins(0, margin, margin, 0);
            button.setLayoutParams(params);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().finish();
                    getActivity().overridePendingTransition(R.anim.pop_in, R.anim.pop_out);
                }
            });

            layout.addView(button);
        }

        return layout;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CONTENT, mContentResId);
    }

}
