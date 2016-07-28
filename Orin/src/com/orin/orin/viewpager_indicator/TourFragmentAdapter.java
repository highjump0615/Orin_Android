package com.orin.orin.viewpager_indicator;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.orin.orin.R;
import com.viewpagerindicator.IconPagerAdapter;

public class TourFragmentAdapter extends FragmentPagerAdapter implements IconPagerAdapter {

    protected static final int[] CONTENT = new int[] {
            R.drawable.tour1,
            R.drawable.tour2,
            R.drawable.tour3,
            R.drawable.tour4,
            R.drawable.tour5
    };

    protected static final String[] TITLES = {
            "", "", "", "", ""
    };

    private int mCount = CONTENT.length;

    public TourFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return TourFragment.newInstance(CONTENT[position], mCount == (position + 1));
    }

    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TourFragmentAdapter.TITLES[position];
    }

    @Override
    public int getIconResId(int index) {
        return CONTENT[index];
    }

    public void setCount(int count) {
        if (count > 0 && count <= 10) {
            mCount = count;
            notifyDataSetChanged();
        }
    }

}