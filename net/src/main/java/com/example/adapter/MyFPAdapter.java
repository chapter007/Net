package com.example.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class MyFPAdapter extends FragmentPagerAdapter{

	ArrayList<Fragment> list;
	private ArrayList<String> titleContainer = new ArrayList<String>();
	
	public MyFPAdapter(FragmentManager fm,ArrayList<Fragment> list) {
		super(fm);
		this.list=list;
		titleContainer.add("主页");
        titleContainer.add("Top10");
	}

	@Override
	public Fragment getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public int getCount() {
		return list.size();
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		return titleContainer.get(position);
	}

}
