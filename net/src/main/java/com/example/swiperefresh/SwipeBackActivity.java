
package com.example.swiperefresh;

import com.example.net.R;
import com.example.net.ToolBar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;



public class SwipeBackActivity extends ToolBar {
	protected SwipeBackLayout layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		layout = (SwipeBackLayout) LayoutInflater.from(this).inflate(
				R.layout.base, null);
		layout.attachToActivity(this);
	}
	
	@Override
	public void startActivity(Intent intent) {
		super.startActivity(intent);
	}
	
	@Override
	protected int getLayoutResource() {
		return R.layout.newsreader;
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}

