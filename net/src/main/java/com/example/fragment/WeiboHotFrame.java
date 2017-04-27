package com.example.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.net.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangjie on 2017/1/12.
 */
public class WeiboHotFrame extends Fragment {
    private ViewPager viewPager;
    private ArrayList<Fragment> views;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.weibo_frame, null);
        initToolbar(view);
        initViewPagerAndTabs(view);
        return view;
    }

    private void initToolbar(View view) {
        Toolbar mToolbar = (Toolbar) view.findViewById(R.id.toolbar);
        mToolbar.setTitle("还没想好放什么");
        DrawerLayout mDrawer= (DrawerLayout) getActivity().findViewById(R.id.left_drawer);
        ActionBarDrawerToggle mToogle=new ActionBarDrawerToggle(getActivity(),mDrawer,mToolbar,0,0);
        mToogle.setDrawerIndicatorEnabled(true);
        mToogle.syncState();
        mDrawer.setDrawerListener(mToogle);
    }

    private void initViewPagerAndTabs(View view) {
        viewPager = (ViewPager) view.findViewById(R.id.weibo_viewpager);
        PagerAdapter pagerAdapter = new PagerAdapter(getChildFragmentManager());
        WeiboHot weiboHot=new WeiboHot();
        pagerAdapter.addFragment(weiboHot, "主页");
        //pagerAdapter.addFragment(mCnbetaTop, "TOP10");
        viewPager.setAdapter(pagerAdapter);

    }

    static class PagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitleList = new ArrayList<>();

        public PagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitleList.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleList.get(position);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public class MyOnPageChangelistener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int arg0) {

        }
    }
}
