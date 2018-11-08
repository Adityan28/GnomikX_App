package com.gnomikx.www.gnomikx;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gnomikx.www.gnomikx.Adapters.MyAccountPagerAdapter;

/**
 * Fragment class for displaying user's account
 */

public class FragmentMyAccount extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_my_account, container, false);

        //finding views
        ViewPager viewPager = rootView.findViewById(R.id.my_account_view_pager);
        TabLayout tabLayout = rootView.findViewById(R.id.my_account_pager_tab_layout);

        String tabTitles[] = {getString(R.string.my_reports), getString(R.string.my_queries),
                                getString(R.string.favorite_blogs), getString(R.string.my_blogs)};

        setUpViewPager(viewPager, tabTitles);

        //setting up tab layout to coordinate with view pager
        tabLayout.setupWithViewPager(viewPager);

        return rootView;
    }

    private void setUpViewPager(ViewPager viewPager, String[] tabTitles) {
        //creating object of pagerAdapter
        MyAccountPagerAdapter pagerAdapter = new MyAccountPagerAdapter(getChildFragmentManager(), tabTitles);
        pagerAdapter.addFragment(new FragmentMyReports());
        pagerAdapter.addFragment(new FragmentMyQueries());
        pagerAdapter.addFragment(new FragmentFavoriteBlogs());
        pagerAdapter.addFragment(new FragmentMyBlogs());
        //setting adapter of view pager
        viewPager.setAdapter(pagerAdapter);
    }
}
