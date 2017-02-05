package com.example.fragment;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.database.Article;
import com.example.net.NewsReader;
import com.example.net.R;
import com.example.swiperefresh.SwipeRefreshLayout;
import com.example.swiperefresh.SwipeRefreshLayout.OnLoadListener;
import com.example.swiperefresh.SwipeRefreshLayout.OnRefreshListener;
import com.example.util.Utility;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.inputmethodservice.Keyboard;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ListView;

public class WeiboHot extends Fragment implements OnRefreshListener,OnLoadListener{

	private ListView weibo_list;
	private SwipeRefreshLayout swipeLayout;
	private String fun="http://japi.juhe.cn/joke/content/list.from?sort=&page=&pagesize=10&time=1418816972&key=970999893fe2e8456cbb6eb9730c39e5";
	private RequestQueue mRequestQueue;
	private WeiboHot mInstance;
	private List<Map<String, Object>> fundata = new ArrayList<Map<String, Object>>();

	@SuppressLint("JavascriptInterface")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.weibohot, null);
		weibo_list=(ListView) view.findViewById(R.id.weibo_list);
		swipeLayout=(SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_weibo);
		swipeLayout.setOnRefreshListener(this);
		swipeLayout.setOnLoadListener(this);
		swipeLayout.setColor(android.R.color.holo_red_light,
				android.R.color.holo_green_light,
				android.R.color.holo_blue_bright,
				android.R.color.holo_orange_light);
		new getWeibo().execute();
		mInstance=this;
		
		return view;
	}


	public class getWeibo extends AsyncTask<String, integer, String>{

		@Override
		protected String doInBackground(String... params) {
			if (Utility.isNetworkConnected(getActivity())) {
				JsonObjectRequest jsonObjectRequest =new JsonObjectRequest(Request.Method.GET,fun,null,
						new Listener<JSONObject>() {
							@Override
							public void onResponse(JSONObject  jsonObject) {
								try {
									if (jsonObject.getString("error_code").equals("0")){
										JSONObject result=jsonObject.getJSONObject("result");
										JSONArray data= (JSONArray) result.get("data");
										Log.i("data",""+data);
										for (int i=0;i<data.length();i++){
											Map<String, Object> map = new HashMap<String, Object>();
											JSONObject object= (JSONObject) data.get(i);
											String content=object.getString("content");
											String updatetime=object.getString("updatetime");
											map.put("content",content);
											map.put("updatetime",updatetime);
											fundata.add(map);
										}
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}

							}
						}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {

					}
				});

				mInstance.addToRequestQueue(jsonObjectRequest, "test_volley");
            }
			return null;
		}
		
	}


	public <T> void addToRequestQueue(Request<T> req,String tag){
		req.setTag(TextUtils.isEmpty(tag) ? "test tag" : tag);
		getRequestQueue().add(req);
	}

	public RequestQueue getRequestQueue(){
		if (mRequestQueue==null){
			mRequestQueue= Volley.newRequestQueue(getActivity().getApplicationContext());
		}
		return mRequestQueue;
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onLoad() {
		
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		
	}

	public void parseData(String Rowdata) throws JSONException {
		Log.i("weibo",""+ Rowdata);
		if (Rowdata!=null) {
			Document document = Jsoup.parse(Rowdata);//Rowdata是js代码，还没能获取到内容
			Elements allnews_all = document.getElementsByClass("pt_ul");//没有这个标签
			Elements allitem = allnews_all.select("li.pt_li");
			Log.i("item",""+allitem);
			Utility.getFileFromBytes(Rowdata,"/sdcard/weibo.html");

		}
	}
}
