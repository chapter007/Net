package com.example.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.fragment.CnbetaFrame;
import com.example.fragment.EngadgetFrame;
import com.example.fragment.WeiboHotFrame;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class MainActivity extends AppCompatActivity{

	private DrawerLayout mDrawer;
	private ListView mNews;
	private ActionBarDrawerToggle mToogle;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_frame);
		mDrawer=(DrawerLayout) findViewById(R.id.left_drawer);
		mNews=(ListView) findViewById(R.id.news_list);
		
		CnbetaFrame cbFrame=new CnbetaFrame();
		getSupportFragmentManager().beginTransaction()
        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        .add(R.id.frame, cbFrame).commit();
		
		List<Map<String,Object>> data=new ArrayList<Map<String, Object>>();
        Map<String,Object> map=new HashMap<String, Object>();
        map.put("icon",R.drawable.cnbeta_little);
        map.put("content","cnbeta");
        data.add(map);
        Map<String,Object> map1=new HashMap<String, Object>();
        map1.put("icon", R.drawable.zhihu);
        map1.put("content","zhihuDay");
        data.add(map1);
        Map<String,Object> map2=new HashMap<String, Object>();
        map2.put("icon", R.drawable.weibo);
        map2.put("content","还没想好放什么");
        data.add(map2);
        SimpleAdapter adapter=new SimpleAdapter(this,data,R.layout.news_list,
                new String[]{"icon","content"},new int[]{R.id.Licon,R.id.Lcontent});
		mNews.setAdapter(adapter);
		mNews.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				mDrawer.closeDrawer(Gravity.LEFT);
				if (arg2==1) {
					EngadgetFrame engadgetFrame=new EngadgetFrame();
					fragmentChange(R.id.frame, engadgetFrame);
				}else if (arg2==0) {
					CnbetaFrame cbFrame=new CnbetaFrame();
					fragmentChange(R.id.frame, cbFrame);
				}else if (arg2==2) {
					WeiboHotFrame wbFrame=new WeiboHotFrame();
					fragmentChange(R.id.frame, wbFrame);
				}
			}
		});
	}



	private void fragmentChange(int layoutid,Fragment fragment){
		getSupportFragmentManager().beginTransaction()
        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        .replace(layoutid, fragment).commit();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

}
