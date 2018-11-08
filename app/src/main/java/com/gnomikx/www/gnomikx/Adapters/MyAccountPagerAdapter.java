package com.gnomikx.www.gnomikx.Adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.gnomikx.www.gnomikx.FragmentFavoriteBlogs;
import com.gnomikx.www.gnomikx.FragmentMyBlogs;
import com.gnomikx.www.gnomikx.FragmentMyQueries;
import com.gnomikx.www.gnomikx.FragmentMyReports;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to act as adapter for the ViewPager of MyAccount
 */

public class MyAccountPagerAdapter extends FragmentPagerAdapter {

    private String tabTitles[];
    private List<Fragment> fragmentList;

    public MyAccountPagerAdapter(FragmentManager fm, String tabTitles[]) {
        super(fm);
        fragmentList = new ArrayList<>();
        this.tabTitles = tabTitles;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    public void addFragment(Fragment fragment) {
        fragmentList.add(fragment);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
